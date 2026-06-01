---
name: implement
description: 'sopt-makers Spring Boot 멀티모듈 프로젝트에서 신규 기능의 구현 위치, 생성 파일, Controller → Service/Facade → Port → Adapter 콜체인, 모듈 의존성 방향을 설계할 때 사용한다. "새 기능", "구현", "어디에 둬야", "API 만들기", "Controller", "Service", "Facade", "Port", "Adapter" 요청 시 사용한다.'
---

# 신규 기능 구현 모듈 설계 가이드

이 Skill은 `sopt-makers-backend`에서 새로운 기능을 구현할 때 코드가 어느 모듈과 패키지에 들어가야 하는지 판단하고, 전체 콜체인을 설계하기 위해 사용한다.

## 사용 상황

다음 요청에서 이 Skill을 사용한다.

* 새로운 기능을 구현할 때
* 신규 API의 Controller, Service, Facade, Port, Adapter 구조를 잡을 때
* 코드가 어느 모듈에 들어가야 하는지 판단할 때
* 특정 기능의 생성 파일 목록을 설계할 때
* 외부 시스템 연동을 Port/Adapter 구조로 설계할 때
* 여러 도메인을 조율해야 하는지 판단할 때

## 목표

답변은 다음을 명확히 해야 한다.

1. 어떤 도메인 기능인지
2. 단일 도메인인지, 다중 도메인 조율인지
3. Facade가 필요한지
4. Port가 필요한지
5. 어떤 파일을 생성/수정해야 하는지
6. 각 파일이 어느 모듈과 패키지에 위치해야 하는지
7. 의존성 방향이 올바른지

---

## Step 1. 도메인 식별

먼저 기능이 속하는 비즈니스 도메인을 식별한다.

| 도메인          | 예시                           |
| ------------ | ---------------------------- |
| `user`       | 유저 프로필, 소셜 계정, 활동 이력         |
| `auth`       | 소셜 로그인, 토큰 발급, 전화번호 인증       |
| `playground` | 게시글, 댓글, 좋아요, 크루 생성/신청/승인/거절 |
| `app`        | 앱 버전, 배너, 팝업, 공지             |
| `admin`      | 어드민 작업, 출석, 백오피스             |

도메인이 애매하면 다음 기준으로 판단한다.

1. 해당 데이터의 생명주기를 소유하는 도메인을 우선한다.
2. 상태 변경의 주체가 되는 도메인을 우선한다.
3. 단순 조회만 필요한 다른 도메인은 조율 대상으로 본다.
4. 여러 도메인이 함께 필요하면 Facade 사용을 검토한다.

---

## Step 2. 코드 배치 규칙

| 코드 유형                      | 위치                                         |
| -------------------------- | ------------------------------------------ |
| REST Controller            | `api/controller/<domain>/`                 |
| HTTP Request/Response DTO  | `api/controller/<domain>/dto/`             |
| Security, Filter           | `api/common/security/`                     |
| Resolver                   | `api/common/resolver/`                     |
| Global Exception Handler   | `api/common/exception/`                    |
| 비즈니스 로직, 유스케이스             | `domain-<name>/service/`                   |
| 도메인 모델                     | `domain-<name>/` 또는 `domain-<name>/model/` |
| 도메인 예외                     | `domain-<name>/exception/`                 |
| Repository Port            | `domain-<name>/port/`                      |
| 외부 기능 Port                 | `domain-<name>/port/`                      |
| Facade                     | `domain-<name>/facade/`                    |
| JPA Entity                 | `storage/db/<domain>/entity/`              |
| Spring Data JPA Repository | `storage/db/<domain>/repository/`          |
| QueryDSL 구현체               | `storage/db/<domain>/querydsl/`            |
| DB Adapter                 | `storage/db/<domain>/adapter/`             |
| Redis Cache                | `storage/redis/<domain>/cache/`            |
| Redis Adapter              | `storage/redis/<domain>/adapter/`          |
| SMS 구현체                    | `clients/sms/`                             |
| S3 구현체                     | `clients/s3/`                              |
| EventBridge 구현체            | `clients/eventbridge/`                     |
| 공통 enum, 응답, 예외, 유틸        | `core/`                                    |

---

## Step 3. 콜체인 설계

### 단일 도메인 기능

하나의 도메인 안에서 비즈니스 로직이 끝나면 Facade를 만들지 않는다.

```text
HTTP Request
  → api/controller/<domain>/XxxController
  → domain-<name>/service/XxxService
  → domain-<name>/port/XxxRepositoryPort
  → storage/db/<domain>/adapter/XxxRepositoryAdapter
```

### 다중 도메인 기능

여러 도메인 서비스를 조율해야 하면 Facade를 사용한다.

```text
HTTP Request
  → api/controller/<domain>/XxxController
  → domain-<name>/facade/XxxFacade
  → domain-<name>/service/XxxService
  → domain-user/service/UserQueryService
  → domain-<name>/port/XxxRepositoryPort
  → storage/db/<domain>/adapter/XxxRepositoryAdapter
```

