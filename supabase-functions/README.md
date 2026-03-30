# Hotel Host Portal — Supabase Edge Functions

## Overview

| Function | Method | Auth | Purpose |
|---|---|---|---|
| `create-hotel` | POST | ✅ JWT | Create hotel + facilities + room types + policies |
| `upload-image` | POST | ✅ JWT | Upload hotel/room image to Storage |
| `delete-image` | DELETE | ✅ JWT | Remove image from Storage + DB |
| `get-locations` | GET | ❌ Public | Province → District → Ward cascade |
| `get-facilities` | GET | ❌ Public | All facility types + facilities |

---

## Directory Structure

```
supabase/functions/
  _shared/
    cors.ts          # CORS headers + response helpers
    supabase.ts      # Supabase client factory + auth helper
    types.ts         # TypeScript types matching the DB schema
  create-hotel/
    index.ts
  upload-image/
    index.ts
  delete-image/
    index.ts
  get-locations/
    index.ts
  get-facilities/
    index.ts
```

---

## 1. Storage Bucket Setup

Run once in the Supabase dashboard SQL editor or via migration:

```sql
-- Create the public bucket
INSERT INTO storage.buckets (id, name, public)
VALUES ('hotel-images', 'hotel-images', true);

-- Only authenticated users can upload
CREATE POLICY "Auth users can upload hotel images"
ON storage.objects FOR INSERT
TO authenticated
WITH CHECK (bucket_id = 'hotel-images');

-- Only the file owner (or service role) can delete
CREATE POLICY "Auth users can delete own hotel images"
ON storage.objects FOR DELETE
TO authenticated
USING (bucket_id = 'hotel-images' AND owner = auth.uid());

-- Public read
CREATE POLICY "Public read hotel images"
ON storage.objects FOR SELECT
TO public
USING (bucket_id = 'hotel-images');
```

---

## 2. Deploy

```bash
# Deploy all functions
supabase functions deploy create-hotel
supabase functions deploy upload-image
supabase functions deploy delete-image
supabase functions deploy get-locations
supabase functions deploy get-facilities

# Set secrets (already available as env vars in hosted Supabase)
supabase secrets set SUPABASE_URL=https://<ref>.supabase.co
supabase secrets set SUPABASE_SERVICE_ROLE_KEY=<key>
supabase secrets set SUPABASE_ANON_KEY=<key>
```

---

## 3. Usage Examples

### create-hotel

```typescript
const BASE = "https://<ref>.supabase.co/functions/v1";

const res = await fetch(`${BASE}/create-hotel`, {
  method: "POST",
  headers: {
    "Authorization": `Bearer ${session.access_token}`,
    "Content-Type": "application/json",
  },
  body: JSON.stringify({
    // ── Basic info ─────────────────────────────────────────
    name: "The Grand Hanoi",
    description: "Luxury boutique hotel in the heart of the Old Quarter.",
    base_currency: "VND",
    is_active: true,

    // ── Location ───────────────────────────────────────────
    province_code: "01",
    district_code: "001",
    ward_code: "00001",
    street: "12 Tran Hung Dao Street",
    latitude: 21.028511,
    longitude: 105.854195,

    // ── Facilities (IDs from the facilities table) ─────────
    facility_ids: [1, 2, 11, 27, 51, 62, 70],

    // ── Room types ──────────────────────────────────────────
    room_types: [
      {
        name: "Deluxe Double",
        description: "Spacious room with city view",
        max_guests: 2,
        bed_count: 1,
        bed_type: "Double",
        base_price_per_night: 1200000,
        has_free_cancellation: true,
        quantity: 10,
      },
      {
        name: "Family Suite",
        max_guests: 4,
        bed_count: 2,
        bed_type: "King",
        base_price_per_night: 2500000,
        has_free_cancellation: false,
        quantity: 4,
      },
    ],

    // ── Policies (policy_type_id matches policy_types table) ─
    policies: [
      { policy_type_id: 1, title: "Check-in", content: "Check-in from 14:00, check-out by 12:00.", is_active: true },
      { policy_type_id: 2, title: "Cancellation", content: "Free cancellation up to 24h before arrival.", is_active: true },
      { policy_type_id: 3, title: "Pets", content: "Pets not allowed.", is_active: true },
    ],
  }),
});

const { hotel_id, room_type_ids } = await res.json();
// → { hotel_id: 42, room_type_ids: [7, 8] }
```

