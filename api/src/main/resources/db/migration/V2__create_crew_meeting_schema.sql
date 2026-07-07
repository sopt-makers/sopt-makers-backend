CREATE TABLE meeting
(
    id                              BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id                         BIGINT      NOT NULL,
    meeting_demand_id               BIGINT,
    title                           VARCHAR(255) NOT NULL,
    sub_title                       VARCHAR(30),
    category                        VARCHAR(50) NOT NULL,
    image_urls                      TEXT        NOT NULL,
    start_date                      TIMESTAMP   NOT NULL,
    end_date                        TIMESTAMP   NOT NULL,
    capacity                        INT         NOT NULL,
    description                     TEXT        NOT NULL,
    process_description             TEXT,
    activity_start_date             TIMESTAMP,
    activity_end_date               TIMESTAMP,
    leader_description              TEXT,
    note                            TEXT,
    is_mentor_needed                BOOLEAN     NOT NULL,
    can_join_only_active_generation BOOLEAN     NOT NULL,
    join_info                       TEXT,
    created_generation              INT         NOT NULL,
    target_active_generation        INT,
    joinable_parts                  TEXT,
    created_at                      TIMESTAMP   NOT NULL,
    updated_at                      TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_meeting_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_meeting_user_id ON meeting (user_id);
CREATE INDEX idx_meeting_category ON meeting (category);
CREATE INDEX idx_meeting_recruit_period ON meeting (start_date, end_date);

CREATE TABLE co_leader
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    meeting_id BIGINT    NOT NULL,
    user_id    BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_co_leader_meeting_user UNIQUE (meeting_id, user_id),
    CONSTRAINT fk_co_leader_meeting FOREIGN KEY (meeting_id) REFERENCES meeting (id),
    CONSTRAINT fk_co_leader_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_co_leader_user_id ON co_leader (user_id);

CREATE TABLE apply
(
    id           BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    type         VARCHAR(20) NOT NULL DEFAULT 'APPLY',
    meeting_id   BIGINT      NOT NULL,
    user_id      BIGINT      NOT NULL,
    content      TEXT,
    applied_date TIMESTAMP   NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    created_at   TIMESTAMP   NOT NULL,
    updated_at   TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_apply_meeting_user UNIQUE (meeting_id, user_id),
    CONSTRAINT fk_apply_meeting FOREIGN KEY (meeting_id) REFERENCES meeting (id),
    CONSTRAINT fk_apply_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_apply_meeting_id ON apply (meeting_id);
CREATE INDEX idx_apply_user_id ON apply (user_id);
CREATE INDEX idx_apply_meeting_status ON apply (meeting_id, status);
