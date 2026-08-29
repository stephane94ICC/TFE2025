CREATE TABLE activity_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    event_type  VARCHAR(50)  NOT NULL,
    user_id     BIGINT       NULL,
    user_email  VARCHAR(255) NULL,
    target_type VARCHAR(50)  NULL,
    target_id   BIGINT       NULL,
    details     VARCHAR(500) NULL,
    ip_address  VARCHAR(45)  NULL,
    created_at  DATETIME     NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE INDEX idx_activity_log_created_at ON activity_log (created_at);
CREATE INDEX idx_activity_log_event_type ON activity_log (event_type);
CREATE INDEX idx_activity_log_user_id    ON activity_log (user_id);