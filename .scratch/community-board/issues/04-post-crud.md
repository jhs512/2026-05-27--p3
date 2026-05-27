# 04 글 CRUD + 권한 + 소프트삭제 + 글 시드

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

`Post` 엔티티(`title`, `content`, author=Member, `viewCount`, `deleted`, `BaseEntity` 상속)의 글 CRUD를 구현한다. 로그인 회원이 작성, 누구나 목록(페이지 단위)·상세 조회(제목·본문·작성자 nickname·작성일·조회수·추천수 자리). 수정·삭제는 작성자 본인 또는 ROLE_ADMIN만. 삭제는 소프트 삭제(`deleted=true`)이며, 삭제된 글은 목록·상세에서 숨기지 않고 "삭제된 글" 플레이스홀더로 제자리에 노출하고 변경 행위는 차단한다(ADR-0001). BaseInitData에 글 5개 시드(작성자 분산). 조회수 실제 증가 로직은 슬라이스 05에서 다룬다(여기서는 필드/표시 자리만).

## Acceptance criteria

- [ ] 로그인 회원이 title/content(`@NotBlank`)로 글을 작성한다
- [ ] 글 목록을 페이지 단위로 조회한다
- [ ] 글 상세에 작성자 nickname·작성일·조회수·추천수가 표시된다
- [ ] 작성자/ADMIN만 수정·삭제하고, 그 외는 차단된다
- [ ] 삭제는 `deleted=true`만 설정하고 물리 삭제하지 않는다
- [ ] 삭제된 글이 목록·상세에 "삭제된 글" 플레이스홀더로 남는다
- [ ] BaseInitData가 글 5개를 시드한다
- [ ] MockMvc 테스트: 작성·목록·상세·수정·소프트삭제·권한차단

## Blocked by

- 02 회원 가입·로그인·로그아웃 (`02-member-auth.md`)
