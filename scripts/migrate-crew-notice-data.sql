\set ON_ERROR_STOP on

-- 실행 예시:
-- psql "$DATABASE_URL" \
--   -v source_schema=legacy_crew \
--   -v target_schema=integrated \
--   -f scripts/migrate-crew-notice-data.sql

\if :{?source_schema}
\else
  \echo 'source_schema 변수가 필요합니다.'
  \quit
\endif

\if :{?target_schema}
\else
  \echo 'target_schema 변수가 필요합니다.'
  \quit
\endif

BEGIN;

INSERT INTO :"target_schema".notice
    (id, title, sub_title, contents, created_date,
     expose_start_date, expose_end_date, created_at, updated_at)
OVERRIDING SYSTEM VALUE
SELECT
    id,
    title,
    "subTitle",
    contents,
    "createdDate",
    "exposeStartDate",
    "exposeEndDate",
    "createdTimestamp",
    "modifiedTimestamp"
FROM :"source_schema".notice
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    sub_title = EXCLUDED.sub_title,
    contents = EXCLUDED.contents,
    created_date = EXCLUDED.created_date,
    expose_start_date = EXCLUDED.expose_start_date,
    expose_end_date = EXCLUDED.expose_end_date,
    updated_at = EXCLUDED.updated_at;

SELECT setval(
    pg_get_serial_sequence(format('%I.notice', :'target_schema'), 'id'),
    COALESCE((SELECT MAX(id) FROM :"target_schema".notice), 1),
    EXISTS (SELECT 1 FROM :"target_schema".notice)
);

SELECT COUNT(*) AS notice_count FROM :"target_schema".notice;

COMMIT;
