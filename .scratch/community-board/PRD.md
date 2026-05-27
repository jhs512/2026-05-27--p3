# PRD: 커뮤니티 게시판 (Community Board)

Status: ready-for-agent

회원이 글을 쓰고 댓글을 달며 글·댓글을 추천하는 Thymeleaf 기반 커뮤니티 게시판. Spring Boot 4.0.6 / JDK 25 / Gradle(KTS), H2.
용어는 `CONTEXT.md`(Member·Post·PostComment·Recommend·조회수)를, 삭제 정책은 `docs/adr/0001-soft-delete-posts-and-comments.md`를 따른다.

## Problem Statement

회원들이 글을 올리고 서로의 글·댓글에 반응(댓글·추천)하며 인기 글을 가늠할 공간이 없다. 사용자는 가입·로그인 후 글을 작성·열람하고, 댓글로 대화하고, 추천으로 호응을 표하고, 조회수로 관심도를 확인할 수 있는 게시판을 원한다.

## Solution

회원 가입/로그인(폼 로그인, 세션)을 갖춘 서버사이드 렌더링(Thymeleaf + layout-dialect, Tailwind/DaisyUI CDN, Pretendard) 게시판을 제공한다. 회원은 글을 CRUD하고, 글에 댓글·1단계 대댓글을 달고, 글·댓글을 토글 방식으로 추천한다. 글에는 세션당 1회 조회수가 쌓인다. 글·댓글 삭제는 물리 삭제 없이 소프트 삭제로 처리해 대화 맥락과 이력을 보존한다.

## User Stories

### 인증·회원
1. 비회원으로서, username·password·nickname으로 회원가입하여, 서비스를 이용할 수 있다.
2. 가입자로서, 이미 사용 중인 username으로는 가입이 거부되어, 로그인 식별자 유일성이 보장된다.
3. 가입 시, 비밀번호가 BCrypt로 해시되어 저장되어, 평문 노출 위험이 없다.
4. 회원으로서, username·password로 폼 로그인하여, 인증된 상태로 기능을 쓸 수 있다.
5. 회원으로서, 로그아웃하여, 세션을 종료할 수 있다.
6. 회원으로서, "내 정보" 페이지에서 username·nickname·가입일을 조회하여, 내 계정을 확인할 수 있다.
7. 회원으로서, "내 정보"에서 nickname을 수정하여, 표시 이름을 바꿀 수 있다.
8. 회원으로서, "내 정보"에서 비밀번호를 변경하여, 계정 보안을 갱신할 수 있다.
9. 회원으로서, username은 수정 불가임을 확인하여, 로그인 식별자가 고정됨을 안다.
10. 비회원으로서, 인증이 필요한 페이지 접근 시 로그인 페이지로 안내되어, 무엇을 해야 할지 안다.

### 글(Post)
11. 회원으로서, 제목·본문으로 글을 작성하여, 내 글을 게시할 수 있다.
12. 누구나, 글 목록을 페이지 단위로 조회하여, 어떤 글이 있는지 훑어볼 수 있다.
13. 누구나, 글 상세를 열어 제목·본문·작성자(nickname)·작성일·조회수·추천수를 확인할 수 있다.
14. 작성자로서, 내 글의 제목·본문을 수정하여, 내용을 고칠 수 있다.
15. 작성자 또는 ADMIN으로서, 글을 삭제하여, 더 이상 활성 글이 아니게 할 수 있다.
16. 누구나, 삭제된 글이 목록·상세에서 "삭제된 글" 플레이스홀더로 제자리에 표시되어, 사라지지 않고 흐름이 보존됨을 본다.
17. 회원으로서, 글 상세를 열 때 같은 세션에서 처음이면 조회수가 1 증가하여, 새로고침해도 중복 집계되지 않는다.
18. 비작성자·비ADMIN으로서, 남의 글 수정·삭제가 차단되어, 권한 경계가 지켜진다.

