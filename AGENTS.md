# AGENTS.md

## Purpose

This file defines the project architecture and implementation rules for AI coding agents working on this repository.

This project is a Spring Boot multi-module backend.
The architecture is a domain-oriented modular monolith.

The root project is not an executable application.
The `api` module is the only executable Spring Boot application module.

## Project Structure

```text
root-project
├── api
├── domain
│   ├── domain-user
│   ├── domain-auth
│   ├── domain-playground
│   ├── domain-crew
│   ├── domain-app
│   └── domain-admin
├── storage
├── clients
└── core
```

## Module Responsibilities

### api

The `api` module is the application entry point.

Responsibilities:

- Spring Boot main application class
- REST controllers
- HTTP request/response DTOs
- Authentication and authorization configuration
- Security filters
- WebMvc configuration
- Swagger/OpenAPI configuration
- Argument resolvers such as `@CurrentUserId`
- Global exception handling for HTTP responses
- Calling domain services or facades

The `api` module may depend on:

- `domain-*`
- `storage`
- `clients`
- `core`

Rules:

- Controllers must not contain business logic.
- Controllers should validate HTTP-level input, convert request data, call domain services/facades, and return responses.
- Controllers must not call JPA repositories directly.
- Controllers must not call external client implementations directly.
- Controllers may call a domain service directly when the use case belongs to a single domain.
- Controllers may call a facade when the use case coordinates multiple domains or external capabilities.

### domain

The `domain` directory contains domain-specific business modules.

Domain modules:

```text
domain
├── domain-user
├── domain-auth
├── domain-playground
├── domain-crew
├── domain-app
└── domain-admin
```

Each domain module owns its own business logic.

Responsibilities:

- Domain services
- Use cases
- Domain models
- Domain-specific exceptions
- Repository port interfaces
- Client port interfaces
- Domain-level DTOs when necessary
- Facades only when orchestration is needed inside the domain module

Domain modules must not depend on implementation modules such as `storage` or `clients`.

### domain-user

`domain-user` is a base domain module.

Other domain modules may depend on `domain-user` only when they need user-related business capabilities.

Allowed examples:

- Checking whether a user exists
- Finding a user by id
- Reading minimal user information needed by another domain
- Validating user status when required by a business flow

Use Query/Command separation only for the user domain at the initial stage.

Use:

- `UserQueryService`: read-only user lookup, existence check, and minimal user information retrieval.
- `UserCommandService`: user state changes such as profile update, withdrawal, status change, or user creation.

Other domain modules should depend on `UserQueryService` when they need user information.

Other domain modules should not depend on `UserCommandService` unless there is a clear user-domain behavior that must be triggered.

Forbidden examples:

- Directly using `UserEntity`
- Directly using JPA repositories
- Modifying user state from another domain without going through user-domain behavior

### domain-auth

Responsible for authentication-related business logic.

Examples:

- Social login
- Token issuing logic
- Phone number verification
- Authentication use cases

If SMS sending is needed, define an external capability port in `domain-auth`, such as `SmsSender`.
The actual implementation must be placed in `clients.sms`.

### domain-playground

Responsible for playground-related business logic.

Examples:

- Playground posts
- Comments
- Likes
- Playground-specific user interactions

### domain-crew

Responsible for crew-related business logic.

Examples:

- Crew creation
- Crew application
- Crew participation
- Crew approval/rejection
- Crew status management

### domain-app

Responsible for app-service business logic.

Examples:

- App versions
- Banners
- Popups
- App-level notices
- App configuration exposed to clients

If file upload is needed, define a capability port in the relevant domain.
The actual S3 implementation must be placed in `clients.s3`.

### domain-admin

Responsible for admin-specific business logic.

Examples:

- Admin operations
- Admin permissions
- Backoffice use cases
- Admin-only workflows

Admin API controllers should still be placed in the `api` module.
Admin business logic should be placed in `domain-admin`.

### storage

The `storage` module contains persistence and data-store implementation details.

