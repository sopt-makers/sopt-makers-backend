---

name: implement
description: sopt-makers Spring Boot 멀티모듈 프로젝트에서 새로운 기능을 구현할 때 어느 모듈에 코드를 둬야 하는지 결정하고, Controller → Service/Facade → Port → Adapter 콜체인을 설계할 때 사용합니다. "새 기능", "구현", "어디에 둬야", "모듈", "Controller", "Service", "Facade", "Port", "Adapter", "API 만들기" 요청 시 트리거.
tools: Read, Grep, Glob, Bash
-----------------------------

# 신규 기능 구현 모듈 설계 가이드

이 스킬은 `sopt-makers-backend` 멀티모듈 프로젝트에서 새로운 기능을 구현할 때 코드 위치, 의존성 방향, 콜체인을 설계하기 위해 사용합니다.

## 사용 상황

다음 상황에서 반드시 이 스킬을 사용합니다.

1. 새로운 기능을 구현할 때
2. 특정 코드를 어느 모듈에 둬야 하는지 판단할 때
3. 신규 API의 Controller → Service/Facade → Port → Adapter 흐름을 설계할 때
4. Repository Port 또는 외부 기능 Port가 필요한지 판단할 때
5. 단일 도메인 기능인지, 다중 도메인 조율 기능인지 판단할 때

---

## Step 1. 도메인 식별

기능이 속하는 비즈니스 도메인을 먼저 파악합니다.

| 도메인          | 예시                           |
| ------------ | ---------------------------- |
| `user`       | 유저 프로필, 소셜 계정, 활동 이력         |
| `auth`       | 소셜 로그인, 토큰 발급, 전화번호 인증       |
| `playground` | 게시글, 댓글, 좋아요, 크루 생성/신청/승인/거절 |
| `app`        | 앱 버전, 배너, 팝업, 공지             |
| `admin`      | 어드민 작업, 출석, 백오피스             |

도메인이 애매하면 다음 기준으로 판단합니다.

1. 데이터의 생명주기를 소유하는 도메인을 우선합니다.
2. 상태 변경의 주체가 되는 도메인을 우선합니다.
3. 단순 조회가 필요한 다른 도메인은 조율 대상으로 봅니다.
4. 여러 도메인이 함께 필요하면 Facade 사용을 검토합니다.

---

## Step 2. 코드 유형별 배치 규칙

| 코드 유형                       | 배치 위치                                      |
| --------------------------- | ------------------------------------------ |
| REST Controller             | `api/controller/<domain>/`                 |
| HTTP Request/Response DTO   | `api/controller/<domain>/dto/`             |
| 보안 설정, 필터                   | `api/common/security/`                     |
| `@CurrentUserId` 등 Resolver | `api/common/resolver/`                     |
| 글로벌 예외 처리                   | `api/common/exception/`                    |
| 비즈니스 로직, 유스케이스              | `domain-<name>/service/`                   |
| 도메인 모델, VO                  | `domain-<name>/` 또는 `domain-<name>/model/` |
| 도메인 예외                      | `domain-<name>/exception/`                 |
| Repository Port 인터페이스       | `domain-<name>/port/`                      |
| 외부 기능 Port 인터페이스            | `domain-<name>/port/`                      |
| Facade                      | `domain-<name>/facade/`                    |
| JPA Entity                  | `storage/db/<domain>/entity/`              |
| Spring Data JPA Repository  | `storage/db/<domain>/repository/`          |
| QueryDSL 구현체                | `storage/db/<domain>/querydsl/`            |
| Persistence Adapter         | `storage/db/<domain>/adapter/`             |
| Redis Cache                 | `storage/redis/<domain>/cache/`            |
| Redis Adapter               | `storage/redis/<domain>/adapter/`          |
| SMS 구현체                     | `clients/sms/`                             |
| S3 구현체                      | `clients/s3/`                              |
| EventBridge 구현체             | `clients/eventbridge/`                     |
| 공통 enum, 에러 코드, 응답, 유틸      | `core/`                                    |

---

## Step 3. 콜체인 설계

### 단일 도메인 기능

단일 도메인 안에서 끝나는 기능은 Facade를 만들지 않습니다.

```text
HTTP Request
  └→ api/controller/<domain>/XxxController
       └→ domain-<name>/service/XxxService
            └→ domain-<name>/port/XxxRepositoryPort
                 └→ storage/db/<domain>/adapter/XxxRepositoryAdapter
```

### 다중 도메인 기능

여러 도메인을 조율해야 하는 경우 Facade를 사용합니다.

```text
HTTP Request
  └→ api/controller/<domain>/XxxController
       └→ domain-<name>/facade/XxxFacade
            ├→ domain-<name>/service/XxxService
            ├→ domain-user/service/UserQueryService
            └→ domain-<name>/port/XxxRepositoryPort
                 └→ storage/db/<domain>/adapter/XxxRepositoryAdapter
```

### 외부 시스템 연동 기능

SMS, S3, EventBridge, OAuth 등 외부 시스템 연동이 필요하면 domain에 Port를 두고 `clients`에서 구현합니다.

```text
domain-<name>/service/XxxService
  └→ domain-<name>/port/XxxCapabilityPort
       └→ clients/<provider>/XxxAdapter
```

---

## Step 4. Facade 필요 여부 판단

Facade는 다음 경우에만 만듭니다.

