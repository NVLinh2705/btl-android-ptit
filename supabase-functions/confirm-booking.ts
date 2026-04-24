/**
 * POST /functions/v1/confirm-booking
 *
 * Confirms a booking (changes status from PENDING to CONFIRMED).
 *
 * Auth: Bearer JWT (host user)
 *
 * Body: { booking_id }
 * Returns: { booking_id, status }
 */

import { err, handleOptions, ok } from "./cors.ts";
import { getAuthUser, getServiceClient } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();

  const user = await getAuthUser(req.headers.get("Authorization"));
  if (!user) return err("Unauthorized", 401);

  let payload: any;
  try {
    payload = await req.json();
  } catch {
    return err("Invalid JSON body");
  }

  const { booking_id } = payload;
  if (!booking_id) {
    return err("'booking_id' is required");
  }

  try {
    const db = getServiceClient();

    const { data: booking, error: fetchErr } = await db
      .from("bookings")
      .select("id, customer_id, status_code, hotels(id, host_id)")
      .eq("id", booking_id)
      .single();

    if (fetchErr || !booking) {
      return err("Booking not found", 404);
    }

    const hotelHostId = booking.hotels?.host_id;
    if (hotelHostId !== user.id) {
      return err("Forbidden: only the hotel host can confirm bookings", 403);
    }

    if (booking.status_code === "CONFIRMED") {
      return ok(
        { booking_id, status: "CONFIRMED", message: "Already confirmed" },
        200
      );
    }

    const { data: updated, error: updateErr } = await db
      .from("bookings")
      .update({ status_code: "CONFIRMED" })
      .eq("id", booking_id)
      .select("id, status_code")
      .single();

    if (updateErr || !updated) {
      console.error("booking update error:", updateErr);
      return err("Failed to confirm booking: " + updateErr?.message, 500);
    }

    return ok(
      {
        booking_id,
        status: "CONFIRMED",
        message: "Booking confirmed",
      },
      200
    );
  } catch (e) {
    console.error("unexpected error:", e);
    return err("Internal server error", 500);
  }
});