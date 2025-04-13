
-- Добавляем пользователей
INSERT INTO mtsp_users (first_name, last_name, email, password_hash) VALUES
    ('Alice', 'Smith', '123', '123'),
    ('Bob', 'Brown', 'bob@example.com', 'hashed_password2'),
    ('Charlie', 'Davis', 'charlie@example.com', 'hashed_password3')
ON CONFLICT DO NOTHING;

-- Добавляем решения
INSERT INTO mtsp_solutions (user_id, request_id, status, total_cost) VALUES
     (1, '123-aboba-321', 'SOLVED', 123.45),
     (2, '123-aboba-321', 'QUEUED', NULL),
     (3, '123-aboba-321', 'INTERMEDIATE', 200.78)
ON CONFLICT DO NOTHING;

-- Добавляем маршруты
INSERT INTO mtsp_routes (solution_id, salesman_index, points) VALUES
      (1, 0, 'A,B,C'),
      (1, 1, 'D,E,F'),
      (2, 0, 'X,Y,Z')
ON CONFLICT DO NOTHING;
