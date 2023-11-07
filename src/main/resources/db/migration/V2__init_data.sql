INSERT INTO member (company, email, name, password, phone, region, role)
VALUES ('Test Company', 'test@test.com', '황재현', '$2a$10$xCaPPCpZXGL2dmQb4QuKjeSgWFi15Y4kbrsc8z084A71JJicqh67O',
        '01011112222', 'BUSAN', 'ADMIN');

INSERT INTO room (max_people_count, image, name)
VALUES (14,
        'https://www.lottehotel.com/content/dam/lotte-hotel/city/daejeon/facilities/business/180708-8-2000-fac-daejeon-city.jpg.thumb.768.768.jpg',
        '대회의실'),
       (10, 'https://www.kmeetingroom.com/_storage/thumbnails/oUkUjbSJ51FlnZEh72cJniGZPv0nXUUclTrvIYxD.jpg', '중회의실'),
       (7, 'https://mediahub.seoul.go.kr/uploads/mediahub/2021/04/fIoyiYBFcMltsFcfRYSJRXSUEOCfAxuR.png', '대회의실 2'),
       (20, 'https://velog.velcdn.com/images/movie/post/f54ab19f-fa2d-4dbe-8e70-4970fff4724f/image.jpeg', '잠실 캠퍼스');