Keep `storage` as a single module for now.
Do not split it into `storage-db` or `storage-redis` unless the module becomes too large.

Recommended package structure:

```text
storage
└── src/main/java/org/sopt/makers/storage
    ├── db
    │   ├── entity
    │   ├── repository
    │   ├── querydsl
    │   └── adapter
    └── redis
        ├── cache
        ├── lock
        └── adapter
```

Responsibilities:

- JPA entities
- Spring Data JPA repositories
- QueryDSL implementations
- Persistence adapters
- Redis cache implementations
- Redisson distributed lock implementations

The `storage` module implements repository port interfaces defined in domain modules.

Example:

```text
domain-user
└── UserRepositoryPort

storage
└── db.adapter.UserRepositoryAdapter implements UserRepositoryPort
```

Domain modules must not depend on `storage`.

### clients

The `clients` module contains external system integration implementations.

The `clients` module is intentionally kept as a single module because it is not large yet.
Do not split it into `client-sms`, `client-s3`, or `client-eventbridge` at this stage.

Recommended package structure:

```text
clients
└── src/main/java/org/sopt/makers/clients
    ├── sms
    ├── s3
    └── eventbridge
```

Responsibilities:

- SMS provider integration
- AWS S3 file upload/download/delete integration
- AWS EventBridge event publishing integration
- External API request/response mapping
- External client configuration

The `clients` module implements client capability ports defined in domain modules.

Examples:

```text
domain-auth
└── SmsSender

clients.sms
└── SmsSenderAdapter implements SmsSender
```

```text
domain-app
└── FileUploader

clients.s3
└── S3FileUploaderAdapter implements FileUploader
```

```text
domain-crew
└── EventPublisher

clients.eventbridge
└── EventBridgeEventPublisherAdapter implements EventPublisher
```

Domain modules must not depend on `clients`.

### core

The `core` module contains shared pure code.

Recommended package structure:

```text
core
└── src/main/java/org/sopt/makers/core
    ├── type
    ├── dto
    └── exception
```

Responsibilities:

- Common enums
- Common DTOs
- Common exception base classes
- Error codes
- API response wrapper if shared globally
- Common utility classes only when truly shared

The `core` module must not depend on any other project module.

Keep `core` small.
Do not put domain-specific logic in `core`.

## Dependency Rules

### Allowed Dependencies

```text
api → domain-*
api → storage
api → clients
api → core

domain-* → core

domain-auth → domain-user
domain-playground → domain-user
domain-crew → domain-user
domain-app → domain-user
domain-admin → domain-user

storage → domain-* only to implement domain port interfaces
storage → core

clients → domain-* only to implement domain capability ports
clients → core
```

### Forbidden Dependencies

```text
core → api
core → domain-*
core → storage
core → clients

domain-* → api
domain-* → storage
domain-* → clients

storage → api
clients → api

domain-auth → domain-crew
domain-auth → domain-playground
domain-auth → domain-app
domain-auth → domain-admin

domain-crew → domain-auth
domain-crew → domain-playground
domain-crew → domain-app
domain-crew → domain-admin

domain-playground → domain-auth
domain-playground → domain-crew
domain-playground → domain-app
domain-playground → domain-admin

domain-app → domain-auth
domain-app → domain-crew
domain-app → domain-playground
domain-app → domain-admin

domain-admin → domain-auth
domain-admin → domain-crew
domain-admin → domain-playground
domain-admin → domain-app
```

### Domain-to-Domain Rule

Domain modules must not directly depend on each other by default.

Exception:

```text
Other domain modules may depend on domain-user.
```

Reason:

`domain-user` is treated as a base domain because user information is commonly needed across authentication, crew, playground, app, and admin workflows.

Even with this exception, other domain modules should depend on `UserQueryService` for read-only user lookup.
They must not directly access user persistence implementation details.

## Service Naming and Query/Command Policy

Apply Query/Command separation only to the `domain-user` module at the initial stage.

Reason:

