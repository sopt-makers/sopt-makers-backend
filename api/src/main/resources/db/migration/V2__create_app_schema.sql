-- ============================================================
-- 앱(app) 도메인 스키마
--
-- 레거시 app 서버(app_prod 스키마) 기준으로 작성하되, 이미 이관되어 코드로
-- 존재하는 엔티티의 테이블/컬럼명을 우선 따른다. 아래 두 테이블은 엔티티가
-- 레거시와 다른 이름을 쓰고 있어 그대로 반영했다.
--   - notifications        -> app_notifications (엔티티 AppNotificationEntity 기준.
--                             기존 official 도메인의 notification 테이블과 이름이 겹치는 것을 피함)
--   - operation_configs.key/value/operation_config_type/operation_config_category
--                          -> config_key/config_value/type/category (엔티티 OperationConfigEntity 기준)
--
-- calendar, app_service는 PR #88(feat/#87, 홈·유저메인뷰·캘린더 이관) 기준으로 추가했다.
-- main_description, icons는 PR #88에서 테이블 없이 코드로 계산해 내려주는 값으로 바뀌어서
-- (CalendarEntity/AppServiceEntity 참고, main_description은 홈 설명 문구를 서버에서 조합) 제외했다.
--
-- 아직 코드로 이관되지 않아 이번에는 제외한 레거시 테이블:
--   - app_users   : 통합 레포의 users 테이블로 대체됨
-- ============================================================

-- ------------------------------------------------------------
-- 솝탬프 (mission, stamp, soptamp_user, clap, appjam_user)
-- ------------------------------------------------------------

CREATE TABLE mission
(
    mission_id    BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    title         VARCHAR(255) NOT NULL,
    level         INT          NOT NULL,
    display       BOOLEAN      NOT NULL,
    profile_image TEXT[],
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    PRIMARY KEY (mission_id)
);

CREATE TABLE stamp
(
    id            BIGINT            NOT NULL GENERATED ALWAYS AS IDENTITY,
    contents      TEXT              NOT NULL,
    images        TEXT[],
    user_id       BIGINT            NOT NULL,
    mission_id    BIGINT            NOT NULL,
    activity_date VARCHAR(10),
    clap_count    INT               NOT NULL DEFAULT 0,
    view_count    INT               NOT NULL DEFAULT 0,
    version       BIGINT            NOT NULL DEFAULT 0,
    created_at    TIMESTAMP         NOT NULL,
    updated_at    TIMESTAMP         NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_stamp_user_id_mission_id ON stamp (user_id, mission_id);
CREATE INDEX idx_stamp_mission_id ON stamp (mission_id);

CREATE TABLE soptamp_user
(
    id              BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id         BIGINT       NOT NULL,
    nickname        VARCHAR(255),
    total_points    BIGINT,
    profile_message TEXT,
    generation      BIGINT,
    part            VARCHAR(255),
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_soptamp_user_user_id UNIQUE (user_id),
    CONSTRAINT uk_soptamp_user_nickname UNIQUE (nickname)
);

CREATE INDEX idx_soptamp_user_generation ON soptamp_user (generation);

CREATE TABLE clap
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    stamp_id   BIGINT    NOT NULL,
    user_id    BIGINT    NOT NULL,
    clap_count INT       NOT NULL DEFAULT 0,
    version    BIGINT    NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_clap_stamp_id_user_id UNIQUE (stamp_id, user_id),
    CONSTRAINT ck_clap_count_range CHECK (clap_count >= 0 AND clap_count <= 50)
);

CREATE INDEX idx_clap_stamp ON clap (stamp_id);
CREATE INDEX idx_clap_rank ON clap (stamp_id, clap_count DESC, updated_at DESC);

CREATE TABLE clap_milestone_hit
(
    stamp_id   BIGINT      NOT NULL,
    milestone  INT         NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    PRIMARY KEY (stamp_id, milestone)
);

CREATE TABLE appjam_user
(
    id          BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id     BIGINT       NOT NULL,
    team_name   VARCHAR(255) NOT NULL,
    team_number VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_appjam_user_team_number CHECK (team_number IN
        ('FIRST', 'SECOND', 'THIRD', 'FOURTH', 'FIFTH', 'SIXTH', 'SEVENTH', 'EIGHTH', 'NINTH', 'TENTH', 'ELEVENTH', 'TWELFTH'))
);

-- ------------------------------------------------------------
-- 콕찌르기 (poke_message, poke_history, friend)
-- ------------------------------------------------------------

CREATE TABLE poke_message
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    content    TEXT         NOT NULL,
    type       VARCHAR(30)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE poke_history
