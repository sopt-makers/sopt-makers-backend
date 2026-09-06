# Step P-0: Legacy Contract & Schema Audit — Full Playground Domain

Source of truth: `sopt-playground-backend/src/main/java/org/sopt/makers/internal/{member, community, vote, wordchaingame, internal}/`. Read-only audit; no legacy files modified. Companion document: `LEGACY_MEMBER_AUDIT.md` (Member domain deep-dive on sorting/caching — Section 1 below summarizes and cross-references it rather than duplicating it in full).

Scope: Community (posts/comments/reactions/reports/categories/anonymous), Member/User (profile, sorting, Ask Q&A, block/report), Vote, WordChain, InternalOpenApi.

## 0. Executive Summary — Endpoint Tally

| Domain | Controller | Endpoint Count |
|---|---|---|
| Member/User | `MemberController` | 24 |
| Member/User | `MemberQuestionController` | 14 |
| Member/User | `MakersMemberController` | 1 |
| **Member/User subtotal** | | **39** |
| Community | `CommunityController` (excl. vote endpoint, see Vote row) | 14 |
| Community | `CommunityCategoryController` | 1 |
| Community | `CommunityCommentController` | 7 |
| **Community subtotal** | | **22** |
| Vote | `CommunityController` (`POST /posts/{postId}/vote` — no dedicated controller) | 1 |
| WordChain | `WordChainGameController` | 4 |
| InternalOpenApi | `InternalOpenApiController` | 9 |
| **Grand total** | | **75** |

Note on double-hosting: the single Vote-mutation endpoint physically lives inside `CommunityController` (`community/controller/CommunityController.java`) but is tallied under Vote since it is Vote's only public contract surface. `CommunityController`'s comment-delete endpoint (`DELETE /api/v1/community/comment/{commentId}`) duplicates `CommunityCommentController`'s `DELETE /api/v1/community/{postId}/comment/{commentId}` — both are live, both call the same service method, and both are counted separately above because they are distinct URL contracts that must each be frozen under the Golden Rule.

---

## 1. Member / User Domain (39 endpoints)

Full sorting-comparator logic (C.1–C.8), Ask/Answer authorization & notification matrix (D.1–D.2), and the Top-50 caching proposal (E.1–E.5) are documented exhaustively in **`LEGACY_MEMBER_AUDIT.md`** — refer there for migration work on those areas. This section reproduces the endpoint inventory (A) and schema mapping (B) for completeness of the full-domain tally.

### 1.A API Contract Matrix

#### 1.A.1 `MemberController` — base path `/api/v1/members`, `@SecurityRequirement(name = "Authorization")`

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | GET | `/{id}` | Required | `id` (path, Long) | `MemberResponse` (`id`, `name`, `generation`, `profileImage`, `hasProfile`, `editActivitiesAble`) | 200 |
| 2 | GET | `/me` | Required | none | `MemberInfoResponse` (`id`,`name`,`generation`,`profileImage`,`hasProfile`,`editActivitiesAble`,`hasCoffeeChat`,`hasWorkPreference`,`enableWorkPreferenceEvent`) | 200 |
| 3 | GET | `/search` | Required | `name` (query, String, required) | `List<MemberResponse>` | 200 |
| 4 | GET | `/tl` | Required | none | `List<TlMemberResponse>` (`id,name,university,profileImage,activities[],introduction,serviceType,selfIntroduction,competitionData`) | 200 |
| 5 | GET | `/ask/list` | Required | `part` (query, optional) | `AskMemberResponse` (`members[]` → `id,name,profileImage,introduction,latestActivity{generation,part,team},career{companyName,title},isAnswerGuaranteed`) | 200 |
| 6 | POST | `/profile` | Required | `MemberProfileSaveRequest` — `@Deprecated`, pending FE cutover | `MemberProfileResponse` | 201 |
| 7 | PUT | `/profile` | Required | `MemberProfileUpdateRequest` | `MemberProfileResponse` | 200 |
| 8 | PATCH | `/work-preference` | Required | `WorkPreferenceUpdateRequest` | `CommonResponse` (`success,message`) | 200 |
| 9 | GET | `/work-preference` | Required | none | `WorkPreferenceResponse` (`workPreference:{ideationStyle,workTime,communicationStyle,workPlace,feedbackStyle}`) | 200 |
| 10 | GET | `/work-preference/recommendations` | Required | none | `WorkPreferenceRecommendationResponse` (`hasWorkPreference,recommendations[]`) | 200 |
| 11 | GET | `/profile/{id}` | Required | `id` (path) | `MemberProfileSpecificResponse` | 200 |
| 12 | GET | `/profile/me` | Required | none | `MemberProfileSpecificResponse` | 200 |
| 13 | GET | `/profile` | Required | `filter,limit,offset,search,generation,employed,orderBy,mbti,team` (all optional) | `MemberAllProfileResponse` (`members[],hasNext,totalMembersCount`) | 200 |
| 14 | GET | `/recommend/me` | Required | none | `MemberRecommendResponse` (`members[]` max 5, `recommendType`) | 200 |
| 15 | GET | `/recommend/{userId}` | Required | `userId` (path) | `MemberRecommendResponse` | 200 |
| 16 | GET | `/recommend/me/generation-part` | Required | none | `SameGenerationAndPartRecommendResponse` (`members[]`) | 200 |
| 17 | GET | `/recommend/{userId}/generation-part` | Required | `userId` (path) | `SameGenerationAndPartRecommendResponse` | 200 |
| 18 | PUT | `/activity/check` | Required | `CheckActivityRequest{isCheck}` | `Map<String,Boolean>` key `"유저 기수 확인 여부가 변경됐습니다."` | 200 |
| 19 | GET | `/crew/{id}` | Required | `id` (path), `page,take` (optional) | `MemberCrewResponse{meetings,meta}` — proxies `MakersCrewClient` | 200 |
| 20 | DELETE | `/profile/link/{linkId}` | Required | `linkId` (path) | `CommonResponse` | 200 |
| 21 | PATCH | `/block/activate` | Required | `MemberBlockRequest{blockedMemberId}` | `Map<String,Boolean>` key `"유저 차단 활성 성공"` | 200 |
| 22 | GET | `/block/{memberId}` | Required | `memberId` (path) | `MemberBlockResponse{status,blockingMember{id,name},blockedMember{id,name}}` | 200 |
| 23 | POST | `/report` | Required | `MemberReportRequest{reportMemberId}` | `Map<String,Boolean>` key `"유저 신고 성공"` | 200 |
| 24 | GET | `/property` | Required | none | `MemberPropertiesResponse{id,major,job,organization,part[],generation[],coffeeChatStatus,receivedCoffeeChatCount,sentCoffeeChatCount,uploadSopticleCount,uploadReviewCount}` | 200 |

