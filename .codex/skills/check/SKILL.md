---
name: check
description: 'sopt-makers Spring Boot 멀티모듈 프로젝트에서 기존 코드 구조, 패키지 위치, Facade 필요 여부, Port/Adapter 설계, 모듈 의존성 위반 여부를 검토할 때 사용한다. "이 구조 맞아", "의존성 위반", "여기 둬도 돼", "import해도 돼", "Facade 필요해", "core에 둬도 돼" 요청 시 사용한다.'
---

# 멀티모듈 구조 검증 가이드

이 Skill은 `sopt-makers-backend`에서 현재 코드 구조가 멀티모듈 아키텍처 규칙을 지키는지 검증할 때 사용한다.

## 사용 상황

다음 요청에서 이 Skill을 사용한다.

* “이 구조 맞아?”
* “이거 의존성 위반이야?”
* “이 클래스 여기 둬도 돼?”
* “domain에서 이거 import해도 돼?”
* “Facade가 필요한 상황이야?”
* “core에 둬도 돼?”
* “Service에서 Repository를 직접 써도 돼?”
* “Entity를 domain에서 써도 돼?”
* “이 build.gradle 의존성 괜찮아?”

## 목표

답변은 다음을 명확히 해야 한다.

1. 현재 구조가 적절한지
2. 의존성 위반이 있는지
3. 어떤 규칙을 위반했는지
4. 왜 문제가 되는지
5. 어떻게 수정해야 하는지
6. 권장 패키지 구조와 콜체인이 무엇인지

---

## Step 1. 검토할 대상 식별

검토 요청을 받으면 먼저 대상을 식별한다.

* 클래스 위치 검토
* 패키지 구조 검토
* 모듈 의존성 검토
* build.gradle.kts 의존성 검토
* API 인터페이스와 Controller 구현체 분리 검토
* Controller 책임 검토
* Service 책임 검토
* Facade 필요 여부 검토
* Port/Adapter 설계 검토
* Entity/Domain Model 분리 검토
* core 배치 적절성 검토

---

## Step 2. 기본 모듈 책임

| 모듈         | 책임                                                                                   |
| ---------- | ------------------------------------------------------------------------------------ |
| `api`      | HTTP 진입점, API 인터페이스, Controller 구현체, HTTP DTO, Security, Filter, Resolver, Global Exception Handler |
| `domain-*` | 비즈니스 로직, 유스케이스, 도메인 모델, Port 인터페이스, 도메인 예외                                           |
| `storage`  | JPA Entity, JpaRepository, QueryDSL, DB/Redis Adapter                                |
| `clients`  | SMS, S3, EventBridge, OAuth, 외부 API Adapter                                          |
| `core`     | 공통 enum, 공통 응답, 공통 예외, 공통 유틸                                                         |

---

## Step 3. 코드 위치 검증 기준

| 코드 유형                    | 올바른 위치                                     |
| ------------------------ | ------------------------------------------ |
| API 문서 인터페이스          | `api/controller/<channel>/XxxApi`          |
| REST Controller 구현체      | `api/controller/<channel>/XxxController`   |
| HTTP DTO                 | `api/controller/<channel>/dto/`            |
| Security, Filter         | `api/common/security/`                     |
| Resolver                 | `api/common/resolver/`                     |
| Global Exception Handler | `api/common/exception/`                    |
| 비즈니스 로직                  | `domain-<name>/service/`                   |
| 도메인 모델                   | `domain-<name>/` 또는 `domain-<name>/model/` |
| 도메인 예외                   | `domain-<name>/exception/`                 |
| Port 인터페이스               | `domain-<name>/port/`                      |
| Facade                   | `domain-<name>/facade/`                    |
| JPA Entity               | `storage/db/<domain>/entity/`              |
| JpaRepository            | `storage/db/<domain>/repository/`          |
| QueryDSL                 | `storage/db/<domain>/querydsl/`            |
| DB Adapter               | `storage/db/<domain>/adapter/`             |
| Redis Adapter            | `storage/redis/<domain>/adapter/`          |
| 외부 시스템 Adapter           | `clients/<provider>/`                      |
| 공통 코드                    | `core/`                                    |

---

## Step 4. 의존성 규칙

허용되는 의존 방향:

```text
api          → domain-*, storage, clients, core
domain-*     → core
domain-auth  → domain-user Port/domain model only
domain-app   → domain-user Port/domain model only
domain-admin → domain-user Port/domain model only
domain-playground → domain-user Port/domain model only
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
* Controller에서 비즈니스 로직 처리

예외:

* `domain-auth`, `domain-playground`, `domain-app`, `domain-admin`에서 user 기능이 필요하면 `domain-user`가 노출한 목적별 Port와 도메인 모델만 사용한다.
* 다른 도메인에서 `UserCommandService`, `UserQueryService`를 직접 주입하지 않는다.
* 다른 도메인 유스케이스가 user 데이터를 변경해야 하면 소비 도메인의 Port를 먼저 만들고 구현은 `domain-user` 또는 `storage`에 둔다.
* 사용자 조회가 필요할 때는 `UserEntity`나 User JpaRepository를 직접 참조하지 않는다.

---

## Step 5. 계층별 검토 기준

### Controller

HTTP API는 `refactor/#21` 구조처럼 API 인터페이스와 구현체를 분리한다.

