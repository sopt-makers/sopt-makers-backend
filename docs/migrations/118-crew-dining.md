# 회식 1차 스프린트 이관 (#118)

## 원본과 범위

- 이슈: https://github.com/sopt-makers/sopt-makers-backend/issues/118
- 원본 기준: `sopt-makers/sopt-crew-backend` main, `ba97d7a81268e16213dbbbb85a18f9bf4fde80d1`
- 배포 PR: #958(v2.12.0), #965(v2.12.1). 후속 수정인 참석 현황의 등록순 정렬과 회식 개설 알림 제거를 포함한다.
- 회식·회차 CRUD, 종료·마감, 참석·취소·참석자 관리, CSV, 공동 결제자 요청·승인·거절·해제, 자동 마감, 리마인드, 삭제 스냅샷을 이관한다.
- 기존 데이터 행을 원본 DB에서 복사하는 작업은 포함하지 않는다.

## 모듈과 호환성

| 책임 | 위치 |
|---|---|
| HTTP 매핑, 요청 검증, 응답 변환, Swagger | `api/controller/crew/dining` |
| 정책, 모델, 유스케이스, 이벤트, 스케줄러, Port | `domain-crew/dining` |
| JPA 엔티티, Repository, 잠금·검색·사용자 조회 Adapter | `storage/db/crew` |
| 알림 전송과 CSV 생성·S3 업로드 | `clients/notification`, `clients/s3` |

- `/dining/v2`의 24개 HTTP 계약과 응답 상태를 유지한다. 회식 API는 원본처럼 응답 데이터를 최상위에 반환한다.
- ID는 통합 서버의 `Long/BIGINT`, 컬럼은 `snake_case`, 사용자 FK는 `users.id`를 사용한다.
- 회식은 `meeting.category=DINING`이며 개설자는 `meeting_member`의 `LEADER`다. 조회 결과는 도메인 모델로 변환한다.
- 일반 모임 생성·수정 API는 DINING을 거부한다. 일반 모임 상세·목록·내 모임·배너 제목 조회에서 회식을 제외한다.
- 도메인에 JPA/스토리지/외부 클라이언트 의존성을 추가하지 않는다. 같은 crew 도메인 내 유스케이스이므로 단순 위임 Facade를 추가하지 않는다.
- 변경 요청은 회식 부모 → 회차 → 참석 기록 순서로 잠근다. 자동 마감도 부모를 먼저 잠근다. 참석 중복은 DB UNIQUE로도 보호한다.
- 분리된 도메인 모델의 변경은 명시적으로 저장하며, 수정 시 기존 생성 시각을 보존한다.
- 삭제 스냅샷은 JSONB이며 원본 FK를 두지 않는다. 참석/회차 FK는 삭제 시 CASCADE를 유지한다.
- 알림은 트랜잭션 커밋 후 비동기로 전송한다. 회식 개설 알림은 보내지 않는다.

## develop 머지 전 기존 DB 반영 순서

대상은 **통합 서버 DB의 실제 운영 스키마**다. 원본 Crew DB에 실행하지 않는다. 개발/운영 스키마가 분리돼 있다면 각각 해당 서버 배포 전에 적용한다. 아래 작업은 이관 작업 중 실제 원격 DB에 실행하지 않았다.

1. 연결 대상과 스키마를 확인한다.
2. `scripts/sql/118_dining_premerge.sql`을 실행한다. 트랜잭션으로 적용하며 재실행 가능하다.

```sh
psql "$DB_URL_FOR_PSQL" \
  -v ON_ERROR_STOP=1 \
  -v target_schema=실제_통합_스키마 \
  -f scripts/sql/118_dining_premerge.sql
```

`DB_URL_FOR_PSQL`은 psql용 연결 문자열이다. JDBC URL의 `jdbc:` 접두사를 그대로 넘기지 않는다.

추가 대상:

