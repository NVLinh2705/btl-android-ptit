# Dự án xây dựng ứng dụng android đặt phòng khách sạn
## Các chức năng chính:
1. Đăng ký và đăng nhập người dùng
2. Tìm kiếm khách sạn theo địa điểm và ngày đặt phòng
3. Xem chi tiết khách sạn và đánh giá
4. Đặt phòng
5. Quản lý đặt phòng của người dùng
6. Thông báo đặt phòng
## Công nghệ sử dụng:
- App: Android Java, XML
- Backend: Supabase (PostgreSQL, REST API)
## Danh sách các bảng

### 1. Nhóm người dùng và phân quyền
- `users`: lưu thông tin người dùng đồng bộ từ `auth.users`.
- `roles`: danh mục vai trò như `CUSTOMER`, `HOST`, `ADMIN`.
- `users_roles`: bảng trung gian gán vai trò cho người dùng.
- `handle_new_user()`: hàm trigger tự động tạo bản ghi người dùng mới và gán vai trò mặc định.

### 2. Dữ liệu địa lý Việt Nam
- `provinces`: danh mục tỉnh/thành phố.
- `districts`: danh mục quận/huyện, tham chiếu đến `provinces`.
- `wards`: danh mục phường/xã, tham chiếu đến `districts`.

### 3. Khách sạn
- `hotels`: thông tin khách sạn, vị trí, mô tả, chủ khách sạn và tọa độ.

### 4. Loại phòng
- `room_types`: các loại phòng thuộc từng khách sạn, gồm sức chứa, giá, diện tích, số giường và tiện nghi cơ bản.

### 5. Tiện ích
- `facility_types`: nhóm tiện ích theo loại.
- `facilities`: danh sách tiện ích cụ thể theo từng nhóm.
- `hotel_facilities`: bảng liên kết tiện ích mà khách sạn cung cấp.
- `room_type_facilities`: bảng liên kết tiện ích của từng loại phòng.

### 6. Chính sách khách sạn
- `policy_types`: danh mục loại chính sách.
- `policies`: nội dung chính sách áp dụng cho từng khách sạn.

### 7. Hình ảnh
- `images`: lưu ảnh khách sạn và ảnh của từng loại phòng, có ảnh đại diện (`is_cover`).

### 8. Đặt phòng
- `booking_status_enum`: kiểu trạng thái đặt phòng.
- `payment_status_enum`: kiểu trạng thái thanh toán.
- `bookings`: thông tin đơn đặt phòng, khách hàng, khách sạn, ngày nhận trả phòng, tổng tiền và trạng thái.
- `booked_rooms`: chi tiết các loại phòng đã đặt trong một đơn đặt phòng.

### 9. Đánh giá
- `reviews`: đánh giá của khách hàng cho khách sạn sau khi hoàn thành đặt phòng.

### 10. Yêu thích
- `favorites`: danh sách khách sạn được khách hàng yêu thích.

### 11. Quyền truy cập dữ liệu
- Các lệnh `GRANT`, `REVOKE` và `ALTER DEFAULT PRIVILEGES` dùng để phân quyền cho `anon`, `authenticated`, và `service_role`.

## Danh sách thành viên:
1. Nguyễn Văn Linh - B22DCCN491 
2. Đoàn Quang Minh - B22DCCN527
3. Khuất Huy Nhân - B22DCCN575
4. Nguyễn Đức Lâm - B22DCCN479

## Phân chia công việc:
Link gg sheet: https://docs.google.com/spreadsheets/d/1izx__EpA9-Qv3SdDgViC_UGvSSpvcpPFraEz_ZQZQCM/edit?usp=sharing
