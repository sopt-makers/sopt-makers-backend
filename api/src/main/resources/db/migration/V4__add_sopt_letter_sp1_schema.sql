ALTER TABLE sopt_letter_topic ADD COLUMN subtitle VARCHAR(255);

CREATE INDEX idx_sopt_letter_created_at ON sopt_letter (created_at);

ALTER TABLE sopt_letter_profile ADD COLUMN has_written BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE sopt_letter_topic_read
(
    id                  BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id             BIGINT    NOT NULL,
    topic_id            BIGINT    NOT NULL,
    last_read_letter_id BIGINT    NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sopt_letter_topic_read_user_topic UNIQUE (user_id, topic_id)
);

CREATE TABLE sopt_letter_like_notification
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT    NOT NULL,
    letter_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sopt_letter_like_notification_letter_user UNIQUE (letter_id, user_id)
);
