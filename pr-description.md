## feat: playground 기능 이관 (resolution, project)

### 작업 내용

---

**1. 타임캡솝(resolution) 기능 이관**

- `domain-playground` : `UserResolution`, `UserResolutionLuckyPick`, `ResolutionTag` 도메인 모델 구현
- `domain-user` : `PlaygroundResolutionUserPort` / `PlaygroundResolutionUserAdapter` 추가 (generation, 활동 이력 cross-domain 조회)
- `storage` : `UserResolutionEntity`, `UserResolutionLuckyPickEntity`, 레포지토리, 어댑터 구현
- `api` : `UserResolutionController`, `LuckyPickService` 관련 DTO 구현

**2. project 기능 이관**

- `domain-playground` : `Project`, `ProjectMember`, `ProjectLink` 도메인 모델, 포트 5개, `ProjectService` 구현
- `domain-user` : `PlaygroundProjectUserPort` / `PlaygroundProjectUserAdapter` 추가 (프로젝트 멤버 유저 정보 cross-domain 조회)
- `storage` : JPA 엔티티 3개, JPA 레포지토리 3개, QueryDSL 레포지토리, 어댑터 4개 구현
- `api` : `ProjectController`, DTO, `InfiniteScrollUtil` 구현

**3. 공홈 연동 HTTP 클라이언트 → 내부 Port 전환**

기존에 `domain-official`이 플그 백엔드를 HTTP로 호출하던 방식 제거

| 변경 전 | 변경 후 |
|---|---|
| `PlaygroundClient` → `GET /api/v1/projects` | `OfficialProjectPort.fetchAll()` → `ProjectService` 내부 호출 |
| `PlaygroundClient` → `GET /internal/api/v1/projects/{projectId}` | `OfficialProjectPort.fetchDetail()` → `ProjectService` 내부 호출 |

`clients` 모듈에서 `PlaygroundClient`, `PlaygroundProjectAdapter`, `PlaygroundProperty`, DTO 3개 삭제

### 주요 설계 변경점

- `Member member` → `Long userId` : 도메인이 storage(JPA Entity)에 의존하지 않도록 분리
- `PlatformService`(HTTP) → `PlaygroundResolutionUserPort` / `PlaygroundProjectUserPort` : 외부 API 호출을 내부 DB 조회로 전환
- `hasProfile` → `!user.isFirstLogin()` : 첫 로그인 시 즉시 `completeFirstLogin()` 호출되므로 로그인한 모든 유저는 `isFirstLogin = false`

### 비고

- DB 스키마 변경 없음. 기존 플그 백엔드가 사용하던 테이블(`user_resolution`, `user_resolution_lucky_pick`, `projects`, `project_users`, `links`)을 그대로 매핑

### 확인 필요 사항
- `links.title` 컬럼 실제 저장값이 `"website"` / `"googlePlay"` / `"appStore"` 등 camelCase 형태인지
- `project_users.role` 컬럼 값이 `ProjectMemberRole` enum 이름과 일치하는지