#### 1.A.2 `MemberQuestionController` — base path `/api/v1/members`, `@SecurityRequirement(name = "Authorization")`

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | POST | `/questions/{receiverId}` | Required | `receiverId` (path); `QuestionSaveRequest{content≤2000 NotBlank, isAnonymous NotNull}` | `Map<String,Long>` `{"questionId": id}` | 201 |
| 2 | PUT | `/questions/{questionId}` | Required | `questionId` (path); `QuestionUpdateRequest{content,isAnonymous}` | `Map<String,Boolean>` `{"success": true}` | 200 |
| 3 | DELETE | `/questions/{questionId}` | Required | `questionId` (path) | `Map<String,Boolean>` `{"success": true}` | 200 |
| 4 | POST | `/questions/{questionId}/answer` | Required | `questionId` (path); `AnswerSaveRequest{content≤2000 NotBlank}` | `Map<String,Long>` `{"answerId": id}` | 201 |
| 5 | PUT | `/answers/{answerId}` | Required | `answerId` (path); `AnswerUpdateRequest{content}` | `Map<String,Boolean>` `{"success": true}` | 200 |
| 6 | DELETE | `/answers/{answerId}` | Required | `answerId` (path) | `Map<String,Boolean>` `{"success": true}` | 200 |
| 7 | POST | `/questions/{questionId}/reactions` | Required | `questionId` (path) | `Map<String,Boolean>` `{"success": true}` (toggle) | 200 |
| 8 | POST | `/answers/{answerId}/reactions` | Required | `answerId` (path) | `Map<String,Boolean>` `{"success": true}` (toggle) | 200 |
| 9 | POST | `/questions/{questionId}/report` | Required | `questionId` (path); `QuestionReportRequest{reason}` | `Map<String,Boolean>` `{"success": true}` | 201 |
| 10 | GET | `/{memberId}/questions` | Required | `memberId` (path); `tab`(`answered`/`unanswered`, default `ANSWERED`), `page,size` (size clamped 1–100, default 10) | `QuestionsResponse{questions[],currentPage,pageSize,totalElements,totalPages,hasNext,hasPrevious}` | 200 |
| 11 | GET | `/me/questions/unanswered-count` | Required | none | `UnansweredCountResponse{count}` | 200 |
| 12 | GET | `/{memberId}/questions/my-latest-answered` | Required | `memberId` (path) | `MyLatestAnsweredQuestionLocationResponse{questionId,page,index}` (all nullable) | 200 |
| 13 | GET | `/{memberId}/questions/{questionId}/location` | Required | `memberId,questionId` (path) | `QuestionLocationResponse{questionId,tab,page,index}` | 200 |
| 14 | GET | `/questions/latest` | Required | none | `LatestAnsweredQuestionsResponse{questions[]→receiverId,receiverName,receiverProfileImage,questionId,content,location}}` | 200 |

`MemberQuestionControllerAdvice` registers `StringToQuestionTabConverter` for case-insensitive `tab=answered|unanswered` binding.

#### 1.A.3 `MakersMemberController` — base path `""` (root), no `@SecurityRequirement`

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | GET | `/makers/profile` | **None (public)** | none | `List<MakersMemberProfileResponse>{id,name,profileImage,activities:List<{id,generation}>,careers:List<{id,companyName,title,isCurrent}>}` | 200 |

### 1.B DB Schema Mapping Matrix (summary)

| Target Domain Model | Legacy Entity | Legacy Table | PK | Key Notes |
|---|---|---|---|---|
| `UserAsk` | `MemberQuestion` | `member_question` | `question_id` | `receiver_id`,`asker_id`→`users`; `is_reported` default false |
| `UserAnswer` | `MemberAnswer` | `member_answer` | `answer_id` | `question_id` UNIQUE (1:1 with question) |
| `AskReaction` | `QuestionReaction` | `question_reaction` | `reaction_id` | UK `(question_id, member_id)` |
| `AnswerReaction` | `AnswerReaction` | `answer_reaction` | `reaction_id` | UK `(answer_id, member_id)` |
| `AskReport` | `QuestionReport` | `question_report` | `report_id` | scalar `question_id`/`reporter_id`, no FK relation |
| `UserBlock` | `MemberBlock` | `member_block` (implicit) | `id` | no unique constraint on `(blocker,blocked)` pair at DB level |
| `UserReport` | `MemberReport` | `member_report` (implicit) | `id` | no uniqueness — repeat reports allowed |
| `TlUser` | `TlMember` | `appjam_tl_members` | `id` | `member_id`→`users` |
| (domain-user owned) | `Member` | `users` | `id` (no `@GeneratedValue`, externally issued) | `work_preference` JSONB; `openToSoulmate` mixed-case column |
| (domain-user owned) | `MemberCareer` | `member_career` | — | **bug**: `isCurrent` mapped to column literally named `member_id`; real FK is separate `memberId`→`user_id` |
| (domain-user owned) | `MemberLink` | `member_links` | — | FK `user_id` |
| (dead code) | `MemberSoptActivity` | — | — | entire class commented out, do not migrate |

See `LEGACY_MEMBER_AUDIT.md` §B for full column-level detail, §C for the 4-tier sorting/weight/orderBy rules, §D for Ask/Answer authorization + notification branching (Alarm vs SMS by current-generation match; report Slack prod-only), and §E for the proposed 2-tier Top-50 cache design.

---

## 2. Community Domain (22 endpoints)

### 2.A API Contract Matrix

#### 2.A.1 `CommunityController` — base path `/api/v1/community`, `@SecurityRequirement(name = "Authorization")`

| # | Method | Path | Auth | Params / Body DTO | Response Type (fields) | Status |
|---|--------|------|------|--------------------|--------------------------|--------|
| 1 | GET | `/posts/{postId}` | userId | `postId` path; `isBlockOn` (default true) | `PostDetailResponse{member,posts,category,isMine,isLiked,likes,anonymousProfile}` | 200 |
| 2 | GET | `/posts` | userId | `category`(required: FREE\|PROMOTION\|SOPTICLE); `filter`(optional); `isBlockOn`(default true); `limit`; `cursor`(opaque Base64 JSON) | `PostAllResponse{category,hasNext,nextCursor,posts:List<PostResponse>}`; `PostResponse{id,sourceType(COMMUNITY\|MEETING),member,writerId,isMine,isLiked,likes,categoryGroup,categoryCode,categoryName,tags[],title,content,hits,commentCount,images,isBlindWriter,sopticleUrl,anonymousProfile,createdAt,comments[],vote,meetingId}` | 200 |
| 3 | POST | `/posts/hit` | userId | body `CommunityHitRequest{postIdList[]}` | `Map<String,Boolean>` key `"success"` | 200 |
| 4 | POST | `/posts` | userId | body `PostSaveRequest{categoryCode,title,content,isBlindWriter,images,link,vote,mention}` | `PostSaveResponse{id,code,title,content,hits,images,isBlindWriter,createdAt}` | 201 |
| 5 | PUT | `/posts` | userId | body `PostUpdateRequest{postId,categoryCode,title,content,isBlindWriter,images,link,mention}` | `PostUpdateResponse{id,code,title,content,hits,images,isBlindWriter,createdAt,updatedAt}` | 200 |
| 6 | POST | `/posts/{postId}/report` | userId | `postId` path | `Map<String,Boolean>` key `"커뮤니티 글 신고 성공"` | 201 |
| 7 | DELETE | `/posts/{postId}` | userId | `postId` path | `Map<String,Boolean>` key `"커뮤니티 글 삭제 성공"` | 200 |
| 8 | DELETE | `/comment/{commentId}` | userId | `commentId` path — **duplicates** `CommunityCommentController` #4 below | `Map<String,Boolean>` key `"댓글 삭제 성공"` | 200 |
| 9 | POST | `/posts/like/{postId}` | userId | `postId` path | `Map<String,Boolean>` key `"커뮤니티 게시글 좋아요 성공"` | 201 |
| 10 | DELETE | `/posts/unlike/{postId}` | userId | `postId` path | `Map<String,Boolean>` key `"커뮤니티 게시글 좋아요 취소 성공"` | 200 |
| 11 | GET | `/posts/popular` | userId | `limit`(default 3) | `List<PopularPostResponse>{id,title,member,hits,likeCount,commentCount,categoryTag,categoryTagLabel}` | 200 |
| 12 | GET | `/posts/sopticle` | none | — | `List<SopticlePostResponse>{id,member,createdAt,title,content,images,sopticleUrl}` | 200 |
| 13 | GET | `/posts/all/recent` | userId | — | `List<RecentPostResponse>{id,title,content,createdAt,likeCount,commentCount,categoryTag,categoryTagLabel,totalVoteCount}` | 200 |
| 14 | GET | `/posts/hot` | none | `@Deprecated` | `Object` → `HotPostResponse{id,title,content}` | 200 |

