# sopt-makers-backend Agent Guide

이 프로젝트는 Spring Boot 멀티모듈 구조를 따른다.

- `api`: HTTP 진입점
- `domain-*`: 비즈니스 로직과 Port
- `storage`: DB/Redis 구현체
- `clients`: 외부 시스템 구현체
- `core`: 공통 코드

`domain-*`은 `storage`, `clients`, JPA Entity, JpaRepository에 직접 의존하지 않는다.  
외부 의존성은 domain의 Port로 추상화하고 구현은 `storage` 또는 `clients`에 둔다.  
여러 도메인 조율은 Facade에서 처리한다.  
`core`는 다른 모듈에 의존하지 않는다.

## 상세 Skill

Codex용 Skill:

- 신규 기능 배치/구현 설계: `.codex/skills/implement/SKILL.md`
- legacy 코드 마이그레이션: `.codex/skills/migrate/SKILL.md`
- 의존성/구조 검증: `.codex/skills/check/SKILL.md`

Claude Code용 Skill:

- 신규 기능 배치/구현 설계: `.claude/skills/implement/SKILL.md`
- legacy 코드 마이그레이션: `.claude/skills/migrate/SKILL.md`
- 의존성/구조 검증: `.claude/skills/check/SKILL.md`

Codex 작업에서는 `.codex/skills`를 우선 참고하고, Claude Code 작업에서는 `.claude/skills`를 우선 참고한다.  
두 Skill 디렉토리의 아키텍처 원칙은 동일하게 유지한다.