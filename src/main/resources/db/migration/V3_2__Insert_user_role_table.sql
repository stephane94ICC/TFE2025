INSERT INTO user_role (user_id, role_id)
SELECT id, 1
FROM users
WHERE id IN (1, 2)
   OR id BETWEEN 4 AND 43
   OR id BETWEEN 84 AND 100;


INSERT INTO user_role (user_id, role_id)
SELECT id, 2
FROM users
WHERE id BETWEEN 44 AND 83;


INSERT INTO user_role (user_id, role_id) VALUES
(3, 3);
