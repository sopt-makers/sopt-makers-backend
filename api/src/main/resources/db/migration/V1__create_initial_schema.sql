-- ============================================================
-- 독립 테이블 (FK 없음)
-- ============================================================

CREATE TABLE admin
(
    admin_id     BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    email        VARCHAR(255) NOT NULL,
    password     VARCHAR(255),
    name         VARCHAR(255),
    account_type VARCHAR(50),
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (admin_id),
    CONSTRAINT uq_admin_email UNIQUE (email)
);

CREATE TABLE generation
(
    id                   INT          NOT NULL,
    name                 VARCHAR(50)  NOT NULL,
    header_image         VARCHAR(500) NOT NULL,
    recruit_header_image VARCHAR(500) NOT NULL,
    home_header_image    VARCHAR(500) NOT NULL,
    dark_mode_key_color  VARCHAR(7),
    dark_mode_text_color VARCHAR(5),
    light_mode_key_color VARCHAR(7),
    light_mode_text_color VARCHAR(5),
    PRIMARY KEY (id)
);

CREATE TABLE executive_member
(
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    generation_id     INT          NOT NULL,
    role              VARCHAR(50)  NOT NULL,
    name              VARCHAR(50)  NOT NULL,
    affiliation       VARCHAR(100) NOT NULL,
    introduction      VARCHAR(500) NOT NULL,
    profile_image_url VARCHAR(500) NOT NULL,
    sns_email         VARCHAR(100),
    sns_linkedin      VARCHAR(200),
    sns_github        VARCHAR(200),
    sns_behance       VARCHAR(200),
    PRIMARY KEY (id)
);

CREATE TABLE recruitment
(
    id                      BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    generation_id           INT         NOT NULL,
    recruit_type            VARCHAR(10) NOT NULL,
    application_start_time  VARCHAR(50),
    application_end_time    VARCHAR(50),
    application_result_time VARCHAR(50),
    interview_start_time    VARCHAR(50),
    interview_end_time      VARCHAR(50),
    final_result_time       VARCHAR(50),
    PRIMARY KEY (id)
);

CREATE TABLE activity_schedule
(
    id            BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    generation_id INT          NOT NULL,
    name          VARCHAR(100) NOT NULL,
    start_date    DATE         NOT NULL,
    end_date      DATE,
    PRIMARY KEY (id)
);

