---

name: migrate
description: sopt-makers Spring Boot 프로젝트에서 legacy/ 디렉토리 코드나 기존 코드를 새 멀티모듈 구조로 마이그레이션할 때 사용합니다. "legacy", "마이그레이션", "옮기기", "기존 코드 분리", "모듈로 이동", "Controller/Service/Repository 분리" 요청 시 트리거.
tools: Read, Grep, Glob, Bash
-----------------------------

# Legacy 코드 멀티모듈 마이그레이션 가이드

이 스킬은 `sopt-makers-backend`에서 `legacy/` 디렉토리 코드 또는 기존 코드를 새 멀티모듈 구조로 옮길 때 사용합니다.

## 사용 상황

다음 상황에서 반드시 이 스킬을 사용합니다.

1. `legacy/` 디렉토리 코드를 새 모듈 구조로 옮길 때
2. 기존 Controller, Service, Entity, Repository를 멀티모듈 구조로 분리할 때
3. 기존 코드의 책임을 `api`, `domain-*`, `storage`, `clients`, `core`로 재배치할 때
4. 기존 Service에서 DB/Redis/외부 API 의존성을 Port로 분리할 때
5. 기존 모놀리식 패키지 구조를 새 패키지 구조로 옮길 때

---

## Step 1. 기존 코드 책임 분류

기존 코드를 먼저 다음 책임으로 분류합니다.

| 기존 코드 유형                        | 새 위치                                       |
| ------------------------------- | ------------------------------------------ |
| Controller                      | `api/controller/<domain>/`                 |
| Request/Response DTO            | `api/controller/<domain>/dto/`             |
| 인증/인가 관련 코드                     | `api/common/security/`                     |
| Resolver                        | `api/common/resolver/`                     |
| Global Exception Handler        | `api/common/exception/`                    |
| 비즈니스 로직 Service                 | `domain-<name>/service/`                   |
| 도메인 모델                          | `domain-<name>/` 또는 `domain-<name>/model/` |
| 도메인 예외                          | `domain-<name>/exception/`                 |
| Repository Port                 | `domain-<name>/port/`                      |
| 외부 기능 Port                      | `domain-<name>/port/`                      |
| JPA Entity                      | `storage/db/<domain>/entity/`              |
| Spring Data JPA Repository      | `storage/db/<domain>/repository/`          |
| QueryDSL 구현체                    | `storage/db/<domain>/querydsl/`            |
| DB Adapter                      | `storage/db/<domain>/adapter/`             |
| Redis Adapter                   | `storage/redis/<domain>/adapter/`          |
| SMS/S3/EventBridge/OAuth Client | `clients/<provider>/`                      |
| 공통 enum, 응답, 예외, 유틸             | `core/`                                    |

---

## Step 2. 도메인 식별

기존 코드가 어느 도메인에 속하는지 판단합니다.

| 도메인          | 예시                           |
| ------------ | ---------------------------- |
| `user`       | 유저 프로필, 소셜 계정, 활동 이력         |
| `auth`       | 소셜 로그인, 토큰 발급, 전화번호 인증       |
| `playground` | 게시글, 댓글, 좋아요, 크루 생성/신청/승인/거절 |
| `app`        | 앱 버전, 배너, 팝업, 공지             |
| `admin`      | 어드민 작업, 출석, 백오피스             |

판단 기준:

1. 데이터 생명주기를 소유하는 도메인을 우선합니다.
2. 상태 변경의 주체가 되는 도메인을 우선합니다.
3. 특정 API가 여러 도메인을 묶는 경우 Facade 후보로 봅니다.
4. 단순히 사용자 정보를 조회하는 경우 `domain-user`의 조회 서비스 또는 조회 Port 사용을 고려합니다.

---

## Step 3. Port 추출 기준

기존 Service 안에 다음 코드가 있으면 Port로 분리합니다.

* JpaRepository 직접 호출
* EntityManager 직접 사용
* QueryDSL 직접 사용
* RedisTemplate 직접 사용
* SMS Client 직접 호출
* S3 Client 직접 호출
* EventBridge Client 직접 호출
* OAuth Provider 직접 호출
* 외부 API Client 직접 호출
* 파일 시스템, 시간, UUID 등 테스트에서 대체 가능한 외부 요인 직접 사용

Repository Port 예시:

```java
public interface XxxRepositoryPort {
    Optional<Xxx> findById(Long id);

    Xxx save(Xxx xxx);
}
```

외부 기능 Port 예시:

```java
public interface XxxSenderPort {
    void send(XxxMessage message);
}
```

---

## Step 4. Adapter 이동 기준

Port 구현체는 의존하는 기술에 따라 위치를 결정합니다.

| 의존 기술                        | Adapter 위치                               |
| ---------------------------- | ---------------------------------------- |
| JPA, QueryDSL, EntityManager | `storage/db/<domain>/adapter/`           |
| Redis                        | `storage/redis/<domain>/adapter/`        |
| SMS                          | `clients/sms/`                           |
| S3                           | `clients/s3/`                            |
| EventBridge                  | `clients/eventbridge/`                   |
| OAuth Provider               | `clients/oauth/` 또는 provider별 client 패키지 |
| 외부 HTTP API                  | `clients/<provider>/`                    |

Adapter는 domain Port를 구현합니다.

```java
@Component
@RequiredArgsConstructor
public class XxxRepositoryAdapter implements XxxRepositoryPort {
    private final XxxJpaRepository xxxJpaRepository;

    @Override
    public Optional<Xxx> findById(final Long id) {
        return xxxJpaRepository.findById(id)
            .map(XxxEntity::toDomain);
    }
}
```

