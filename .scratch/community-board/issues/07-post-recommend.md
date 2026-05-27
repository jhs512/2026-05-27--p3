# 07 글 추천(PostRecommend 토글)

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

`PostRecommend` 조인 엔티티(member ↔ post, (member, post) 유니크 제약)로 글 추천을 토글 방식으로 구현한다. 추천이 없으면 생성(추천), 있으면 삭제(취소). 추천수는 해당 글의 PostRecommend 행 수. 자기추천 허용(작성자==추천자 검증 없음). 비회원이 추천 시도 시 로그인으로 유도. 삭제된 글에는 추천을 차단. 글 상세에 현재 추천수와 내 추천 여부를 표시한다.

## Acceptance criteria

- [ ] 로그인 회원이 글을 추천하면 추천수가 +1된다
- [ ] 다시 누르면 취소되어 추천수가 -1된다(토글)
- [ ] 같은 글을 반복해서 눌러도 추천수는 0/1만 오간다(1인 1추천, 유니크 제약)
- [ ] 작성자가 자기 글을 추천할 수 있다
- [ ] 비회원 추천 시도 시 로그인으로 유도된다
- [ ] 삭제된 글에는 추천이 차단된다
- [ ] 글 상세에 현재 추천수와 내 추천 여부가 표시된다
- [ ] MockMvc 테스트: 토글 on/off·중복방지·자기추천·삭제글 차단

## Blocked by

- 04 글 CRUD (`04-post-crud.md`)
