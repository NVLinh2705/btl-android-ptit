// Setup type definitions for built-in Supabase Runtime APIs
import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { corsHeaders, err, handleOptions, ok } from "./cors.ts";
import { getAuthUser, getServiceClient } from "./supabase.ts";
interface LikeHotelPayload {
  hotelId: number;
}

Deno.serve(async (req: Request) => {
  const user = await getAuthUser(req.headers.get("Authorization"));
  if (!user) return err("Unauthorized", 401);

  // ── Parse body ─────────────────────────────────────────────────────────
  let payload: LikeHotelPayload;
  try {
    payload = await req.json();
  } catch {
    return err("Invalid JSON body");
  }

  if(payload.hotelId === null) return err("'hotelId' is required!")

  const db = getServiceClient();

  const {data : exist, error} = await db
    .from("favorites")
    .select("id")
    .eq("customer_id", user.id)
    .eq("hotel_id",  payload.hotelId)
    .maybeSingle();

  if (!exist) {
    const {data : fav, error : favErr} = await db
      .from("favorites")
      .insert({
        customer_id : user.id,
        hotel_id : payload.hotelId,
      })
      .select("id")
      .maybeSingle();

    if(!fav || favErr) {
      return err("Unable to insert favorites: " + favErr?.message, 500);
    }
    return ok({message: "ok: liked hotel " + payload.hotelId}, 200);
  }
  else {
    const {data, error} = await db
      .from("favorites")
      .delete()
      .eq("id", exist.id)
      .select();

    if(error) return err("Failed to delete favorites: " + error.message, 500);
    return ok({message: "ok: unliked hotel " + payload.hotelId}, 200);
  }

});