Controller 패키지의 `<channel>`은 API 노출 채널이며, 비즈니스 도메인과 반드시 1:1로 매핑되지 않는다.

* 가능한 채널명은 기존 controller 패키지 기준으로 `auth`, `user`, `official`, `app`, `admin`, `playground`, `crew` 등을 사용한다.
* 채널명이 도메인명과 같을 수는 있지만, 항상 같아야 하는 것은 아니다.
* 출석 API처럼 앱에 노출되는 기능은 `api/controller/app/attendance/`에 둘 수 있다.
* Controller/HTTP DTO 위치는 노출 채널 기준으로, Service/Port/Model 위치는 비즈니스 책임 기준으로 검토한다.

`XxxApi` 인터페이스:

* `api/controller/<channel>/`에 둔다.
* `@Tag`, `@Operation`, `@Schema(hidden = true)`, `@ParameterObject` 등 Swagger/OpenAPI 문서 애노테이션을 담당한다.
* 메서드 시그니처만 선언한다.
* Service/Facade 주입, ResponseFactory 호출, 구현 로직을 가지면 안 된다.

`XxxController` 구현체:

* 같은 패키지에서 `XxxApi`를 `implements`한다.
* `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@ModelAttribute`, `@PathVariable`, `@RequestHeader`, `@CookieValue`, `@Valid` 등 실제 Spring MVC 매핑과 검증 애노테이션을 담당한다.
* HTTP 요청 처리, 인증 사용자 식별, Service/Facade 호출, Response DTO 변환을 담당한다.

Controller는 다음만 담당한다.

* HTTP 요청 수신
* Request DTO 검증
* 인증 사용자 식별
* Application/Domain 호출
* Response DTO 변환

Controller에 있으면 안 되는 것:

* 비즈니스 정책 판단
* DB 조회/저장
* 외부 API 호출
* 복잡한 상태 변경 로직
* 여러 Repository 조합

문제가 있으면 Service 또는 Facade로 이동한다.

### Service

Service는 다음을 담당한다.

* 비즈니스 규칙
* 유스케이스
* 도메인 상태 변경
* Port 호출

Service에 있으면 안 되는 것:

* JpaRepository 직접 주입
* JPA Entity 직접 사용
* EntityManager 직접 사용
* QueryDSL 직접 사용
* RedisTemplate 직접 사용
* 외부 Client 직접 호출
* 다른 도메인 Service 무분별한 직접 호출

문제가 있으면 Port를 만들고 Adapter로 분리한다.

### Facade

Facade는 다음 경우에 적절하다.

* 여러 도메인 서비스를 조율한다.
* 하나의 API에서 여러 도메인의 유스케이스가 함께 실행된다.
* 도메인 간 직접 의존을 피하기 위한 조율 계층이 필요하다.

Facade가 부적절한 경우:

* 단일 Service만 감싼다.
* 아무 로직 없이 위임만 한다.
* Controller에서 Service 호출만 해도 충분하다.

### Port

Port는 domain이 필요로 하는 외부 의존성을 추상화한다.

Port가 필요한 경우:

* DB 조회/저장
* Redis 접근
* 외부 API 호출
* SMS/S3/EventBridge 호출
* OAuth Provider 호출
* 테스트에서 대체해야 하는 외부 요소

Port 위치:

```text
domain-<name>/port/
```

### Adapter

Adapter는 Port 구현체다.

| 구현 대상           | 위치                                |
| --------------- | --------------------------------- |
| DB/JPA/QueryDSL | `storage/db/<domain>/adapter/`    |
| Redis           | `storage/redis/<domain>/adapter/` |
| SMS             | `clients/sms/`                    |
| S3              | `clients/s3/`                     |
| EventBridge     | `clients/eventbridge/`            |
| OAuth/외부 API    | `clients/<provider>/`             |

### Core

core에는 진짜 공통 코드만 둔다.

core에 두기 적절한 것:

* 공통 응답
* 공통 예외 기반 클래스
* 공통 에러 코드
* 여러 도메인에서 공유하는 일반 enum
* 도메인에 종속되지 않는 유틸

core에 두면 안 되는 것:

* 특정 도메인 정책
* 특정 도메인 전용 enum
* 특정 API 전용 로직
* storage/clients 의존 코드
* Spring Web에 강하게 묶인 HTTP 계층 코드

---

## Step 6. 자주 발생하는 위반과 수정 방향

