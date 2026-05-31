---

name: check
description: sopt-makers Spring Boot 멀티모듈 프로젝트에서 기존 코드 구조, 패키지 위치, Facade 필요 여부, Port/Adapter 설계, 모듈 의존성 위반 여부를 검토할 때 사용합니다. "이 구조 맞아", "의존성 위반", "여기 둬도 돼", "import해도 돼", "Facade 필요해", "core에 둬도 돼", "build.gradle 괜찮아" 요청 시 트리거.
tools: Read, Grep, Glob, Bash
-----------------------------

# 멀티모듈 구조 검증 가이드

이 스킬은 `sopt-makers-backend`에서 현재 코드 구조가 멀티모듈 아키텍처 규칙을 지키는지 검증할 때 사용합니다.

## 사용 상황

다음 상황에서 반드시 이 스킬을 사용합니다.

1. 코드 위치가 적절한지 검토할 때
2. 모듈 간 의존성 위반 여부를 확인할 때
3. domain에서 특정 클래스를 import해도 되는지 확인할 때
4. Facade가 필요한 상황인지 판단할 때
5. Port/Adapter 설계가 맞는지 검토할 때
6. core에 특정 코드를 둬도 되는지 판단할 때
7. build.gradle.kts 의존성이 적절한지 검토할 때

---

## Step 1. 검토 대상 식별

검토 요청을 받으면 먼저 대상을 식별합니다.

* 클래스 위치 검토
* 패키지 구조 검토
* 모듈 의존성 검토
* build.gradle.kts 의존성 검토
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
| `api`      | HTTP 진입점, Controller, HTTP DTO, Security, Filter, Resolver, Global Exception Handler |
| `domain-*` | 비즈니스 로직, 유스케이스, 도메인 모델, Port 인터페이스, 도메인 예외                                           |
| `storage`  | JPA Entity, JpaRepository, QueryDSL, DB/Redis Adapter                                |
| `clients`  | SMS, S3, EventBridge, OAuth, 외부 API Adapter                                          |
| `core`     | 공통 enum, 공통 응답, 공통 예외, 공통 유틸                                                         |

---

## Step 3. 코드 위치 검증 기준

| 코드 유형                    | 올바른 위치                                     |
| ------------------------ | ------------------------------------------ |
| REST Controller          | `api/controller/<domain>/`                 |
| HTTP DTO                 | `api/controller/<domain>/dto/`             |
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
* 사용자 조회가 필요할 때는 `UserEntity`나 User JpaRepository를 직접 참조하지 않습니다.

---

## Step 5. 계층별 검토 기준

### Controller

Controller는 다음만 담당합니다.

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

문제가 있으면 Service 또는 Facade로 이동합니다.

### Service

Service는 다음을 담당합니다.

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

문제가 있으면 Port를 만들고 Adapter로 분리합니다.

### Facade

Facade는 다음 경우에 적절합니다.

* 여러 도메인 서비스를 조율합니다.
* 하나의 API에서 여러 도메인의 유스케이스가 함께 실행됩니다.
* 도메인 간 직접 의존을 피하기 위한 조율 계층이 필요합니다.

Facade가 부적절한 경우:

* 단일 Service만 감쌉니다.
* 아무 로직 없이 위임만 합니다.
* Controller에서 Service 호출만 해도 충분합니다.

### Port

Port는 domain이 필요로 하는 외부 의존성을 추상화합니다.

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

Adapter는 Port 구현체입니다.

| 구현 대상           | 위치                                |
| --------------- | --------------------------------- |
| DB/JPA/QueryDSL | `storage/db/<domain>/adapter/`    |
| Redis           | `storage/redis/<domain>/adapter/` |
| SMS             | `clients/sms/`                    |
| S3              | `clients/s3/`                     |
| EventBridge     | `clients/eventbridge/`            |
| OAuth/외부 API    | `clients/<provider>/`             |

### Core

core에는 진짜 공통 코드만 둡니다.

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
| Facade가 단일 Service만 위임            | 불필요한 계층 증가                      | Facade 제거                               |
| core에 도메인 전용 enum 배치              | core가 도메인에 오염됨                  | 해당 domain 모듈로 이동                        |
| domain 간 직접 의존                    | 도메인 결합도 증가                      | Facade 또는 User 조회 서비스로 조율               |
| domain에서 UserEntity import        | domain이 storage/user JPA 구조에 묶임 | UserQueryService 또는 조회 Port 사용          |

---

## Step 7. build.gradle.kts 검토 기준

### domain 모듈

domain 모듈은 기본적으로 `core`만 의존합니다.

```kotlin
dependencies {
    implementation(project(":core"))
}
```

user 조회가 필요한 일부 도메인은 예외적으로 `domain-user`를 의존할 수 있습니다.

```kotlin
dependencies {
    implementation(project(":domain-user"))
    implementation(project(":core"))
}
```

domain 모듈에 다음 의존성이 있으면 의존성 위반 가능성이 높습니다.

```kotlin
implementation(project(":storage"))
implementation(project(":clients"))
```

### storage 모듈

storage는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있습니다.

```kotlin
dependencies {
    implementation(project(":domain-user"))
    implementation(project(":domain-app"))
    implementation(project(":core"))
}
```

### clients 모듈

clients는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있습니다.

```kotlin
dependencies {
    implementation(project(":domain-auth"))
    implementation(project(":core"))
}
```

### api 모듈

api는 HTTP 진입점으로 필요한 domain, storage, clients, core를 조립할 수 있습니다.

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

## Step 8. 구조 검토 출력 형식

구조 검토 요청에는 아래 형식으로 답변합니다.

````md
## 구조 검토 결과: [대상]

### 결론
- 적절함 / 일부 수정 필요 / 의존성 위반 있음

### 현재 구조
```text
...
````

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
Controller
  └→ Service/Facade
       └→ Port
            └→ Adapter
```

### 의존성 검증

* ...

```

---

## Step 9. 답변 원칙

- 단순히 “안 됩니다”라고 하지 말고 어떤 의존성 규칙을 위반하는지 설명합니다.
- 위반이 없다면 왜 괜찮은지 설명합니다.
- 애매한 경우에는 유지보수성과 의존 방향 기준으로 판단합니다.
- Facade가 필요한지 여부를 명확히 판단합니다.
- Port가 필요한 경우 어떤 Port를 만들지 제안합니다.
- `domain-*`에서 `storage`, `clients`, JPA Entity, JpaRepository import가 없어야 한다는 점을 우선 확인합니다.
- 가능한 경우 권장 패키지 구조와 콜체인을 함께 제시합니다.
```
