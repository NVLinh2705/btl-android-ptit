/**
 * GET /functions/v1/get-hotel-facilities?hotel_id=1
 *
 * Returns all facilities of a hotel as HotelFacility[] shape:
 * [
 *   {
 *     id,
 *     name,
 *     name_vi,
 *     type_id,
 *     type_name,
 *     type_name_vi
 *   }
 * ]
 *
 * Auth: public.
 */

import { err, handleOptions, ok } from "./cors.ts";
import { getServiceClient } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "GET") return err("Method not allowed", 405);

  const url = new URL(req.url);
  const hotelIdRaw = url.searchParams.get("hotel_id");

  if (!hotelIdRaw) return err("'hotel_id' query param is required");

  const hotelId = parseInt(hotelIdRaw, 10);
  if (isNaN(hotelId)) return err("'hotel_id' must be a number");

  const db = getServiceClient();

  const { data, error } = await db
    .from("hotel_facilities")
    .select(`
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
    .eq("hotel_id", hotelId)
    .order("facility_id", { ascending: true });

  if (error) {
    return err("Failed to fetch hotel facilities: " + error.message, 500);
  }

  const facilities = (data ?? [])
    .map((row: any) => {
      const f = row.facilities;
      return {
        id: f?.id,
        name: f?.name,
        name_vi: f?.name_vi,
        type_id: f?.facility_types?.id,
        type_name: f?.facility_types?.name,
        type_name_vi: f?.facility_types?.name_vi,
      };
    })
    .filter((item: any) => item.id != null);

  return ok(facilities);
});