Vote endpoint (`POST /posts/{postId}/vote`) hosted here but tallied under Vote domain (§3).

#### 2.A.2 `CommunityCategoryController` — base path `/api/v1/community/category`

| # | Method | Path | Auth | Params | Response Type | Status |
|---|--------|------|------|--------|----------------|--------|
| 1 | GET | `""` | Required (no principal param used) | none | `List<CommunityCategoryResponse>` recursive `{code,name,content,hasBlind,children[]}` | 200 |

#### 2.A.3 `CommunityCommentController` — base path `/api/v1/community/{postId}/comment`

| # | Method | Path | Auth | Params / Body DTO | Response Type | Status |
|---|--------|------|------|--------------------|----------------|--------|
| 1 | POST | `""` | userId | `postId` path; `CommentSaveRequest{content,isBlindWriter,isChildComment,webLink,parentCommentId,mention,anonymousMention{anonymousNicknames[]}}` | `Map<String,Boolean>` key `"댓글 생성 성공"` | 201 |
| 2 | GET | `""` | userId | `postId` path; `isBlockOn`(default true) | `List<CommentResponse>` hierarchical `{id,member,isMine,postId,parentCommentId,content,isBlindWriter,anonymousProfile,isReported,createdAt,isDeleted,isLiked,likeCount,replies[]}` | 200 |
| 3 | PATCH | `/{commentId}` | userId | `commentId` path; `CommentUpdateRequest{content}` | `Map<String,Boolean>` key `"댓글 수정 성공"` | 200 |
| 4 | DELETE | `/{commentId}` | userId | `commentId` path | `Map<String,Boolean>` key `"댓글 삭제 성공"` | 200 |
| 5 | POST | `/{commentId}/report` | userId | `commentId` path | `Map<String,Boolean>` key `"커뮤니티 댓글 신고 성공"` | 201 |
| 6 | POST | `/{commentId}/like` | userId | `postId,commentId` path | `Map<String,Boolean>` key `"커뮤니티 댓글 좋아요 성공"` | 201 |
| 7 | DELETE | `/{commentId}/unlike` | userId | `postId,commentId` path | `Map<String,Boolean>` key `"커뮤니티 댓글 좋아요 취소 성공"` | 200 |

### 2.B DB Schema Mapping Matrix

| Entity | Table | PK | FK / Notes | Unique Constraints | Auditing |
|---|---|---|---|---|---|
| `CommunityPost` | `community_post` (default) | `id` | `writer_id`→Member, `category_id`→Category, `anonymous_profile_id`→AnonymousProfile (nullable); `images` via `ListArrayType`→`text[]`; unidirectional `@OneToMany comments` (`@JoinColumn(postId)`, no `mappedBy`) coexists with `CommunityComment`'s own scalar `postId` — dual mapping to the same physical column | — | extends `AuditingTimeEntity` |
| `CommunityPostLike` | `community_post_like` | `id`(col `community_post_like_id`) | `member_id`,`post_id`→ Member/CommunityPost, both `updatable=false` | none declared | extends `AuditingTimeEntity` |
| `DeletedCommunityPost` | `deleted_community_post` | `id` | `writer_id`→Member (EAGER); `categoryId` bare `Long`, no FK | none | manual `deletedAt` only (not audited) |
| `CommunityHit` | **not an `@Entity`** | — | plain POJO, no repository references it — vestigial/dead | — | n/a |
| `ReportPost` | `report_post` | `id` | `postId`,`reporterId` bare `Long`s | none | manual `createdAt` |
| `Category` | `category` | `id`(col `category_id`) | self-FK `parent`→Category (join column literally named `parent`) | `code` UNIQUE NOT NULL | none |
| `CommunityComment` | `community_comment` (`@DynamicInsert`) | `id` | `postId`,`writerId`,`parentCommentId` all bare scalars (no JPA relation); `anonymous_profile_id`→AnonymousProfile nullable | none | extends `AuditingTimeEntity` |
| `CommunityCommentLike` | `community_comment_like` | `id`(col `community_comment_like_id`) | `member_id`,`comment_id` NOT NULL, `updatable=false` | UK `(member_id,comment_id)` | extends `AuditingTimeEntity` |
| `DeletedCommunityComment` | `deleted_community_comment` (`@DynamicInsert`) | `id` | bare scalars; **loses `anonymousProfile` link entirely** on soft-delete | none | manual `deletedAt` only |
| `ReportComment` | `report_comment` | `id` | bare scalars | none | manual `createdAt` |
| `AnonymousNickname` | `anonymous_nickname` | `id`(col `anonymous_nickname_id`) | none | none | none — selected via native query hardcoding schema `internal_dev` |
| `AnonymousProfile` | `anonymous_profile` | `id`(col `anonymous_profile_id`) | `user_id`→Member (`NO_CONSTRAINT`), `post_id`→CommunityPost (real FK), `an_id`→AnonymousNickname (real FK), `anpi_id`→AnonymousProfileImage (`NO_CONSTRAINT`) | UK `(user_id,post_id)` | extends `AuditingTimeEntity` |
| `AnonymousProfileImage` | `anonymous_profile_image` | `id` | none | none | none |

External entity heavily joined against: `MemberBlock` (`blocker_id`,`blocked_member_id`→Member, `isBlocked` default true) — filters blocked authors out of list queries.

### 2.C Business Rules & Side Effects

