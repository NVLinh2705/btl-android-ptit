Hotel Booking App – Firestore Data Model

Target DB: Cloud Firestore (NoSQL)

Conventions:
- Each top-level collection is listed with its document fields.
- `Document ID` is the Firestore document key (string).
- Types: String, Number, Boolean, Timestamp, Map, Array, GeoPoint, Reference.
- Relations are logical (Firestore does not enforce foreign keys).

---

## 1. Users

Collection: `users`

Represents both customers and hosts.

Fields:
| Field          | Type      | Description                                     |
|----------------|-----------|-------------------------------------------------|
| `fullName`     | String    | User full name                                  |
| `email`        | String    | Unique email                                    |
| `phone`        | String    | Phone number                                    |
| `passwordHash` | String    | Hashed password                                 |
| `role`         | String    | `"CUSTOMER"`, `"HOST"`, or `"ADMIN"`           |
| `createdAt`    | Timestamp | Account creation time                           |
| `isActive`     | Boolean   | Soft delete/active flag                         |

Relations:
- `users` documents are referenced by many other collections via `userId` or `userRef`.

---

## 2. Hotels

Collection: `hotels`

Fields:
| Field                 | Type      | Description                                                     |
|-----------------------|-----------|-----------------------------------------------------------------|
| `hostRef`             | Reference | Reference to `users/{hostId}` (role = HOST)                     |
| `name`                | String    | Hotel name                                                      |
| `description`         | String    | Description / summary                                           |
| `address`             | Map       | Address object (see section 3)                                  |
| `starRating`          | Number    | 1–5, optional                                                   |
| `baseCurrency`        | String    | ISO code, e.g. `"VND"`, `"USD"`                               |
| `phone`               | String    | Contact phone                                                   |
| `email`               | String    | Contact email                                                   |
| `legalBusinessName`   | String    | Legal company/hotel name                                        |
| `legalRegistrationNo` | String    | Business registration number                                    |
| `taxCode`             | String    | Tax code (optional)                                             |
| `createdAt`           | Timestamp | Creation time                                                   |
| `isActive`            | Boolean   | Whether the hotel is visible/bookable                           |

Subcollections:
- `roomTypes` (see section 4)
- `rooms` (optional, see section 5)
- `policies` (see section 7)
- `images` (see section 8)

Relations:
- `hostRef` → `users/{hostId}`.
- Many `bookings` reference a `hotel` by `hotelRef`.

---

## 3. Address & Vietnamese Locations

### 3.1 Address field in hotel

In each `hotels` document, `address` is a Map with standardized codes plus a GeoPoint for Google Maps.

`hotels/{hotelId}.address` (Map):
| Key            | Type    | Description                                                     |
|----------------|---------|-----------------------------------------------------------------|
| `provinceCode` | String  | Code of province/city (e.g. `"HN"`, `"HCM"`)                  |
| `districtCode` | String  | Code of district                                                |
| `wardCode`     | String  | Code of ward/commune                                            |
| `street`       | String  | Street name + house number (free text)                          |
| `fullText`     | String  | Human-readable full address                                     |
| `location`     | GeoPoint| Latitude/longitude (from Google Maps Geocoding API)             |

Usage with Google Maps:
- When creating/updating a hotel, use `provinceCode`, `districtCode`, `wardCode`, and `street` to build an address string.
- Call Google Geocoding API once to get `GeoPoint` (lat/lng) and save it in `address.location`.
- Later, use this GeoPoint for map markers and distance queries.

### 3.2 Location master data (Vietnam provinces/districts/wards)

Predefined location data is stored in separate collections, read-only for the app.

Collection: `provinces`

Fields:
| Field        | Type   | Description                      |
|--------------|--------|----------------------------------|
| `name`       | String | Province/city name               |
| `code`       | String | Unique province code             |
| `slug`       | String | Optional slug for search         |

Subcollection: `districts` under each `provinces/{provinceId}`

Fields:
| Field  | Type   | Description               |
|--------|--------|---------------------------|
| `name` | String | District name             |
| `code` | String | Unique district code      |

Subcollection: `wards` under each `districts/{districtId}`

Fields:
| Field  | Type   | Description               |
|--------|--------|---------------------------|
| `name` | String | Ward/commune name         |
| `code` | String | Unique ward code          |

Notes:
- The app loads `provinces`, then `districts` of a selected province, then `wards` of a selected district to populate dropdowns.
- Hotels store only `provinceCode`, `districtCode`, `wardCode`, not full names; UI can join them in memory when needed.
- Managing streets as a full predefined dataset for all of Vietnam is large; for this project, store `street` as free-text in `address`.

---

## 4. Room Types

Subcollection: `hotels/{hotelId}/roomTypes`

Fields:
| Field               | Type      | Description                                     |
|---------------------|-----------|-------------------------------------------------|
| `name`              | String    | e.g. "Deluxe Double Room"                      |
| `description`       | String    | Room type description                           |
| `maxGuests`         | Number    | Maximum guests                                  |
| `bedCount`          | Number    | Number of beds                                  |
| `bedType`           | String    | e.g. "1 double bed"                            |
| `sizeM2`            | Number    | Room size in m² (optional)                      |
| `basePricePerNight` | Number    | Default price per night                         |
| `hasFreeCancellation`| Boolean  | Whether free cancellation is available          |
| `createdAt`         | Timestamp | Creation time                                   |
| `isActive`          | Boolean   | Whether this room type is bookable              |

