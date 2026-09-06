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

CREATE TABLE activity_review
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT    NOT NULL,
    content    TEXT      NOT NULL,
    generation INT       NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

-- ============================================================
-- FK 참조 테이블
-- ============================================================

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

CREATE TABLE category
(
    category_id    BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    code           VARCHAR(20) NOT NULL,
    category_group VARCHAR(20) NOT NULL,
    name           VARCHAR(255),
    content        VARCHAR(500),
    has_all        BOOLEAN,
    has_blind      BOOLEAN,
    has_question   BOOLEAN,
    is_active      BOOLEAN     NOT NULL DEFAULT TRUE,
    parent         BIGINT,
    display_order  INT,
    PRIMARY KEY (category_id),
    CONSTRAINT uk_category_code UNIQUE (code),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent) REFERENCES category (category_id)
);

CREATE TABLE anonymous_nickname
(
    anonymous_nickname_id BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    nickname               VARCHAR(255) NOT NULL,
    PRIMARY KEY (anonymous_nickname_id)
);

CREATE TABLE anonymous_profile_image
(
    anonymous_profile_image_id BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    image_url                   VARCHAR(255) NOT NULL,
    PRIMARY KEY (anonymous_profile_image_id)
);

CREATE TABLE anonymous_profile
(
    anonymous_profile_id       BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id                    BIGINT    NOT NULL,
    post_id                    BIGINT    NOT NULL,
    anonymous_nickname_id      BIGINT    NOT NULL,
    anonymous_profile_image_id BIGINT    NOT NULL,
    created_at                 TIMESTAMP NOT NULL,
    updated_at                 TIMESTAMP NOT NULL,
    PRIMARY KEY (anonymous_profile_id),
    CONSTRAINT uk_anonymous_profile_user_post UNIQUE (user_id, post_id),
    CONSTRAINT fk_anonymous_profile_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_anonymous_profile_nickname FOREIGN KEY (anonymous_nickname_id) REFERENCES anonymous_nickname (anonymous_nickname_id),
    CONSTRAINT fk_anonymous_profile_image FOREIGN KEY (anonymous_profile_image_id) REFERENCES anonymous_profile_image (anonymous_profile_image_id)
);

CREATE TABLE community_post
(
    id                    BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    writer_id             BIGINT       NOT NULL,
    category_id           BIGINT       NOT NULL,
    title                 VARCHAR(255),
    content               TEXT         NOT NULL,
    hits                  INT          NOT NULL DEFAULT 0,
    images                TEXT[],
    is_question           BOOLEAN      NOT NULL,
    is_blind_writer       BOOLEAN      NOT NULL,
    is_reported           BOOLEAN      NOT NULL DEFAULT FALSE,
    is_hot                BOOLEAN      NOT NULL DEFAULT FALSE,
    sopticle_url          VARCHAR(500),
    anonymous_profile_id  BIGINT,
    created_at            TIMESTAMP    NOT NULL,
    updated_at            TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_community_post_writer FOREIGN KEY (writer_id) REFERENCES users (id),
    CONSTRAINT fk_community_post_category FOREIGN KEY (category_id) REFERENCES category (category_id),
    CONSTRAINT fk_community_post_anonymous_profile FOREIGN KEY (anonymous_profile_id) REFERENCES anonymous_profile (anonymous_profile_id)
);

-- anonymous_profile 이관 시점에는 community_post가 없어 FK 없이 post_id 컬럼만 유지했다. 이제 community_post가 생성되었으므로 FK를 완성한다.
ALTER TABLE anonymous_profile
    ADD CONSTRAINT fk_anonymous_profile_post FOREIGN KEY (post_id) REFERENCES community_post (id);

CREATE TABLE community_post_like
(
    community_post_like_id BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id                 BIGINT    NOT NULL,
    post_id                 BIGINT    NOT NULL,
    created_at               TIMESTAMP NOT NULL,
    updated_at               TIMESTAMP NOT NULL,
    PRIMARY KEY (community_post_like_id),
    CONSTRAINT fk_community_post_like_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_community_post_like_post FOREIGN KEY (post_id) REFERENCES community_post (id)
);

