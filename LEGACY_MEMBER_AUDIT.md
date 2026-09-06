# Step M-0: Legacy Contract & Schema Audit — Member Domain

Source of truth: `sopt-playground-backend/src/main/java/org/sopt/makers/internal/member/` (controller, domain, dto, service, service/sorting). Read-only audit; no legacy files modified.

## A. API Contract Matrix

### A.1 `MemberController` — base path `/api/v1/members`, `@SecurityRequirement(name = "Authorization")` (all endpoints require JWT `Authorization` header unless noted; `@AuthenticationPrincipal Long userId` resolves the caller from the token)

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | GET | `/{id}` | Required | `id` (path, Long) | `MemberResponse` (`id`, `name`, `generation`, `profileImage`, `hasProfile`, `editActivitiesAble`) | 200 |
| 2 | GET | `/me` | Required | none (userId from token) | `MemberInfoResponse` (`id`, `name`, `generation`, `profileImage`, `hasProfile`, `editActivitiesAble`, `hasCoffeeChat`, `hasWorkPreference`, `enableWorkPreferenceEvent`) | 200 |
| 3 | GET | `/search` | Required | `name` (query, String, required) | `List<MemberResponse>` | 200 |
| 4 | GET | `/tl` | Required | none (userId from token, unused for filtering) | `List<TlMemberResponse>` (`id`,`name`,`university`,`profileImage`,`activities[]`,`introduction`,`serviceType`,`selfIntroduction`,`competitionData`) | 200 |
| 5 | GET | `/ask/list` | Required | `part` (query, String, optional) | `AskMemberResponse` (`members[]` → `id,name,profileImage,introduction,latestActivity{generation,part,team},career{companyName,title},isAnswerGuaranteed`) | 200 |
| 6 | POST | `/profile` | Required | `MemberProfileSaveRequest` body — `@Deprecated`, scheduled for removal once FE cutover completes | `MemberProfileResponse` | 201 |
| 7 | PUT | `/profile` | Required | `MemberProfileUpdateRequest` body | `MemberProfileResponse` | 200 |
| 8 | PATCH | `/work-preference` | Required | `WorkPreferenceUpdateRequest` body | `CommonResponse` (`success:Boolean`, `message:String`) | 200 |
| 9 | GET | `/work-preference` | Required | none | `WorkPreferenceResponse` (`workPreference:{ideationStyle,workTime,communicationStyle,workPlace,feedbackStyle}`) | 200 |
| 10 | GET | `/work-preference/recommendations` | Required | none | `WorkPreferenceRecommendationResponse` (`hasWorkPreference:boolean`, `recommendations[]`) | 200 |
| 11 | GET | `/profile/{id}` | Required | `id` (path, Long) | `MemberProfileSpecificResponse` (large profile DTO, see B/careers reorder logic in controller) | 200 |
| 12 | GET | `/profile/me` | Required | none | `MemberProfileSpecificResponse` | 200 |
| 13 | GET | `/profile` | Required | `filter,limit,offset,search,generation,employed,orderBy,mbti,team` (all query, optional) | `MemberAllProfileResponse` (`members: List<MemberProfileResponse>`, `hasNext:Boolean`, `totalMembersCount:Integer`) | 200 |
| 14 | GET | `/recommend/me` | Required | none | `MemberRecommendResponse` (`members[]` max 5, `recommendType` enum) | 200 |
| 15 | GET | `/recommend/{userId}` | Required | `userId` (path, Long) | `MemberRecommendResponse` | 200 |
| 16 | GET | `/recommend/me/generation-part` | Required | none | `SameGenerationAndPartRecommendResponse` (`members[]`) | 200 |
| 17 | GET | `/recommend/{userId}/generation-part` | Required | `userId` (path, Long) | `SameGenerationAndPartRecommendResponse` | 200 |
| 18 | PUT | `/activity/check` | Required | `CheckActivityRequest` body (`isCheck:Boolean`) | `Map<String,Boolean>` — key is the literal Korean sentence `"유저 기수 확인 여부가 변경됐습니다."` → `true` | 200 |
| 19 | GET | `/crew/{id}` | Required | `id` (path, Long), `page,take` (query, optional) | `MemberCrewResponse` (`meetings:List<MemberCrewVo>`, `meta:PaginationMeta`) — proxies `MakersCrewClient` | 200 |
| 20 | DELETE | `/profile/link/{linkId}` | Required | `linkId` (path, Long) | `CommonResponse` | 200 |
| 21 | PATCH | `/block/activate` | Required | `MemberBlockRequest` body (`blockedMemberId:Long`) | `Map<String,Boolean>` — key `"유저 차단 활성 성공"` → `true` | 200 |
| 22 | GET | `/block/{memberId}` | Required | `memberId` (path, Long) | `MemberBlockResponse` (`status:Boolean`, `blockingMember{id,name}`, `blockedMember{id,name}`) | 200 |
| 23 | POST | `/report` | Required | `MemberReportRequest` body (`reportMemberId:Long`) | `Map<String,Boolean>` — key `"유저 신고 성공"` → `true` | 200 |
| 24 | GET | `/property` | Required | none | `MemberPropertiesResponse` (`id,major,job,organization,part[],generation[],coffeeChatStatus,receivedCoffeeChatCount,sentCoffeeChatCount,uploadSopticleCount,uploadReviewCount`) | 200 |

