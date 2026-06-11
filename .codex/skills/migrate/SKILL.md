---
name: migrate
description: 'sopt-makers Spring Boot 프로젝트에서 legacy 코드나 기존 코드를 새 멀티모듈 구조로 마이그레이션할 때 사용한다. "legacy", "마이그레이션", "옮기기", "기존 코드 분리", "모듈로 이동", "Controller/Service/Repository 분리" 요청 시 사용한다.'
---

# Legacy 코드 멀티모듈 마이그레이션 가이드

이 Skill은 `sopt-makers-backend`에서 legacy 코드 또는 기존 코드를 새 멀티모듈 구조로 옮길 때 사용한다.

## 사용 상황

다음 요청에서 이 Skill을 사용한다.

* `legacy/` 디렉토리 코드를 새 모듈 구조로 옮길 때
* 기존 Controller, Service, Entity, Repository를 멀티모듈 구조로 분리할 때
* 기존 코드의 책임을 `api`, `domain-*`, `storage`, `clients`, `core`로 재배치할 때
* 기존 Service에서 DB/Redis/외부 API 의존성을 Port로 분리할 때
* 기존 모놀리식 패키지 구조를 새 패키지 구조로 옮길 때

## 목표

답변은 다음을 명확히 해야 한다.

1. 기존 코드의 책임 분류
2. 각 코드의 이동 위치
3. 새로 만들어야 하는 Port
4. 새로 만들어야 하는 Adapter
5. 기존 의존성 위반 제거 방식
6. 안전한 마이그레이션 순서
7. 이동 후 콜체인

---

## Step 1. 기존 코드 책임 분류

기존 코드를 먼저 다음 책임으로 분류한다.

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
| Repository 추상화                  | `domain-<name>/port/`                      |
| 외부 기능 추상화                       | `domain-<name>/port/`                      |
| JPA Entity                      | `storage/db/<domain>/entity/`              |
| Spring Data JPA Repository      | `storage/db/<domain>/repository/`          |
| QueryDSL 구현체                    | `storage/db/<domain>/querydsl/`            |
| DB Adapter                      | `storage/db/<domain>/adapter/`             |
| Redis Adapter                   | `storage/redis/<domain>/adapter/`          |
| SMS/S3/EventBridge/OAuth Client | `clients/<provider>/`                      |
| 공통 enum, 응답, 예외, 유틸             | `core/`                                    |

---

## Step 2. 도메인 식별

기존 코드가 어느 도메인에 속하는지 판단한다.

| 도메인          | 예시                           |
| ------------ | ---------------------------- |
| `user`       | 유저 프로필, 소셜 계정, 활동 이력         |
| `auth`       | 소셜 로그인, 토큰 발급, 전화번호 인증       |
| `playground` | 게시글, 댓글, 좋아요, 크루 생성/신청/승인/거절 |
| `app`        | 앱 버전, 배너, 팝업, 공지             |
| `admin`      | 어드민 작업, 출석, 백오피스             |

판단 기준:

1. 데이터 생명주기를 소유하는 도메인을 우선한다.
2. 상태 변경의 주체가 되는 도메인을 우선한다.
3. 특정 API가 여러 도메인을 묶는 경우 Facade 후보로 본다.
4. 단순히 사용자 정보를 조회하는 경우 `domain-user`의 조회 서비스 또는 조회 Port 사용을 고려한다.

---

## Step 3. Port 추출 기준

기존 Service 안에 다음 코드가 있으면 Port로 분리한다.

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

Port 구현체는 의존하는 기술에 따라 위치를 결정한다.

| 의존 기술                        | Adapter 위치                               |
| ---------------------------- | ---------------------------------------- |
| JPA, QueryDSL, EntityManager | `storage/db/<domain>/adapter/`           |
| Redis                        | `storage/redis/<domain>/adapter/`        |
| SMS                          | `clients/sms/`                           |
| S3                           | `clients/s3/`                            |
| EventBridge                  | `clients/eventbridge/`                   |
| OAuth Provider               | `clients/oauth/` 또는 provider별 client 패키지 |
| 외부 HTTP API                  | `clients/<provider>/`                    |

Adapter는 domain Port를 구현한다.

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

마이그레이션 시 JPA Entity와 Domain Model을 분리한다.

### JPA Entity

* `storage/db/<domain>/entity/`에 둔다.
* `Entity` 접미사를 붙인다.
* JPA annotation을 가진다.
* DB 매핑 책임을 가진다.

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

* `domain-<name>/` 또는 `domain-<name>/model/`에 둔다.
* JPA annotation을 가지지 않는다.
* 비즈니스 상태와 행위를 표현한다.

```java
public record User(Long id) {
}
```

---

## Step 6. 의존성 규칙

마이그레이션 후 허용되는 의존 방향:

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
* Controller에서 비즈니스 로직 처리

예외:

* `domain-auth`, `domain-playground`, `domain-app`, `domain-admin`에서 `domain-user`의 조회 서비스 또는 조회 Port를 사용하는 것은 허용한다.

---

## Step 7. 마이그레이션 기본 순서

기본적으로 다음 순서로 제안한다.

1. 기존 코드의 책임을 분류한다.
2. 도메인을 식별한다.
3. Controller와 HTTP DTO를 `api`로 이동한다.
4. 비즈니스 로직을 `domain-<name>/service/`로 이동한다.
5. JPA Entity를 `storage/db/<domain>/entity/`로 이동한다.
6. JpaRepository를 `storage/db/<domain>/repository/`로 이동한다.
7. domain service가 필요로 하는 Repository Port를 정의한다.
8. `storage/db/<domain>/adapter/`에서 Repository Port를 구현한다.
9. 외부 시스템 호출이 있으면 외부 기능 Port를 정의한다.
10. `clients/<provider>/`에서 외부 기능 Port를 구현한다.
11. Controller → Service/Facade → Port → Adapter 콜체인을 연결한다.
12. build.gradle.kts 의존성을 정리한다.
13. domain에서 storage/clients/JPA import가 사라졌는지 확인한다.
14. 테스트를 보완한다.

---

## Step 8. build.gradle.kts 점검

마이그레이션 시 다음 방향을 유지한다.

* `api`는 필요한 domain 모듈에 의존할 수 있다.
* `domain-*`은 `core`에만 의존하는 것을 기본으로 한다.
* user 조회가 필요한 도메인은 `domain-user` 의존을 예외적으로 가질 수 있다.
* `storage`는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있다.
* `clients`는 Port 구현을 위해 필요한 domain 모듈에 의존할 수 있다.
* `core`는 다른 프로젝트 모듈에 의존하지 않는다.

예시:

```kotlin
dependencies {
    implementation(project(":domain-user"))
    implementation(project(":core"))
}
```

단, domain 모듈에 `storage`나 `clients` 의존성을 추가하지 않는다.

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

## Step 10. 답변 형식

마이그레이션 설계 답변은 가능한 한 다음 형식을 따른다.

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
```

### 생성할 Port

```java
public interface XxxRepositoryPort {
    ...
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

````

---

## Step 11. 답변 원칙

- 기존 코드의 책임과 이동 위치를 표로 정리한다.
- 마이그레이션 순서는 안전한 단계별 흐름으로 제안한다.
- Port와 Adapter를 반드시 함께 설명한다.
- Entity와 Domain Model의 분리를 명확히 한다.
- 의존성 위반이 발생할 수 있는 지점을 먼저 지적한다.
- build.gradle.kts 수정 방향을 함께 검토한다.
- 기존 기능의 동작을 유지하는 것을 우선한다.
