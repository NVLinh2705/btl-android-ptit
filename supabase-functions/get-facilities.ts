/**
 * GET /functions/v1/get-facilities
 *
 * Returns all facility types with their nested facilities.
 *
 * Query params (all optional):
 *   ?flat=true          — return a flat array of facilities (no grouping)
 *   ?type_id=1          — filter by facility_type_id
 *   ?search=wifi        — case-insensitive name search
 *
 * No auth required — public data.
 */

import { corsHeaders, err, handleOptions, ok } from "./cors.ts";
import { getServiceClient } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "GET") return err("Method not allowed", 405);

  const url = new URL(req.url);
  const flat = url.searchParams.get("flat") === "true";
  const typeIdRaw = url.searchParams.get("type_id");
  const search = url.searchParams.get("search")?.trim() ?? "";
  const db = getServiceClient();

  // ── Build facility query ───────────────────────────────────────────────
  let facilityQuery = db
    .from("facilities")
    .select("id, name, name_vi, facility_type_id");

  if (typeIdRaw) {
    const typeId = parseInt(typeIdRaw, 10);
    if (!isNaN(typeId)) facilityQuery = facilityQuery.eq("facility_type_id", typeId);
  }

  if (search) {
    facilityQuery = facilityQuery.or(
      `name.ilike.%${search}%,name_vi.ilike.%${search}%`
    );
  }

  const { data: facilities, error: facErr } = await facilityQuery.order("id");
  if (facErr) return err("Failed to fetch facilities: " + facErr.message, 500);

  if (flat) return ok(facilities);

  // ── Grouped by type ───────────────────────────────────────────────────
  const { data: types, error: typeErr } = await db
    .from("facility_types")
    .select("id, name, name_vi")
    .order("id");

  if (typeErr) return err("Failed to fetch facility types: " + typeErr.message, 500);

  const grouped = types?.map((t) => ({
    ...t,
    facilities: facilities?.filter((f) => f.facility_type_id === t.id) ?? [],
  }));

  return ok(grouped);
});