- `domain-user` is a base domain that can be referenced by other domain modules.
- Other domains usually need read-only user lookup.
- Separating user query and command responsibilities prevents other domains from depending on user mutation operations unnecessarily.

Use:

- `UserQueryService`: read-only user lookup, existence check, and minimal user information retrieval.
- `UserCommandService`: user state changes such as profile update, withdrawal, status change, or user creation.

For other domains, use a single service class by default.

Examples:

- `AuthService`
- `CrewService`
- `PlaygroundService`
- `AppService`
- `AdminService`

Split other domain services into Query/Command services only when the service grows large enough or read/write responsibilities become clearly separated.

## Facade Policy

Use Facade classes only when orchestration across multiple domains or external systems is required.

Examples:

- `AuthFacade`: combines auth logic, user creation/query, token issuing, and SMS sending.
- `CrewFacade`: combines user lookup, crew business logic, and event publishing.
- `AppFacade`: combines app business logic and file upload if the use case requires coordination.

Facade rules:

- Facades may coordinate multiple domain services.
- Facades may coordinate domain services and external capability ports.
- Facades should not contain core domain rules.
- Domain rules should remain inside each domain service.
- Controllers may call facades when a use case spans multiple domains.
- Controllers may call domain services directly when the use case belongs to a single domain.
- Do not create a facade when a single domain service is enough.

## Naming Convention

### Service

Use `Service` for domain business logic.

Examples:

- `UserQueryService`
- `UserCommandService`
- `AuthService`
- `CrewService`
- `PlaygroundService`
- `AppService`
- `AdminService`

A service should express business use cases and domain rules.

### Facade

Use `Facade` for orchestration across multiple domain services or external capabilities.

Examples:

- `AuthFacade`
- `CrewFacade`
- `AppFacade`

A facade should coordinate a flow.
It should not become the owner of core domain rules.

### External Capability Ports

Use role-based names for ports that represent external capabilities.

Examples:

- `SmsSender`
- `FileUploader`
- `EventPublisher`

Do not use broad names for external capabilities.

Avoid:

- `SmsService`
- `FileService`
- `EventService`

Reason:

These names are too broad and do not clearly express the single external capability being used.

Prefer capability-oriented names:

```text
SmsSender       # sends SMS messages
FileUploader    # uploads files
EventPublisher  # publishes events
```

### Adapter

Use the `Adapter` suffix for infrastructure implementations of domain ports.

Examples:

- `UserRepositoryAdapter`
- `SmsSenderAdapter`
- `S3FileUploaderAdapter`
- `EventBridgeEventPublisherAdapter`

Reason:

The suffix makes it clear that the class adapts an infrastructure technology to a domain-defined port.

## Port and Adapter Rule

Use a port-and-adapter style for infrastructure dependencies.

Domain modules define ports.
Infrastructure modules implement ports.

Examples:

### Repository Port

In `domain-user`:

```java
public interface UserRepositoryPort {
    Optional<User> findById(Long userId);
    boolean existsById(Long userId);
}
```

In `storage`:

```java
@Repository
public class UserRepositoryAdapter implements UserRepositoryPort {
    // Uses Spring Data JPA repository internally
}
```

### Client Capability Port

In `domain-auth`:

```java
public interface SmsSender {
    void send(String phoneNumber, String message);
}
```

In `clients.sms`:

```java
@Component
public class SmsSenderAdapter implements SmsSender {
    // Calls external SMS provider
}
```

### Event Publishing Port

In a domain module:

```java
public interface EventPublisher {
    void publish(String eventName, Object payload);
}
```

In `clients.eventbridge`:

```java
@Component
public class EventBridgeEventPublisherAdapter implements EventPublisher {
    // Publishes events to AWS EventBridge
}
```

## Package Convention

Base package:

```text
org.sopt.makers
```

Recommended package names:

