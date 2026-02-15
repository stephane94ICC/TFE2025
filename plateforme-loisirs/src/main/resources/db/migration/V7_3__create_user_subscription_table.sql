CREATE TABLE user_subscription (
    user_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    PRIMARY KEY (user_id, subscription_id, start_date),
    CONSTRAINT fk_user_subscription_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_subscription_subscription
        FOREIGN KEY (subscription_id) REFERENCES subscription(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