Note: endpoint #6 (`POST /profile`) is annotated `@Deprecated` with comment "프론트 연결 되면 삭제 예정" (pending removal once FE migrates) — must still be preserved in Phase 1 per Golden Rule even though deprecated.

### A.2 `MemberQuestionController` — base path `/api/v1/members`, `@SecurityRequirement(name = "Authorization")`

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | POST | `/questions/{receiverId}` | Required | `receiverId` (path, Long); `QuestionSaveRequest` (`content:String≤2000 NotBlank`, `isAnonymous:Boolean NotNull`) | `Map<String,Long>` → `{"questionId": <id>}` | 201 |
| 2 | PUT | `/questions/{questionId}` | Required | `questionId` (path, Long); `QuestionUpdateRequest` (`content`,`isAnonymous`) | `Map<String,Boolean>` → `{"success": true}` | 200 |
| 3 | DELETE | `/questions/{questionId}` | Required | `questionId` (path, Long) | `Map<String,Boolean>` → `{"success": true}` | 200 |
| 4 | POST | `/questions/{questionId}/answer` | Required | `questionId` (path, Long); `AnswerSaveRequest` (`content:String≤2000 NotBlank`) | `Map<String,Long>` → `{"answerId": <id>}` | 201 |
| 5 | PUT | `/answers/{answerId}` | Required | `answerId` (path, Long); `AnswerUpdateRequest` (`content`) | `Map<String,Boolean>` → `{"success": true}` | 200 |
| 6 | DELETE | `/answers/{answerId}` | Required | `answerId` (path, Long) | `Map<String,Boolean>` → `{"success": true}` | 200 |
| 7 | POST | `/questions/{questionId}/reactions` | Required | `questionId` (path, Long) | `Map<String,Boolean>` → `{"success": true}` (toggle) | 200 |
| 8 | POST | `/answers/{answerId}/reactions` | Required | `answerId` (path, Long) | `Map<String,Boolean>` → `{"success": true}` (toggle) | 200 |
| 9 | POST | `/questions/{questionId}/report` | Required | `questionId` (path, Long); `QuestionReportRequest` (`reason:String`) | `Map<String,Boolean>` → `{"success": true}` | 201 |
| 10 | GET | `/{memberId}/questions` | Required | `memberId` (path, Long); `tab` (query, `QuestionTab`: `answered`/`unanswered`, optional, custom converter defaults null→`ANSWERED`), `page`,`size` (query, Integer, optional; size clamped 1–100, default 10) | `QuestionsResponse` (`questions[]`,`currentPage`,`pageSize`,`totalElements:Long`,`totalPages`,`hasNext`,`hasPrevious`) | 200 |
| 11 | GET | `/me/questions/unanswered-count` | Required | none | `UnansweredCountResponse` (`count:Long`) | 200 |
| 12 | GET | `/{memberId}/questions/my-latest-answered` | Required | `memberId` (path, Long) | `MyLatestAnsweredQuestionLocationResponse` (`questionId,page,index` — all nullable when none found) | 200 |
| 13 | GET | `/{memberId}/questions/{questionId}/location` | Required (no `@AuthenticationPrincipal` used in method, but class-level security still applies) | `memberId`,`questionId` (path, Long) | `QuestionLocationResponse` (`questionId,tab:QuestionTab,page,index`) | 200 |
| 14 | GET | `/questions/latest` | Required (no principal used) | none | `LatestAnsweredQuestionsResponse` (`questions[]` → `receiverId,receiverName,receiverProfileImage,questionId,content,location:QuestionLocationResponse`) | 200 |

