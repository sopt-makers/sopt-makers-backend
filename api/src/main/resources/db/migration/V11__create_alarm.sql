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