* 여러 도메인 서비스를 조율해야 하는 경우
* 하나의 API에서 서로 다른 도메인의 유스케이스가 함께 실행되는 경우
* 도메인 간 직접 의존을 피하기 위한 조율 계층이 필요한 경우
* 사용자 조회 후 다른 도메인 기능을 실행하는 복합 흐름인 경우

Facade를 만들지 않는 경우:

* 단일 Service만 호출하면 되는 경우
* 단순히 Service를 감싸기만 하는 경우
* Controller → Service 직접 호출로 충분한 경우

---

## Step 5. Port 필요 여부 판단

다음 의존성이 domain service에 필요하면 반드시 Port를 만듭니다.

* DB 조회/저장
* Redis 접근
* QueryDSL 조회
* SMS 발송
* S3 업로드/삭제
* EventBridge 이벤트 발행
* OAuth Provider 호출
* 외부 API 호출
* 테스트에서 대체하고 싶은 시간, UUID, 파일 시스템 등 외부 요인

Repository Port 예시:

```java
public interface XxxRepositoryPort {
    Optional<Xxx> findById(Long id);

    Xxx save(Xxx xxx);
}
```

외부 기능 Port 예시:

```java
public interface SmsSenderPort {
    void send(String phoneNumber, String message);
}
```

---

## Step 6. 의존성 규칙

허용되는 의존 방향:

```text
api          → domain-*, storage, clients, core   ✅
domain-*     → core                               ✅
domain-auth/playground/app/admin → domain-user    ✅
storage      → domain-* (Port 구현용), core        ✅
clients      → domain-* (Port 구현용), core        ✅
core         → 아무것도 의존하지 않음              ✅
```

금지되는 의존:

```text
domain-* → storage             ❌
domain-* → clients             ❌
domain-* → JPA Entity          ❌
domain-* → JpaRepository       ❌
domain-* → 다른 domain-* 직접 의존 ❌
core     → 다른 모듈             ❌
```

예외:

* `domain-auth`, `domain-playground`, `domain-app`, `domain-admin`에서 `domain-user`의 조회 서비스 또는 조회 Port를 사용하는 것은 허용합니다.
* 다른 도메인에서 사용자 정보가 필요할 때는 `UserEntity`가 아니라 `UserQueryService` 또는 domain-user의 조회용 Port를 사용합니다.

---

## Step 7. 네이밍 컨벤션

```text
서비스:
- AuthService
- AttendanceService
- AppVersionService
- UserQueryService, UserCommandService  // user 도메인만 예외적으로 분리

Facade:
- AuthFacade
- FeedFacade
- AttendanceFacade

Repository Port:
- UserRepositoryPort
- AttendanceRepositoryPort
- BannerRepositoryPort

외부 기능 Port:
- SmsSenderPort
- TokenIssuerPort
- OAuthAuthenticatorPort
- S3FileUploaderPort
- EventBridgePublisherPort

Persistence Adapter:
- UserRepositoryAdapter
- AttendanceRepositoryAdapter
- BannerRepositoryAdapter

외부 기능 Adapter:
- SmsSenderAdapter
- S3FileUploaderAdapter
- EventBridgePublisherAdapter

JPA Entity:
- UserEntity
- AttendanceEntity
- BannerEntity

JPA Repository:
- UserJpaRepository
- AttendanceJpaRepository
- BannerJpaRepository

Domain Model:
- User
- Attendance
- Banner
```

---

## Step 8. 구현 플랜 출력 형식

신규 기능 구현 요청에는 아래 형식으로 답변합니다.

````md
## 구현 플랜: [기능명]

### 판단
- 도메인:
- 단일/다중 도메인:
- Facade 필요 여부:
- Port 필요 여부:

### 생성할 파일
- `api/controller/<domain>/XxxController`
- `api/controller/<domain>/dto/XxxRequest`
- `api/controller/<domain>/dto/XxxResponse`
- `domain-<name>/service/XxxService`
- `domain-<name>/port/XxxRepositoryPort`
- `storage/db/<domain>/adapter/XxxRepositoryAdapter`

### 수정할 파일
- `[기존 파일 경로]`: [변경 내용]

### build.gradle.kts 수정
- `[모듈명]/build.gradle.kts`: [추가/제거할 의존성]

### 콜체인
```text
HTTP Request
  └→ ...
````

### Port 인터페이스 시그니처

```java
public interface XxxRepositoryPort {
    // 필요한 메서드
}
```

### 의존성 검증

* `domain-*`에서 `storage` 직접 참조 없음
* JPA Entity는 `storage`에만 위치
* 외부 시스템은 Port로 추상화

```

---

## Step 9. 답변 원칙

- 파일 경로를 가능한 한 실제 패키지 구조에 맞춰 구체적으로 제안합니다.
- 단순히 “서비스에 구현”이라고 하지 말고 어떤 모듈의 어떤 서비스인지 명시합니다.
- Port가 필요한 경우 Port와 Adapter를 함께 제안합니다.
- Entity와 Domain Model을 혼동하지 않습니다.
- User 정보 조회가 필요하면 `UserEntity` 직접 참조를 금지합니다.
- Facade는 여러 도메인을 조율할 때만 제안합니다.
- `core`에는 정말 공통인 코드만 둡니다.
- build.gradle.kts 수정이 필요하면 어떤 모듈에 어떤 의존성을 추가해야 하는지 명시합니다.
```
