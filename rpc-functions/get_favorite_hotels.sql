CREATE OR REPLACE FUNCTION get_favorite_hotels(
    p_customer_id UUID
)
RETURNS TABLE (
    id              INTEGER,
    name            TEXT,
    avatar          TEXT,
    location        TEXT,
    rating          NUMERIC,
    average_price   INTEGER
)
LANGUAGE SQL
STABLE
SECURITY DEFINER
AS $$
    SELECT
        h.id,
        h.name,
        COALESCE(
            (SELECT url FROM images 
             WHERE hotel_id = h.id AND is_cover = true 
             LIMIT 1),
            (SELECT url FROM images 
             WHERE hotel_id = h.id 
             LIMIT 1),
            'https://via.placeholder.com/300x200?text=' || h.name
        ) AS avatar,
        
        COALESCE(p.name, '') || 
        CASE WHEN d.name IS NOT NULL THEN ', ' || d.name ELSE '' END ||
        CASE WHEN w.name IS NOT NULL THEN ', ' || w.name ELSE '' END AS location,
        
        COALESCE(
            ROUND(AVG(r.rating)::NUMERIC, 1),
            0
        ) AS rating,
        
        COALESCE(
            ROUND(AVG(rt.base_price_per_night)::NUMERIC)::INTEGER,
            0
        ) AS average_price
    
    FROM favorites f
    INNER JOIN hotels h ON h.id = f.hotel_id
    LEFT JOIN provinces p ON p.code = h.province_code
    LEFT JOIN districts d ON d.code = h.district_code
    LEFT JOIN wards w ON w.code = h.ward_code
    LEFT JOIN reviews r ON r.hotel_id = h.id
    LEFT JOIN room_types rt ON rt.hotel_id = h.id AND rt.is_active = true
    
    WHERE f.customer_id = p_customer_id
    
    GROUP BY h.id, h.name, p.name, d.name, w.name, f.created_at
    ORDER BY f.created_at DESC
$$;

GRANT EXECUTE ON FUNCTION get_favorite_hotels(UUID) TO anon, authenticated;