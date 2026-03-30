/**
 * GET /functions/v1/get-locations
 *
 * Returns administrative location data for the address selector.
 *
 * Query params (one required):
 *   ?type=provinces
 *   ?type=districts&province_code=01
 *   ?type=wards&district_code=001
 *
 * No auth required — public data.
 */

import { corsHeaders, err, handleOptions, ok } from "./cors.ts";
import { getServiceClient } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "GET") return err("Method not allowed", 405);

  const url = new URL(req.url);
  const type = url.searchParams.get("type");
  const db = getServiceClient();

  if (type === "provinces") {
    const { data, error } = await db
      .from("provinces")
      .select("code, name, name_en, full_name")
      .order("name");

    if (error) return err("Failed to fetch provinces: " + error.message, 500);
    return ok(data);
  }

  if (type === "districts") {
    const provinceCode = url.searchParams.get("province_code");
    if (!provinceCode) return err("'province_code' is required for type=districts");

    const { data, error } = await db
      .from("districts")
      .select("code, name, name_en, full_name, province_code")
      .eq("province_code", provinceCode)
      .order("name");

    if (error) return err("Failed to fetch districts: " + error.message, 500);
    return ok(data);
  }

  if (type === "wards") {
    const districtCode = url.searchParams.get("district_code");
    if (!districtCode) return err("'district_code' is required for type=wards");

    const { data, error } = await db
      .from("wards")
      .select("code, name, name_en, full_name, district_code")
      .eq("district_code", districtCode)
      .order("name");

    if (error) return err("Failed to fetch wards: " + error.message, 500);
    return ok(data);
  }

  return err("'type' must be one of: provinces | districts | wards");
});