CREATE TABLE part_type
(
    id            BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    generation_id INT           NOT NULL,
    part_type     VARCHAR(20)   NOT NULL,
    description   VARCHAR(1000) NOT NULL,
    curriculums   TEXT          NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE recruit_part_introduction
(
    id                      BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    generation_id           INT           NOT NULL,
    part                    VARCHAR(20)   NOT NULL,
    introduction_content    VARCHAR(2000) NOT NULL,
    introduction_preference VARCHAR(1000) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE core_value
(
    id                 BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    generation_id      INT          NOT NULL,
    value              VARCHAR(50)  NOT NULL,
    description        VARCHAR(500) NOT NULL,
    detail_description VARCHAR(100) NOT NULL,
    image_url          VARCHAR(500) NOT NULL,
    display_order      INT          NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE faq
(
    id        BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    part      VARCHAR(20) NOT NULL,
    questions TEXT        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE main_news
(
    id         INT          NOT NULL GENERATED ALWAYS AS IDENTITY,
    image      VARCHAR(255) NOT NULL,
    title      VARCHAR(255) NOT NULL,
    link       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE homepage_review
(
    id          BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    title       VARCHAR(255) NOT NULL,
    content     VARCHAR(200) NOT NULL,
    author_info VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE review
(
    id                      BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    title                   VARCHAR(1000) NOT NULL,
    description             VARCHAR(2000) NOT NULL,
    thumbnail_url           VARCHAR(500),
    platform                VARCHAR(50)   NOT NULL,
    author                  VARCHAR(20)   NOT NULL,
    author_profile_image_url VARCHAR(500),
    generation              INT           NOT NULL,
    part                    VARCHAR(20)   NOT NULL,
    category                VARCHAR(20)   NOT NULL,
    subject                 TEXT          NOT NULL,
    url                     VARCHAR(500)  NOT NULL,
    created_at              TIMESTAMP     NOT NULL,
    updated_at              TIMESTAMP     NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE notification
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    email      VARCHAR(255) NOT NULL,
    generation INT          NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE sopt_story
(
    id             BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    title          VARCHAR(100) NOT NULL,
    description    VARCHAR(600) NOT NULL,
    thumbnail_url  VARCHAR(500),
    sopt_story_url VARCHAR(500) NOT NULL,
    like_count     INT          NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE alarms
(
    id          BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    status      VARCHAR(20)  NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    action      VARCHAR(20)  NOT NULL,
    part        VARCHAR(20)  NOT NULL,
    target_type VARCHAR(20)  NOT NULL,
    generation  INT,
    targets     TEXT,
    category    VARCHAR(20)  NOT NULL,
    title       VARCHAR(255) NOT NULL,
    content     TEXT,
    link_path   TEXT,
    link_type   VARCHAR(10)  NOT NULL,
    intended_at TIMESTAMP,
    send_at     TIMESTAMP,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE banners
(
    id               BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    location         VARCHAR(50) NOT NULL,
    content_type     VARCHAR(50) NOT NULL,
    publisher        VARCHAR(255) NOT NULL,
    link             VARCHAR(255),
    pc_image_key     VARCHAR(255),
    mobile_image_key VARCHAR(255),
    start_date       DATE        NOT NULL,
    end_date         DATE        NOT NULL,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP   NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE schedules
(
    schedule_id BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_date  TIMESTAMP,
    end_date    TIMESTAMP,
    attribute   VARCHAR(50),
    title       VARCHAR(255),
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP   NOT NULL,
    PRIMARY KEY (schedule_id)
);

CREATE TABLE phone_verifications
(
    id          BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    name        VARCHAR(255),
    phone       VARCHAR(255) NOT NULL,
    code        VARCHAR(255) NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    is_verified BOOLEAN      NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE lectures
(
    lecture_id BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    name       VARCHAR(255),
    part       VARCHAR(50),
    generation INT,
    place      VARCHAR(255),
    start_date TIMESTAMP,
    end_date   TIMESTAMP,
    attribute  VARCHAR(50),
    status     VARCHAR(50),
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL,
    PRIMARY KEY (lecture_id)
);

CREATE TABLE users
(
    id                        BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name                      VARCHAR(255) NOT NULL,
    phone                     VARCHAR(255) NOT NULL,
    email                     VARCHAR(255),
    birthday                  DATE,
    auth_platform_id          VARCHAR(255) NOT NULL,
    profile_image             VARCHAR(255),
    is_first_login            BOOLEAN      NOT NULL DEFAULT TRUE,
    auth_platform_type        VARCHAR(50)  NOT NULL,
    address                   VARCHAR(255),
    university                VARCHAR(255),
    major                     VARCHAR(255),
    introduction              VARCHAR(255),
    mbti                      VARCHAR(10),
    mbti_description          VARCHAR(255),
    soju_capacity             DOUBLE PRECISION,
    interest                  VARCHAR(255),
    is_pour_sauce_lover       BOOLEAN,
    is_hard_peach_lover       BOOLEAN,
    is_mint_choco_lover       BOOLEAN,
    is_red_bean_fish_bread_lover BOOLEAN,
    is_soju_lover             BOOLEAN,
    is_rice_tteok_lover       BOOLEAN,
    ideal_type                VARCHAR(255),
    self_introduction         VARCHAR(255),
    skill                     VARCHAR(255),
    allow_official            BOOLEAN,
    is_phone_blind            BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at                TIMESTAMP    NOT NULL,
    updated_at                TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_auth_platform_id_and_auth_platform_type UNIQUE (auth_platform_id, auth_platform_type)
);

CREATE TABLE user_register_infos
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name       VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    birthday   DATE         NOT NULL,
    generation INT,
    part       VARCHAR(50)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

-- ============================================================
-- FK 참조 테이블
-- ============================================================

CREATE TABLE meeting
(
    id                              BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id                         BIGINT       NOT NULL,
    meeting_demand_id               BIGINT,
    title                           VARCHAR(255) NOT NULL,
    sub_title                       VARCHAR(30),
    category                        VARCHAR(50)  NOT NULL,
    image_urls                      TEXT         NOT NULL,
    start_date                      TIMESTAMP    NOT NULL,
    end_date                        TIMESTAMP    NOT NULL,
    capacity                        INT          NOT NULL,
    description                     TEXT         NOT NULL,
    process_description             TEXT,
    activity_start_date             TIMESTAMP,
    activity_end_date               TIMESTAMP,
    leader_description              TEXT,
    note                            TEXT,
    is_mentor_needed                BOOLEAN      NOT NULL,
    can_join_only_active_generation BOOLEAN      NOT NULL,
    join_info                       TEXT,
    created_generation              INT          NOT NULL,
    target_active_generation        INT,
    joinable_parts                  TEXT,
    created_at                      TIMESTAMP    NOT NULL,
    updated_at                      TIMESTAMP    NOT NULL,
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

CREATE TABLE flash
(
    id                  BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    leader_user_id      BIGINT       NOT NULL,
    meeting_id          BIGINT       NOT NULL,
    title               VARCHAR(30)  NOT NULL,
    description         VARCHAR(500) NOT NULL,
    timing_type         VARCHAR(30)  NOT NULL,
    start_date          TIMESTAMP    NOT NULL,
    end_date            TIMESTAMP    NOT NULL,
    activity_start_date TIMESTAMP    NOT NULL,
    activity_end_date   TIMESTAMP    NOT NULL,
    place_type          VARCHAR(30)  NOT NULL,
    place               VARCHAR(255),
    minimum_capacity    INT          NOT NULL,
    maximum_capacity    INT          NOT NULL,
    created_generation  INT          NOT NULL,
    image_urls          TEXT         NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_flash_meeting UNIQUE (meeting_id),
    CONSTRAINT fk_flash_leader_user FOREIGN KEY (leader_user_id) REFERENCES users (id),
    CONSTRAINT fk_flash_meeting FOREIGN KEY (meeting_id) REFERENCES meeting (id)
);

CREATE INDEX idx_flash_leader_user_id ON flash (leader_user_id);

CREATE TABLE meeting_keyword_preference
(
    user_id       BIGINT    NOT NULL,
    keyword_types TEXT      NOT NULL,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id),
    CONSTRAINT fk_meeting_keyword_preference_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE tag
(
    id                    BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    tag_type              VARCHAR(20) NOT NULL,
    meeting_id            BIGINT      NOT NULL,
    flash_id              BIGINT,
    welcome_message_types TEXT        NOT NULL,
    meeting_keyword_types TEXT        NOT NULL,
    created_at            TIMESTAMP   NOT NULL,
    updated_at            TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_tag_meeting UNIQUE (meeting_id),
    CONSTRAINT uk_tag_flash UNIQUE (flash_id),
    CONSTRAINT fk_tag_meeting FOREIGN KEY (meeting_id) REFERENCES meeting (id),
    CONSTRAINT fk_tag_flash FOREIGN KEY (flash_id) REFERENCES flash (id)
);

CREATE TABLE sub_lectures
(
    sub_lecture_id BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    lecture_id     BIGINT    NOT NULL,
    round          INT,
    start_at       TIMESTAMP,
    code           VARCHAR(255),
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP NOT NULL,
    PRIMARY KEY (sub_lecture_id),
    CONSTRAINT uq_sub_lectures_lecture_round UNIQUE (lecture_id, round),
    CONSTRAINT fk_sub_lectures_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (lecture_id)
);

CREATE TABLE attendances
(
    attendance_id BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id       BIGINT      NOT NULL,
    lecture_id    BIGINT      NOT NULL,
    status        VARCHAR(50),
    created_at    TIMESTAMP   NOT NULL,
    updated_at    TIMESTAMP   NOT NULL,
    PRIMARY KEY (attendance_id),
    CONSTRAINT fk_attendances_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_attendances_lecture FOREIGN KEY (lecture_id) REFERENCES lectures (lecture_id)
);

CREATE TABLE sub_attendances
(
    sub_attendance_id BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    attendance_id     BIGINT      NOT NULL,
    sub_lecture_id    BIGINT      NOT NULL,
    status            VARCHAR(50),
    attended_at       TIMESTAMP,
    created_at        TIMESTAMP   NOT NULL,
    updated_at        TIMESTAMP   NOT NULL,
    PRIMARY KEY (sub_attendance_id),
    CONSTRAINT fk_sub_attendances_attendance FOREIGN KEY (attendance_id) REFERENCES attendances (attendance_id),
    CONSTRAINT fk_sub_attendances_sub_lecture FOREIGN KEY (sub_lecture_id) REFERENCES sub_lectures (sub_lecture_id)
);

CREATE TABLE sopt_story_like
(
    id           BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    sopt_story_id BIGINT     NOT NULL,
    ip           VARCHAR(45) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sopt_story_like_ip UNIQUE (sopt_story_id, ip),
    CONSTRAINT fk_sopt_story_like_story FOREIGN KEY (sopt_story_id) REFERENCES sopt_story (id)
);

CREATE TABLE user_activity_histories
(
    id               BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id          BIGINT      NOT NULL,
    generation       INT,
    team             VARCHAR(50),
    part             VARCHAR(50) NOT NULL,
    role             VARCHAR(50) NOT NULL,
    is_sopt          BOOLEAN     NOT NULL,
    attendance_score FLOAT,
    created_at       TIMESTAMP   NOT NULL,
    updated_at       TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_id_and_generation UNIQUE (user_id, generation, is_sopt),
    CONSTRAINT fk_user_activity_histories_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_careers
(
    id           BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id      BIGINT,
    company_name VARCHAR(255),
    title        VARCHAR(255),
    start_date   VARCHAR(50),
    end_date     VARCHAR(50),
    is_current   BOOLEAN,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_careers_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_links
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT,
    title      VARCHAR(255),
    url        VARCHAR(255),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_links_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_work_preferences
(
    id                  BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id             BIGINT      NOT NULL,
    ideation_style      VARCHAR(50),
    work_time           VARCHAR(50),
    communication_style VARCHAR(50),
    work_place          VARCHAR(50),
    feedback_style      VARCHAR(50),
    created_at          TIMESTAMP   NOT NULL,
    updated_at          TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_work_preferences_user FOREIGN KEY (user_id) REFERENCES users (id)
);
