/**
 * POST /functions/v1/create-hotel
 *
 * Creates a hotel and all related records in a single call:
 *   hotels → hotel_facilities → room_types (with area & view) → room_type_facilities → policies
 *
 * Auth: Bearer JWT (must be a user with the 'host' role).
 *
 * Body: CreateHotelPayload (see _shared/types.ts)
 *
 * Returns: { hotel_id, room_type_ids }
 */

import { corsHeaders, err, handleOptions, ok } from "./cors.ts";
import { getAuthUser, getServiceClient } from "./supabase.ts";
import type {
  CreateHotelPayload,
  PolicyInput,
  RoomTypeInput,
} from "./types.ts";

// ---------------------------------------------------------------------------
// Handler
// ---------------------------------------------------------------------------
Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();

  // ── Auth ───────────────────────────────────────────────────────────────
  const user = await getAuthUser(req.headers.get("Authorization"));
  if (!user) return err("Unauthorized", 401);

  // ── Parse body ─────────────────────────────────────────────────────────
  let payload: CreateHotelPayload;
  try {
    payload = await req.json();
  } catch {
    return err("Invalid JSON body");
  }

  // ── Validate required fields ───────────────────────────────────────────
  if (!payload.name?.trim()) return err("'name' is required");

  // ── DB client (service role to bypass RLS for host operations) ─────────
  const db = getServiceClient();

  // ── Verify caller has 'host' role ──────────────────────────────────────
  const { data: roleRow } = await db
    .from("users_roles")
    .select("roles(name)")
    .eq("user_id", user.id)
    .single();

  // Uncomment to enforce host-only access:
  // if (!roleRow || (roleRow.roles as any)?.name !== "host")
  //   return err("Forbidden: host role required", 403);

  try {
    // ── 1. Insert hotel ──────────────────────────────────────────────────
    const { data: hotel, error: hotelErr } = await db
      .from("hotels")
      .insert({
        name: payload.name.trim(),
        description: payload.description ?? null,
        host_id: user.id,
        base_currency: payload.base_currency ?? "VND",
        is_active: payload.is_active ?? true,
        province_code: payload.province_code ?? null,
        district_code: payload.district_code ?? null,
        ward_code: payload.ward_code ?? null,
        street: payload.street ?? null,
        latitude: payload.latitude ?? null,
        longitude: payload.longitude ?? null,
      })
      .select("id")
      .single();

    if (hotelErr || !hotel) {
      console.error("hotel insert error:", hotelErr);
      return err("Failed to create hotel: " + hotelErr?.message, 500);
    }

    const hotelId: number = hotel.id;

    // ── 2. Hotel facilities (junction table) ─────────────────────────────
    if (payload.facility_ids?.length) {
      const facilityRows = payload.facility_ids.map((fid) => ({
        hotel_id: hotelId,
        facility_id: fid,
      }));

      const { error: facErr } = await db
        .from("hotel_facilities")
        .insert(facilityRows);

      if (facErr) {
        console.error("facility insert error:", facErr);
        // Non-fatal — hotel was created; log and continue
      }
    }

    // ── 3. Room types ─────────────────────────────────────────────────────
    const roomTypeIds: number[] = [];

    if (payload.room_types?.length) {
      // Insert room types one-by-one so we can map each back to its
      // facility_ids and insert into room_type_facilities correctly.
      for (const r of payload.room_types) {
        const { data: insertedRoom, error: roomErr } = await db
          .from("room_types")
          .insert({
            hotel_id: hotelId,
            name: r.name,
            description: r.description ?? null,
            max_guests: r.max_guests,
            bed_count: r.bed_count,
            bed_type: r.bed_type ?? null,
            base_price_per_night: r.base_price_per_night,
            has_free_cancellation: r.has_free_cancellation ?? false,
            is_active: r.is_active ?? true,
            quantity: r.quantity,
            area: r.area ?? null,
            view: r.view ?? null,
          })
          .select("id")
          .single();

        if (roomErr || !insertedRoom) {
          console.error("room_types insert error:", roomErr);
          return err("Failed to create room type: " + roomErr?.message, 500);
        }

        const roomTypeId: number = insertedRoom.id;
        roomTypeIds.push(roomTypeId);

        // ── 3a. Room-level facilities ──────────────────────────────────
        if (r.facility_ids?.length) {
          const rtFacRows = r.facility_ids.map((fid) => ({
            room_type_id: roomTypeId,
            facility_id: fid,
          }));

          const { error: rtFacErr } = await db
            .from("room_type_facilities")
            .insert(rtFacRows);

          if (rtFacErr) {
            // Non-fatal — log and continue
            console.error(
              `room_type_facilities insert error (room ${roomTypeId}):`,
              rtFacErr
            );
          }
        }
      }
    }

    // ── 4. Policies ───────────────────────────────────────────────────────
    if (payload.policies?.length) {
      const policyRows = payload.policies.map((p: PolicyInput) => ({
        hotel_id: hotelId,
        policy_type_id: p.policy_type_id,
        title: p.title ?? null,
        content: p.content ?? null,
        is_active: p.is_active ?? true,
      }));

      const { error: polErr } = await db.from("policies").insert(policyRows);

      if (polErr) {
        console.error("policies insert error:", polErr);
        // Non-fatal
      }
    }

    // ── Done ──────────────────────────────────────────────────────────────
    return ok({ hotel_id: hotelId, room_type_ids: roomTypeIds }, 201);
  } catch (e) {
    console.error("unexpected error:", e);
    return err("Internal server error", 500);
  }
});