INSERT INTO eattable.user (id, created_at, updated_at, deleted, deleted_at, email, nickname, password, phone_number,
                           role)
VALUES (1, '2024-08-22 15:23:22.999418', '2024-08-22 15:23:22.999418', false, null, 'test@test.com', '테스트',
        '$2a$10$AbQ9P53kqpqK.eqWUtYEu.96V16TczI/wb6QYx7cclJIELGMIvzO.', '010-1234-5678', 'ROLE_USER');

INSERT INTO eattable.restaurant (id, created_at, updated_at, address, category_name, deleted, deleted_at, description,
                                 image_url, is_available, latitude, longitude, name, phone, reservation_duration,
                                 user_id)
VALUES (1, '2024-08-22 15:24:47.313979', '2024-08-22 15:24:47.313979', '서울특별시 용산구 어딘가', '한식', false, null, '맛집',
        'test_image_url', true, 37.123456, 126.123456, '맛있는 식당', '02-1234-5678', 60, 1);

INSERT INTO eattable.restaurant_table (id, capacity, count, restaurant_id)
VALUES (1, 1, 5, 1);
INSERT INTO eattable.restaurant_table (id, capacity, count, restaurant_id)
VALUES (2, 2, 5, 1);
INSERT INTO eattable.restaurant_table (id, capacity, count, restaurant_id)
VALUES (3, 3, 5, 1);
INSERT INTO eattable.restaurant_table (id, capacity, count, restaurant_id)
VALUES (4, 4, 5, 1);

INSERT INTO eattable.table_availability (id, date, end_time, remaining_table_count, start_time, restaurant_id,
                                         restaurant_table_id)
VALUES (1, '2024-09-01', '11:00:00', 25, '10:00:00', 1, 1);
INSERT INTO eattable.table_availability (id, date, end_time, remaining_table_count, start_time, restaurant_id,
                                         restaurant_table_id)
VALUES (2, '2024-09-01', '11:00:00', 25, '10:00:00', 1, 2);
INSERT INTO eattable.table_availability (id, date, end_time, remaining_table_count, start_time, restaurant_id,
                                         restaurant_table_id)
VALUES (3, '2024-09-01', '11:00:00', 25, '10:00:00', 1, 3);
INSERT INTO eattable.table_availability (id, date, end_time, remaining_table_count, start_time, restaurant_id,
                                         restaurant_table_id)
VALUES (4, '2024-09-01', '11:00:00', 25, '10:00:00', 1, 4);