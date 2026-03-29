// ---------------------------------------------------------------------------
// Domain types — mirror the public schema
// ---------------------------------------------------------------------------

export interface RoomTypeInput {
  name: string;
  description?: string;
  max_guests: number;
  bed_count: number;
  bed_type?: string;
  base_price_per_night: number;
  has_free_cancellation?: boolean;
  is_active?: boolean;
  quantity: number;
}

export interface PolicyInput {
  policy_type_id: number;
  title?: string;
  content?: string;
  is_active?: boolean;
}

export interface CreateHotelPayload {
  // ── Basic info ──────────────────────────────────────────────────────────
  name: string;
  description?: string;
  base_currency?: string;         // default 'VND'
  is_active?: boolean;            // default true

  // ── Location ────────────────────────────────────────────────────────────
  province_code?: string;
  district_code?: string;
  ward_code?: string;
  street?: string;
  latitude?: number;
  longitude?: number;

  // ── Related records ─────────────────────────────────────────────────────
  facility_ids?: number[];        // rows inserted into hotel_facilities
  room_types?: RoomTypeInput[];   // rows inserted into room_types
  policies?: PolicyInput[];       // rows inserted into policies
}

// Returned after a successful hotel creation
export interface CreateHotelResult {
  hotel_id: number;
  room_type_ids: number[];
}

// ---------------------------------------------------------------------------
// Image upload types
// ---------------------------------------------------------------------------
export interface UploadImageResult {
  image_id: number;
  url: string;
  is_cover: boolean;
}