CREATE TABLE deleted_community_post
(
    id               BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    writer_id        BIGINT,
    category_id      BIGINT,
    title            VARCHAR(255),
    content          VARCHAR(10000),
    hits             INT,
    images           TEXT[],
    is_question      BOOLEAN,
    is_blind_writer  BOOLEAN,
    is_reported      BOOLEAN,
    deleted_at       TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_deleted_community_post_writer FOREIGN KEY (writer_id) REFERENCES users (id)
);

CREATE TABLE report_post
(
    id           BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    post_id      BIGINT    NOT NULL,
    reporter_id  BIGINT    NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_report_post_post FOREIGN KEY (post_id) REFERENCES community_post (id),
    CONSTRAINT fk_report_post_reporter FOREIGN KEY (reporter_id) REFERENCES users (id)
);

CREATE TABLE community_comment
(
    id                   BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    content              TEXT      NOT NULL,
    post_id              BIGINT    NOT NULL,
    writer_id            BIGINT    NOT NULL,
    parent_comment_id    BIGINT,
    is_blind_writer      BOOLEAN   NOT NULL,
    is_reported          BOOLEAN   NOT NULL DEFAULT FALSE,
    is_deleted           BOOLEAN   NOT NULL DEFAULT FALSE,
    anonymous_profile_id BIGINT,
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_community_comment_post FOREIGN KEY (post_id) REFERENCES community_post (id),
    CONSTRAINT fk_community_comment_writer FOREIGN KEY (writer_id) REFERENCES users (id),
    CONSTRAINT fk_community_comment_parent FOREIGN KEY (parent_comment_id) REFERENCES community_comment (id),
    CONSTRAINT fk_community_comment_anonymous_profile FOREIGN KEY (anonymous_profile_id) REFERENCES anonymous_profile (anonymous_profile_id)
);

CREATE TABLE community_comment_like
(
    community_comment_like_id BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    member_id                  BIGINT    NOT NULL,
    comment_id                  BIGINT    NOT NULL,
    created_at                  TIMESTAMP NOT NULL,
    updated_at                  TIMESTAMP NOT NULL,
    PRIMARY KEY (community_comment_like_id),
    CONSTRAINT uk_comment_like_member_comment UNIQUE (member_id, comment_id),
    CONSTRAINT fk_community_comment_like_member FOREIGN KEY (member_id) REFERENCES users (id),
    CONSTRAINT fk_community_comment_like_comment FOREIGN KEY (comment_id) REFERENCES community_comment (id)
);

CREATE TABLE deleted_community_comment
(
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    content           VARCHAR(10000),
    post_id           BIGINT,
    writer_id         BIGINT,
    parent_comment_id BIGINT,
    is_blind_writer   BOOLEAN,
    is_reported       BOOLEAN,
    deleted_at        TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_deleted_community_comment_writer FOREIGN KEY (writer_id) REFERENCES users (id)
);

CREATE TABLE report_comment
(
    id          BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    comment_id  BIGINT    NOT NULL,
    reporter_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_report_comment_comment FOREIGN KEY (comment_id) REFERENCES community_comment (id),
    CONSTRAINT fk_report_comment_reporter FOREIGN KEY (reporter_id) REFERENCES users (id)
);

CREATE TABLE vote
(
    id                  BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    post_id             BIGINT    NOT NULL,
    is_multiple_options BOOLEAN   NOT NULL,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_vote_post UNIQUE (post_id),
    CONSTRAINT fk_vote_post FOREIGN KEY (post_id) REFERENCES community_post (id)
);

CREATE TABLE vote_option
(
    id         BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    vote_id    BIGINT      NOT NULL,
    content    VARCHAR(40) NOT NULL,
    vote_count INT         NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_vote_option_vote FOREIGN KEY (vote_id) REFERENCES vote (id)
);

