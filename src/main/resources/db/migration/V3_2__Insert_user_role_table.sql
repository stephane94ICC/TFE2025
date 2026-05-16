INSERT INTO user_role (user_id, role_id)
SELECT id, 1
FROM users
WHERE id NOT IN (2, 3);

INSERT INTO user_role (user_id, role_id) VALUES
(2, 2),
(3, 3);