`MemberQuestionControllerAdvice` (`@ControllerAdvice(assignableTypes = MemberQuestionController.class)`) registers a custom `PropertyEditor` binding query-param strings to `QuestionTab` via `StringToQuestionTabConverter` — required to keep `tab=answered|unanswered` case-insensitive parsing behavior.

### A.3 `MakersMemberController` — base path `""` (root), no `@SecurityRequirement`

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | GET | `/makers/profile` | **None (public)** | none | `List<MakersMemberProfileResponse>` (`id,name,profileImage,activities:List<MemberSoptActivityResponse>{id,generation},careers:List<MemberCareerResponse>{id,companyName,title,isCurrent}`) | 200 |

## B. DB Schema Mapping Matrix

Verified against actual `@Entity`/`@Table`/`@Column` annotations in the legacy entities. Note: legacy code has **no explicit `@Index` / `@Table(indexes=...)` annotations anywhere in this package** — only PK auto-indexes and the two `@UniqueConstraint`s below exist at the JPA level; any additional index must be a net-new optimization decision, not a "preserve existing index" requirement.

| Target Domain Model (Ask-unified naming) | Legacy Entity Class | Legacy Table | PK | FK Columns | Unique Constraints | Notes |
|---|---|---|---|---|---|---|
| `UserAsk` | `MemberQuestion` | `member_question` | `question_id` (IDENTITY) | `receiver_id` → `users.id` (NOT NULL), `asker_id` → `users.id` (nullable in schema, but effectively always set by `createQuestion`), `anonymous_nickname_id`, `anonymous_profile_image_id` | none | `content` TEXT NOT NULL len 2000; `is_anonymous` NOT NULL; `is_reported` NOT NULL default false; extends `AuditingTimeEntity` (adds `created_at`/`updated_at`) |
| `UserAnswer` | `MemberAnswer` | `member_answer` | `answer_id` (IDENTITY) | `question_id` → `member_question.question_id`, **NOT NULL, UNIQUE** (enforces 1:1 question↔answer) | `question_id` unique | `content` TEXT NOT NULL len 2000; extends `AuditingTimeEntity` |
| `AskReaction` | `QuestionReaction` | `question_reaction` | `reaction_id` (IDENTITY) | `question_id` → `member_question` (NOT NULL), `member_id` → `users` (NOT NULL) | `uk_question_reaction_question_member` on (`question_id`,`member_id`) | extends `AuditingTimeEntity` |
| `AnswerReaction` | `AnswerReaction` | `answer_reaction` | `reaction_id` (IDENTITY) | `answer_id` → `member_answer` (NOT NULL), `member_id` → `users` (NOT NULL) | `uk_answer_reaction_answer_member` on (`answer_id`,`member_id`) | extends `AuditingTimeEntity` |
| `AskReport` | `QuestionReport` | `question_report` | `report_id` (IDENTITY) | `question_id` (scalar Long, **not** a JPA relation — NOT NULL), `reporter_id` (scalar Long, NOT NULL) | none | `reason` nullable; `created_at` set manually via `@PrePersist` (not `AuditingTimeEntity`) |
| `UserBlock` | `MemberBlock` | (table name **not overridden** — defaults to `member_block` via Hibernate physical naming strategy; class name is `MemberBlock`) | `id` (IDENTITY, unannotated column name) | `blocker_id` → `users` (NOT NULL), `blocked_member_id` → `users` (NOT NULL) | none declared (no unique constraint on blocker+blocked pair at DB level — uniqueness only enforced in app logic via `findByBlockerAndBlockedMember` upsert) | `is_blocked` NOT NULL, `ColumnDefault("true")`; extends `AuditingTimeEntity` |
| `UserReport` | `MemberReport` | (table name **not overridden** — defaults to `member_report`) | `id` (IDENTITY) | `reporter_id` → `users` (NOT NULL), `reported_member_id` → `users` (NOT NULL) | none | `reason` TEXT nullable; extends `AuditingTimeEntity`; no uniqueness — a user can report the same target repeatedly |
| `TlUser` | `TlMember` | `appjam_tl_members` | `id` (IDENTITY) | `member_id` → `users` (NOT NULL) | none | `tl_generation` Integer NOT NULL; `service_type` enum (STRING) NOT NULL; `self_introduction` NOT NULL len 2048; `competition_data` NOT NULL len 2048 |

