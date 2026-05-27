# 08 댓글 추천(PostCommentRecommend 토글)

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

`PostCommentRecommend` 조인 엔티티(member ↔ postComment, (member, postComment) 유니크 제약)로 댓글 추천을 토글 방식으로 구현한다. 글 추천(슬라이스 07)과 동일한 토글/카운트 패턴을 댓글에 적용한다. 추천수=행 수, 자기추천 허용, 비회원 로그인 유도, 삭제된 댓글 추천 차단, 댓글에 추천수·내 추천 여부 표시.

## Acceptance criteria

- [ ] 로그인 회원이 댓글을 추천/취소한다(토글)
- [ ] 같은 댓글 반복해도 추천수는 0/1만 오간다(1인 1추천, 유니크 제약)
- [ ] 작성자가 자기 댓글을 추천할 수 있다
- [ ] 비회원 추천 시도 시 로그인으로 유도된다
- [ ] 삭제된 댓글에는 추천이 차단된다
- [ ] 댓글에 추천수·내 추천 여부가 표시된다
- [ ] MockMvc 테스트: 토글·중복방지·자기추천·삭제댓글 차단

## Blocked by

- 06 댓글·대댓글 CRUD (`06-post-comment.md`)
- 07 글 추천 (`07-post-recommend.md`)