---

### upload-image (hotel cover photo)

```typescript
const form = new FormData();
form.append("file", fileInput.files[0]);          // File object
form.append("hotel_id", "42");
form.append("is_cover", "true");                   // mark as cover

const res = await fetch(`${BASE}/upload-image`, {
  method: "POST",
  headers: { "Authorization": `Bearer ${session.access_token}` },
  body: form,
});

const { image_id, url, is_cover } = await res.json();
// → { image_id: 15, url: "https://...supabase.co/storage/v1/...", is_cover: true }
```

### upload-image (room type photo)

```typescript
const form = new FormData();
form.append("file", file);
form.append("hotel_id", "42");
form.append("room_type_id", "7");   // attach to specific room type
form.append("is_cover", "false");

const res = await fetch(`${BASE}/upload-image`, {
  method: "POST",
  headers: { "Authorization": `Bearer ${session.access_token}` },
  body: form,
});
```

---

### upload-image (batch — all hotel photos after create-hotel)

```typescript
async function uploadHotelImages(
  hotelId: number,
  roomTypeIds: number[],
  hotelFiles: File[],           // index 0 = cover
  roomFiles: File[][]           // roomFiles[i] = photos for roomTypeIds[i]
) {
  const token = session.access_token;

  // Hotel images
  for (let i = 0; i < hotelFiles.length; i++) {
    const form = new FormData();
    form.append("file", hotelFiles[i]);
    form.append("hotel_id", String(hotelId));
    form.append("is_cover", i === 0 ? "true" : "false");
    await fetch(`${BASE}/upload-image`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      body: form,
    });
  }

  // Room type images
  for (let ri = 0; ri < roomTypeIds.length; ri++) {
    for (const file of roomFiles[ri] ?? []) {
      const form = new FormData();
      form.append("file", file);
      form.append("hotel_id", String(hotelId));
      form.append("room_type_id", String(roomTypeIds[ri]));
      form.append("is_cover", "false");
      await fetch(`${BASE}/upload-image`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: form,
      });
    }
  }
}
```

---

### delete-image

```typescript
await fetch(`${BASE}/delete-image`, {
  method: "DELETE",
  headers: {
    "Authorization": `Bearer ${session.access_token}`,
    "Content-Type": "application/json",
  },
  body: JSON.stringify({ image_id: 15 }),
});
```

---

### get-locations

```typescript
// Provinces
const provinces = await fetch(`${BASE}/get-locations?type=provinces`).then(r => r.json());

// Districts for a province
const districts = await fetch(`${BASE}/get-locations?type=districts&province_code=01`).then(r => r.json());

// Wards for a district
const wards = await fetch(`${BASE}/get-locations?type=wards&district_code=001`).then(r => r.json());
```

---

### get-facilities

```typescript
// All facilities grouped by type
const grouped = await fetch(`${BASE}/get-facilities`).then(r => r.json());

// Flat list
const flat = await fetch(`${BASE}/get-facilities?flat=true`).then(r => r.json());

// Filter by type
const bathFacilities = await fetch(`${BASE}/get-facilities?type_id=1`).then(r => r.json());

// Search
const wifiResults = await fetch(`${BASE}/get-facilities?search=wifi`).then(r => r.json());
```

---

## 4. Recommended Client-Side Flow

```
1. Load provinces      → GET /get-locations?type=provinces
2. Load facilities     → GET /get-facilities
3. Host fills form
4. POST /create-hotel  → get hotel_id + room_type_ids
5. Upload hotel images → POST /upload-image (hotel_id, is_cover=true for first)
6. Upload room images  → POST /upload-image (hotel_id + room_type_id for each)
```

---

## 5. Error Response Shape

All errors return `{ "error": "<message>" }` with the appropriate HTTP status:

| Status | Meaning |
|---|---|
| 400 | Bad request / missing field |
| 401 | Missing or invalid JWT |
| 403 | Caller doesn't own the resource |
| 404 | Resource not found |
| 405 | Wrong HTTP method |
| 500 | Supabase/server error |