### 외부 시스템 연동 기능

SMS, S3, EventBridge, OAuth 등 외부 시스템이 필요하면 domain에 Port를 두고 `clients`에서 구현한다.

```text
Domain Service
  → domain-<name>/port/XxxCapabilityPort
  → clients/<provider>/XxxAdapter
```

---

## Step 4. Facade 판단 기준

Facade는 다음 경우에만 만든다.

* 여러 도메인 서비스를 조율해야 한다.
* 하나의 API에서 서로 다른 도메인의 유스케이스가 함께 실행된다.
* 도메인 간 직접 의존을 피하기 위한 조율 계층이 필요하다.
* 인증 사용자 조회 후 다른 도메인 기능을 실행하는 복합 흐름이다.

Facade를 만들지 않는 경우:

* 단일 Service만 호출한다.
* 단순히 Service를 한 번 감싸기만 한다.
* Controller에서 Service로 바로 위임해도 의존성 위반이 없다.

---

## Step 5. Port 판단 기준

다음 의존성이 domain service에 필요하면 Port를 만든다.

* DB 조회/저장
* Redis 접근
* QueryDSL 조회
* SMS 발송
* S3 업로드/삭제
* EventBridge 이벤트 발행
* OAuth Provider 호출
* 외부 API 호출
* 현재 시간, UUID 생성 등 테스트에서 대체하고 싶은 외부 요인

Repository Port 예시:

```java
public interface UserRepositoryPort {
    Optional<User> findById(Long userId);

    User save(User user);
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
api          → domain-*, storage, clients, core
domain-*     → core
domain-auth  → domain-user
domain-app   → domain-user
domain-admin → domain-user
domain-playground → domain-user
storage      → domain-*, core
clients      → domain-*, core
core         → no dependencies
```

금지되는 의존:

* `domain-*` → `storage`
* `domain-*` → `clients`
* `domain-*` → JPA Entity
* `domain-*` → JpaRepository
* `domain-*` → 다른 domain 직접 의존
* `core` → 다른 모듈

예외:

* `domain-auth`, `domain-playground`, `domain-app`, `domain-admin`에서 `domain-user`의 조회 서비스 또는 조회 Port를 사용하는 것은 허용한다.
* 다른 도메인에서 사용자 정보가 필요할 때는 `UserEntity`가 아니라 `UserQueryService` 또는 domain-user의 조회용 Port를 사용한다.

---

## Step 7. 네이밍 규칙

Service:

```text
AuthService
AttendanceService
AppVersionService
```

User 도메인만 예외적으로 Query/Command 서비스를 분리할 수 있다.

```text
UserQueryService
UserCommandService
```

Facade:

```text
AuthFacade
FeedFacade
AttendanceFacade
```

Repository Port:

```text
UserRepositoryPort
AttendanceRepositoryPort
BannerRepositoryPort
```

외부 기능 Port:

```text
SmsSenderPort
S3FileUploaderPort
EventBridgePublisherPort
OAuthAuthenticatorPort
```

Persistence Adapter:

```text
UserRepositoryAdapter
AttendanceRepositoryAdapter
BannerRepositoryAdapter
```

External Adapter:

```text
SmsSenderAdapter
S3FileUploaderAdapter
EventBridgePublisherAdapter
OAuthAuthenticatorAdapter
```

JPA Entity:

```text
UserEntity
AttendanceEntity
BannerEntity
```

JPA Repository:

```text
UserJpaRepository
AttendanceJpaRepository
BannerJpaRepository
```

도메인 모델:

```text
User
Attendance
Banner
```

---

## Step 8. 답변 형식

신규 구현 설계 답변은 가능한 한 다음 형식을 따른다.

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
- `...`

### 콜체인
```text
HTTP Request
  → ...
```

### Port 인터페이스 예시

```java
public interface XxxRepositoryPort {
    ...
}
```

### 의존성 검증

* `domain-*`에서 `storage`를 직접 참조하지 않음
* JPA Entity는 `storage`에만 위치
* 외부 시스템은 Port로 추상화

````

---

## Step 9. 답변 원칙

- 파일 경로를 가능한 한 구체적으로 제안한다.
- 단순히 “서비스에 둔다”고 하지 말고 어떤 모듈의 어떤 서비스인지 명시한다.
- Port가 필요한 경우 Port와 Adapter를 함께 제안한다.
- Entity와 Domain Model을 혼동하지 않는다.
- User 정보 조회가 필요하면 `UserEntity` 직접 참조를 금지한다.
- Facade는 여러 도메인 조율이 있을 때만 제안한다.
- `core`에는 정말 공통인 코드만 둔다.
- build.gradle.kts 수정이 필요하면 어떤 모듈에 어떤 의존성을 추가해야 하는지 명시한다.