Relations:
- Referenced from `bookedRooms` via `roomTypeRef`.

---

## 5. Rooms (optional, physical rooms)

Subcollection: `hotels/{hotelId}/rooms`

Fields:
| Field          | Type      | Description                                   |
|----------------|-----------|-----------------------------------------------|
| `roomTypeRef`  | Reference | Reference to `roomTypes/{roomTypeId}`         |
| `roomNumber`   | String    | Room number/identifier                        |
| `floor`        | String    | Floor or building info (optional)             |
| `status`       | String    | `"AVAILABLE"`, `"OUT_OF_SERVICE"`, etc.      |

Relations:
- Optionally referenced by `bookedRooms` via `roomRef`.

---

## 6. Facilities

Collection: `facilityTypes`

Fields:
| Field  | Type   | Description                                 |
|--------|--------|---------------------------------------------|
| `name` | String | e.g. "Bathroom", "Bedroom", "Wellness"    |

Collection: `facilities`

Fields:
| Field            | Type      | Description                                      |
|------------------|-----------|--------------------------------------------------|
| `facilityTypeRef`| Reference | Reference to `facilityTypes/{facilityTypeId}`    |
| `name`           | String    | e.g. "Toilet", "Towels", "TV", "Minibar"   |

Hotel facilities are stored in each `hotels` document as an array of references or IDs:

`hotels/{hotelId}.facilityRefs` (Array of Reference):
- Each item → `facilities/{facilityId}`.

Example facility types (data seed):
- Bathroom: toilet, towels, slippers, toiletries, hair dryer, shower, toilet paper
- Bedroom: linen, wardrobe
- View: city view, sea view
- Outdoors, Kitchen, Living area, Activities, Media & technology, Food & drink,
  Reception services, Cleaning, Safety & security, Swimming pool, Wellness.

---

## 7. Policies

Collection: `policyTypes`

Fields:
| Field  | Type   | Description                                            |
|--------|--------|--------------------------------------------------------|
| `code` | String | e.g. "CHECKIN", "PARKING", "AGE_RESTRICTION"        |
| `name` | String | Human-readable name                                    |

Subcollection: `hotels/{hotelId}/policies`

Fields:
| Field           | Type      | Description                                      |
|-----------------|-----------|--------------------------------------------------|
| `policyTypeRef` | Reference | Reference to `policyTypes/{policyTypeId}`        |
| `title`         | String    | Optional title                                   |
| `content`       | String    | Free-text content (VN or EN)                     |
| `isActive`      | Boolean   | Whether this policy is currently applied         |

Examples of policy types:
- Checkin/Checkout
- Parking
- Age restriction
- Pets
- Internet
- Accepted payment methods
- Prepayment, cancellation, refund
- Children and extra beds

---

## 8. Images

Subcollection: `hotels/{hotelId}/images`

Fields:
| Field        | Type      | Description                                     |
|--------------|-----------|-------------------------------------------------|
| `roomTypeRef`| Reference | Optional reference to `roomTypes/{roomTypeId}` |
| `url`        | String    | Image URL (e.g. Cloud Storage)                  |
| `caption`    | String    | Optional caption                                |
| `sortOrder`  | Number    | Order for galleries                             |
| `isCover`    | Boolean   | Whether this is the cover image                 |
| `createdAt`  | Timestamp | Upload time                                     |

---

## 9. Booking Status

Collection: `bookingStatuses`

Fields:
| Field  | Type   | Description                                             |
|--------|--------|---------------------------------------------------------|
| `code` | String | e.g. "PENDING", "CONFIRMED", "CANCELLED", "COMPLETED" |
| `name` | String | Human-readable description                              |

You may also store status as a simple String on each booking instead of using a collection.

---

## 10. Bookings

Collection: `bookings`

Fields:
| Field            | Type      | Description                                            |
|------------------|-----------|--------------------------------------------------------|
| `bookingCode`    | String    | Human-friendly code (for display/search)              |
| `customerRef`    | Reference | Reference to `users/{customerId}`                     |
| `hotelRef`       | Reference | Reference to `hotels/{hotelId}`                       |
| `createdAt`      | Timestamp | Creation time                                          |
| `checkinDate`    | Timestamp | Date-only (set time to midnight) or Timestamp         |
| `checkoutDate`   | Timestamp | Date-only or Timestamp                                |
| `numAdults`      | Number    | Number of adults                                      |
| `numChildren`    | Number    | Number of children                                    |
| `totalAmount`    | Number    | Total price (snapshot)                                |
| `currency`       | String    | e.g. "VND"                                           |
| `statusCode`     | String    | Booking status code (or Reference to `bookingStatuses`)|
| `paymentStatus`  | String    | e.g. "UNPAID", "PAID", "REFUNDED"                 |
| `paymentMethod`  | String    | e.g. "CREDIT_CARD", "CASH_ON_ARRIVAL"              |
| `specialRequests`| String    | Optional requests                                      |
| `guestName`      | String    | If different from account                             |
| `guestEmail`     | String    | If different from account                             |
| `cancelledAt`    | Timestamp | When cancelled (nullable)                             |