Additional entities in scope but not part of the CLAUDE.md 5.3 mapping table (owned by `domain-user` per the migration rules, but physically colocated in this legacy package — flag for `domain-user` team, not `domain-playground`):
- `Member` → table `users`, PK `id` is a plain `@Id Long` **with no `@GeneratedValue`** (IDs are issued externally, presumably by the auth/SSO system) — columns: `address, university, major, introduction, mbti, mbti_description, soju_capacity, interest, ideal_type, self_introduction, skill, open_to_work, open_to_side_project, allow_official, has_profile, edit_activities_able (default true), openToSoulmate (default false, note mixed-case column name, not snake_case), is_phone_blind (NOT NULL default true), work_preference (jsonb via `JsonType`)`; `links`/`careers` are `@OneToMany` unidirectional (`@JoinColumn(user_id)`, cascade ALL + orphanRemoval).
- `MemberCareer` → table `member_career`; **note the bug/quirk in legacy code**: `@Column(name = "member_id") private Boolean isCurrent;` — the `isCurrent` field is mapped to a column physically named `member_id` (mismatched name, likely a copy-paste artifact), while the real FK to `users` is the separate field `memberId` mapped to `user_id`. This must be preserved byte-for-byte if `member_career` migrates in Phase 1, or explicitly called out as a fix if remapped.
- `MemberLink` → table `member_links`, FK column `user_id`.
- `SoptMemberHistory` → table `sopt_member_history`, no FK relations (flat contact-list table used for join tracking).
- `SoptOrganizer` → no `@Table` override (defaults to `sopt_organizer`), FK `user_id`.
- `WorkPreference` — `@Embeddable`, not a separate table; embedded as JSONB column `work_preference` on `users`.
- `UserFavor` — `@Embeddable`, embedded directly into `users` columns (`is_pour_sauce_lover`, etc.), not JSON.
- `MemberSoptActivity` — **entire class is commented out** (`// package ...`), dead code, not an active entity. Comment says "인증중앙화로 인해 삭제 예정" (scheduled for deletion due to auth centralization). Do not migrate.

## C. Sorting & Filtering Rules (`MemberSortingService` + comparator/strategy packages)

### C.1 Comparator selection priority (`MemberSortingService.selectSortingComparator`)
Only applies when `orderBy` query param is **absent**. Priority, evaluated in order:
1. **Team filter** (`team == "운영팀"` or `team == "미디어팀"`) → `TeamActivityMemberComparator`
2. **Employed filter** (`employed == 1`) → `EmployedMemberComparator`
3. **Otherwise** → `DefaultMemberComparator`

### C.2 Weight-strategy selection (`selectWeightStrategy`)
- `employed == 1` → `EmployedProfileWeightStrategy`
- else → `DefaultProfileWeightStrategy`
(Independent of the comparator choice — e.g. team-filter sorting still uses the employed weight strategy if `employed=1` is also passed.)

