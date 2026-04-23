-- ============================================================
-- RPC: get_hotel_detail(p_hotel_id, p_user_id)
--
-- Returns a single JSON object with all hotel detail data:
--   hotel core fields + location names
--   stats (avg_rating, total_reviews, total_images, total_facilities)
--   is_liked (boolean, per-user)
--   facilities    (first 10, with type info)
--   images        (first 10 hotel-level, cover first)
--   reviews       (first 6, newest first, with reviewer info)
--   policies      (all active, with policy_type info)
--
-- Replaces 8 separate Supabase client queries with a single call.
-- Run this in the Supabase SQL editor or as a migration.
-- ============================================================

CREATE OR REPLACE FUNCTION get_hotel_detail(
    p_hotel_id  INTEGER,
    p_user_id   UUID DEFAULT NULL   -- NULL for anonymous callers
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE                              -- no writes; allows query planning optimisations
SECURITY DEFINER                    -- runs as function owner, same as service-role key
AS $$
DECLARE
    v_hotel     JSONB;
    v_result    JSONB;
BEGIN
    -- ── 1. Hotel core + location names (single row) ────────────────────────
    SELECT jsonb_build_object(
        'id',            h.id,
        'name',          h.name,
        'description',   h.description,
        'host_id',       h.host_id,
        'address',        h.address,
        'base_currency', h.base_currency,
        'is_active',     h.is_active,
        'longitude',     h.longitude,
        'latitude',      h.latitude,
        'created_at',    h.created_at,
        'province_code', h.province_code,
        'district_code', h.district_code,
        'ward_code',     h.ward_code,
        -- Joined location names (NULL-safe)
        'province_name', p.name,
        'district_name', d.name,
        'ward_name',     w.name
    )
    INTO v_hotel
    FROM   hotels h
    LEFT JOIN provinces p ON p.code = h.province_code
    LEFT JOIN districts d ON d.code = h.district_code
    LEFT JOIN wards     w ON w.code = h.ward_code
    WHERE  h.id = p_hotel_id;

    -- Hotel not found → return NULL so the caller can 404
    IF v_hotel IS NULL THEN
        RETURN NULL;
    END IF;

    -- ── 2. Build the full response in one expression ───────────────────────
    --    Each sub-select runs as a lateral/correlated subquery;
    --    PostgreSQL executes them all in a single plan node per hotel row.
    SELECT v_hotel || jsonb_build_object(

        -- ── Stats (avg_rating, counts) ──────────────────────────────────
        'stats', (
            SELECT jsonb_build_object(
                'avg_rating',       ROUND(AVG(r.rating)::NUMERIC, 1),
                'total_reviews',    COUNT(r.id),
                'total_images',     (
                    SELECT COUNT(*)
                    FROM   images i
                    WHERE  i.hotel_id     = p_hotel_id
                      AND  i.room_type_id IS NULL
                ),
                'total_facilities', (
                    SELECT COUNT(*)
                    FROM   hotel_facilities hf
                    WHERE  hf.hotel_id = p_hotel_id
                )
            )
            FROM reviews r
            WHERE r.hotel_id = p_hotel_id
        ),

        -- ── is_liked (per user) ─────────────────────────────────────────
        'is_liked', (
            CASE
                WHEN p_user_id IS NULL THEN FALSE
                ELSE EXISTS (
                    SELECT 1
                    FROM   favorites f
                    WHERE  f.hotel_id    = p_hotel_id
                      AND  f.customer_id = p_user_id
                )
            END
        ),

        -- ── Facilities (first 10, with type info) ───────────────────────
        'facilities', (
            SELECT COALESCE(jsonb_agg(fac_obj ORDER BY sub_id), '[]'::jsonb)
            FROM (
                SELECT hf.id as sub_id,
                       jsonb_build_object(
                           'id',          f.id,
                           'name',        f.name,
                           'name_vi',     f.name_vi,
                           'type_id',     ft.id,
                           'type_name',   ft.name,
                           'type_name_vi', ft.name_vi
                       ) AS fac_obj
                FROM   hotel_facilities hf
                JOIN   facilities       f  ON f.id  = hf.facility_id
                JOIN   facility_types   ft ON ft.id = f.facility_type_id
                WHERE  hf.hotel_id = p_hotel_id
                ORDER  BY hf.id
                LIMIT  10
            ) sub
        ),

        -- ── Images (first 10, cover first, hotel-level only) ────────────
        'images', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id',          i.id,
                    'url',         i.url,
                    'is_cover',    i.is_cover,
                    'room_type_id', i.room_type_id,
                    'created_at',  i.created_at
                )
                ORDER BY i.is_cover DESC, i.created_at ASC
            ), '[]'::jsonb)
            FROM (
                SELECT id, url, is_cover, room_type_id, created_at
                FROM   images
                WHERE  hotel_id     = p_hotel_id
                  AND  room_type_id IS NULL
                ORDER  BY is_cover DESC, created_at ASC
                LIMIT  10
            ) i
        ),

        -- ── Reviews (first 6, newest first, with reviewer) ──────────────
        'reviews', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id',         rv.id,
                    'rating',     rv.rating,
                    'comment',    rv.comment,
                    'created_at', rv.created_at,
                    'reviewer',   jsonb_build_object(
                        'id',         u.id,
                        'full_name',  u.full_name,
                        'avatar_url', u.avatar_url
                    )
                )
                ORDER BY rv.created_at DESC
            ), '[]'::jsonb)
            FROM (
                SELECT rv.id, rv.rating, rv.comment, rv.created_at, rv.customer_id
                FROM   reviews rv
                WHERE  rv.hotel_id = p_hotel_id
                ORDER  BY rv.created_at DESC
                LIMIT  6
            ) rv
            JOIN users u ON u.id = rv.customer_id
        ),

        -- ── Policies (all active, with policy_type) ──────────────────────
        'policies', (
            SELECT COALESCE(jsonb_agg(
                jsonb_build_object(
                    'id',        po.id,
                    'title',     po.title,
                    'content',   po.content,
                    'is_active', po.is_active,
                    'type_id',   pt.id,
                    'type_code', pt.code,
                    'type_name', pt.name
                )
                ORDER BY po.id
            ), '[]'::jsonb)
            FROM   policies     po
            JOIN   policy_types pt ON pt.id = po.policy_type_id
            WHERE  po.hotel_id = p_hotel_id
              AND  po.is_active = TRUE
        )

    )
    INTO v_result;

    RETURN v_result;
END;
$$;

-- Allow anon + authenticated roles to call this function via PostgREST
GRANT EXECUTE ON FUNCTION get_hotel_detail(INTEGER, UUID) TO anon, authenticated;

-- ── Recommended supporting indexes (add if not already present) ──────────

-- Already in schema: idx_hotel_facilities_hotel, idx_policies_hotel
-- Add these if missing:

CREATE INDEX IF NOT EXISTS idx_images_hotel_cover
    ON images (hotel_id, room_type_id, is_cover, created_at)
    WHERE room_type_id IS NULL;

CREATE INDEX IF NOT EXISTS idx_reviews_hotel_date
    ON reviews (hotel_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_favorites_customer_hotel
    ON favorites (customer_id, hotel_id);