- `meeting.dining_ended_at` (TIMESTAMP)
- `meeting.is_date_modified` (BOOLEAN NOT NULL DEFAULT FALSE)
- `meeting.is_joinable_parts_modified` (BOOLEAN NOT NULL DEFAULT FALSE)
- `dining_round`, `dining_attendance`, `dining_deletion_history`
- 회식/회차/사용자 FK, 회차별 사용자 UNIQUE, 검색·정렬 인덱스

3. **V1이 이미 실행된 DB는 Flyway 체크섬을 동기화한다.** 프로젝트 방침에 따라 새 버전 파일 대신 V1에 누적했으므로 DDL만 실행하면 기존 V1 체크섬 불일치가 남는다. 해당 환경과 동일한 DB·스키마·전체 마이그레이션 경로를 지정해 새 코드 기준으로 `repair`를 실행한 뒤 `validate`한다.

```sh
# FLYWAY_URL / FLYWAY_USER / FLYWAY_PASSWORD는 해당 환경의 연결 설정을 사용한다.
# FLYWAY_URL은 JDBC URL이며, 애플리케이션과 동일한 currentSchema도 유지한다.
flyway -schemas="$DB_SCHEMA" -defaultSchema="$DB_SCHEMA" \
  -locations=filesystem:api/src/main/resources/db/migration repair
flyway -schemas="$DB_SCHEMA" -defaultSchema="$DB_SCHEMA" \
  -locations=filesystem:api/src/main/resources/db/migration validate
```

`repair`는 스키마 DDL을 적용하지 않으므로 반드시 선반영 SQL 이후에 수행한다. V1 기록이 없는 새 DB에는 선반영 SQL이나 repair를 실행하지 않고 Flyway가 V1부터 실행하게 둔다. 체크섬 검증을 비활성화하거나 이력 테이블을 삭제하지 않는다.

4. 설정을 확인한 뒤 develop 머지/배포한다.

- 원본 서버에서 사용하던 `DINING_IMAGE` 값을 통합 서버에도 설정한다. `crew.dining.default-image`로도 주입할 수 있다. 미설정 시 이미지 없는 회식 생성은 명시적인 설정 오류로 종료한다.
- 기존 S3/알림 설정을 재사용한다. CSV의 개인정보 열은 개설자 또는 해당 회차의 승인된 결제자만 요청할 수 있다.
- 스케줄러는 `local`, `dev`, `prod`, `traffic` 프로필에서 동작하며 시각은 Asia/Seoul이다. 전날 16:00 리마인드, 당일 23:00 마감 안내, 다음날 11:59 자동 마감이다.
- 원본과 동일하게 알림 스케줄러는 다중 인스턴스의 중복 발송 선점 기능이 없다. 운영 인스턴스를 늘리면 별도 선점 처리가 필요하다.

## 검증 방법

```sh
./gradlew build
```

회식 통합 테스트는 기본 H2 환경에서도 실행된다. PostgreSQL의 실제 FK·JSONB·잠금·Flyway·Hibernate validate까지 검증하려면 **전용 테스트 DB**를 지정한다. `dining_test` 스키마를 생성·변경하며 테스트용 사용자/회식 데이터가 입력되므로 운영 DB를 지정하지 않는다.

```sh
DINING_TEST_DB_URL='jdbc:postgresql://127.0.0.1:55438/postgres?currentSchema=dining_test' \
DINING_TEST_DB_USER='로컬_테스트_사용자' \
./gradlew :api:cleanTest :api:test --tests '*DiningMigrationIntegrationTest'
```

PostgreSQL 조건부 테스트에는 동시 참석의 단일 등록, 회식 삭제 중 참석 요청 차단과 이력 보존, 커밋 후 알림·롤백 시 미발송 검증이 포함된다. 새 V1 전체 적용, 기존 V1에 선반영 SQL 적용, 같은 SQL의 재실행도 별도 로컬 PostgreSQL 스키마에서 확인했다.

V1의 기존 `IDEN  TITY` 오타는 빈 DB 생성 검증 중 발견해 `IDENTITY`로 함께 수정했다.
