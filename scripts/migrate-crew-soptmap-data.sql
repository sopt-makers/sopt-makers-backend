\set ON_ERROR_STOP on

-- 실행 예시:
-- psql "$DATABASE_URL" \
--   -v source_schema=legacy_crew \
--   -v target_schema=integrated \
--   -f scripts/migrate-crew-soptmap-data.sql
--
-- 선물 URL을 포함한 운영 데이터를 파일에 직접 기록하지 않고 동일 DB의 스키마 간에 복사한다.

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

SELECT EXISTS (
    SELECT 1
    FROM :"source_schema".sopt_map source_map
    LEFT JOIN :"target_schema".users target_user ON target_user.id = source_map.creator_id
    WHERE target_user.id IS NULL
) AS has_missing_creator \gset

\if :has_missing_creator
  \echo '통합 users에 존재하지 않는 솝맵 creator_id가 있습니다.'
  ROLLBACK;
  \quit
\endif

SELECT EXISTS (
    SELECT 1
    FROM :"source_schema".map_recommended source_recommend
    LEFT JOIN :"target_schema".users target_user ON target_user.id = source_recommend.user_id
    WHERE target_user.id IS NULL
) AS has_missing_recommend_user \gset

\if :has_missing_recommend_user
  \echo '통합 users에 존재하지 않는 솝맵 추천 user_id가 있습니다.'
  ROLLBACK;
  \quit
\endif

INSERT INTO :"target_schema".subway_station
    (id, name, lines, created_at, updated_at)
OVERRIDING SYSTEM VALUE
SELECT
    id,
    name,
    lines::text,
    COALESCE("createdTimestamp", CURRENT_TIMESTAMP),
    COALESCE("modifiedTimestamp", "createdTimestamp", CURRENT_TIMESTAMP)
FROM :"source_schema".subway_station
ON CONFLICT (id) DO UPDATE SET
    name = EXCLUDED.name,
    lines = EXCLUDED.lines,
    updated_at = EXCLUDED.updated_at;

INSERT INTO :"target_schema".property
    (id, property_key, properties, created_at, updated_at)
OVERRIDING SYSTEM VALUE
SELECT
    id,
    key,
    COALESCE(properties, '{}'::jsonb)::text,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM :"source_schema".property
WHERE key = 'soptMapEvent'
ON CONFLICT (property_key) DO UPDATE SET
    properties = EXCLUDED.properties,
    updated_at = CURRENT_TIMESTAMP;

INSERT INTO :"target_schema".sopt_map
    (id, nearby_station_ids, place_name, description, map_tags,
     naver_link, kakao_link, creator_id, created_at, updated_at)
OVERRIDING SYSTEM VALUE
SELECT
    id,
    nearby_station_ids::text,
    place_name,
    description,
    map_tags::text,
    naver_link,
    kakao_link,
    creator_id,
    COALESCE("createdTimestamp", CURRENT_TIMESTAMP),
    COALESCE("modifiedTimestamp", "createdTimestamp", CURRENT_TIMESTAMP)
FROM :"source_schema".sopt_map
ON CONFLICT (id) DO UPDATE SET
    nearby_station_ids = EXCLUDED.nearby_station_ids,
    place_name = EXCLUDED.place_name,
    description = EXCLUDED.description,
    map_tags = EXCLUDED.map_tags,
    naver_link = EXCLUDED.naver_link,
    kakao_link = EXCLUDED.kakao_link,
    creator_id = EXCLUDED.creator_id,
    updated_at = EXCLUDED.updated_at;

INSERT INTO :"target_schema".map_recommended
    (id, user_id, sopt_map_id, active, created_at, updated_at)
OVERRIDING SYSTEM VALUE
SELECT
    id,
    user_id,
    sopt_map_id,
    active,
    COALESCE("createdTimestamp", CURRENT_TIMESTAMP),
    COALESCE("modifiedTimestamp", "createdTimestamp", CURRENT_TIMESTAMP)
FROM :"source_schema".map_recommended
ON CONFLICT (user_id, sopt_map_id) DO UPDATE SET
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

INSERT INTO :"target_schema".event_gift
    (id, user_id, map_id, gift_url, claimable, active, created_at, updated_at)
OVERRIDING SYSTEM VALUE
SELECT
    id,
    user_id,
    map_id,
    gift_url,
    claimable,
    active,
    COALESCE("createdTimestamp", CURRENT_TIMESTAMP),
    COALESCE("modifiedTimestamp", "createdTimestamp", CURRENT_TIMESTAMP)
FROM :"source_schema".event_gift
ON CONFLICT (id) DO UPDATE SET
    user_id = EXCLUDED.user_id,
    map_id = EXCLUDED.map_id,
    gift_url = EXCLUDED.gift_url,
    claimable = EXCLUDED.claimable,
    active = EXCLUDED.active,
    updated_at = EXCLUDED.updated_at;

SELECT setval(
    pg_get_serial_sequence(format('%I.subway_station', :'target_schema'), 'id'),
    COALESCE((SELECT MAX(id) FROM :"target_schema".subway_station), 1),
    EXISTS (SELECT 1 FROM :"target_schema".subway_station)
);
SELECT setval(
    pg_get_serial_sequence(format('%I.property', :'target_schema'), 'id'),
    COALESCE((SELECT MAX(id) FROM :"target_schema".property), 1),
    EXISTS (SELECT 1 FROM :"target_schema".property)
);
SELECT setval(
    pg_get_serial_sequence(format('%I.sopt_map', :'target_schema'), 'id'),
    COALESCE((SELECT MAX(id) FROM :"target_schema".sopt_map), 1),
    EXISTS (SELECT 1 FROM :"target_schema".sopt_map)
);
SELECT setval(
    pg_get_serial_sequence(format('%I.map_recommended', :'target_schema'), 'id'),
    COALESCE((SELECT MAX(id) FROM :"target_schema".map_recommended), 1),
    EXISTS (SELECT 1 FROM :"target_schema".map_recommended)
);
SELECT setval(
    pg_get_serial_sequence(format('%I.event_gift', :'target_schema'), 'id'),
    COALESCE((SELECT MAX(id) FROM :"target_schema".event_gift), 1),
    EXISTS (SELECT 1 FROM :"target_schema".event_gift)
);

SELECT 'subway_station' AS table_name, COUNT(*) AS row_count
FROM :"target_schema".subway_station
UNION ALL
SELECT 'sopt_map', COUNT(*) FROM :"target_schema".sopt_map
UNION ALL
SELECT 'map_recommended', COUNT(*) FROM :"target_schema".map_recommended
UNION ALL
SELECT 'event_gift', COUNT(*) FROM :"target_schema".event_gift
UNION ALL
SELECT 'property:soptMapEvent', COUNT(*)
FROM :"target_schema".property
WHERE property_key = 'soptMapEvent';

COMMIT;