**Category/filter resolution** (`CommunityCategoryPolicy.resolveCategoryCodes`):
- Public request enums: `CommunityPostListCategory{FREE,PROMOTION,SOPTICLE}`, `CommunityPostListFilter{ALL,EVENT,PROJECT,RECRUIT,PLAN,DESIGN,SERVER,WEB,IOS,ANDROID,ETC}`.
- Canonical: `CommunityCategoryCode{FREE,MEETING,PROMOTION,PROMOTION_EVENT,PROMOTION_PROJECT,PROMOTION_RECRUIT,PROMOTION_ETC,SOPTICLE,SOPTICLE_PLAN,SOPTICLE_DESIGN,SOPTICLE_SERVER,SOPTICLE_WEB,SOPTICLE_IOS,SOPTICLE_ANDROID,SOPTICLE_ETC}`. UI tag: `CommunityPostTag{FREE("자유"),MEETING("모임"),EVENT("행사"),PROJECT("프로젝트"),RECRUIT("채용"),PROMOTION("홍보"),SOPTICLE("솝티클")}`.
- `FREE` → filter must be null (else 400 `"자유 카테고리는 filter 값을 받을 수 없습니다."`) → `[FREE]`.
- `PROMOTION` → filter defaults `ALL`; must be `{ALL,EVENT,PROJECT,RECRUIT,ETC}` else 400; `ALL`→ all 5 promotion codes, else single mapped code.
- `SOPTICLE` → filter defaults `ALL`; must be `{ALL,PLAN,DESIGN,SERVER,WEB,IOS,ANDROID,ETC}` else 400; `ALL`→ all 8 sopticle codes, else single mapped code.
- **Legacy `GET /posts` does NOT expose a `categoryCode` query param** — only `category`(required)+`filter`(optional). The canonical `categoryCode`-first / legacy-fallback contract described in CLAUDE.md §4 is a **target-state (Phase 1) addition**, not something already present in legacy; migration must introduce it additively without breaking this existing `category+filter` contract.
- `CommunityCategoryResponse.toResponseCode` is a bespoke remap (not `code.name()`): `PROMOTION_EVENT→"EVENT"`, `SOPTICLE_PLAN→"PLAN"`, etc.; `FREE/PROMOTION/SOPTICLE/MEETING` and others pass through unchanged.

**Crew meeting merge**: triggers only when `category==FREE` (`getFreePostsWithMeetingPosts`); `PROMOTION`/`SOPTICLE` never merge Crew data — consistent with CLAUDE.md §4 rule 4. Backed by `CrewMeetingFeedCacheService` (Redis, key `community:free-feed:meeting:{userId}:{snapshotTime}`, TTL 10 min) fronting `MakersCrewClient.getPosts`. Merge sort: `createdAt DESC, sourceType.name() ASC ("COMMUNITY" before "MEETING"), id DESC`. `snapshotTime` pinned at first page and threaded through cursor for stable pagination. Crew posts always render `categoryGroup=categoryCode=FREE`, `categoryName="자유"`, `tags=[MEETING]`, `sourceType=MEETING`, `meetingId` populated (community posts: `meetingId=null`) — confirms "MEETING is a tag, not a CommunityCategoryCode" per CLAUDE.md. Same merge pattern (with weighted popularity score `hits + commentCount*5 + likeCount*3`) also used in `getPopularPosts`/`getRecentPosts` home-preview endpoints.

**Anonymous posting**: `AnonymousProfileService.getOrCreateAnonymousProfile(member, post)` keyed by unique `(member,post)`. New nickname excludes ones already used on this post AND the 50 globally most-recent anonymous profiles (native `ORDER BY RANDOM() LIMIT 1`, schema hardcoded `internal_dev` — must not port verbatim). Profile image randomly chosen from ids 1–5 (id 6 = "Makers logo", excluded), cached in an in-process `ConcurrentHashMap` for JVM lifetime (no invalidation). Same profile is reused for the post AND all of that member's comments on that post (consistency guarantee).

**Comment nesting/reply**: `CommentSaveRequest.validate()` — `isChildComment=true` requires `parentCommentId`, `isChildComment=false` forbids it. Parent existence checked for child comments; `anonymousMention` nicknames validated both globally and against the specific post. `parentCommentId` is a bare scalar (no FK) — hierarchy is response-mapping-only, technically supports N levels though API/UI treats it as comment-vs-reply. Comment like/unlike validates comment belongs to `postId` in URL, plus not-already-liked / must-be-liked guards; DB UK backstops races.

