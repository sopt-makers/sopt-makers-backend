CREATE TABLE member
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
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
    id                       BIGINT      NOT NULL AUTO_INCREMENT,
    generation_id            INT         NOT NULL,
    recruit_type             VARCHAR(10) NOT NULL,
    application_start_time   VARCHAR(50) NOT NULL,
    application_end_time     VARCHAR(50) NOT NULL,
    application_result_time  VARCHAR(50) NOT NULL,
    interview_start_time     VARCHAR(50) NOT NULL,
    interview_end_time       VARCHAR(50) NOT NULL,
    final_result_time        VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE activity_schedule
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    generation_id INT          NOT NULL,
    name          VARCHAR(100) NOT NULL,
    start_date    DATE         NOT NULL,
    end_date      DATE,
    PRIMARY KEY (id)
);