### C.3 `DefaultMemberComparator` (tie-break chain)
1. Latest SOPT generation (`lastGeneration`), **descending**
2. Profile weight (`DefaultProfileWeightStrategy` or `EmployedProfileWeightStrategy` per C.2), **descending**
3. Name, Korean/Unicode natural `String.compareTo`, **ascending**

### C.4 `EmployedMemberComparator` (used when `employed=1` and no team filter)
1. Profile weight (`EmployedProfileWeightStrategy`), **descending** — generation tier is **not** considered at all
2. Name, **ascending**

### C.5 `TeamActivityMemberComparator` (used when `team ∈ {운영팀, 미디어팀}`)
1. Most recent generation in which the member belonged to `targetTeam` (derived from `userDetails.soptActivities()`), **descending**. If neither user has any activity record in that team, falls back to overall `lastGeneration` descending. If only one side has team history, that side always sorts first regardless of generation numbers.
2. Profile weight (per C.2), **descending**
3. Name, **ascending**

### C.6 Profile weight strategies (point values)

| Field | `DefaultProfileWeightStrategy` | `EmployedProfileWeightStrategy` |
|---|---|---|
| Profile image present | +5 | +3 |
| Introduction (`member.introduction`) present | +3 | +3 |
| Career entries | +3 each | **+5 each** |
| Links | +1 each | +1 each |
| Birthday/phone/email present (from `InternalUserDetails`) | +1 each | +1 each |
| address/university/major/skill/mbti/mbtiDescription/sojuCapacity/interest/idealType/selfIntroduction present | +1 each | +1 each |
| Each non-null `UserFavor` boolean field (6 fields) | +1 each | +1 each |

The two strategies are otherwise field-identical; only the **career weight (3→5)** and **profile-image weight (5→3)** differ, reflecting that "재직중" (employed) sort prioritizes career completeness over photo presence.

### C.7 Explicit `orderBy` (1–4), overrides all of the above when present (`MemberSortingService.createComparatorByOrderCondition`)
`OrderByCondition.valueOf(Integer)` maps: `1→LATEST_REGISTERED`, `2→OLDEST_REGISTERED`, `3→LATEST_GENERATION`, `4→OLDEST_GENERATION`. The weight strategy (C.2) is still selected based on `employed`, and used as a tie-breaker for options 3/4 only.

1. **`LATEST_REGISTERED`**: sort by `Member.id` **descending** (higher/newer numeric PK first). `null` Member entries sort last.
2. **`OLDEST_REGISTERED`**: sort by `Member.id` **ascending**. `null` Member entries sort last.
3. **`LATEST_GENERATION`**: same 3-step chain as `DefaultMemberComparator` (generation desc → weight desc → name asc).
4. **`OLDEST_GENERATION`**: generation **ascending** → weight desc → name asc.

Note: `filter` (part) and `team`/`employed` continue to act only as **candidate-set filters** (`filterPlatformConditions`, DB filter) — `orderBy` never disables filtering, it only replaces the comparator.

### C.8 Filter semantics recap (`MemberService.getMemberProfiles` / `filterPlatformConditions`)
- `filter` (1–6) maps to a canonical `Part` string (`PLAN,DESIGN,WEB,SERVER,ANDROID,IOS`) via `getMemberPart`, matched against each `SoptActivity.part()` after normalization (`normalizeMemberTabPartFilterActivityPart` folds legacy Korean/English/synonym spellings, e.g. `"PM"→"PLAN"`, `"프론트엔드"→"WEB"`, `"백엔드"→"SERVER"`).
- `team` special values: `"임원진"` (executive) = has a SOPT activity whose team is non-null/non-empty and is **not** `미디어팀`/`운영팀`; `"메이커스"` = either a non-SOPT (Makers) activity, or an activity whose team literally equals `"메이커스"`; any other string = exact `activity.team()` match.
- `generation`, `part`, `team` are all evaluated against the **same** `SoptActivity` entry via `anyMatch` (i.e. a member matches if at least one of their activities satisfies generation+part+team jointly — not independently across different activities).
- `search` matches (case-sensitive substring, `contains`) across name (from platform), `member.university`, or any `MemberCareer.companyName` — first token match wins, OR semantics across fields.
- `mbti` and `employed` are pushed down to `memberProfileQueryRepository.findAllMemberIdsByDbFilters(mbti, employed, search)` as a first-pass DB-level filter (candidate ID list) *before* platform enrichment — `search` is passed into that DB query too, but the code additionally re-filters with `matchesSearchAcrossFields` after platform data is loaded (i.e. search filtering happens twice: once in the DB candidate query, once in-memory against platform+university+career).
- Default `limit` = 30 when absent/≤0; default `offset` = 0 when absent/negative.

