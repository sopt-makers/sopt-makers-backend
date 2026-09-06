-- ============================================================
-- 독립 테이블 (FK 없음)
-- ============================================================

CREATE TABLE projects
(
    id              BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    creator_id      BIGINT,
    name            VARCHAR(255),
    generation      INTEGER,
    category        VARCHAR(255),
    start_at        DATE,
    end_at          DATE,
    service_type    TEXT[],
    is_available    BOOLEAN,
    is_founding     BOOLEAN,
    summary         TEXT,
    detail          TEXT,
    logo_image      VARCHAR(255),
    thumbnail_image VARCHAR(255),
    images          TEXT[],
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE links
(
    id         BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    project_id BIGINT,
    title      VARCHAR(255),
    url        VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_links_project FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE TABLE project_users
(
    id             BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY,
    project_id     BIGINT,
    user_id        BIGINT,
    role           VARCHAR(255),
    description    TEXT,
    is_team_member BOOLEAN,
    PRIMARY KEY (id),
    CONSTRAINT fk_project_users_project FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE TABLE anonymous_profile_image
(
    id        BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    image_url VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE coffee_chat
(
    id                      BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    member_id               BIGINT       NOT NULL,
    is_coffee_chat_activate BOOLEAN      NOT NULL DEFAULT TRUE,
    career                  VARCHAR(50),
    introduction            TEXT,
    section                 VARCHAR(255) NOT NULL,
    coffee_chat_bio         TEXT         NOT NULL,
    coffee_chat_topic_type  VARCHAR(255) NOT NULL,
    topic                   TEXT         NOT NULL,
    meeting_type            VARCHAR(50)  NOT NULL,
    guideline               TEXT,
    created_at              TIMESTAMP    NOT NULL,
    updated_at              TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE coffee_chat_history
(
    id              BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    receiver_id     BIGINT    NOT NULL,
    sender_id       BIGINT    NOT NULL,
    request_content TEXT,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE coffee_chat_review
(
    id                    BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    reviewer_id           BIGINT       NOT NULL,
    coffee_chat_id        BIGINT       NOT NULL,
    anonymous_profile_image BIGINT     NOT NULL,
    nickname              VARCHAR(10)  NOT NULL,
    content               TEXT         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_coffee_chat_review_chat FOREIGN KEY (coffee_chat_id) REFERENCES coffee_chat (id),
    CONSTRAINT fk_coffee_chat_review_image FOREIGN KEY (anonymous_profile_image) REFERENCES anonymous_profile_image (id)
);

CREATE TABLE word_chain_gameroom
(
    id              BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_word      VARCHAR(255),
    created_at      TIMESTAMP,
    created_user_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE word
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT,
    word       VARCHAR(255),
    room_id    BIGINT,
    created_at TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_word_room FOREIGN KEY (room_id) REFERENCES word_chain_gameroom (id)
);

CREATE TABLE word_chain_game_winner
(
    id      BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT,
    score   INTEGER,
    room_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_word_chain_game_winner_room FOREIGN KEY (room_id) REFERENCES word_chain_gameroom (id)
);

CREATE TABLE popup
(
    id                           BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_date                   DATE      NOT NULL,
    end_date                     DATE      NOT NULL,
    pc_image_url                 VARCHAR(255) NOT NULL,
    mobile_image_url             VARCHAR(255) NOT NULL,
    link_url                     VARCHAR(255),
    open_in_new_tab              BOOLEAN,
    show_only_to_recent_generation BOOLEAN,
    created_at                   TIMESTAMP NOT NULL,
    updated_at                   TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_resolution
(
    id              BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id         BIGINT    NOT NULL,
    content         TEXT      NOT NULL,
    generation      INTEGER   NOT NULL,
    resolution_tags VARCHAR(255),
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_resolution_lucky_pick
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT    NOT NULL,
    result     BOOLEAN   NOT NULL DEFAULT FALSE,
    has_drawn  BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_user_resolution_lucky_pick_user UNIQUE (user_id)
);
