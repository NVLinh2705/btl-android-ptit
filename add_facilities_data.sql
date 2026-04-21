-- SQL script to insert data for facility_types and facilities

-- Step 1: Insert the 15 main facility types
-- The IDs will be auto-incremented from 1 to 15 in this order.
INSERT INTO facility_types (name) VALUES
('Bathroom'),
('Bedroom'),
('Outdoors & View'),
('Kitchen'),
('Media & Technology'),
('Food & Drink'),
('Swimming Pool'),
('Wellness & Spa'),
('Activities'),
('Reception Services'),
('Cleaning Services'),
('Safety & Security'),
('General'),
('Accessibility'),
('Parking');

-- Step 2: Insert at least 7 facilities for each type, including Vietnamese names.

-- Facilities for 'Bathroom' (facility_type_id = 1)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(1, 'Private bathroom', 'Phòng tắm riêng'),
(1, 'Shower', 'Vòi sen'),
(1, 'Bathtub', 'Bồn tắm'),
(1, 'Free toiletries', 'Đồ vệ sinh cá nhân miễn phí'),
(1, 'Hairdryer', 'Máy sấy tóc'),
(1, 'Towels', 'Khăn tắm'),
(1, 'Slippers', 'Dép đi trong nhà'),
(1, 'Toilet paper', 'Giấy vệ sinh');

-- Facilities for 'Bedroom' (facility_type_id = 2)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(2, 'Linen', 'Ga trải giường'),
(2, 'Wardrobe or closet', 'Tủ quần áo'),
(2, 'Air conditioning', 'Điều hòa không khí'),
(2, 'Heating', 'Hệ thống sưởi'),
(2, 'Desk', 'Bàn làm việc'),
(2, 'Soundproofing', 'Cách âm'),
(2, 'Extra long beds (> 2 metres)', 'Giường siêu dài (> 2m)');

-- Facilities for 'Outdoors & View' (facility_type_id = 3)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(3, 'City view', 'Nhìn ra thành phố'),
(3, 'Sea view', 'Nhìn ra biển'),
(3, 'Garden view', 'Nhìn ra vườn'),
(3, 'Pool view', 'Nhìn ra hồ bơi'),
(3, 'Balcony', 'Ban công'),
(3, 'Terrace', 'Sân hiên'),
(3, 'Patio', 'Sân trong'),
(3, 'Outdoor furniture', 'Nội thất ngoài trời');

-- Facilities for 'Kitchen' (facility_type_id = 4)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(4, 'Refrigerator', 'Tủ lạnh'),
(4, 'Microwave', 'Lò vi sóng'),
(4, 'Electric kettle', 'Ấm đun nước điện'),
(4, 'Stovetop', 'Bếp nấu'),
(4, 'Kitchenware', 'Dụng cụ nhà bếp'),
(4, 'Dining table', 'Bàn ăn'),
(4, 'Coffee machine', 'Máy pha cà phê');

-- Facilities for 'Media & Technology' (facility_type_id = 5)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(5, 'Flat-screen TV', 'TV màn hình phẳng'),
(5, 'Cable channels', 'Truyền hình cáp'),
(5, 'Satellite channels', 'Truyền hình vệ tinh'),
(5, 'Telephone', 'Điện thoại'),
(5, 'Streaming service (like Netflix)', 'Dịch vụ streaming (như Netflix)'),
(5, 'Laptop safe', 'Két an toàn cho laptop'),
(5, 'Radio', 'Đài Radio');

-- Facilities for 'Food & Drink' (facility_type_id = 6)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(6, 'Minibar', 'Quầy bar mini'),
(6, 'Room service', 'Dịch vụ phòng'),
(6, 'Bar', 'Quầy bar'),
(6, 'Restaurant', 'Nhà hàng'),
(6, 'Breakfast in the room', 'Bữa sáng tại phòng'),
(6, 'Snack bar', 'Quầy đồ ăn nhẹ'),
(6, 'Wine/champagne', 'Rượu vang/sâm panh');

-- Facilities for 'Swimming Pool' (facility_type_id = 7)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(7, 'Outdoor pool (all year)', 'Hồ bơi ngoài trời (quanh năm)'),
(7, 'Indoor pool', 'Hồ bơi trong nhà'),
(7, 'Rooftop pool', 'Hồ bơi trên sân thượng'),
(7, 'Pool/beach towels', 'Khăn tắm hồ bơi/bãi biển'),
(7, 'Sun loungers or beach chairs', 'Ghế dài tắm nắng'),
(7, 'Pool bar', 'Quầy bar hồ bơi'),
(7, 'Infinity pool', 'Hồ bơi vô cực');