## D. Question/Answer Authorization & Notification Matrix

### D.1 Authorization rules (verified in `MemberQuestionService`)

| Action | Rule as implemented | Source |
|---|---|---|
| Create question (self-question guard) | **Currently disabled** — the check `if (askerId.equals(receiverId)) throw BadRequestException` is commented out in `createQuestion`. Self-questioning is technically allowed today. | `MemberQuestionService.createQuestion:73-75` |
| Update question | Only `question.getAsker().getId().equals(userId)` (asker) may update, **and only while `!question.hasAnswer()`** — enforced by `validateQuestionOwner` + explicit `hasAnswer()` check after. Receiver cannot update, ever. | `updateQuestion:107-145` |
| Delete question | `isAsker OR isReceiver`; if `isAsker && question.hasAnswer()` → `BadRequestException` (asker cannot delete an already-answered question); receiver **can always** delete regardless of answer state. Neither asker nor receiver → `ForbiddenException`. | `deleteQuestion:148-164` |
| Create answer | Only `question.getReceiver().getId().equals(userId)`; also blocked if `memberAnswerRetriever.existsByQuestion(question)` (one answer per question, enforced again at app layer in addition to the DB unique constraint on `member_answer.question_id`). | `createAnswer:167-184` |
| Update answer | Only `answer.getQuestion().getReceiver().getId().equals(userId)` — i.e. gated on the **question's receiver**, not an "answer owner" field (there is no separate answerer identity; the answer is implicitly authored by the receiver). | `updateAnswer:186-196` |
| Delete answer | Same receiver-of-question check as update. | `deleteAnswer:198-208` |
| Toggle question/answer reaction | No ownership restriction — any authenticated member may react; idempotent toggle via existence check. | `toggleQuestionReaction/toggleAnswerReaction` |
| Report question | Blocked only if `questionReportRetriever.existsByQuestionIdAndReporterId` already true (duplicate-report guard); no restriction on reporting your own question. | `reportQuestion:234-244` |
| `getMyLatestAnsweredQuestionLocation` | Explicit guard: `userId.equals(receiverId)` → `BadRequestException("자신에게 질문할 수 없습니다.")` (this is the one place a self-target check is actually active). | `getMyLatestAnsweredQuestionLocation:290-294` |

### D.2 Notification branching (`sendQuestionNotification`, `sendAnswerNotification`)

