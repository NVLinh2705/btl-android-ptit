/**
 * GET /functions/v1/get-hotel-reviews?hotel_id=1&page=1&order=created_at.desc
 *
 * Returns paginated reviews for a hotel, newest first.
 * Also returns a rating breakdown (count per star 1–5) on every page
 * so the client can render a rating histogram without a second request.
 *
 * Query params:
 *   hotel_id  number  (required)
 *   page      number  (default: 1)
 *   min_rating number (optional, filter by min star rating 1-10)
 *   max_rating number (optional, filter by max star rating 1-10)
 *   beginDate  string (optional, format YYYY-MM-DD)
 *   endDate    string (optional, format YYYY-MM-DD)
 *   order     string  (optional: created_at.desc | created_at.asc | rating.desc | rating.asc)
 *
 * Returns:
 *   {
 *     data: Review[],
 *     page, page_size, total, total_pages, has_next, has_prev,
 *     avg_rating: number | null,
 *     rating_breakdown: { 1: n, 2: n, 3: n, 4: n, 5: n }
 *   }
 *
 * Auth: public.
 */

import { err, handleOptions, ok } from "./cors.ts";
import { getServiceClient } from "./supabase.ts";

const PAGE_SIZE = 10;

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();
  if (req.method !== "GET") return err("Method not allowed", 405);

  const url        = new URL(req.url);
  const hotelIdRaw = url.searchParams.get("hotel_id");
  const pageRaw    = url.searchParams.get("page") ?? "1";
  const minRatingRaw = url.searchParams.get("min_rating");
  const maxRatingRaw = url.searchParams.get("max_rating");
  const beginDateRaw = url.searchParams.get("beginDate");
  const endDateRaw   = url.searchParams.get("endDate");
  const orderRaw   = url.searchParams.get("order") ?? "created_at.desc";

  if (!hotelIdRaw) return err("'hotel_id' query param is required");

  const hotelId     = parseInt(hotelIdRaw, 10);
  const page        = Math.max(1, parseInt(pageRaw, 10) || 1);
  const minRating   = minRatingRaw ? parseInt(minRatingRaw, 10) : null;
  const maxRating   = maxRatingRaw ? parseInt(maxRatingRaw, 10) : null;

  const allowedOrder = new Set([
    "created_at.desc",
    "created_at.asc",
    "rating.desc",
    "rating.asc",
  ]);

  if (isNaN(hotelId)) return err("'hotel_id' must be a number");
  if (minRating !== null && (isNaN(minRating) || minRating < 1 || minRating > 10)) {
    return err("'min_rating' must be between 1 and 10");
  }
  if (maxRating !== null && (isNaN(maxRating) || maxRating < 1 || maxRating > 10)) {
      return err("'max_rating' must be between 1 and 10");
  }
  if (!allowedOrder.has(orderRaw)) {
    return err("'order' must be one of: created_at.desc, created_at.asc, rating.desc, rating.asc");
  }

  const [orderField, orderDirection] = orderRaw.split(".");
  const isAscending = orderDirection === "asc";

  const db     = getServiceClient();
  const offset = (page - 1) * PAGE_SIZE;

  // ── Rating breakdown + avg (always across all reviews, ignoring filter) ──
  const { data: allRatings } = await db
    .from("reviews")
    .select("rating")
    .eq("hotel_id", hotelId);

  const ratingBreakdown: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 };
  let ratingSum = 0;
  for (const r of allRatings ?? []) {
    ratingBreakdown[r.rating] = (ratingBreakdown[r.rating] ?? 0) + 1;
    ratingSum += r.rating;
  }
  const totalAll = allRatings?.length ?? 0;
  const avgRating = totalAll > 0
    ? Math.round((ratingSum / totalAll) * 10) / 10
    : null;

  // ── Build filtered count query ────────────────────────────────────────────
  let countQuery = db
    .from("reviews")
    .select("*", { count: "exact", head: true })
    .eq("hotel_id", hotelId);

  if (minRating) countQuery = countQuery.gte("rating", minRating);
  if (maxRating) countQuery = countQuery.lte("rating", maxRating);
  if (beginDateRaw) countQuery = countQuery.gte("created_at", beginDateRaw);
  if (endDateRaw) countQuery = countQuery.lte("created_at", endDateRaw);


  const { count: total, error: countErr } = await countQuery;
  if (countErr) return err("Failed to count reviews: " + countErr.message, 500);

  const totalCount = total ?? 0;
  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  // ── Paginated reviews ─────────────────────────────────────────────────────
  let dataQuery = db
    .from("reviews")
    .select(`
      id, rating, comment, created_at,
      users ( id, full_name, avatar_url ),
      room_types (id, name, images(url))
    `)
    .eq("hotel_id", hotelId)
    .order(orderField, { ascending: isAscending })
    .range(offset, offset + PAGE_SIZE - 1);

  if (minRating) dataQuery = dataQuery.gte("rating", minRating);
  if (maxRating) dataQuery = dataQuery.lte("rating", maxRating);
  if (beginDateRaw) dataQuery = dataQuery.gte("created_at", beginDateRaw);
  if (endDateRaw) dataQuery = dataQuery.lte("created_at", endDateRaw);

  const { data: reviews, error: dataErr } = await dataQuery;
  if (dataErr) return err("Failed to fetch reviews: " + dataErr.message, 500);

  return ok({
    data: (reviews ?? []).map((r: any) => ({
      id:         r.id,
      rating:     r.rating,
      comment:    r.comment,
      created_at: r.created_at,
      reviewer: {
        id:         r.users?.id,
        full_name:  r.users?.full_name,
        avatar_url: r.users?.avatar_url,
      },
      room_type: {
          id: r.room_types?.id,
          name: r.room_types?.name,
          image_url: r.room_types?.images[0]?.url
      }
    })),
    page,
    page_size:        PAGE_SIZE,
    total:            totalCount,
    total_pages:      totalPages,
    has_next:         page < totalPages,
    has_prev:         page > 1,
    avg_rating:       avgRating,
    rating_breakdown: ratingBreakdown,
  });
});