### 댓글(PostComment)
19. 회원으로서, 글에 댓글을 달아, 글에 대해 의견을 남길 수 있다.
20. 회원으로서, 특정 댓글에 대댓글(1단계)을 달아, 그 댓글에 답할 수 있다.
21. 회원으로서, 대댓글에는 다시 대댓글을 달 수 없어, 깊이가 1단계로 유지됨을 안다.
22. 누구나, 글 상세에서 댓글과 그 아래 대댓글을 계층으로 조회할 수 있다.
23. 작성자로서, 내 댓글을 수정하여, 내용을 고칠 수 있다.
24. 작성자 또는 ADMIN으로서, 댓글을 삭제할 수 있다.
25. 누구나, 삭제된 댓글이 본문 대신 "삭제된 댓글입니다"로 표시되고 그 아래 대댓글은 계속 보여, 대화 맥락이 보존됨을 본다.
26. 누구나, 삭제된 댓글도(대댓글 유무와 무관하게) 항상 플레이스홀더로 남음을 본다.
27. 비작성자·비ADMIN으로서, 남의 댓글 수정·삭제가 차단된다.

### 추천(Recommend)
28. 회원으로서, 글을 추천하여, 호응을 표할 수 있다.
29. 회원으로서, 이미 추천한 글을 다시 누르면 추천이 취소되어, 토글로 동작한다.
30. 회원으로서, 같은 글을 여러 번 눌러도 추천수는 0/1만 오가, 1인 1추천이 보장된다.
31. 회원으로서, 댓글을 추천·취소할 수 있다(글과 동일한 토글).
32. 회원으로서, 내가 쓴 글·댓글도 추천할 수 있다(자기추천 허용).
33. 누구나, 글·댓글의 현재 추천수를 확인할 수 있다.
34. 회원으로서, 내가 이미 추천한 글·댓글이 활성 상태로 표시되어, 추천 여부를 안다.
35. 비회원으로서, 추천 시도 시 로그인으로 안내된다.
36. 누구나, 삭제된 글·댓글에는 추천이 차단됨을 본다.

### 샘플 데이터
37. 개발자로서, 앱 첫 기동 시 회원 5명(admin·system=ADMIN, 일반 3명)·글 5개·댓글 5개가 자동 생성되어, 빈 화면 없이 기능을 확인할 수 있다.
38. 개발자로서, 이미 회원이 1명이라도 있으면 샘플 데이터가 다시 생성되지 않아, 중복이 없다.

## Implementation Decisions

### 패키지 구조 (도메인 기반)
- `com.back.global` — 횡단 관심사
  - `jpa/entity/BaseEntity`: `@MappedSuperclass`, `id`, `createDate`, `modifyDate`(`@CreatedDate`/`@LastModifiedDate`). 모든 엔티티가 상속.
  - `jpa/JpaConfig`(또는 `BackApplication`): `@EnableJpaAuditing`.
  - `security/SecurityConfig`: 폼 로그인 + 세션, `BCryptPasswordEncoder` 빈, USER/ADMIN 권한, 정적/공개 경로 허용.
  - `initData/BaseInitData`: `baseInitDataApplicationRunner` 빈, `@Transactional`, 회원 존재 시 조기 반환.
- `com.back.domain.member` — Member(`username` 불변, `password`, `nickname`, `role`), MemberRepository, **MemberService**, MemberController.
- `com.back.domain.post.post` — Post(`title`, `content`, author=Member, `viewCount`, `deleted`), PostRepository, **PostService**, **PostViewService**, PostController.
- `com.back.domain.post.postComment` — PostComment(`content`, author=Member, `parent`=self-FK(nullable), `deleted`), PostCommentRepository, **PostCommentService**, PostCommentController.
- 추천 조인 엔티티는 각 대상 패키지에 둔다: `domain.post.post`의 PostRecommend(`member`↔`post`), `domain.post.postComment`의 PostCommentRecommend(`member`↔`postComment`). 토글/카운트 규칙은 공통.

### 도메인 규칙
- **권한**: 글·댓글 수정·삭제는 작성자 본인 또는 ROLE_ADMIN만. 그 외 차단.
- **역할**: USER / ADMIN 두 가지. system·admin 계정은 ADMIN.
- **대댓글 깊이**: PostComment.parent는 최상위 댓글(parent==null)만 가리킬 수 있다. 대댓글에 대댓글 시도는 거부(부모가 이미 자식인 경우 차단)하여 깊이 1단계 보장.
- **추천 토글**: (member, 대상) 조합은 유일(DB 유니크 제약 권장). 없으면 생성(추천), 있으면 삭제(취소). 추천수 = 해당 대상의 Recommend 행 수. 자기추천 허용(작성자==추천자 검증 없음).
- **조회수**: 세션(`HttpSession`)에 열람한 Post ID 집합을 보관. 상세 진입 시 해당 세션에서 처음이면 `viewCount` +1 후 세션에 기록. 동일 세션 재방문은 미집계. PostComment에는 조회수 없음.
- **소프트 삭제(ADR-0001)**: Post·PostComment 삭제는 `deleted=true`만 설정, 물리 삭제 없음. 삭제된 Post는 목록·상세에서 "삭제된 글" 플레이스홀더로 제자리 노출. 삭제된 PostComment는 "삭제된 댓글입니다"로 본문 대체, 하위 대댓글 유지. 삭제된 대상에는 수정·댓글 작성·추천 등 변경 행위 차단.

