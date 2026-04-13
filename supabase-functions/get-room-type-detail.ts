/**
 * GET /functions/v1/get-room-type-detail?room_type_id=1
 *
 * Returns full detail for a single room type:
 *   - All columns from room_types (area, view, etc.)
 *   - Parent hotel summary (id, name, base_currency, address)
 *   - All images (cover first, then ordered by created_at)
 *   - All facilities grouped by facility_type
 *
 * Query params:
 *   room_type_id  number  (required)
 *
 * Auth: public.
 */

import { err, handleOptions, ok } from "./cors.ts";
import { getServiceClient } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "GET") return err("Method not allowed", 405);

  const url          = new URL(req.url);
  const rtIdRaw      = url.searchParams.get("room_type_id");

  if (!rtIdRaw) return err("'room_type_id' query param is required");
  const roomTypeId = parseInt(rtIdRaw, 10);
  if (isNaN(roomTypeId)) return err("'room_type_id' must be a number");

  const db = getServiceClient();

  // ── 1. Room type row ──────────────────────────────────────────────────────
  const { data: roomType, error: rtErr } = await db
    .from("room_types")
    .select(`
      id, hotel_id, name, description,
      max_guests, bed_count, bed_type,
      base_price_per_night, has_free_cancellation,
      is_active, quantity, area, view,
      created_at
    `)
    .eq("id", roomTypeId)
    .single();

  if (rtErr || !roomType) return err("Room type not found", 404);

  // ── 2. Parent hotel summary ───────────────────────────────────────────────
  const { data: hotel } = await db
    .from("hotels")
    .select("id, name, base_currency, address, is_active")
    .eq("id", roomType.hotel_id)
    .single();

  // ── 3. All images (cover first) ───────────────────────────────────────────
  const { data: images, error: imgErr } = await db
    .from("images")
    .select("id, url, is_cover, created_at")
    .eq("hotel_id", roomType.hotel_id)
    .eq("room_type_id", roomTypeId)
    .order("is_cover",    { ascending: false })
    .order("created_at",  { ascending: true });

  if (imgErr) return err("Failed to fetch images: " + imgErr.message, 500);

  // ── 4. All facilities, then group by type in JS ───────────────────────────
  const { data: facilityRows, error: facErr } = await db
    .from("room_type_facilities")
    .select(`
      facility_id,
      facilities (
        id, name, name_vi,
        facility_types ( id, name, name_vi )
      )
    `)
    .eq("room_type_id", roomTypeId);

  if (facErr) return err("Failed to fetch facilities: " + facErr.message, 500);

  // Group by facility_type
  const facilityGroupMap: Record<number, {
    type_id:     number;
    type_name:   string;
    type_name_vi: string;
    items:       any[];
  }> = {};

  for (const row of facilityRows ?? []) {
    const f  = row.facilities as any;
    const ft = f?.facility_types as any;
    const typeId: number = ft?.id ?? 0;

    if (!facilityGroupMap[typeId]) {
      facilityGroupMap[typeId] = {
        type_id:      typeId,
        type_name:    ft?.name    ?? "Other",
        type_name_vi: ft?.name_vi ?? "Khác",
        items:        [],
      };
    }

    facilityGroupMap[typeId].items.push({
      id:      f?.id,
      name:    f?.name,
      name_vi: f?.name_vi,
    });
  }

  const facilitiesGrouped = Object.values(facilityGroupMap).sort(
    (a, b) => a.type_id - b.type_id
  );

  // Flat list as well — useful for quick chips display
  const facilitiesFlat = (facilityRows ?? []).map((row: any) => ({
    id:           row.facilities?.id,
    name:         row.facilities?.name,
    name_vi:      row.facilities?.name_vi,
    type_id:      row.facilities?.facility_types?.id,
    type_name:    row.facilities?.facility_types?.name,
    type_name_vi: row.facilities?.facility_types?.name_vi,
  }));

  // ── 5. Assemble ───────────────────────────────────────────────────────────
  return ok({
    ...roomType,
    hotel: hotel ?? null,
    images:             images           ?? [],
    total_images:       images?.length   ?? 0,
    facilities_grouped: facilitiesGrouped,
    facilities_flat:    facilitiesFlat,
    total_facilities:   facilityRows?.length ?? 0,
  });
});