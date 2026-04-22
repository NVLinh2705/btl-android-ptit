/**
 * GET /functions/v1/get-hotel?hotel_id=1
 *
 * Optimised version: replaces 8 sequential DB round-trips with a single
 * Postgres RPC call (get_hotel_detail). All joins, aggregations, and
 * sub-selects run inside one query plan on the DB server.
 *
 * Performance improvement:
 *   Before: 8 sequential network round-trips (hotel + facilities +
 *           facility count + images + image count + reviews +
 *           rating agg + policies + favorite)
 *   After:  1 RPC call — everything computed server-side in one plan
 *
 * Auth: public (no JWT required), but pass JWT to get correct is_liked.
 *
 * Prerequisites:
 *   Run get_hotel_detail.sql in the Supabase SQL editor first.
 */

import { err, handleOptions, ok } from "./cors.ts";
import { getAuthUser, getServiceClient } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS")
    return handleOptions();
  if (req.method !== "GET")
    return err("Method not allowed", 405);

  // ── Parse hotel_id ──────────────────────────────────────────────────────
  const url        = new URL(req.url);
  const hotelIdRaw = url.searchParams.get("hotel_id");

  if (!hotelIdRaw)
    return err("'hotel_id' query param is required");
  const hotelId = parseInt(hotelIdRaw, 10);
  if (isNaN(hotelId))
    return err("'hotel_id' must be a number");

  // ── Optional auth (for is_liked) ────────────────────────────────────────
  // We still call getAuthUser so is_liked is personalised when a JWT is sent.
  // Anonymous callers simply get is_liked = false.
  const user = await getAuthUser(req.headers.get("Authorization"));
  const userId: string | null = user?.id ?? null;

  const db = getServiceClient();

  // ── Single RPC call — replaces all 8 previous queries ───────────────────
  const { data, error } = await db.rpc("get_hotel_detail", {
    p_hotel_id: hotelId,
    p_user_id:  userId,
  });

  if (error) {
    console.error("get_hotel_detail RPC error:", error);
    return err("Failed to fetch hotel: " + error.message, 500);
  }

  // The function returns NULL when the hotel doesn't exist
  if (data === null) {
    return err("Hotel not found", 404);
  }

  return ok(data);
});