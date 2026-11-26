ALTER TABLE sopt_letter_topic ADD COLUMN subtitle VARCHAR(255);

CREATE INDEX idx_sopt_letter_created_at ON sopt_letter (created_at);

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
