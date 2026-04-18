/**
 * GET /functions/v1/get-available-room-types
 *
 * Query params:
 *   hotel_id       number (required)
 *   checkin        string YYYY-MM-DD (required)
 *   checkout       string YYYY-MM-DD (required)
 *   room_quantity  number (required, >=1)
 *   adults         number (required, >=1)
 *   children       number (optional, >=0, default 0)
 *
 * Returns room types that are still available for the requested stay.
 */

import { err, handleOptions, ok } from "./cors.ts";
import { getServiceClient } from "./supabase.ts";

const MS_PER_DAY = 24 * 60 * 60 * 1000;

function parsePositiveInt(raw: string | null, field: string, min = 1): number | null {
  if (!raw) return null;
  const value = parseInt(raw, 10);
  if (isNaN(value) || value < min) return null;
  return value;
}

function uniqueById<T extends { id?: number }>(items: T[]): T[] {
  const seen = new Set<number>();
  const result: T[] = [];
  for (const item of items) {
    if (!item.id || seen.has(item.id)) continue;
    seen.add(item.id);
    result.push(item);
  }
  return result;
}

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "GET") return err("Method not allowed", 405);

  const url = new URL(req.url);

  const hotelId = parsePositiveInt(url.searchParams.get("hotel_id"), "hotel_id");
  const roomQuantity = parsePositiveInt(url.searchParams.get("room_quantity"), "room_quantity");
  const adults = parsePositiveInt(url.searchParams.get("adults"), "adults");
  const children = parsePositiveInt(url.searchParams.get("children") ?? "0", "children", 0);

  const checkin = url.searchParams.get("checkin");
  const checkout = url.searchParams.get("checkout");

  if (!hotelId) return err("'hotel_id' must be a positive number");
  if (!roomQuantity) return err("'room_quantity' must be a positive number");
  if (!adults) return err("'adults' must be a positive number");
  if (children === null) return err("'children' must be >= 0");
  if (!checkin || !checkout) return err("'checkin' and 'checkout' are required (YYYY-MM-DD)");

  const checkinDate = new Date(`${checkin}T00:00:00.000Z`);
  const checkoutDate = new Date(`${checkout}T00:00:00.000Z`);
  if (isNaN(checkinDate.getTime()) || isNaN(checkoutDate.getTime())) {
    return err("Invalid date format. Use YYYY-MM-DD");
  }

  const nights = Math.floor((checkoutDate.getTime() - checkinDate.getTime()) / MS_PER_DAY);
  if (nights <= 0) return err("'checkout' must be after 'checkin'");

  const totalGuests = adults + children;
  const db = getServiceClient();

  // 1) Candidate room types: active + enough guest capacity.
  const { data: roomTypes, error: roomTypesErr } = await db
    .from("room_types")
    .select(`
      id,
      hotel_id,
      name,
      max_guests,
      bed_count,
      bed_type,
      area,
      view,
      quantity,
      has_free_cancellation,
      base_price_per_night
    `)
    .eq("hotel_id", hotelId)
    .eq("is_active", true)
    .gte("max_guests", totalGuests)
    .order("base_price_per_night", { ascending: true });

  if (roomTypesErr) return err("Failed to fetch room types: " + roomTypesErr.message, 500);
  if (!roomTypes?.length) {
    return ok({
      hotel_id: hotelId,
      checkin,
      checkout,
      nights,
      room_quantity: roomQuantity,
      adults,
      children,
      data: [],
    });
  }

  const roomTypeIds = roomTypes.map((x) => x.id);

  // 2) Reserved quantity per room_type in overlapping date range,
  //    counting only PENDING/CONFIRMED bookings.
  const { data: reservedRows, error: reservedErr } = await db
    .from("booked_rooms")
    .select(`
      room_type_id,
      quantity,
      bookings!inner (
        status_code,
        checkin_date,
        checkout_date
      )
    `)
    .in("room_type_id", roomTypeIds)
    .in("bookings.status_code", ["PENDING", "CONFIRMED"])
    .lt("bookings.checkin_date", checkout)
    .gt("bookings.checkout_date", checkin);

  if (reservedErr) return err("Failed to fetch reserved rooms: " + reservedErr.message, 500);

  const reservedMap: Record<number, number> = {};
  for (const row of reservedRows ?? []) {
    const roomTypeId = (row as any).room_type_id as number;
    const qty = Number((row as any).quantity ?? 0);
    reservedMap[roomTypeId] = (reservedMap[roomTypeId] ?? 0) + qty;
  }

  // 3) Facilities for each room type.
  const { data: facilityRows, error: facilityErr } = await db
    .from("room_type_facilities")
    .select(`
      room_type_id,
      facilities (
        id,
        name,
        name_vi,
        facility_types (
          id,
          name,
          name_vi
        )
      )
    `)
    .in("room_type_id", roomTypeIds);

  if (facilityErr) return err("Failed to fetch facilities: " + facilityErr.message, 500);

  const facilitiesMap: Record<number, any[]> = {};
  for (const row of facilityRows ?? []) {
    const roomTypeId = (row as any).room_type_id as number;
    const f = (row as any).facilities;
    if (!f) continue;

    if (!facilitiesMap[roomTypeId]) facilitiesMap[roomTypeId] = [];
    facilitiesMap[roomTypeId].push({
      id: f.id,
      name: f.name,
      name_vi: f.name_vi,
      type_id: f.facility_types?.id,
      type_name: f.facility_types?.name,
      type_name_vi: f.facility_types?.name_vi,
    });
  }

  // 4) One thumbnail image for each room type (cover preferred).
  const { data: imageRows, error: imageErr } = await db
    .from("images")
    .select("room_type_id, url, is_cover, created_at")
    .eq("hotel_id", hotelId)
    .in("room_type_id", roomTypeIds)
    .order("is_cover", { ascending: false })
    .order("created_at", { ascending: true });

  if (imageErr) return err("Failed to fetch room images: " + imageErr.message, 500);

  const thumbnailMap: Record<number, string> = {};
  for (const row of imageRows ?? []) {
    const roomTypeId = (row as any).room_type_id as number;
    if (!thumbnailMap[roomTypeId]) {
      thumbnailMap[roomTypeId] = (row as any).url;
    }
  }

  // 5) Hotel-level policies (no room-level policy table in current schema).
  const { data: policyRows, error: policyErr } = await db
    .from("policies")
    .select(`
      id,
      title,
      content,
      policy_types (
        code,
        name
      )
    `)
    .eq("hotel_id", hotelId)
    .eq("is_active", true)
    .order("id", { ascending: true });

  if (policyErr) return err("Failed to fetch policies: " + policyErr.message, 500);

  const hotelPolicies = (policyRows ?? []).map((p: any) => ({
    id: p.id,
    title: p.title,
    content: p.content,
    type_code: p.policy_types?.code,
    type_name: p.policy_types?.name,
  }));

  const preferredKeywords = [
    "wifi",
    "air conditioning",
    "private bathroom",
    "bathroom",
    "city view",
    "sea view",
    "flat-screen tv",
    "tv",
    "shower",
    "bathtub",
  ];

  const data = roomTypes
    .map((rt: any) => {
      const reserved = reservedMap[rt.id] ?? 0;
      const availableQuantity = Math.max(0, Number(rt.quantity ?? 0) - reserved);

      const allFacilities = uniqueById(facilitiesMap[rt.id] ?? []);
      const highlighted = uniqueById(
        allFacilities.filter((f) => {
          const n = String(f.name ?? "").toLowerCase();
          return preferredKeywords.some((k) => n.includes(k));
        }),
      );
      const facilityHighlights = [...highlighted, ...allFacilities]
        .slice(0, 8);

      const basePrice = Number(rt.base_price_per_night ?? 0);
      return {
        id: rt.id,
        hotel_id: rt.hotel_id,
        name: rt.name,
        max_guests: rt.max_guests,
        bed_count: rt.bed_count,
        bed_type: rt.bed_type,
        area: rt.area,
        view: rt.view,
        has_free_cancellation: !!rt.has_free_cancellation,
        base_price_per_night: basePrice,
        nights,
        total_price: basePrice * nights,
        thumbnail_url: thumbnailMap[rt.id] ?? null,
        available_quantity: availableQuantity,
        facilities: facilityHighlights,
        policies: hotelPolicies,
      };
    })
    .filter((item: any) => item.available_quantity >= roomQuantity)
    .sort((a: any, b: any) => {
      if (b.available_quantity !== a.available_quantity) {
        return b.available_quantity - a.available_quantity;
      }
      return a.base_price_per_night - b.base_price_per_night;
    });

  return ok({
    hotel_id: hotelId,
    checkin,
    checkout,
    nights,
    room_quantity: roomQuantity,
    adults,
    children,
    data,
  });
});