**Ownership (edit/delete)**: writer-only everywhere, **no admin override** for either posts or comments (differs from Member/Ask's receiver-can-always-delete pattern — that rule does not apply here). Comment delete: soft-delete (`isDeleted=true`) + parallel insert into `DeletedCommunityComment` (dual write) + anonymizes `@mention` tokens in **direct replies only** (one level, not recursive) via `CommentMentionAnonymizer`. Deleted-comment response nulls most fields and forces `isDeleted=true, replies=[]` (tombstone shape).

**Report handling — asymmetric side effects**:
- Post report: Slack only in `prod` (`try/catch(RuntimeException)`, swallowed); `ReportPost` row saved **unconditionally outside** the try/catch, in every environment.
- Comment report: Slack fires in **every** environment, **no try/catch** — a Slack failure propagates and rolls back the `ReportComment` insert too. This asymmetry must be a deliberate migration decision, not silently normalized.

**Caching / invalidation-relevant paths**: view-count dedupe via Redis `post:view:{yyyy-MM-dd}:{userId}:{postId}` TTL 24h (compensating delete on DB-increment failure); Crew feed cache as above (purely TTL, no write-side invalidation hook); anonymous-profile-image in-process cache (no invalidation). No caching exists for post-list/detail/popular/recent beyond these — computed live otherwise.

**N+1 patterns**: `getTodayPosts()...createPostWithPoints` (backs deprecated `GET /posts/hot`) issues 2 extra queries **per post** (comment count, like count) — still wired despite `@Deprecated`. `CommentMentionAnonymizer.anonymizeMentionsInReplies` saves each reply in a loop (not batched). All other hot-path list/preview methods are properly bulk-fetched (member/vote/like/anonymous-profile maps) — no N+1 there.

**Pagination**: `normalizeLimit` clamps to max **50**, never rejects an over-large `limit` (silently truncates). Cursor-based (`CommunityFeedCursor{snapshotTime, community:{createdAt,postId}, meetingConsumedCount}`, Base64Url JSON; invalid → 400). Sort: `createdAt DESC, id DESC` for pure community; merged FREE feed adds `sourceType.name() ASC` as the middle tie-break. `hasNext` via fetch-limit+1 trick, OR'd with Crew cache's own `hasMorePage()` for merged feed.

**Notable gotchas** (full list, preserve-or-flag decisions needed):
1. Native SQL hardcodes schema `internal_dev` for anonymous-nickname random pick — will target the wrong schema elsewhere; must be re-parameterized behind the Storage Adapter, not copied verbatim.
2. `CommunityHit` domain class is not a JPA entity — dead code.
3. `DeletedCommunityPost.content` capped at 10,000 chars vs. unbounded live `CommunityPost.content` — long-post soft-delete can truncate/fail.
4. `AnonymousProfile.member`/`.profileImg` use `@ForeignKey(NO_CONSTRAINT)` — two of four FK columns have no real DB constraint; a deliberate legacy workaround, not a bug to silently "fix."
5. `Category.parent` join column is literally named `parent` (not `parent_id`).
6. Dual mapping of comment↔post relationship (see entity table above) — `post.getComments()` may not reflect same-transaction inserts elsewhere.
7. Copy/paste bug: comment-delete permission check throws `"수정 권한이 없는 유저입니다."` (an "edit" message) for a delete action.
8. `DELETE /api/v1/community/comment/{commentId}` duplicates `DELETE /api/v1/community/{postId}/comment/{commentId}` — both live, must both be frozen under Golden Rule unless product confirms one is dead.
9. `GET /posts/hot` is `@Deprecated` but still mounted and still runs its N+1 pattern; the writer job that populates "hot" status was not found in scope — verify it's still running before assuming full dead-code status.
10. Nearly every mutation returns `Map<String,Boolean>` keyed by a **Korean, action-specific string** (e.g. `{"커뮤니티 글 신고 성공": true}`) — must be preserved verbatim; sole English exception is `POST /posts/hit` → `{"success": true}`.
11. `PostSaveRequest.title` has no blank/null validation (unlike `content`/`isBlindWriter`/`images`/`categoryCode`); for Sopticle-category posts, server **overwrites** `title/content/images/sopticleUrl` from scraped metadata regardless of client input.
12. Posts are silently **dropped** (not placeholder'd) from popular/latest listings when author platform-lookup or anonymous-profile resolution fails — a data-integrity gap upstream causes silent omission, not a surfaced error.

---

## 3. Vote Domain (1 endpoint)

No dedicated controller — hosted inside `CommunityController` (`/api/v1/community`), and vote data is embedded read-only in several other Community response DTOs.

### 3.A API Contract Matrix

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | POST | `/api/v1/community/posts/{postId}/vote` | userId | `postId` path; body `VoteSelectionRequest{selectedOptions: List<Long>}` | `VoteResponse{id,isMultiple,hasVoted,totalParticipants,options:List<VoteOptionResponse{id,content,voteCount,votePercent,isSelected}>}` | 200 |

Read-only embedding points (not separate endpoints, listed for completeness): vote creation is a side effect of `POST /posts` (body carries optional `VoteRequest{isMultiple,voteOptions[]}`); `VoteResponse` is embedded in `GET /posts/{postId}` and `GET /posts` responses; `RecentPostResponse.totalVoteCount` (from `GET /posts/all/recent`) surfaces only `totalParticipants` (nullable when no vote, not `0`).

### 3.B DB Schema Mapping Matrix

| Entity | Table | PK | FK / Notes | Unique Constraints |
|---|---|---|---|---|
| `Vote` | `vote` (default) | `id` | `post_id`→`community_post`, `@OneToOne`, `unique=true` (DB-enforces 1 vote per post); `isMultipleOptions` NOT NULL; no auditing base entity | `post_id` UNIQUE |
| `VoteOption` | `vote_option` (default) | `id` | `vote_id`→`vote`; `content` NOT NULL; `voteCount` mutated via non-atomic `this.voteCount++` (relies on JPA dirty-checking inside `@Transactional`) | none |
| `VoteSelection` | `vote_selection` (default) | `id` | `user_id`→Member, `vote_option_id`→VoteOption, both `optional=false` | UK `(user_id, vote_option_id)` — **only** prevents re-selecting the *same option* twice; does **not** prevent selecting multiple different options of the same vote (that guard is app-level only, see below) |

Cascade chain confirmed end-to-end: `CommunityPost` delete → `Vote` (`CascadeType.REMOVE`, orphanRemoval) → `VoteOption` (same) → `VoteSelection` (same). No explicit `VoteRepository.delete(...)` call exists anywhere — deletion is entirely cascade-driven.

### 3.C Business Rules & Side Effects

**Cardinality & creation**: A post may exist without a vote (creation is entirely optional, gated on `PostSaveRequest.vote != null`). 1:1 relationship is immutable after creation — **no update-vote endpoint exists**; a vote can only ever be created once alongside its post, never edited, and only removed via post-deletion cascade. Vote **creator = post author only**, by construction (not by an explicit ownership check inside `VoteService.createVote` itself — it trusts the caller). SOPTICLE-category posts **cannot** have votes (`BadRequestException`).

**Option-count policy** (creation-time only): 2–5 options inclusive; each option non-blank, ≤40 chars.

**Multiple-choice**: configurable per-vote via `isMultipleOptions`. Enforcement (`validateVoteSelectionPolicy`) only rejects **more than 1** selection in a single call when `isMultipleOptions==false` — does not block 0.

**Selection/casting — real behavioral quirk to flag for migration parity discussion**: the "already voted" guard (`existsByVoteOptionInAndMember(selectedOptions, member)`) checks only against the **specific options submitted in the current call**, not against all of the vote's options. A member who already voted for Option A and later calls the endpoint again selecting a *different* Option B is **not blocked** by this check — allowing accumulation of selections across multiple calls even on a nominally single-choice vote (bounded only by the per-call `isMultipleOptions` size check, which is not cumulative). No DB-level constraint closes this gap (the UK is per-option, not per-vote). **Decision needed**: preserve as-is (legacy parity) or flag as a bug to fix during migration.

**Deadline/closing**: none exists — no expiry field, no status enum, no scheduled job. Votes stay open indefinitely.

**Results visibility**: never gated — `voteCount`/`votePercent` are always returned regardless of `hasVoted`; anonymous/null `memberId` still gets full counts with `isSelected=false` for all options. There is no "vote to see results" mechanism.

**Response shape gotchas**:
- **Percent vs. participant-count divergence**: `votePercent` denominator = sum of `voteCount` across options (i.e., **selection count**, double-counts a multi-select member), while `totalParticipants` = `COUNT(DISTINCT member)` (correctly counts a multi-select member once). These two numbers are **not reconcilable** to each other whenever `isMultipleOptions==true` and any member picks >1 option — must preserve both fields' exact (differing) semantics.
- Options always returned sorted **ascending by `id`** (creation order), never by vote count.
- Current user's own selection is echoed via `isSelected` per option and `hasVoted` at the vote level.
- `VoteResponse`/`vote` field is `null` (not an empty object) when the post has no vote.
- The 404 thrown when voting on a post without a vote (`ResponseStatusException(NOT_FOUND, "해당 게시글에는 투표가 존재하지 않습니다.")`) bypasses the custom `BadRequestException`/`PlaygroundException → {"message":...}` handler entirely and uses Spring's default `ResponseStatusException` body shape — an inconsistency vs. every other error in this domain that must be reconciled or explicitly preserved.

**N+1 / dead code**: `VoteQueryRepository` is **entirely dead code** (injects QueryDSL Q-types, defines zero query methods, never referenced elsewhere) — do not assume it holds logic to port. `VoteRepository.findByPost_IdIn` uses `@EntityGraph({"post","voteOptions"})` for the bulk list path (no N+1); `findByPost_Id` (single, `@EntityGraph("voteOptions")`) is **unused** dead code — single-post lookup actually reuses the batched `getVoteMapByPostIds(List.of(postId), userId)` path. `VoteSelectionRepository`'s bulk `IN`-keyed count/selection queries are the good pattern to mirror in the target module.

---

## 4. WordChain Domain (4 endpoints)

### 4.A API Contract Matrix

`WordChainGameController` — base path `/api/v1/chainWordGame`, `@SecurityRequirement(name="Authorization")` (Swagger-only; `SecurityConfig` `.permitAll()`s all paths — enforcement, where present, is via `@AuthenticationPrincipal` resolution only). **No WebSocket/STOMP mechanism exists anywhere in the codebase** (confirmed by repo-wide grep) — this is pure REST polling.

| # | Method | Path | Auth | Params / Body DTO | Response Type (shape) | Status |
|---|--------|------|------|--------------------|------------------------|--------|
| 1 | POST | `/wordGame` | `@AuthenticationPrincipal Long userId` (resolved member required) | body `WordChainGameGenerateRequest{roomId, word}` | `WordChainGameGenerateResponse{roomId,word,user:MemberSimpleResonse{id,name,profileImage}}` | 200 |
| 2 | GET | `/gameRoom` | none enforced | `limit`(optional), `cursor`(optional `Long` — last-seen room id, null/0 for first page) | `WordChainGameAllResponse{rooms:List<WordChainGameRoomResponse{roomId,startWord,startUser,words:List<{word,user}>}>,hasNext}` | 200 |
| 3 | POST | `/newGame` | `@AuthenticationPrincipal Long userId` | none | `WordChainGameGenerateResponse{roomId,word,user}` — `user` is explicitly **null** for the very first room ever created system-wide | 200 |
| 4 | GET | `/winners` | none enforced | `limit`(optional), `cursor`(`int`, default `0` — **offset-based**, a different pagination style from `/gameRoom`'s ID-cursor) | `WordChainGameWinnerAllResponse{winners:List<{roomId,winner}>,hasNext}` | 200 |

`GET /gameRoom` and `GET /winners` have no `@AuthenticationPrincipal` param at all — combined with `SecurityConfig`'s blanket permit-all, these two reads are effectively open to unauthenticated callers.

### 4.B DB Schema Mapping Matrix

| Entity | Table | PK | FK / Notes |
|---|---|---|---|
| `Word` | `word` (explicit) | `id` | `memberId`,`roomId` — plain scalars, **no FK annotations**; `createdAt` manually set (no auditing base); no unique constraint (duplicate-word guard is app-level `existsByWordAndRoomId`, scoped per room) |
| `WordChainGameRoom` | `word_chain_gameroom` (explicit) | `id` | `createdUserId` nullable (null = very first room ever); `@OneToMany(FetchType.EAGER, cascade=ALL, orphanRemoval=true) @JoinColumn(room_id)` unidirectional `wordList` — **EAGER collection causes N+1** on every room fetch (see below); duplicates the separate scalar `Word.roomId` field used elsewhere |
| `WordChainGameWinner` | **`@Table` with no `name` attribute** — physical name relies on Hibernate default naming, unlike its two siblings which explicitly name their tables; must be verified against the live DB, not assumed | `id` | `userId` no FK annotation; `roomId` column name relies on implicit naming (inconsistent with sibling entities' explicit `@Column(name=...)`); **no timestamp column at all** — time-range queries (`countByUserIdAndCreatedAtBetween`) borrow the joined room's `createdAt` |

### 4.C Business Rules & Side Effects

**Word validity — exact chain** (`WordChainGameService.createWord`, order preserved incl. one duplicate call bug):
1. `checkWordIsOneLetter` (misnamed — actually rejects **length < 2**, i.e. enforces minimum 2 chars) — called **twice** (copy/paste leftover, harmless since idempotent).
2. `checkWordIsKoreanLetter` — regex `[ㄱ-ㅎㅏ-ㅣ가-힣]+`.
3. `checkRoomIsValid` — room must exist.
4. `checkIsChainingWord` — compares against the room's last word (or `startWord` if none yet submitted): last syllable of previous word must equal first syllable of next, **or** satisfy 두음법칙 (initial-sound rule) via a full jamo-decomposition special-case table (`ㄴ→ㅇ/ㄴ`, `ㄹ→ㅇ/ㄹ`, `ㄹ→ㅇ/ㄴ`, each additionally requiring matching medial+final) — must preserve this table exactly, it is a meaningful Korean-language business rule, not incidental.
5. `checkIsInDictionary` — calls the external 표준국어대사전 Open API (raw `HttpURLConnection`, no timeout configured, no URL-encoding beyond a regex Hangul-strip) requiring `pos=="명사"` (noun); **all exceptions are swallowed via `e.printStackTrace()`** (not the injected `Slf4j` logger) and treated as "word not found" — a transient network failure to this external API silently rejects legitimate words. This external dependency and its failure-swallowing behavior must be ported behind a proper client/Port with explicit error handling, not copied verbatim.
6. `checkIsLastWordWriterIsMakingNextWord` — same member cannot submit two words in a row.
7. `checkDuplicateWord` — no reuse of a word already used **in the same room** (a different room may reuse it).

**Room lifecycle**: `POST /newGame` takes no body/params — there is **no configurable max-players or time-limit anywhere in this domain**. Creating a new room: (a) if any room has ever existed, the just-ended room must have received ≥1 word (else 400) and the **last word's author is explicitly forbidden** from being the one to start the new room (400 if they try — the person who "won" cannot immediately also start the next round; some other member must trigger it); (b) winner of the just-ended room = whoever wrote its last word, persisted as a **new, append-only** `WordChainGameWinner` row (`score = previous best score for that user + 1`, not an update) — ending a room is entirely implicit, triggered only by the next `POST /newGame` call, with no explicit "close room" action or timeout. `startWord` for a new room is randomly picked from a fixed 32-word Korean list embedded in code (`gameStartWord`).

**Turn order / timeout**: no server-side timer or `@Scheduled` task anywhere — enforcement is purely the two reactive checks above (no self-chaining, no duplicate word).

**Real-time**: none — confirmed no WebSocket/STOMP config exists anywhere in the repo; state is observable only via polling `GET /gameRoom` / `GET /winners`.

**Authorization**: open to any authenticated member for the two write endpoints; no generation-specific gating anywhere in this domain (unlike Member/Ask's current-generation Alarm/SMS branching). The two GET endpoints are effectively open to anonymous callers.

**N+1 / performance**:
- `WordChainGameRoom.wordList`'s `FetchType.EAGER` unidirectional `@OneToMany` triggers one extra `SELECT` per room on every room-list fetch (`findAllLimitedGameRoom`/`findAllGameRoom`) — classic N+1, must become a fetch-joined/batched query in the migration.
- Both room-list query methods call `.groupBy(room.id)` with no aggregate selected — a vestigial no-op that adds SQL overhead for nothing.
- `toRoomResponse` calls `platformService.getInternalUsers(userIds)` **once per room** inside the list-mapping stream (not batched across all rooms in the page) — an N+1 pattern at the external-platform-call level, distinct from (and in addition to) the DB-level EAGER-collection N+1 above.

**Admin endpoints**: none exist — no reset/seed/admin management API in this domain.

**Notable gotchas**: `checkWordIsOneLetter` double-called (harmless bug); overloaded method name `getAllGameRooms` used for both the word-submission POST and the room-list GET (confusing but legal); `POST /newGame`'s `user` field is legitimately `null` for the first-ever room (must be preserved as a valid nullable case, not treated as an error); `WordChainGameWinner`'s implicit table name must be verified against the live schema before migration; two different pagination styles under one controller (`/gameRoom`=ID-cursor `Long`, `/winners`=offset `int`) must each be preserved exactly per-endpoint.

---

## 5. InternalOpenApi Domain (9 endpoints)

### 5.A API Contract Matrix

`InternalOpenApiController` — base path `/internal/api/v1`, `@SecurityRequirement(name="Authorization")` is **Swagger documentation only, no runtime enforcement** — there is no class-wide filter/interceptor; auth is checked **manually, per-method**, and only on 2 of the 9 endpoints.

| # | Method | Path | Auth mechanism | Params / Body DTO | Response Type | Status |
|---|--------|------|-----------------|--------------------|-----------------|--------|
| 1 | GET | `/projects/{id}` | **None** | `id` path | `ProjectDetailResponse` (external DTO, out of scope) | 200 |
| 2 | GET | `/members/{memberId}/project` *(mapping string has no leading `/`, inconsistent with all other mappings in the class)* | **None** | `memberId` path | `InternalMemberProjectResponse{soptProjectCount:Integer}` | 200 |
| 3 | GET | `/community/posts/latest` | **None** | none | `List<InternalLatestPostResponse>{id,userId,profileImage,name,generationAndPart,category,title,content,webLink,createdAt}` | 200 |
| 4 | GET | `/community/posts/popular` | **None** | none (`limit=3` hardcoded server-side) | `List<InternalPopularPostResponse>{id,userId,profileImage,name,generationAndPart,rank,category,title,content,webLink}` | 200 |
| 5 | GET | `/members/profile/me` | **None** | `memberId`(query, `String`, parsed via `Long.valueOf` twice) | `InternalMemberProfileResponse{memberId,name,profileImage,introduction,mbti,university,activities:List<CardinalInfoResponse{cardinalInfo}>}` | 200 |
| 6 | GET | `/members/profile` | **None** | `memberIds`(query, comma-separated `String`, URL-decoded then split) | `List<InternalMemberProfileListResponse>{memberId,name,profileImage,introduction,activities[]}` — **note: no `mbti`/`university`, unlike #5** | 200 |
| 7 | POST | `/members/profile/recommend` | **None** | body `InternalRecommendMemberListRequest{generations:List<Integer> NotEmpty, filters:List<SearchContentResponse{key,value}>}` | `InternalRecommendMemberListResponse{userIds:Set<Long>}` | 200 |
| 8 | POST | `/members` | **Manual**: header `apiKey` → `ApiKeyValidator.validate()` | body `CreateDefaultUserProfileRequest{userId}` + header `apiKey` | `String` plain text: `"기본 유저 프로필이 성공적으로 생성되었습니다. user id: {id}"` | 201 |
| 9 | DELETE | `/members/{memberId}` | **Manual**: header `apiKey` → `ApiKeyValidator.validate()` | `memberId` path + header `apiKey` | `String` plain text: `"기본 유저 프로필이 성공적으로 삭제되었습니다. user id: {id}"` | 200 |

**Auth mechanism detail**: `ApiKeyValidator.validate(providedApiKey)` does plain `Objects.equals(authConfig.getInternalPlatformApiKey(), providedApiKey)` against property `internal.platform.api-key`; mismatch throws `ResponseStatusException(UNAUTHORIZED, "잘못된 api key 입니다.")`. **Only endpoints #8/#9 call this** — the other 7, including all member-profile and community-post reads and the recommend-list endpoint, have **zero runtime authentication** despite the class-level Swagger annotation implying otherwise. `InternalTokenManager.java` is 100% commented-out dead code (superseded by "인증중앙화" per its own trailing comment) — do not port.

**Likely status-code bug**: `GlobalExceptionHandler`'s catch-all `@ExceptionHandler(Exception.class)` (no explicit `ResponseStatusException` handler) very likely intercepts the `ApiKeyValidator` 401 before Spring's `ResponseStatusExceptionResolver` can act, meaning a bad/missing `apiKey` on #8/#9 probably actually returns **HTTP 500** instead of documented 401 — verify with an integration test before assuming the 401 contract in the target module.

### 5.C Business Rules & Side Effects

**Purpose**: machine-to-machine feed for 앱팀 (App team — project count, community latest/popular, member profile single/bulk, recommend list) and 플랫폼팀 (Platform team — create/delete default member profile, presumably a registration/deregistration hook); endpoint #1 serves 공홈 (official homepage) project detail.

**`CreateDefaultUserProfileRequest` (#8) — not idempotent**: creates exactly one `Member` row keyed on the given `userId` (reused directly as PK) with all profile fields null, `hasProfile=true`, `allowOfficial=false`, `editActivitiesAble=true`, `openToSoulmate=false`, `isPhoneBlind=true`. A second call for the same `userId` throws `ConflictException` → HTTP 409 (no silent no-op/update). `DELETE /members/{memberId}` (#9) is a hard delete; missing member → 404.

**Recommend-list algorithm** (`InternalApiService.getMemberIdsByRecommendFilter`): DB-filters member IDs by `hasProfile=true` AND (optional) exact `mbti` match AND (optional) `university` substring match; separately, for each requested generation, calls the external platform (`searchInternalUsers`, page 0, size 200 hardcoded) to collect that generation's user IDs; final result = **intersection** of the two ID sets.
- **Bug**: if any single generation returns zero platform profiles, the loop `break`s entirely — later generations in the request are silently skipped rather than merely `continue`d past.
- **Bug**: only the first 200 platform users per generation are ever considered (no pagination beyond page 0).
- **Status-code gap**: empty/null `generations` throws a plain `IllegalArgumentException`, unhandled by `GlobalExceptionHandler` → falls to the generic handler → **HTTP 500** instead of 400.

**"Latest posts" (#3)**: fetches the single most-recent post per **3** categories only (`FREE, PROMOTION, SOPTICLE` — from `CommunityPostListCategory`), **despite the Swagger doc claiming "최상위 카테고리별(자유,질문,홍보,파트Talk,솝티클) ... 총 5개"** (5 categories/posts) — the doc is stale; "Question"/"Part-Talk" don't exist as categories in this codebase. Real max response size is **3**, not 5. Posts with unresolvable author/anonymous-profile data are silently dropped.

**"Popular posts" (#4)**: "popular" here = raw `hits` (view count) descending, restricted to posts created in the trailing **1 month**, hardcoded `limit=3`. **This is a different algorithm from the home-preview weighted score** (`hits + commentCount*5 + likeCount*3`) used elsewhere in `CommunityPostService` — the internal API intentionally does NOT use the weighted formula and does NOT merge Crew meeting posts. Zero posts in the trailing month → `PlaygroundException` → **HTTP 500** (not an empty 200 list, not 404).

**Caching**: none anywhere on this call path (no `@Cacheable`, and the Crew-feed cache used elsewhere in Community is not touched by these methods) — every call hits the DB / external platform live.

**N+1**: none found on the direct endpoint paths — author/user enrichment is properly batched (`platformService.getInternalUserDetailsMap`/`getInternalUsers`, one call per distinct-ID set) for endpoints #1,#3,#4,#6. Endpoint #7's per-generation platform fetch is a bounded, request-driven fan-out (N calls for N generations in the request) rather than classic per-row N+1, but worth flagging as a latency/rate-limit risk for large generation lists.

**Notable gotchas (consolidated)**:
1. Auth enforced on only 2/9 endpoints — a genuine security gap to resolve (at minimum, decide and document) during migration, not silently replicate without flagging.
2. Auth failure likely mis-reports as 500, not 401 (see above).
3. `@GetMapping("members/{memberId}/project")` omits its leading slash — verify path resolution is unaffected before porting.
4. `InternalMemberProfileResponse` (single) has `mbti`+`university`; `InternalMemberProfileListResponse` (bulk) omits both — asymmetric contract, must preserve per-endpoint exactly as-is.
5. `/community/posts/latest` Swagger doc (5 categories) is stale vs. actual behavior (3 categories, 3 max posts).
6. Empty `generations` → 500 instead of 400; zero popular-posts-in-window → 500 instead of empty list/404 — both existing behaviors to knowingly preserve or knowingly fix (Golden Rule requires preserving unless explicitly renegotiated).
7. Recommend algorithm's early `break` on first empty-generation result, and its 200-user-per-generation page cap, can under-return valid recommendations.
8. `PlatformService.getInternalUsers` swallows platform-outage exceptions and returns an empty list rather than propagating — a platform outage on the bulk-profile endpoint yields an **empty-but-200** response, not an error; callers relying on error signaling for outages will not get one.

---

## 6. Cross-Domain Hexagonal / Clean-Architecture Rules

These rules apply uniformly to the migration of every domain audited above (per root `CLAUDE.md` §2 and `sopt-makers-backend/AGENTS.md`), with domain-specific flashpoints noted from the audits:

1. **Domain isolation — no direct JPA/QueryDSL/external-I/O in `domain-*` modules.** Flashpoints found in legacy that must NOT be carried into `domain-*` as-is:
   - Community's native SQL with a hardcoded `internal_dev` schema literal (anonymous-nickname random pick) must move entirely into the Storage Adapter and be schema-agnostic.
   - WordChain's raw `HttpURLConnection` call to the external 표준국어대사전 dictionary API must be wrapped in a `clients/` adapter behind a domain Port (e.g. `DictionaryLookupPort`), with explicit (not swallowed) error handling.
   - InternalOpenApi's and WordChain's calls to `platformService`/`MakersCrewClient` must be reached only through purpose-specific domain Ports (`PlaygroundCrewRelationPort`-style), never a direct client import from `domain-*`.
2. **Entity → domain model conversion at the Storage Adapter boundary.** Every legacy `@Entity` audited above (Community's `CommunityPost`/`CommunityComment`/`Vote`/etc., Word­Chain's `Word`/`WordChainGameRoom`/`WordChainGameWinner`, Member's `MemberQuestion`/`MemberAnswer`/etc.) must be converted to pure domain records before crossing into `domain-*` Query/Command services — none of these entities' JPA-specific quirks (EAGER collections, `@ForeignKey(NO_CONSTRAINT)`, dual-mapped columns, native-query schema hardcoding) may leak past the adapter.
3. **User vs. Member ubiquitous-language separation** (CLAUDE.md §5.2): applies most directly to the Member/Ask domain (internal Java naming unified to `Ask*`, while external API paths/JSON keys keep legacy `question`/`answer` vocabulary) but the same discipline extends to Community and Vote — e.g. `CommunityPost.writer`/`AnonymousProfile.member` reference the platform's `Member`/`User` identity only as a scalar ID at the domain layer, never a JPA relation, mirroring the "no `@ManyToOne` to User" rule already mandated for Playground-owned models.
4. **Strict call-chain preservation** (`Controller implements Api` → `QueryService`/`CommandService` → `Port` → `Adapter`) applies identically across Community, Vote, WordChain, and InternalOpenApi — none of these legacy domains currently have this layering (they are monolithic `*Service` classes calling repositories directly), so the migration must introduce the Query/Command split without altering any of the response shapes cataloged in §2–§5 above.
5. **Golden Rule — 100% external contract freeze** applies to every quirk cataloged above, including ones that read as "bugs": the Korean-keyed `Map<String,Boolean>` responses (Community, Member), the plain-text responses (InternalOpenApi #8/#9), the `null`-vs-`0`/`null`-vs-missing-field distinctions (Vote's `totalVoteCount`, WordChain's `user:null` on first room), the divergent pagination styles (WordChain's ID-cursor vs. offset-cursor across its own two endpoints), and the inconsistent HTTP status codes on error paths (InternalOpenApi's likely-500-instead of 401/400/404) — none of these may be "fixed" silently during Phase 1 migration; each requires an explicit product/engineering decision if change is ever proposed.

## 7. Consolidated Cross-Domain Gotcha Catalog (for migration triage)

| Domain | Gotcha | Preserve or Flag? |
|---|---|---|
| Community | Native SQL hardcodes `internal_dev` schema | Flag — must be fixed in Storage Adapter regardless (breaks in any other schema), but external behavior for the *default* schema must stay identical |
| Community | Duplicate comment-delete endpoint (`CommunityController` vs `CommunityCommentController`) | Preserve both under Golden Rule unless product confirms one is dead |
| Community | Asymmetric Slack try/catch + env-gating between post-report and comment-report | Flag for explicit decision; default to preserving both behaviors as-is |
| Vote | "Already voted" check scoped to submitted options only, not vote-wide | Flag for explicit decision — real accumulation-of-selections gap |
| Vote | 404 on vote-less post bypasses standard error-body shape | Preserve as-is unless product wants uniform error shape |
| WordChain | Dictionary-lookup failures silently swallowed via `printStackTrace`, treated as "not a word" | Flag — must at least log properly in target module; whether to change user-facing behavior needs a decision |
| WordChain | Winner cannot start next room (last-word-writer blocked) | Preserve — deliberate anti-abuse rule |
| WordChain | Two pagination styles across `/gameRoom` (ID-cursor) and `/winners` (offset) | Preserve exactly per-endpoint |
| InternalOpenApi | Auth enforced on only 2/9 endpoints | Flag prominently — likely unintentional security gap, needs explicit sign-off either way |
| InternalOpenApi | Auth failure likely returns 500 instead of 401 | Verify via test; preserve verified actual behavior, not documented intent |
| InternalOpenApi | `/community/posts/latest` Swagger doc stale (claims 5 categories, delivers ≤3) | Preserve actual (3-category) behavior; correct the doc, not the code, unless product wants the doc's original 5-category behavior |
| InternalOpenApi | Empty `generations` / zero popular-posts-in-window both surface as 500 | Preserve as-is per Golden Rule unless product explicitly approves a status-code fix |

---

**Report generation note**: this document was assembled from four independent read-only code audits (Community, Vote, WordChain, InternalOpenApi) plus the pre-existing Member-domain audit (`LEGACY_MEMBER_AUDIT.md`), each verified against actual legacy source under `sopt-playground-backend/src/main/java/org/sopt/makers/internal/`. No legacy files were modified. No files under `sopt-makers-backend/` other than this one were created or modified.