| 위반                                | 문제                              | 수정 방향                                   |
| --------------------------------- | ------------------------------- | --------------------------------------- |
| Domain Service가 JpaRepository를 주입 | domain이 storage 기술에 묶임          | Repository Port 생성 후 storage Adapter 구현 |
| Domain Service가 JPA Entity를 사용    | domain이 JPA에 묶임                 | Domain Model로 변환해서 사용                   |
| Domain Service가 SMS/S3 Client를 호출 | domain이 clients에 묶임             | 외부 기능 Port 생성 후 clients Adapter 구현      |
| Controller가 비즈니스 검증 수행            | HTTP 계층에 정책이 섞임                 | Domain Service로 이동                      |
| Controller 채널을 도메인과 무조건 동일시        | 노출 표면과 비즈니스 책임 판단이 섞임       | `api/controller/<channel>/...`와 `domain-<name>/...`를 따로 판단 |
| Controller에 Swagger 문서 애노테이션과 구현 로직 혼재 | 문서 책임과 실행 책임이 섞임              | `XxxApi` 인터페이스로 문서 애노테이션 분리         |
| `XxxController`가 `XxxApi`를 구현하지 않음      | API 문서 인터페이스 패턴이 깨짐            | 같은 패키지의 `XxxApi`를 만들고 implements 적용  |
| Facade가 단일 Service만 위임            | 불필요한 계층 증가                      | Facade 제거                               |
| core에 도메인 전용 enum 배치              | core가 도메인에 오염됨                  | 해당 domain 모듈로 이동                        |
| 다른 도메인 Service 직접 주입              | 도메인 내부 구현에 결합됨                  | 목적별 Port 생성 후 Adapter에서 Service 호출       |
| domain 간 직접 의존                    | 도메인 결합도 증가                      | Facade 또는 목적별 Port로 조율                  |
| domain에서 UserEntity import        | domain이 storage/user JPA 구조에 묶임 | domain-user Port 또는 소비 도메인 Port 사용       |

---

## Step 7. build.gradle.kts 검토 기준

### domain 모듈

domain 모듈은 기본적으로 `core`만 의존한다.

```kotlin
dependencies {
    implementation(project(":core"))
}
```

user 기능이 필요한 일부 도메인은 예외적으로 `domain-user`의 Port와 도메인 모델에만 의존할 수 있다.

```kotlin
dependencies {
    implementation(project(":domain-user"))
    implementation(project(":core"))
}
```

domain 모듈에 다음 의존성이 있으면 의심한다.

```kotlin
implementation(project(":storage"))
implementation(project(":clients"))
```

### storage 모듈

storage는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있다.

```kotlin
dependencies {
    implementation(project(":domain-user"))
    implementation(project(":domain-app"))
    implementation(project(":core"))
}
```

### clients 모듈

clients는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있다.

```kotlin
dependencies {
    implementation(project(":domain-auth"))
    implementation(project(":core"))
}
```

### api 모듈

api는 HTTP 진입점으로 필요한 domain, storage, clients, core를 조립할 수 있다.

```kotlin
dependencies {
    implementation(project(":domain-user"))
    implementation(project(":domain-auth"))
    implementation(project(":storage"))
    implementation(project(":clients"))
    implementation(project(":core"))
}
```

---

## Step 8. 검토 답변 형식

구조 검토 답변은 가능한 한 다음 형식을 따른다.

````md
## 구조 검토 결과: [대상]

### 결론
- 적절함 / 일부 수정 필요 / 의존성 위반 있음

### 현재 구조
```text
...
```

### 문제점

* ...

### 수정 방향

* ...

### 권장 구조

```text
...
```

### 권장 콜체인

```text
HTTP Request
  → XxxController implements XxxApi
    (API 문서 계약: XxxApi)
  → Service/Facade
  → Port
  → Adapter
```

### 의존성 검증

* ...

````

---

## Step 9. 답변 원칙

- 단순히 “안 돼요”라고 하지 말고 어떤 의존성 규칙을 위반하는지 설명한다.
- 위반이 없다면 왜 괜찮은지 설명한다.
- 애매한 경우에는 유지보수성과 의존 방향 기준으로 판단한다.
- Facade가 필요한지 여부를 명확히 판단한다.
- Controller 위치 검토 시 도메인명이 아니라 API 노출 채널 기준으로 `api/controller/<channel>/...`에 있는지 확인한다.
- domain 모듈 위치는 Controller 채널과 별개로 데이터 생명주기와 비즈니스 책임 기준으로 확인한다.
- HTTP API 구조 검토 시 `XxxApi` 인터페이스와 `XxxController implements XxxApi` 분리가 되어 있는지 확인한다.
- Swagger/OpenAPI 애노테이션은 `XxxApi`, Spring MVC 매핑과 실제 구현 로직은 `XxxController`에 있는지 확인한다.
- Port가 필요한 경우 어떤 Port를 만들지 제안한다.
- `domain-*`에서 `storage`, `clients`, JPA Entity, JpaRepository import가 없어야 한다는 점을 우선 확인한다.
- 가능한 경우 권장 패키지 구조와 콜체인을 함께 제시한다.
