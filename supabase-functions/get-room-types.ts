/**
 * GET /functions/v1/get-room-types?hotel_id=1
 *
 * Returns all active room types for a hotel.
 * Each room type includes:
 *   - All columns from room_types (area, view, etc.)
 *   - First 10 facilities (via room_type_facilities → facilities → facility_types)
 *   - First/cover image  (one image row, is_cover preferred)
 *   - Total facility count (so the client can show "+ N more")
 *   - Total image count
 *
 * Query params:
 *   hotel_id      number   (required)
 *   include_inactive  "true"  (optional, default false — omit inactive room types)
 *
 * Auth: public.
 */

import { err, handleOptions, ok } from "./cors.ts";
import { getServiceClient } from "./supabase.ts";

const FACILITY_PREVIEW = 10;

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "GET") return err("Method not allowed", 405);

  const url              = new URL(req.url);
  const hotelIdRaw       = url.searchParams.get("hotel_id");
  const includeInactive  = url.searchParams.get("include_inactive") === "true";

  if (!hotelIdRaw) return err("'hotel_id' query param is required");
  const hotelId = parseInt(hotelIdRaw, 10);
  if (isNaN(hotelId)) return err("'hotel_id' must be a number");

  const db = getServiceClient();

  // ── 1. Room types ──────────────────────────────────────────────────────────
  let rtQuery = db
    .from("room_types")
    .select(`
      id, hotel_id, name, description,
      max_guests, bed_count, bed_type,
      base_price_per_night, has_free_cancellation,
      is_active, quantity, area, view,
      created_at
    `)
    .eq("hotel_id", hotelId)
    .order("created_at", { ascending: true });

  if (!includeInactive) rtQuery = rtQuery.eq("is_active", true);

  const { data: roomTypes, error: rtErr } = await rtQuery;
  if (rtErr) return err("Failed to fetch room types: " + rtErr.message, 500);
  if (!roomTypes?.length) return ok([]);

  const roomTypeIds = roomTypes.map((r) => r.id);

  // ── 2. Facilities for all room types (single query, then group in JS) ──────
  const { data: allFacilities } = await db
    .from("room_type_facilities")
    .select(`
      room_type_id,
      facilities (
        id, name, name_vi,
        facility_types ( id, name, name_vi )
      )
    `)
    .in("room_type_id", roomTypeIds);

  // ── 3. Facility counts per room type ──────────────────────────────────────
  // We need the total count per room type to compute "N more"
  // Group the full result set in JS — avoids N+1 queries
  const facilityCountMap: Record<number, number> = {};
  const facilityRowsMap: Record<number, any[]>   = {};

  for (const row of allFacilities ?? []) {
    const rid = row.room_type_id;
    if (!facilityCountMap[rid]) { facilityCountMap[rid] = 0; facilityRowsMap[rid] = []; }
    facilityCountMap[rid]++;
    if (facilityRowsMap[rid].length < FACILITY_PREVIEW) {
      facilityRowsMap[rid].push({
        id:           (row.facilities as any)?.id,
        name:         (row.facilities as any)?.name,
        name_vi:      (row.facilities as any)?.name_vi,
        type_id:      (row.facilities as any)?.facility_types?.id,
        type_name:    (row.facilities as any)?.facility_types?.name,
        type_name_vi: (row.facilities as any)?.facility_types?.name_vi,
      });
    }
  }

  // ── 4. Images: one cover (or first) per room type ─────────────────────────
  // Fetch all hotel-scoped room images, pick one per room type in JS
  const { data: allImages } = await db
    .from("images")
    .select("id, room_type_id, url, is_cover, created_at")
    .eq("hotel_id", hotelId)
    .in("room_type_id", roomTypeIds)
    .order("is_cover", { ascending: false })
    .order("created_at", { ascending: true });

  // Image count map and cover map
  const imageCountMap: Record<number, number> = {};
  const coverImageMap: Record<number, any>    = {};

  for (const img of allImages ?? []) {
    const rid = img.room_type_id!;
    imageCountMap[rid] = (imageCountMap[rid] ?? 0) + 1;
    if (!coverImageMap[rid]) coverImageMap[rid] = img; // first = cover (sorted above)
  }

  // ── 5. Assemble ────────────────────────────────────────────────────────────
  const result = roomTypes.map((rt) => ({
    ...rt,
    cover_image:      coverImageMap[rt.id] ?? null,
    total_images:     imageCountMap[rt.id]  ?? 0,
    facilities:       facilityRowsMap[rt.id] ?? [],
    total_facilities: facilityCountMap[rt.id] ?? 0,
  }));

  return ok(result);
});