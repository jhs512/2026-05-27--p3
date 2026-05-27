# 05 글 조회수(세션당 1회)

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

`PostViewService`로 글 조회수를 세션당 1회만 증가시킨다. `HttpSession`에 열람한 Post ID 집합을 보관하고, 글 상세 진입 시 해당 세션에서 처음 보는 글이면 `viewCount` +1 후 세션에 기록한다. 동일 세션의 재방문/새로고침은 미집계. 삭제된 글은 집계하지 않는다. PostComment에는 조회수가 없다.

## Acceptance criteria

- [ ] 글 상세 첫 방문 시 viewCount가 1 증가한다
- [ ] 같은 세션에서 새로고침·재방문 시 증가하지 않는다
- [ ] 새 세션에서는 다시 1 증가한다
- [ ] 삭제된 글은 조회수가 증가하지 않는다
- [ ] MockMvc 테스트: 동일 세션 반복 요청 시 조회수가 1회만 증가

## Blocked by

- 04 글 CRUD (`04-post-crud.md`)
