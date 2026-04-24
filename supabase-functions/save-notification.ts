/**
 * POST /functions/v1/save-notification
 *
 * Saves a notification to the notifications table.
 * Can be called from Android (when FCM message received) or backend (when sending push).
 *
 * Auth: Bearer JWT (authenticated user) or Service Role
 *
 * Body: { customer_id, title, content, type? }
 * Returns: { notification_id }
 */

import { corsHeaders, err, handleOptions, ok } from "./cors.ts";
import { getServiceClient, getUserClient, getAuthUser } from "./supabase.ts";

Deno.serve(async (req: Request) => {
  if (req.method === "OPTIONS") return handleOptions();

  // ── Parse body ─────────────────────────────────────────────────────────
  let payload: any;
  try {
    payload = await req.json();
  } catch {
    return err("Invalid JSON body");
  }

  const { customer_id, title, content, type } = payload;

  if (!customer_id || !title) {
    return err("'customer_id' and 'title' are required");
  }

  try {
    // ── Use service role to bypass RLS ──────────────────────────────────
    const db = getServiceClient();

    const { data: notif, error: saveErr } = await db
      .from("notifications")
      .insert({
        customer_id,
        title,
        content: content ?? null,
        type: type ?? "general",
      })
      .select("id")
      .single();

    if (saveErr || !notif) {
      console.error("notification insert error:", saveErr);
      return err("Failed to save notification: " + saveErr?.message, 500);
    }

    return ok({ notification_id: notif.id }, 201);
  } catch (e) {
    console.error("unexpected error:", e);
    return err("Internal server error", 500);
  }
});