-- Facilities for 'Wellness & Spa' (facility_type_id = 8)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(8, 'Spa and wellness centre', 'Trung tâm spa và chăm sóc sức khỏe'),
(8, 'Fitness centre', 'Trung tâm thể dục'),
(8, 'Sauna', 'Phòng xông hơi'),
(8, 'Massage', 'Mát-xa'),
(8, 'Hot tub/Jacuzzi', 'Bồn tắm nước nóng/Jacuzzi'),
(8, 'Steam room', 'Phòng xông hơi ướt'),
(8, 'Beauty Services', 'Dịch vụ làm đẹp');

-- Facilities for 'Activities' (facility_type_id = 9)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(9, 'Live music/performance', 'Nhạc sống/biểu diễn'),
(9, 'Happy hour', 'Giờ khuyến mãi'),
(9, 'Themed dinner nights', 'Đêm tối theo chủ đề'),
(9, 'Tour or class about local culture', 'Tour/lớp học về văn hóa địa phương'),
(9, 'Bicycle rental', 'Cho thuê xe đạp'),
(9, 'Children''s playground', 'Sân chơi trẻ em'),
(9, 'Games room', 'Phòng chơi game');

-- Facilities for 'Reception Services' (facility_type_id = 10)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(10, '24-hour front desk', 'Lễ tân 24 giờ'),
(10, 'Concierge service', 'Dịch vụ trợ giúp đặc biệt'),
(10, 'Luggage storage', 'Giữ hành lý'),
(10, 'Tour desk', 'Bàn đặt tour'),
(10, 'Currency exchange', 'Thu đổi ngoại tệ'),
(10, 'Private check-in/check-out', 'Nhận/trả phòng riêng'),
(10, 'Invoice provided', 'Cung cấp hóa đơn');

-- Facilities for 'Cleaning Services' (facility_type_id = 11)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(11, 'Daily housekeeping', 'Dọn phòng hàng ngày'),
(11, 'Laundry', 'Giặt ủi'),
(11, 'Dry cleaning', 'Giặt khô'),
(11, 'Ironing service', 'Dịch vụ ủi'),
(11, 'Trouser press', 'Máy là quần'),
(11, 'Shoeshine', 'Đánh giày'),
(11, 'Wake-up service', 'Dịch vụ báo thức');

-- Facilities for 'Safety & Security' (facility_type_id = 12)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(12, '24-hour security', 'An ninh 24 giờ'),
(12, 'Key card access', 'Thẻ khóa điện tử'),
(12, 'Security alarm', 'Báo động an ninh'),
(12, 'Smoke alarms', 'Báo động khói'),
(12, 'CCTV in common areas', 'Camera quan sát ở khu vực chung'),
(12, 'Fire extinguishers', 'Bình chữa cháy'),
(12, 'Safety deposit box', 'Két an toàn');

-- Facilities for 'General' (facility_type_id = 13)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(13, 'Free WiFi', 'WiFi miễn phí'),
(13, 'Non-smoking rooms', 'Phòng không hút thuốc'),
(13, 'Family rooms', 'Phòng gia đình'),
(13, 'Lift', 'Thang máy'),
(13, 'Designated smoking area', 'Khu vực hút thuốc riêng'),
(13, 'Airport shuttle', 'Dịch vụ đưa đón sân bay'),
(13, 'Gift shop', 'Cửa hàng quà tặng');

-- Facilities for 'Accessibility' (facility_type_id = 14)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(14, 'Wheelchair accessible', 'Lối đi cho xe lăn'),
(14, 'Upper floors accessible by elevator', 'Tầng trên có thể đi bằng thang máy'),
(14, 'Toilet with grab rails', 'Nhà vệ sinh có tay vịn'),
(14, 'Lowered sink', 'Bồn rửa thấp'),
(14, 'Emergency cord in bathroom', 'Dây kéo khẩn cấp trong phòng tắm'),
(14, 'Visual aids: Braille', 'Hỗ trợ thị giác: chữ nổi Braille'),
(14, 'Auditory guidance', 'Hướng dẫn bằng âm thanh');

-- Facilities for 'Parking' (facility_type_id = 15)
INSERT INTO facilities (facility_type_id, name, name_vi) VALUES
(15, 'Free private parking', 'Chỗ đỗ xe riêng miễn phí'),
(15, 'On-site parking', 'Chỗ đỗ xe tại chỗ'),
(15, 'Valet parking', 'Dịch vụ đỗ xe hộ'),
(15, 'Secured parking', 'Chỗ đỗ xe an toàn'),
(15, 'Parking garage', 'Nhà để xe'),
(15, 'Electric vehicle charging station', 'Trạm sạc xe điện'),
(15, 'Accessible parking', 'Bãi đậu xe cho người khuyết tật');