### 설정·프로파일
- `application.yml`(공통) + `application-dev.yml`(개발) + `application-test.yml`(테스트).
- dev: H2 파일 DB(`./db_dev.mv.db`), `ddl-auto: update`, h2-console 활성.
- test: H2 인메모리, `ddl-auto: create`.
- 의존성: DevTools, Lombok, Spring Data JPA, Validation, Spring Security, Thymeleaf, thymeleaf-layout-dialect, H2.
- 뷰: Thymeleaf + layout-dialect 공통 레이아웃, Tailwind 4 Play CDN, DaisyUI 5 CDN, Pretendard dynamic subset 웹폰트.

### 입력 검증
- 회원가입: username/password/nickname `@NotBlank`, username 유일성.
- 글: title/content `@NotBlank`.
- 댓글: content `@NotBlank`.

### BaseInitData 구성
- 회원 5명: `system`(ADMIN), `admin`(ADMIN), `user1`·`user2`·`user3`(USER), 비밀번호는 BCrypt 해시.
- 글 5개(작성자 분산), 댓글 5개(일부는 대댓글로 깊이 1 시연), 일부 추천·조회 시드 가능.

## Testing Decisions

- **좋은 테스트의 기준**: 외부에서 관측 가능한 동작(HTTP 응답, DB 상태 변화, 화면에 노출되는 결과)만 검증하고 내부 구현 세부(메서드 호출 순서, private 필드)에는 의존하지 않는다.
- **방식**: 서비스 동작을 **MockMvc** 통합 테스트로 검증한다(컨트롤러→서비스→리포지터리 슬라이스 전체). 각 테스트에 `@Transactional`을 붙여 종료 후 롤백한다. **BaseInitData**가 생성한 샘플 데이터(회원·글·댓글)를 픽스처로 활용해 테스트 준비를 간소화한다(테스트 프로파일 인메모리 DB + `ddl-auto: create` + 기동 시 러너 실행).
- **대상 모듈**: MemberService(가입 중복검사·BCrypt·프로필 수정), PostService/PostViewService(소프트 삭제·삭제 글 플레이스홀더·세션당 1회 조회수), PostCommentService(대댓글 깊이 1 검증·소프트 삭제·"삭제된 댓글" 표시), Recommend(토글 1회/취소·1인 1추천·자기추천 허용·삭제 대상 추천 차단), 권한 경계(비작성자·비ADMIN 수정/삭제 차단).
- **프로젝트 첫 테스트라 prior art 없음** — 이 PRD의 패턴이 이후 테스트의 기준이 된다.

## Out of Scope

- 회원 탈퇴, 비밀번호 찾기/재설정, 이메일 인증, 소셜 로그인.
- 글 검색·정렬·카테고리·태그·첨부파일·이미지 업로드.
- 대댓글 2단계 이상, 댓글 페이징.
- 삭제 데이터의 물리 정리(purge)/복구 UI, 신고·차단·관리자 대시보드.
- REST/JSON API, 프런트엔드 SPA(서버사이드 렌더링만).
- 운영 배포·외부 DB(MySQL 등) 전환.

## Further Notes

- `username`은 로그인 식별자이자 불변. 화면 표시는 `nickname` 사용.
- Spring Security `UserDetails` 어댑터와 도메인 `Member`를 혼동하지 않는다(`CONTEXT.md` _Avoid_: User).
- 추천 엔티티를 Post/PostComment별로 분리한 것은 JPA FK 무결성·쿼리 단순성 때문이며, 토글/카운트의 도메인 의미는 동일하다.
- 삭제 정책의 근거·대안은 ADR-0001 참조.