---

## Step 5. Entity와 Domain Model 분리

마이그레이션 시 JPA Entity와 Domain Model을 분리합니다.

### JPA Entity

* `storage/db/<domain>/entity/`에 둡니다.
* `Entity` 접미사를 붙입니다.
* JPA annotation을 가집니다.
* DB 매핑 책임을 가집니다.
* Domain Model 변환 메서드를 가질 수 있습니다.

```java
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private Long id;

    public User toDomain() {
        return new User(id);
    }

    public static UserEntity from(final User user) {
        return new UserEntity(user.id());
    }
}
```

### Domain Model

* `domain-<name>/` 또는 `domain-<name>/model/`에 둡니다.
* `Entity` 접미사를 붙이지 않습니다.
* JPA annotation을 가지지 않습니다.
* 비즈니스 상태와 행위를 표현합니다.

```java
public record User(Long id) {
}
```

---

## Step 6. 의존성 규칙

마이그레이션 후 허용되는 의존 방향:

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

---

## Step 7. 마이그레이션 기본 순서

기본적으로 다음 순서로 제안합니다.

1. 기존 코드의 책임을 분류합니다.
2. 도메인을 식별합니다.
3. Controller와 HTTP DTO를 `api`로 이동합니다.
4. 비즈니스 로직을 `domain-<name>/service/`로 이동합니다.
5. JPA Entity를 `storage/db/<domain>/entity/`로 이동합니다.
6. JpaRepository를 `storage/db/<domain>/repository/`로 이동합니다.
7. domain service가 필요로 하는 Repository Port를 정의합니다.
8. `storage/db/<domain>/adapter/`에서 Repository Port를 구현합니다.
9. 외부 시스템 호출이 있으면 외부 기능 Port를 정의합니다.
10. `clients/<provider>/`에서 외부 기능 Port를 구현합니다.
11. Controller → Service/Facade → Port → Adapter 콜체인을 연결합니다.
12. build.gradle.kts 의존성을 정리합니다.
13. domain에서 storage/clients/JPA import가 사라졌는지 확인합니다.
14. 테스트를 보완합니다.

---

## Step 8. build.gradle.kts 점검

마이그레이션 시 다음 방향을 유지합니다.

* `api`는 필요한 domain 모듈에 의존할 수 있습니다.
* `domain-*`은 기본적으로 `core`에만 의존합니다.
* user 조회가 필요한 도메인은 `domain-user` 의존을 예외적으로 가질 수 있습니다.
* `storage`는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있습니다.
* `clients`는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있습니다.
* `core`는 다른 프로젝트 모듈에 의존하지 않습니다.

domain 모듈에 다음 의존성은 추가하지 않습니다.

```kotlin
implementation(project(":storage"))
implementation(project(":clients"))
```

---

## Step 9. 자주 발생하는 마이그레이션 위반

| 기존 패턴                                     | 수정 방향                                     |
| ----------------------------------------- | ----------------------------------------- |
| Service에서 JpaRepository 직접 주입             | Repository Port 생성 후 storage Adapter에서 구현 |
| Service에서 JPA Entity 사용                   | Domain Model로 변환해서 사용                     |
| Service에서 SMS/S3/EventBridge Client 직접 호출 | 외부 기능 Port 생성 후 clients Adapter에서 구현      |
| Controller에서 비즈니스 검증 처리                   | Domain Service로 이동                        |
| domain 코드가 storage 패키지를 import            | Port를 통해 역전                               |
| domain 코드가 clients 패키지를 import            | Port를 통해 역전                               |
| core에 도메인 전용 정책 이동                        | 해당 domain 모듈로 이동                          |
| 단일 Service 위임용 Facade 생성                  | Facade 제거 또는 다중 도메인 조율이 있는지 재검토           |

---

## Step 10. 마이그레이션 플랜 출력 형식

마이그레이션 요청에는 아래 형식으로 답변합니다.

````md
## 마이그레이션 플랜: [대상 기능/클래스]

### 판단
- 도메인:
- 기존 구조:
- 목표 구조:
- Facade 필요 여부:
- Port 필요 여부:

### 기존 코드 책임 분류
| 기존 코드 | 현재 책임 | 이동 위치 |
|---|---|---|

### 새 패키지 구조
```text
api/controller/<domain>/
domain-<name>/service/
domain-<name>/port/
storage/db/<domain>/entity/
storage/db/<domain>/repository/
storage/db/<domain>/adapter/
````

### 생성할 Port

```java
public interface XxxRepositoryPort {
    // 필요한 메서드
}
```

### 생성할 Adapter

* `storage/db/<domain>/adapter/XxxRepositoryAdapter`

### 이동 순서

1. ...
2. ...
3. ...

### build.gradle.kts 수정

* `...`

### 의존성 주의사항

* `domain-*`에서 `storage` 직접 참조 금지
* JPA Entity는 `storage`에만 위치
* 외부 Client 직접 호출은 Port로 분리

```

---

## Step 11. 답변 원칙

- 기존 코드의 책임과 이동 위치를 표로 정리합니다.
- 마이그레이션 순서는 안전한 단계별 흐름으로 제안합니다.
- Port와 Adapter를 반드시 함께 설명합니다.
- Entity와 Domain Model의 분리를 명확히 합니다.
- 의존성 위반이 발생할 수 있는 지점을 먼저 지적합니다.
- build.gradle.kts 수정 방향을 함께 검토합니다.
- 기존 기능의 동작을 유지하는 것을 우선합니다.
```
