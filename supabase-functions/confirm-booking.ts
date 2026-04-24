/**
 * POST /functions/v1/confirm-booking
 *
 * Confirms a booking (changes status from PENDING to CONFIRMED).
 * Also saves a notification for the customer.
 * Optionally triggers FCM push (requires Firebase Admin SDK setup).
 *
 * Auth: Bearer JWT (host user)
 *
 * Body: { booking_id }
 * Returns: { booking_id, status, notification_id? }
 */

import { corsHeaders, err, handleOptions, ok } from "./cors.ts";
import { getAuthUser, getServiceClient } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();

  // ── Auth ───────────────────────────────────────────────────────────────
  const user = await getAuthUser(req.headers.get("Authorization"));
  if (!user) return err("Unauthorized", 401);

  // ── Parse body ─────────────────────────────────────────────────────────
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

    // ── 1. Fetch booking to verify exists and get customer_id ───────────
    const { data: booking, error: fetchErr } = await db
      .from("bookings")
      .select("id, customer_id, status_code, hotels(id, host_id)")
      .eq("id", booking_id)
      .single();

    if (fetchErr || !booking) {
      return err("Booking not found", 404);
    }

    // ── 2. Verify caller is the host of this hotel ──────────────────────
    const hotelHostId = booking.hotels?.host_id;
    if (hotelHostId !== user.id) {
      return err("Forbidden: only the hotel host can confirm bookings", 403);
    }

    // ── 3. Check if already confirmed ───────────────────────────────────
    if (booking.status_code === "CONFIRMED") {
      return ok(
        { booking_id, status: "CONFIRMED", message: "Already confirmed" },
        200
      );
    }

    // ── 4. Update booking status ───────────────────────────────────────
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

    // ── 5. Save notification for customer ──────────────────────────────
    let notifId: number | null = null;
    const { data: notif, error: notifErr } = await db
      .from("notifications")
      .insert({
        customer_id: booking.customer_id,
        title: "Booking Confirmed",
        content: `Your booking #${booking_id} has been confirmed by the hotel.`,
        type: "booking_confirmed",
      })
      .select("id")
      .single();

    if (!notifErr && notif) {
      notifId = notif.id;
    }

    // ── 6. TODO: Send FCM push notification ────────────────────────────
    // (Requires Firebase Admin SDK setup in Deno environment)
    // For now, just mark notification as saved and app will poll it.

    return ok(
      {
        booking_id,
        status: "CONFIRMED",
        notification_id: notifId,
        message: "Booking confirmed and notification sent",
      },
      200
    );
  } catch (e) {
    console.error("unexpected error:", e);
    return err("Internal server error", 500);
  }
});
