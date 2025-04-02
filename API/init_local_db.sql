-- Добавляем организации
INSERT INTO mtsp_organizations (name) VALUES
      ('OpenAI'),
      ('Google'),
      ('Microsoft')
ON CONFLICT DO NOTHING;

-- Добавляем пользователей
INSERT INTO mtsp_users (organization_id, first_name, last_name, email, password_hash, is_admin) VALUES
    (1, 'Alice', 'Smith', '123', '123', TRUE),
    (1, 'Bob', 'Brown', 'bob@example.com', 'hashed_password2', FALSE),
    (2, 'Charlie', 'Davis', 'charlie@example.com', 'hashed_password3', FALSE)
ON CONFLICT DO NOTHING;

-- Добавляем решения
INSERT INTO mtsp_solutions (user_id, status, total_cost) VALUES
     (1, 'SOLVED', 123.45),
     (2, 'QUEUED', NULL),
     (3, 'INTERMEDIATE', 200.78)
ON CONFLICT DO NOTHING;

-- Добавляем маршруты
INSERT INTO mtsp_routes (solution_id, salesman_index, points) VALUES
      (1, 0, ARRAY['A', 'B', 'C']),
      (1, 1, ARRAY['D', 'E', 'F']),
      (2, 0, ARRAY['X', 'Y', 'Z'])
ON CONFLICT DO NOTHING;
