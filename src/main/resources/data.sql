-- 테스트 유저
INSERT IGNORE INTO users (id, name) VALUES (1, '강혁');
INSERT IGNORE INTO users (id, name) VALUES (2, '테스트유저');

-- 포인트 초기화
INSERT IGNORE INTO point (user_id, balance) VALUES (1, 50000);
INSERT IGNORE INTO point (user_id, balance) VALUES (2, 30000);

-- 커피 메뉴
INSERT IGNORE INTO menu (id, name, price) VALUES (1, '아메리카노', 4500);
INSERT IGNORE INTO menu (id, name, price) VALUES (2, '카페라떼', 5000);
INSERT IGNORE INTO menu (id, name, price) VALUES (3, '바닐라라떼', 5500);
INSERT IGNORE INTO menu (id, name, price) VALUES (4, '카라멜마키아토', 6000);
INSERT IGNORE INTO menu (id, name, price) VALUES (5, '에스프레소', 3500);
