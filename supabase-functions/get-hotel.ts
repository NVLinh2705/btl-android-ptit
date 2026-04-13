/**
 * GET /functions/v1/get-hotel?hotel_id=1
 *
 * Returns a single hotel with:
 *   - All columns from hotels (address replaces street)
 *   - Province / district / ward names (joined)
 *   - First 10 facilities (via hotel_facilities → facilities → facility_types)
 *   - First 10 images  (cover image always first, then gallery)
 *   - First 6 reviews  (with reviewer name + avatar)
 *   - All policies     (with policy_type name)
 *   - Aggregate stats  (avg_rating, total_reviews, total_images, total_facilities)
 *
 * Auth: public — no JWT required.
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

  // ── 1. Core hotel row (with location joins) ──────────────────────────────
  const { data: hotel, error: hotelErr } = await db
    .from("hotels")
    .select(`
      id, name, description, host_id,
      address,
      base_currency, is_active,
      longitude, latitude,
      created_at,
      province_code, district_code, ward_code
    `)
    .eq("id", hotelId)
    .single();

  if (hotelErr || !hotel) {
    return err("Hotel not found", 404);
  }

  // ── 2. First 10 facilities ───────────────────────────────────────────────
  const { data: hotelFacilities } = await db
    .from("hotel_facilities")
    .select(`
      facility_id,
      facilities (
        id, name, name_vi,
        facility_types ( id, name, name_vi )
      )
    `)
    .eq("hotel_id", hotelId)
    .limit(10);

  const facilities = (hotelFacilities ?? []).map((hf: any) => ({
    id:          hf.facilities?.id,
    name:        hf.facilities?.name,
    name_vi:     hf.facilities?.name_vi,
    type_id:     hf.facilities?.facility_types?.id,
    type_name:   hf.facilities?.facility_types?.name,
    type_name_vi: hf.facilities?.facility_types?.name_vi,
  }));

  // ── 3. Total facility count ──────────────────────────────────────────────
  const { count: totalFacilities } = await db
    .from("hotel_facilities")
    .select("*", { count: "exact", head: true })
    .eq("hotel_id", hotelId);

  // ── 4. First 10 images (cover first) ─────────────────────────────────────
  const { data: images } = await db
    .from("images")
    .select("id, url, is_cover, room_type_id, created_at")
    .eq("hotel_id", hotelId)
    .is("room_type_id", null)          // hotel-level images only
    .order("is_cover", { ascending: false })
    .order("created_at", { ascending: true })
    .limit(10);

  // ── 5. Total image count ─────────────────────────────────────────────────
  const { count: totalImages } = await db
    .from("images")
    .select("*", { count: "exact", head: true })
    .eq("hotel_id", hotelId)
    .is("room_type_id", null);

  // ── 6. First 6 reviews (newest first) ────────────────────────────────────
  const { data: reviews } = await db
    .from("reviews")
    .select(`
      id, rating, comment, created_at,
      users ( id, full_name, avatar_url )
    `)
    .eq("hotel_id", hotelId)
    .order("created_at", { ascending: false })
    .limit(6);

  // ── 7. Aggregate: avg rating + total reviews ─────────────────────────────
  const { data: ratingAgg } = await db
    .from("reviews")
    .select("rating")
    .eq("hotel_id", hotelId);

  const totalReviews = ratingAgg?.length ?? 0;
  const avgRating = totalReviews > 0
    ? Math.round((ratingAgg!.reduce((s : any, r : any) => s + r.rating, 0) / totalReviews) * 10) / 10
    : null;

  // ── 8. All policies (with policy_type) ───────────────────────────────────
  const { data: policies } = await db
    .from("policies")
    .select(`
      id, title, content, is_active,
      policy_types ( id, code, name )
    `)
    .eq("hotel_id", hotelId)
    .eq("is_active", true)
    .order("id");

  // ── Assemble response ─────────────────────────────────────────────────────
  return ok({
    ...hotel,
    stats: {
      avg_rating:       avgRating,
      total_reviews:    totalReviews,
      total_images:     totalImages ?? 0,
      total_facilities: totalFacilities ?? 0,
    },
    facilities,
    images:   images  ?? [],
    reviews:  (reviews ?? []).map((r: any) => ({
      id:         r.id,
      rating:     r.rating,
      comment:    r.comment,
      created_at: r.created_at,
      reviewer: {
        id:         r.users?.id,
        full_name:  r.users?.full_name,
        avatar_url: r.users?.avatar_url,
      },
    })),
    policies: (policies ?? []).map((p: any) => ({
      id:          p.id,
      title:       p.title,
      content:     p.content,
      is_active:   p.is_active,
      type_id:     p.policy_types?.id,
      type_code:   p.policy_types?.code,
      type_name:   p.policy_types?.name,
    })),
  });
});