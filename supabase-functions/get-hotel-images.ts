/**
 * GET /functions/v1/get-hotel-images?hotel_id=1&page=1
 *
 * Returns paginated hotel-level images (room_type_id IS NULL).
 * Cover image is always sorted first on page 1.
 *
 * Query params:
 *   hotel_id  number  (required)
 *   page      number  (default: 1, 1-indexed)
 *
 * Returns:
 *   {
 *     data:        Image[],
 *     page:        number,
 *     page_size:   10,
 *     total:       number,
 *     total_pages: number,
 *     has_next:    boolean,
 *     has_prev:    boolean,
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

  const url = new URL(req.url);
  const hotelIdRaw = url.searchParams.get("hotel_id");
  const pageRaw    = url.searchParams.get("page") ?? "1";

  if (!hotelIdRaw) return err("'hotel_id' query param is required");

  const hotelId = parseInt(hotelIdRaw, 10);
  const page    = Math.max(1, parseInt(pageRaw, 10) || 1);

  if (isNaN(hotelId)) return err("'hotel_id' must be a number");

  const db     = getServiceClient();
  const offset = (page - 1) * PAGE_SIZE;

  // ── Total count ───────────────────────────────────────────────────────────
  const { count: total, error: countErr } = await db
    .from("images")
    .select("*", { count: "exact", head: true })
    .eq("hotel_id", hotelId)
    .is("room_type_id", null);

  if (countErr) return err("Failed to count images: " + countErr.message, 500);

  const totalCount = total ?? 0;
  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  // ── Paginated data ────────────────────────────────────────────────────────
  const { data, error: dataErr } = await db
    .from("images")
    .select("id, url, is_cover, created_at")
    .eq("hotel_id", hotelId)
    .is("room_type_id", null)
    .order("is_cover", { ascending: false })   // cover first
    .order("created_at", { ascending: true })
    .range(offset, offset + PAGE_SIZE - 1);

  if (dataErr) return err("Failed to fetch images: " + dataErr.message, 500);

  return ok({
    data:        data ?? [],
    page,
    page_size:   PAGE_SIZE,
    total:       totalCount,
    total_pages: totalPages,
    has_next:    page < totalPages,
    has_prev:    page > 1,
  });
});