CREATE TABLE vote_selection
(
    id             BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id        BIGINT    NOT NULL,
    vote_option_id BIGINT    NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_vote_selection_user_option UNIQUE (user_id, vote_option_id),
    CONSTRAINT fk_vote_selection_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_vote_selection_option FOREIGN KEY (vote_option_id) REFERENCES vote_option (id)
);

-- 솝트 리포트: member/user 연관관계는 FK 없이 순수 BIGINT(userId)로 디커플링한다.
CREATE TABLE sopt_report_stats
(
    id           BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    template_key VARCHAR(255) NOT NULL,
    data         JSON         NOT NULL,
    category     VARCHAR(50),
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sopt_report_stats_template_key UNIQUE (template_key)
);

-- Amplitude 분석 파이프라인이 적재하는 원시 이벤트 테이블. 컬럼명은 외부 적재 규격을 그대로 따른다.
CREATE TABLE amplitude_event_raw_data
(
    "$insert_id"                             VARCHAR(255) NOT NULL,
    user_id                                  VARCHAR(255),
    event_type                               VARCHAR(255),
    event_time                               VARCHAR(255),
    "event_properties_[Amplitude] Page Path" VARCHAR(255),
    PRIMARY KEY ("$insert_id")
);

CREATE TABLE word_chain_gameroom
(
    id              BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    start_word      VARCHAR(255),
    created_at      TIMESTAMP NOT NULL,
    created_user_id BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE word
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT       NOT NULL,
    word       VARCHAR(255) NOT NULL,
    room_id    BIGINT       NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE word_chain_game_winner
(
    id      BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    score   INT,
    room_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- ============================================================
-- Member(Ask/Relation/TL) 도메인 마이그레이션 (Step M-1)
-- Playground 소유 테이블만 생성한다. domain-user 소유 테이블(users, member_career,
-- member_links 등)은 이미 위에서 생성되었으므로 여기서 재생성하지 않는다.
-- 내부 Java 모델명은 Ask 통일 규칙을 따르되(UserAsk 등), 물리 테이블/컬럼명은
-- 레거시 운영 스키마를 그대로 보존한다.
-- ============================================================

CREATE TABLE member_question
(
    question_id                BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    receiver_id                BIGINT    NOT NULL,
    asker_id                   BIGINT,
    content                    TEXT      NOT NULL,
    is_anonymous               BOOLEAN   NOT NULL,
    anonymous_nickname_id      BIGINT,
    anonymous_profile_image_id BIGINT,
    is_reported                BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at                 TIMESTAMP NOT NULL,
    updated_at                 TIMESTAMP NOT NULL,
    PRIMARY KEY (question_id),
    CONSTRAINT fk_member_question_receiver FOREIGN KEY (receiver_id) REFERENCES users (id),
    CONSTRAINT fk_member_question_asker FOREIGN KEY (asker_id) REFERENCES users (id),
    CONSTRAINT fk_member_question_anonymous_nickname FOREIGN KEY (anonymous_nickname_id) REFERENCES anonymous_nickname (anonymous_nickname_id),
    CONSTRAINT fk_member_question_anonymous_profile_image FOREIGN KEY (anonymous_profile_image_id) REFERENCES anonymous_profile_image (anonymous_profile_image_id)
);

CREATE TABLE member_answer
(
    answer_id   BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    question_id BIGINT    NOT NULL,
    content     TEXT      NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (answer_id),
    CONSTRAINT uk_member_answer_question UNIQUE (question_id),
    CONSTRAINT fk_member_answer_question FOREIGN KEY (question_id) REFERENCES member_question (question_id)
);

CREATE TABLE question_reaction
(
    reaction_id BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    question_id BIGINT    NOT NULL,
    member_id   BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (reaction_id),
    CONSTRAINT uk_question_reaction_question_member UNIQUE (question_id, member_id),
    CONSTRAINT fk_question_reaction_question FOREIGN KEY (question_id) REFERENCES member_question (question_id),
    CONSTRAINT fk_question_reaction_member FOREIGN KEY (member_id) REFERENCES users (id)
);

CREATE TABLE answer_reaction
(
    reaction_id BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    answer_id   BIGINT    NOT NULL,
    member_id   BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (reaction_id),
    CONSTRAINT uk_answer_reaction_answer_member UNIQUE (answer_id, member_id),
    CONSTRAINT fk_answer_reaction_answer FOREIGN KEY (answer_id) REFERENCES member_answer (answer_id),
    CONSTRAINT fk_answer_reaction_member FOREIGN KEY (member_id) REFERENCES users (id)
);

CREATE TABLE question_report
(
    report_id   BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    question_id BIGINT    NOT NULL,
    reporter_id BIGINT    NOT NULL,
    reason      VARCHAR(255),
    created_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (report_id),
    CONSTRAINT fk_question_report_question FOREIGN KEY (question_id) REFERENCES member_question (question_id),
    CONSTRAINT fk_question_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (id)
);

CREATE TABLE member_block
(
    id                BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    blocker_id        BIGINT    NOT NULL,
    blocked_member_id BIGINT    NOT NULL,
    is_blocked        BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_member_block_blocker FOREIGN KEY (blocker_id) REFERENCES users (id),
    CONSTRAINT fk_member_block_blocked_member FOREIGN KEY (blocked_member_id) REFERENCES users (id)
);

CREATE TABLE member_report
(
    id                 BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    reporter_id        BIGINT    NOT NULL,
    reported_member_id BIGINT    NOT NULL,
    reason             TEXT,
    created_at         TIMESTAMP NOT NULL,
    updated_at         TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_member_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT fk_member_report_reported_member FOREIGN KEY (reported_member_id) REFERENCES users (id)
);

CREATE TABLE appjam_tl_members
(
    id                BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    member_id         BIGINT        NOT NULL,
    tl_generation     INT           NOT NULL,
    service_type      VARCHAR(50)   NOT NULL,
    self_introduction VARCHAR(2048) NOT NULL,
    competition_data  VARCHAR(2048) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_appjam_tl_members_member FOREIGN KEY (member_id) REFERENCES users (id)
);

CREATE TABLE coffee_chat
(
    id                     BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    is_coffee_chat_activate BOOLEAN      NOT NULL DEFAULT TRUE,
    career                 VARCHAR(50)   NOT NULL,
    introduction           VARCHAR(200),
    section                VARCHAR(500)  NOT NULL,
    coffee_chat_bio        VARCHAR(40)   NOT NULL,
    coffee_chat_topic_type VARCHAR(500)  NOT NULL,
    topic                  VARCHAR(1000) NOT NULL,
    meeting_type           VARCHAR(50)   NOT NULL,
    guideline              VARCHAR(1000),
    member_id              BIGINT        NOT NULL,
    created_at             TIMESTAMP     NOT NULL,
    updated_at             TIMESTAMP     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_coffee_chat_member UNIQUE (member_id),
    CONSTRAINT fk_coffee_chat_member FOREIGN KEY (member_id) REFERENCES users (id)
);

CREATE TABLE coffee_chat_history
(
    id              BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    receiver_id     BIGINT    NOT NULL,
    sender_id       BIGINT    NOT NULL,
    request_content TEXT,
    created_at      TIMESTAMP NOT NULL,
    updated_at      TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_coffee_chat_history_receiver FOREIGN KEY (receiver_id) REFERENCES users (id),
    CONSTRAINT fk_coffee_chat_history_sender FOREIGN KEY (sender_id) REFERENCES users (id)
);

CREATE TABLE user_activity_check
(
    id                   BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id              BIGINT    NOT NULL,
    edit_activities_able BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMP NOT NULL,
    updated_at           TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_user_activity_check_user UNIQUE (user_id),
    CONSTRAINT fk_user_activity_check_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ============================================================
-- Crew 모임 게시판(무무) — domain-playground/post/*
-- 레거시 Playground 커뮤니티(community_post 등)와 물리적으로 분리된 별도 테이블. 두 기능을 하나의 테이블로
-- 병합하면 레거시 Playground 원본 컬럼(anonymous_profile_id, sopticle_url, is_question 등)이 유실되고
-- 커뮤니티 71개 API 계약이 깨지므로, 저장 모델은 분리하고 필요 시 Port 계층에서만 통합 창구를 둔다.
-- meeting_id는 domain-crew의 meeting 테이블을 참조해야 하지만, domain-crew 스키마 전체가 아직 어떤
-- 마이그레이션 파일에도 존재하지 않아(meeting/apply/property 등) FK를 걸지 않는다.
-- ============================================================

CREATE TABLE meeting_post
(
    id              BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    writer_id       BIGINT,
    category        VARCHAR(20)  NOT NULL,
    content_type    VARCHAR(20)  NOT NULL,
    meeting_id      BIGINT       NOT NULL,
    title           VARCHAR(255),
    content         TEXT         NOT NULL,
    images          TEXT[],
    hits            INT          NOT NULL DEFAULT 0,
    comment_count   INT          NOT NULL DEFAULT 0,
    like_count      INT          NOT NULL DEFAULT 0,
    is_question     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_blind_writer BOOLEAN      NOT NULL DEFAULT FALSE,
    is_reported     BOOLEAN      NOT NULL DEFAULT FALSE,
    is_hot          BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_meeting_post_writer FOREIGN KEY (writer_id) REFERENCES users (id)
);

CREATE TABLE meeting_post_comment
(
    id                BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    post_id           BIGINT    NOT NULL,
    writer_id         BIGINT,
    content           TEXT      NOT NULL,
    parent_comment_id BIGINT,
    depth             INT       NOT NULL DEFAULT 0,
    comment_order     INT       NOT NULL DEFAULT 0,
    like_count        INT       NOT NULL DEFAULT 0,
    is_deleted        BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_meeting_post_comment_post FOREIGN KEY (post_id) REFERENCES meeting_post (id),
    CONSTRAINT fk_meeting_post_comment_writer FOREIGN KEY (writer_id) REFERENCES users (id)
);

CREATE TABLE meeting_post_like
(
    meeting_post_like_id BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    post_id              BIGINT    NOT NULL,
    user_id              BIGINT    NOT NULL,
    created_at            TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP NOT NULL,
    PRIMARY KEY (meeting_post_like_id),
    CONSTRAINT fk_meeting_post_like_post FOREIGN KEY (post_id) REFERENCES meeting_post (id),
    CONSTRAINT fk_meeting_post_like_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_meeting_post_like_post_user UNIQUE (post_id, user_id)
);

CREATE TABLE meeting_post_comment_like
(
    meeting_post_comment_like_id BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    comment_id                   BIGINT    NOT NULL,
    user_id                      BIGINT    NOT NULL,
    created_at                   TIMESTAMP NOT NULL,
    updated_at                   TIMESTAMP NOT NULL,
    PRIMARY KEY (meeting_post_comment_like_id),
    CONSTRAINT fk_meeting_post_comment_like_comment FOREIGN KEY (comment_id) REFERENCES meeting_post_comment (id),
    CONSTRAINT fk_meeting_post_comment_like_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_meeting_post_comment_like_comment_user UNIQUE (comment_id, user_id)
);

CREATE TABLE meeting_post_report
(
    id          BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    post_id     BIGINT    NOT NULL,
    reporter_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_meeting_post_report_post FOREIGN KEY (post_id) REFERENCES meeting_post (id),
    CONSTRAINT fk_meeting_post_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT uk_meeting_post_report_post_reporter UNIQUE (post_id, reporter_id)
);

CREATE TABLE meeting_post_comment_report
(
    id          BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    comment_id  BIGINT    NOT NULL,
    reporter_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_meeting_post_comment_report_comment FOREIGN KEY (comment_id) REFERENCES meeting_post_comment (id),
    CONSTRAINT fk_meeting_post_comment_report_reporter FOREIGN KEY (reporter_id) REFERENCES users (id),
    CONSTRAINT uk_meeting_post_comment_report_comment_reporter UNIQUE (comment_id, reporter_id)
);