Subcollection: `bookings/{bookingId}/bookedRooms`

Fields:
| Field           | Type      | Description                                           |
|-----------------|-----------|-------------------------------------------------------|
| `roomTypeRef`   | Reference | Reference to `hotels/{hotelId}/roomTypes/{roomTypeId}`|
| `roomRef`       | Reference | Optional reference to a specific room document       |
| `quantity`      | Number    | Number of rooms of this type                         |
| `pricePerNight` | Number    | Snapshot of price per night at booking time          |
| `nights`        | Number    | Calculated nights                                    |
| `subtotal`      | Number    | Snapshot: `pricePerNight * nights * quantity`        |

Relations:
- `customerRef` → `users/{userId}`.
- `hotelRef` → `hotels/{hotelId}`.
- `bookedRooms.roomTypeRef` → `roomTypes/{roomTypeId}`.

---

## 11. Reviews

Collection: `reviews`

Fields:
| Field         | Type      | Description                                      |
|---------------|-----------|--------------------------------------------------|
| `bookingRef`  | Reference | Reference to `bookings/{bookingId}`             |
| `hotelRef`    | Reference | Reference to `hotels/{hotelId}`                 |
| `customerRef` | Reference | Reference to `users/{customerId}`               |
| `rating`      | Number    | Overall rating 1–5                              |
| `comment`     | String    | Optional text comment                            |
| `createdAt`   | Timestamp | Creation time                                    |
| `isAnonymous` | Boolean   | Whether to hide the user name in UI             |

Relations:
- Usually one review per booking: enforce in app logic (1–1 between `booking` and `review`).

---

## 12. Favorites

Collection: `favorites`

Each document represents a customer liking a hotel.

Fields:
| Field         | Type      | Description                             |
|---------------|-----------|-----------------------------------------|
| `customerRef` | Reference | Reference to `users/{customerId}`       |
| `hotelRef`    | Reference | Reference to `hotels/{hotelId}`         |
| `createdAt`   | Timestamp | When the hotel was added to favorites   |

To avoid duplicates, you can use a composite document ID pattern: `"{customerId}_{hotelId}"`.

---

## 13. Relations Overview (logical)

- `users` (CUSTOMER) 1–N `bookings` via `customerRef`.
- `users` (HOST) 1–N `hotels` via `hostRef`.
- `hotels` 1–N `roomTypes`, `rooms`, `policies`, `images` via subcollections.
- `bookings` 1–N `bookedRooms` via subcollection.
- `customers` 1–N `reviews`, `hotels` 1–N `reviews`, and typically `bookings` 1–1 `reviews`.
- `customers` N–N `hotels` via `favorites`.
- `facilityTypes` 1–N `facilities`; `hotels` N–N `facilities` via `facilityRefs` array.
- `policyTypes` 1–N `policies` per hotel.
- `provinces` 1–N `districts`; `districts` 1–N `wards`; `hotels.address` stores codes referencing them.

This schema matches Firestore’s strengths (subcollections, document references, and denormalized snapshots) while keeping your entities and relationships clear for the hotel booking domain.

1. Core user entities

Customer

customer_id INTEGER PK
full_name TEXT
email TEXT UNIQUE
phone TEXT
password_hash TEXT
created_at TIMESTAMP
is_active BOOLEAN
Host

host_id INTEGER PK
full_name TEXT
email TEXT UNIQUE
phone TEXT
password_hash TEXT
legal_name TEXT (company name if needed)
tax_id TEXT (optional – legal info)
created_at TIMESTAMP
is_verified BOOLEAN
2. Hotel & address

Address

address_id INTEGER PK
country TEXT
city TEXT
district TEXT
street TEXT
postal_code TEXT
latitude REAL
longitude REAL
Hotel

hotel_id INTEGER PK
host_id INTEGER FK → Host
address_id INTEGER FK → Address
name TEXT
description TEXT
star_rating INTEGER (1–5)
checkin_from TIME
checkin_to TIME
checkout_until TIME
min_price_per_night DECIMAL(10,2)
currency TEXT (e.g. "USD", "VND")
legal_name TEXT (optional – if different from display name)
license_number TEXT (optional – legal info)
created_at TIMESTAMP
is_active BOOLEAN
3. Room & room types

RoomType

room_type_id INTEGER PK
hotel_id INTEGER FK → Hotel
name TEXT (e.g. "Deluxe Double Room")
description TEXT
max_occupancy INTEGER
bed_type TEXT (e.g. "1 queen bed")
size_m2 REAL
base_price_per_night DECIMAL(10,2)
currency TEXT
is_refundable BOOLEAN
free_cancellation_until_hours INTEGER (hours before check-in)
includes_breakfast BOOLEAN