```text
org.sopt.makers.api
org.sopt.makers.domain.user
org.sopt.makers.domain.auth
org.sopt.makers.domain.playground
org.sopt.makers.domain.crew
org.sopt.makers.domain.app
org.sopt.makers.domain.admin
org.sopt.makers.storage
org.sopt.makers.clients
org.sopt.makers.core
```

Do not mix `com.sopt` and `org.sopt.makers`.
Use `org.sopt.makers` consistently.

## API Module Rules

Controllers should be placed in the `api` module.

Recommended package structure:

```text
api
└── src/main/java/org/sopt/makers/api
    ├── common
    │   ├── security
    │   ├── config
    │   ├── resolver
    │   └── exception
    └── controller
        ├── user
        ├── auth
        ├── playground
        ├── crew
        ├── app
        └── admin
```

Rules:

- Controllers call domain services or facades.
- Controllers must not call JPA repositories directly.
- Controllers must not call clients directly.
- Controllers must not contain business logic.
- HTTP request/response DTOs should stay in `api` unless they are used by multiple layers intentionally.
- Authentication principal resolution such as `@CurrentUser` belongs in `api.common.resolver`.
- Security filters and Spring Security configuration belong in `api.common.security`.

## Storage Rules

JPA entities belong in `storage`, not in domain modules.

Rules:

- Do not expose JPA entities to `api`.
- Do not expose JPA entities as domain models unless explicitly intended.
- Use adapters to map between JPA entities and domain models.
- Spring Data repositories should stay inside `storage`.
- QueryDSL classes should stay inside `storage`.
- Redis implementation details should stay inside `storage.redis`.

## Clients Rules

External integration details belong in `clients`.

Rules:

- SMS implementation belongs in `clients.sms`.
- S3 implementation belongs in `clients.s3`.
- EventBridge implementation belongs in `clients.eventbridge`.
- External API DTOs should stay in `clients` unless there is a strong reason to expose them.
- Domain modules should define interfaces for external capabilities.
- `clients` implements those interfaces.
- Do not split `clients` into separate modules yet because the current size does not justify the added Gradle/module complexity.

## Core Rules

The `core` module should be pure and stable.

Allowed examples:

- `BaseException`
- `ErrorCode`
- Common enum types
- Common response wrapper
- Shared constants that are truly global

Forbidden examples:

- User business logic
- Authentication business logic
- Crew business logic
- JPA entities
- Spring Data repositories
- External client implementations
- Controller classes

## Gradle Module Policy

Use Gradle multi-module structure.

The root project should mainly contain shared Gradle configuration.

The executable Spring Boot plugin should be applied only to the `api` module.

Domain, storage, clients, and core modules should be library modules.

Expected module includes:

```kotlin
include(
    "api",
    "domain:domain-user",
    "domain:domain-auth",
    "domain:domain-playground",
    "domain:domain-crew",
    "domain:domain-app",
    "domain:domain-admin",
    "storage",
    "clients",
    "core"
)
```

## Implementation Priorities

When creating or modifying code, follow this order:

1. Identify the owning domain.
2. Put business logic in the correct `domain-*` module.
3. Use `UserQueryService` for user lookup from other domains.
4. Define repository ports in the domain module for persistence.
5. Define external capability ports in the domain module for external systems.
6. Implement persistence ports in `storage`.
7. Implement external capability ports in `clients`.
8. Use a facade only when the use case coordinates multiple domains or external systems.
9. Expose HTTP endpoints in `api`.
10. Put only truly shared code in `core`.

## Do Not Do

- Do not place business logic in controllers.
- Do not place JPA entities in domain modules.
- Do not make domain modules depend on `storage`.
- Do not make domain modules depend on `clients`.
- Do not make unrelated domain modules depend on each other.
- Do not split `clients` into multiple modules yet.
- Do not split `storage` into multiple modules yet unless explicitly requested.
- Do not put domain-specific code in `core`.
- Do not use `com.sopt` package names.
- Do not expose infrastructure DTOs to domain modules.
- Do not name external capability ports as broad services such as `EventService`, `SmsService`, or `FileService`.