- **Question created** → notify the **receiver**:
  - If `receiver.lastGeneration() == Constant.CURRENT_GENERATION` → in-app **Alarm** via `PushNotificationEvent`(`QuestionNotificationMessage`) published on `ApplicationEventPublisher`.
  - Else → **SMS** via `SmsNotificationService` (`QuestionNotificationSmsMessage`, includes a deep link `https://playground.sopt.org/members/{receiverId}?tab=ask`).
  - **⚠ Discrepancy vs. CLAUDE.md §5.6**: the spec states the comparison should be against "current generation (39기)"; the actual legacy code compares against `Constant.CURRENT_GENERATION`, whose live value in this codebase is **`38`**, not `39`. Confirm the intended production generation number with the team before hardcoding `39` in the new domain — the constant is presumably bumped each new generation, so the *rule* (compare against the platform's "current generation" constant) is what must be preserved, not the literal number 38 or 39.
  - Failure isolation: the **entire block is wrapped in `try { } catch (Exception e) { log.error(...) }`** — any notification failure (Alarm publish or SMS send) is swallowed and logged; it never propagates to roll back the `@Transactional` question-creation.
- **Answer created** → notify the **question's original asker** (`question.getAsker().getId()`), generation-independent — always an in-app **Alarm** (`AnswerNotificationMessage`), never SMS. Same try/catch failure-isolation pattern.
- **Question/Answer report** (`reportQuestion`) — **no Slack or other external notification** is fired for question/answer reports in this legacy code (unlike member-level reports below); it only persists a `QuestionReport` row and flips `MemberQuestion.isReported = true`.
- **Member-level report** (`MemberService.reportUser`, distinct from question report, triggered by `POST /api/v1/members/report`) — sends a Slack message via `SlackClient.postReportMessage`, but **only when `activeProfile.equals("prod")`** (checked via injected `@Value` active-profile string); wrapped in `try { } catch (RuntimeException ex) { log.error(...) }` so a Slack failure never blocks the `MemberReport` DB save, which happens unconditionally after `sendReportToSlack(...)` is called (i.e. the Slack call happens first but its failure doesn't stop the persist that follows).

## E. `/api/v1/members/profile` Top-50 Default-List Caching Proposal

### E.1 What actually executes today for the "cacheable" case
`MemberService.getMemberProfiles` (the endpoint behind `GET /profile`), when `search`, `generation`, `employed`, `mbti`, `team`, `filter`, and `orderBy` are all absent, still does, per request:
1. `memberProfileQueryRepository.findAllMemberIdsByDbFilters(null, null, null)` — full/near-full ID scan.
2. `platformService.getInternalUsers(allFilteredIds)` — bulk call to the platform/user service for **every** member (not just the page), because filtering/sorting depend on `InternalUserDetails` (name, birthday, phone, email, `soptActivities`) for the whole candidate set.
3. `memberRepository.findAllByIdIn(...)` — bulk DB load of `Member` (Playground-owned columns) for the whole candidate set, to compute profile weight (C.6) and search matching.
4. In-memory sort over the **entire** member population using `DefaultMemberComparator` (C.3), then `.skip(offset).limit(limit)`.
5. For only the final page (≤50 members here), `memberQuestionRetriever.findLatestRecentQuestionsByReceiverIds(...)` (recent-question preview, 7-day window) and `coffeeChatRetriever.existsCoffeeChat(member)` per member.

So today's "default view" is **not actually cheap** — steps 1–4 touch the whole member base on every request regardless of `limit`/`offset`, and the target condition (`offset+limit<=50`, no search/filter/orderBy) is exactly the highest-traffic, most-repeated query shape (it's the default landing view of the directory).

### E.2 Why naive caching is insufficient
- **Plain ID-list caching** (cache the sorted `List<Long>` of the top 50) still requires re-fetching `InternalUserDetails` + `Member` + coffeechat + question-preview for all 50 IDs on every request — it only saves the expensive full-population sort (steps 1–4), not the per-member enrichment (step 5), which is exactly the data that changes most often (coffeechat toggle, new question in the last 7 days).
- **Full DTO caching** (cache the finished `List<MemberProfileResponse>`) is fast to serve but goes stale immediately on: a profile edit (`PUT /profile`), a coffeechat activation toggle, or any new question landing inside the 7-day preview window for one of the 50 cached members — all of which are unrelated to the ranking itself and happen far more frequently than the ranking (generation/weight/name) changes.

### E.3 Proposed structure — two-tier cache, split along volatility, not along "ID vs DTO"

**Tier 1 — Ranked ID list (`members:top50:ids`)**
- Cache key: a single well-known key (no variant needed, since this tier only exists for the zero-filter/zero-search/no-orderBy/`offset+limit<=50` case).
- Value: ordered `List<Long>` of member IDs (the sorted population, or at least enough of it to satisfy any `offset+limit<=50` request — i.e. store top 50, and any request whose `offset+limit` exceeds 50 bypasses the cache entirely and falls through to the live path, per the stated scope).
- TTL: 10 minutes (per CLAUDE.md default), **plus event-driven eviction** on any write that can change the ranking: profile save/update (`PUT/POST /profile`, since it changes profile weight), and SOPT-activity/generation changes if those are ever mutated from within this service. Do **not** evict on coffeechat toggle or new-question events — those don't affect ranking.
- Rationale: the ranking (generation, static/semi-static profile completeness) changes far less often than presence/coffeechat/question-preview, so this tier can tolerate a longer effective staleness window and gets the largest traffic-shedding benefit (it's what protects against a spike, since it removes the O(N) fetch+sort from the hot path).

**Tier 2 — Per-member "directory card" cache (`member:card:{id}`)**
- Cache key: per member ID, holding the **static/slow-changing subset** only — i.e. everything sourced from `Member` (address, university, mbti, links, careers, etc.) and the *static part* of `InternalUserDetails` (name, birthday, profileImage, soptActivities) needed to render `MemberProfileResponse` minus `isCoffeeChatActivate` and `questionPreview`.
- TTL: longer, e.g. 30–60 minutes, since this changes only on explicit profile edits.
- Eviction: **event-driven** — evict `member:card:{id}` synchronously (same transaction commit hook, e.g. `TransactionSynchronization.afterCommit`) whenever `PUT /profile`, `POST /profile`, `PATCH /work-preference`, or `DELETE /profile/link/{linkId}` succeeds for that `id`. This keeps profile edits instantly visible (no TTL wait) while still shielding the read path from repeat cost.
- The two **highest-volatility fields — `isCoffeeChatActivate` and `questionPreview`** — are **never cached**; they are always fetched live per request for the (at most 50) members on the current page, exactly as today (step 5 above). This is the deliberate trade-off: these two fields are cheap to bulk-fetch for ≤50 IDs (a single `IN (...)` query each) and are the fields users most expect to see update immediately (a coffeechat toggle or a brand-new question should never be masked by a cache).

**Request flow for the qualifying case:**
1. Try Tier 1 → get ranked ID list; if miss, run the full pipeline once (protected by a per-key mutex/`SETNX` lock to avoid thundering-herd recompute on cache expiry) and populate Tier 1.
2. Slice `[offset, offset+limit)` from the ID list.
3. For those ≤50 IDs, batch-`GET` Tier 2 (`MGET`); for any miss, load from `Member`/`InternalUserDetails` and backfill Tier 2 individually.
4. Always live-fetch `isCoffeeChatActivate` (bulk) and `questionPreview` (bulk, already batched today) for exactly those IDs, and merge into the Tier-2 payload before returning.

### E.4 Eviction policy summary

| Event | Tier 1 (ranking) | Tier 2 (per-member card) |
|---|---|---|
| TTL expiry | 10 min | 30–60 min |
| Profile save/update (`PUT`/`POST /profile`) | Evict (weight may have changed) | Evict for that member ID |
| Link delete (`DELETE /profile/link/{id}`) | No effect (link count affects weight — technically should evict; treat as part of "profile update" evictions) | Evict for that member ID |
| Work preference update | No effect (not part of ranking or `MemberProfileResponse` fields shown in listing) | No effect |
| Coffeechat on/off | No effect | No effect (field is never cached — always live) |
| New question posted | No effect | No effect (field is never cached — always live) |
| SOPT activity/generation change (if/when mutable here) | Evict | Evict for that member ID |

### E.5 Technical rationale
This hybrid avoids the two failure modes named in the brief: it protects against traffic spikes because the expensive full-population sort (the true O(N) cost driver, not the per-row DTO assembly) is the part that's cached longest and shielded by a recompute lock, while it protects real-time correctness because the two fields users are most sensitive to seeing "live" (coffeechat status, latest question) are structurally excluded from any cache tier and always read fresh for the small (≤50) page being rendered. It also confines cache invalidation to precise, event-driven per-ID eviction on the write paths that actually affect the cached fields, rather than either a coarse whole-cache flush or a purely TTL-only strategy that would otherwise force a choice between staleness and thrashing.
