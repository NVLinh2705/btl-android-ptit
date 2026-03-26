# PostGIS Usage Guide for Hotel Booking App (Supabase)

This guide shows how to use PostGIS with your PostgreSQL/Supabase hotel booking database to store hotel locations and run distance/nearby queries.

You already have `longitude` and `latitude` columns on the `hotels` table.

---

## 1. Enable PostGIS in Supabase

In Supabase, you typically enable PostGIS once per project:

1. Open the **SQL** editor in the Supabase dashboard.
2. Run:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
```

If it runs successfully, PostGIS functions/types (like `geometry`, `geography`, `ST_Distance`, etc.) are available.

---

## 2. Add a geographic point column to `hotels`

Keep your existing `longitude`/`latitude` for simplicity, and add a PostGIS column for spatial queries.

```sql
ALTER TABLE hotels
ADD COLUMN IF NOT EXISTS location geography(Point, 4326);
```

- `geography(Point, 4326)` stores a WGS84 lon/lat point (same coordinate system as Google Maps).
- You can also use `geometry(Point, 4326)`, but `geography` makes distance calculations in meters simpler.

---

## 3. Populate `location` from existing longitude/latitude

Run this once after adding the column (and whenever you backfill data):

```sql
UPDATE hotels
SET location = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography
WHERE longitude IS NOT NULL
  AND latitude  IS NOT NULL;
```

For new hotels, you should set `location` when inserting data (either in your backend or via a trigger).

Example insert (from backend):

```sql
INSERT INTO hotels (name, description, longitude, latitude, base_currency, is_active, location)
VALUES (
  'Demo Hotel',
  'Test description',
  105.8342,
  21.0278,
  'VND',
  TRUE,
  ST_SetSRID(ST_MakePoint(105.8342, 21.0278), 4326)::geography
);
```

---

## 4. Create a spatial index for faster queries

To make `ST_DWithin`/`ST_Distance` fast on large datasets, add a GiST index:

```sql
CREATE INDEX IF NOT EXISTS idx_hotels_location_geog
ON hotels
USING GIST (location);
```

This index lets Postgres quickly filter hotels by distance.

---

## 5. Find hotels near a point (e.g., user location)

### 5.1 Hotels within a given radius (in kilometers)

Example: find hotels within 5 km of a given point (`user_lon`, `user_lat`):

```sql
WITH params AS (
  SELECT
    105.8342::double precision AS user_lon,
    21.0278::double precision  AS user_lat,
    5_000::double precision    AS radius_m -- 5 km
)
SELECT h.*
FROM hotels h, params p
WHERE h.location IS NOT NULL
  AND ST_DWithin(
        h.location,
        ST_SetSRID(ST_MakePoint(p.user_lon, p.user_lat), 4326)::geography,
        p.radius_m
      );
```

- `ST_DWithin(geog1, geog2, radius)` returns true if two points are within `radius` meters.

### 5.2 Hotels sorted by distance

```sql
WITH params AS (
  SELECT
    105.8342::double precision AS user_lon,
    21.0278::double precision  AS user_lat
)
SELECT
  h.*,
  ST_Distance(
    h.location,
    ST_SetSRID(ST_MakePoint(p.user_lon, p.user_lat), 4326)::geography
  ) AS distance_m
FROM hotels h, params p
WHERE h.location IS NOT NULL
ORDER BY distance_m
LIMIT 20;
```

Use this pattern in Supabase RPC functions or directly from your backend.

---

## 6. Filter by admin unit + distance

You can combine your provinces/districts/wards with spatial filters.

Example: hotels in a specific province, ordered by distance from a point:

```sql
WITH params AS (
  SELECT
    'HNI'::text              AS province_code, -- example code
    105.8342::double precision AS user_lon,
    21.0278::double precision  AS user_lat
)
SELECT
  h.*,
  ST_Distance(
    h.location,
    ST_SetSRID(ST_MakePoint(p.user_lon, p.user_lat), 4326)::geography
  ) AS distance_m
FROM hotels h, params p
WHERE h.location IS NOT NULL
  AND h.province_code = p.province_code
ORDER BY distance_m;
```

You can also filter by district/ward using `district_code` and `ward_code` columns.

---

## 7. Using PostGIS via Supabase RPC (optional)

For mobile apps, you often don’t want to send raw SQL. Instead, you can create Postgres functions (RPC) and call them via Supabase client SDK.

Example RPC to get nearby hotels:

```sql
CREATE OR REPLACE FUNCTION public.get_nearby_hotels(
  in_lon double precision,
  in_lat double precision,
  in_radius_m double precision
)
RETURNS SETOF hotels
LANGUAGE sql
STABLE
AS $$
  SELECT h.*
  FROM hotels h
  WHERE h.location IS NOT NULL
    AND ST_DWithin(
          h.location,
          ST_SetSRID(ST_MakePoint(in_lon, in_lat), 4326)::geography,
          in_radius_m
        )
  ORDER BY ST_Distance(
            h.location,
            ST_SetSRID(ST_MakePoint(in_lon, in_lat), 4326)::geography
          );
$$;
```

Then call `rpc('get_nearby_hotels', { in_lon, in_lat, in_radius_m })` from your mobile app.

---

## 8. Summary

- Enable PostGIS once with `CREATE EXTENSION IF NOT EXISTS postgis;`.
- Add a `location geography(Point, 4326)` column to `hotels` and keep it in sync with `longitude`/`latitude`.
- Create a GiST index on `location`.
- Use `ST_DWithin` for radius filters and `ST_Distance` for ordering by distance.
- Wrap complex queries in SQL functions (RPC) for easier use from your Supabase-powered mobile app.
