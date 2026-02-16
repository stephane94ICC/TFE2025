CREATE TABLE activity_category (
    activity_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (activity_id, category_id),
    CONSTRAINT fk_activity_category_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_activity_category_category
        FOREIGN KEY (category_id) REFERENCES category(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;
