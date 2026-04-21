/**
 * POST /functions/v1/upload-image
 *
 * Accepts multipart/form-data with:
 *   file        File    (required) — image binary
 *   hotel_id    string  (required) — owning hotel
 *   room_type_id string (optional) — if uploading a room image
 *   is_cover    string  "true"|"false" (default "false")
 *
 * Workflow:
 *   1. Validate & decode the file
 *   2. Upload to Supabase Storage (bucket: hotel-images)
 *   3. Get the public URL
 *   4. If is_cover=true, unset existing cover for this hotel/room
 *   5. Insert row into public.images
 *
 * Returns: { image_id, url, is_cover }
 *
 * Auth: Bearer JWT required.
 *
 * Storage bucket layout:
 *   hotel-images/
 *     {hotel_id}/cover/{uuid}.{ext}
 *     {hotel_id}/gallery/{uuid}.{ext}
 *     {hotel_id}/rooms/{room_type_id}/{uuid}.{ext}
 */

import { corsHeaders, err, handleOptions, ok } from "./cors.ts";
import { getAuthUser, getServiceClient } from "./supabase.ts";

// ---------------------------------------------------------------------------
// Constants
// ---------------------------------------------------------------------------
const BUCKET = "btl-android-n7";
const FOLDER = "hotel";
const MAX_BYTES = 5 * 1024 * 1024; // 5 MB
const ALLOWED_MIME = /^image\//;    // accept any image/* mime type
const EXT_MAP: Record<string, string> = {
  "image/jpeg": "jpg",
  "image/png": "png",
  "image/webp": "webp",
  "image/gif": "gif",
  "image/avif": "avif",
  "image/heic": "heic",
  "image/tiff": "tiff",
  "image/bmp": "bmp",
};

// ---------------------------------------------------------------------------
// Handler
// ---------------------------------------------------------------------------
Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "POST") return err("Method not allowed", 405);

  // ── Auth ───────────────────────────────────────────────────────────────
  const user = await getAuthUser(req.headers.get("Authorization"));
  if (!user) return err("Unauthorized", 401);

  // ── Parse multipart form ───────────────────────────────────────────────
  let formData: FormData;
  try {
    formData = await req.formData();
  } catch {
    return err("Expected multipart/form-data");
  }

  const file = formData.get("file") as File | null;
  const hotelIdRaw = formData.get("hotel_id") as string | null;
  const roomTypeIdRaw = formData.get("room_type_id") as string | null;
  const isCoverRaw = (formData.get("is_cover") as string | null) ?? "false";

  // ── Validate inputs ────────────────────────────────────────────────────
  if (!file) return err("'file' field is required");
  if (!hotelIdRaw) return err("'hotel_id' field is required");

  const hotelId = parseInt(hotelIdRaw, 10);
  if (isNaN(hotelId)) return err("'hotel_id' must be a number");

  const roomTypeId = roomTypeIdRaw ? parseInt(roomTypeIdRaw, 10) : null;
  if (roomTypeIdRaw && isNaN(roomTypeId!)) {
    return err("'room_type_id' must be a number");
  }

  const isCover = isCoverRaw === "true";

  if (!ALLOWED_MIME.test(file.type)) {
    return err(`Unsupported file type: ${file.type}. Any image/* type is accepted`);
  }

  if (file.size > MAX_BYTES) {
    return err("File exceeds 5 MB limit");
  }

  const db = getServiceClient();

  // ── Verify caller owns the hotel ───────────────────────────────────────
  const { data: hotelRow, error: hotelErr } = await db
    .from("hotels")
    .select("id, host_id")
    .eq("id", hotelId)
    .single();

  if (hotelErr || !hotelRow) return err("Hotel not found", 404);
  if (hotelRow.host_id !== user.id) return err("Forbidden", 403);

  // ── If room_type_id provided, verify it belongs to this hotel ──────────
  if (roomTypeId !== null) {
    const { data: rtRow, error: rtErr } = await db
      .from("room_types")
      .select("id")
      .eq("id", roomTypeId)
      .eq("hotel_id", hotelId)
      .single();

    if (rtErr || !rtRow) return err("Room type not found for this hotel", 404);
  }

  // ── Build storage path ────────────────────────────────────────────────
  const uuid = crypto.randomUUID();
  const ext = EXT_MAP[file.type] ?? "jpg";
  let storagePath: string;

  if (roomTypeId !== null) {
    storagePath = `${FOLDER}/${hotelId}/rooms/${roomTypeId}/${uuid}.${ext}`;
  } else if (isCover) {
    storagePath = `${FOLDER}/${hotelId}/cover/${uuid}.${ext}`;
  } else {
    storagePath = `${FOLDER}/${hotelId}/gallery/${uuid}.${ext}`;
  }

  // ── Upload to Storage ─────────────────────────────────────────────────
  const arrayBuffer = await file.arrayBuffer();
  const { error: uploadErr } = await db.storage
    .from(BUCKET)
    .upload(storagePath, arrayBuffer, {
      contentType: file.type,
      upsert: false,
    });

  if (uploadErr) {
    console.error("storage upload error:", uploadErr);
    return err("Failed to upload image: " + uploadErr.message, 500);
  }

  // ── Get public URL ────────────────────────────────────────────────────
  const { data: urlData } = db.storage.from(BUCKET).getPublicUrl(storagePath);
  const publicUrl = urlData.publicUrl;

  // ── If setting as cover, unset existing cover for this scope ──────────
  if (isCover) {
    const coverQuery = db
      .from("images")
      .update({ is_cover: false })
      .eq("hotel_id", hotelId)
      .eq("is_cover", true);

    // Scope cover to room type or hotel-level
    if (roomTypeId !== null) {
      await coverQuery.eq("room_type_id", roomTypeId);
    } else {
      await coverQuery.is("room_type_id", null);
    }
  }

  // ── Insert image record ───────────────────────────────────────────────
  const { data: imageRow, error: imgErr } = await db
    .from("images")
    .insert({
      hotel_id: hotelId,
      room_type_id: roomTypeId,
      url: publicUrl,
      is_cover: isCover,
    })
    .select("id, url, is_cover")
    .single();

  if (imgErr || !imageRow) {
    console.error("images insert error:", imgErr);
    // Try to clean up the orphaned storage file
    await db.storage.from(BUCKET).remove([storagePath]);
    return err("Failed to save image record: " + imgErr?.message, 500);
  }

  return ok({
    image_id: imageRow.id,
    url: imageRow.url,
    is_cover: imageRow.is_cover,
  }, 201);
});