INSERT INTO user_subscription (user_id, subscription_id, start_date, end_date) VALUES
(2, 1, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 MONTH)),
(3, 2, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 3 MONTH));