(
    id           BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    poker_id     BIGINT    NOT NULL,
    poked_id     BIGINT    NOT NULL,
    message      TEXT,
    is_reply     BOOLEAN   NOT NULL DEFAULT FALSE,
    is_anonymous BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE friend
(
    id             BIGINT      NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id        BIGINT      NOT NULL,
    friend_user_id BIGINT      NOT NULL,
    poke_count     INT         NOT NULL DEFAULT 1,
    anonymous_name VARCHAR(30),
    created_at     TIMESTAMP   NOT NULL,
    updated_at     TIMESTAMP   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_friend_user_friend UNIQUE (user_id, friend_user_id)
);

-- ------------------------------------------------------------
-- 운세 (fortune_card, fortune_word, user_fortune)
-- ------------------------------------------------------------

CREATE TABLE fortune_card
(
    id                BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    name              VARCHAR(255) NOT NULL,
    description       VARCHAR(255) NOT NULL,
    image_url         TEXT         NOT NULL,
    image_color_code  VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE fortune_word
(
    id              BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    title            VARCHAR(255) NOT NULL,
    fortune_card_id BIGINT       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE user_fortune
(
    id              BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id         BIGINT NOT NULL,
    fortune_word_id BIGINT NOT NULL,
    checked_at      DATE   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_user_fortune_user_id UNIQUE (user_id)
);

-- ------------------------------------------------------------
-- 인앱 알림 / 푸시 토큰
-- ------------------------------------------------------------

CREATE TABLE app_notifications
(
    id                     BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id                BIGINT       NOT NULL,
    notification_id        VARCHAR(255) NOT NULL,
    notification_title     TEXT         NOT NULL,
    notification_content   TEXT,
    notification_type      VARCHAR(20)  NOT NULL,
    notification_category  VARCHAR(20)  NOT NULL,
    deep_link              VARCHAR(255),
    web_link               VARCHAR(255),
    is_read                BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at             TIMESTAMP    NOT NULL,
    updated_at             TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_app_notifications_user_id_created_at ON app_notifications (user_id, created_at);
CREATE INDEX idx_app_notifications_user_id_category_created_at ON app_notifications (user_id, notification_category, created_at);
CREATE INDEX idx_app_notifications_user_id_notification_id ON app_notifications (user_id, notification_id);

CREATE TABLE push_token
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT       NOT NULL,
    token      TEXT         NOT NULL,
    platform   VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_push_token_user_id ON push_token (user_id);

-- ------------------------------------------------------------
-- 운영 설정 (operation_configs)
-- ------------------------------------------------------------

CREATE TABLE operation_configs
(
    id          BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    config_key   VARCHAR(255) NOT NULL,
    config_value VARCHAR(500) NOT NULL,
    type        VARCHAR(20)  NOT NULL,
    category    VARCHAR(30)  NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_operation_configs_category_key UNIQUE (category, config_key)
);

-- ------------------------------------------------------------
-- 솝레터 (sopt_letter_topic, sopt_letter_profile, sopt_letter, sopt_letter_like)
-- ------------------------------------------------------------

CREATE TABLE sopt_letter_topic
(
    id         BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    title      VARCHAR(255) NOT NULL,
    cta_text   VARCHAR(50),
    is_default BOOLEAN      NOT NULL DEFAULT FALSE,
    started_at TIMESTAMP    NOT NULL,
    ended_at   TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE sopt_letter_profile
(
    id           BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id      BIGINT       NOT NULL,
    nickname     VARCHAR(255) NOT NULL,
    is_onboarded BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sopt_letter_profile_user UNIQUE (user_id),
    CONSTRAINT uk_sopt_letter_profile_nickname UNIQUE (nickname)
);

CREATE TABLE sopt_letter
(
    id                BIGINT           NOT NULL GENERATED ALWAYS AS IDENTITY,
    author_profile_id BIGINT           NOT NULL,
    topic_id          BIGINT           NOT NULL,
    degree            DOUBLE PRECISION NOT NULL,
    message           TEXT             NOT NULL,
    color             VARCHAR(20),
    shape_type        VARCHAR(20),
    like_count        INT              NOT NULL DEFAULT 0,
    created_at        TIMESTAMP        NOT NULL,
    updated_at        TIMESTAMP        NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_sopt_letter_topic_id_id ON sopt_letter (topic_id, id);
CREATE INDEX idx_sopt_letter_author_profile_id_created_at ON sopt_letter (author_profile_id, created_at);

CREATE TABLE sopt_letter_like
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT    NOT NULL,
    letter_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_sopt_letter_like_sopt_letter_user UNIQUE (letter_id, user_id)
);

CREATE INDEX idx_sopt_letter_like_user_id_letter_id ON sopt_letter_like (user_id, letter_id);

-- ------------------------------------------------------------
-- 홈 / 캘린더 (app_service, calendar)
-- ------------------------------------------------------------

CREATE TABLE app_service
(
    id            BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    service_name  VARCHAR(255) NOT NULL,
    active_user   BOOLEAN      NOT NULL,
    inactive_user BOOLEAN      NOT NULL,
    icon_url      TEXT,
    deep_link     TEXT,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE calendar
(
    id                        BIGINT       NOT NULL GENERATED ALWAYS AS IDENTITY,
    generation                INT          NOT NULL,
    title                     VARCHAR(255) NOT NULL,
    is_one_day_schedule       BOOLEAN      NOT NULL,
    is_only_active_generation BOOLEAN      NOT NULL,
    start_date                DATE         NOT NULL,
    end_date                  DATE         NOT NULL,
    type                      VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_calendar_type CHECK (type IN ('EVENT', 'SEMINAR', 'ETC', 'JOINT_SEMINAR', 'BREAK'))
);
