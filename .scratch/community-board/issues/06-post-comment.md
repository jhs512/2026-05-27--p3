# 06 댓글·대댓글 CRUD + 소프트삭제 + 댓글 시드

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

`PostComment` 엔티티(`content`, author=Member, `parent` self-FK(nullable), `deleted`, `BaseEntity` 상속)의 댓글·대댓글을 구현한다. 글 상세에서 댓글을 작성하고, 최상위 댓글에 대댓글(깊이 1단계)을 작성한다 — 대댓글에 다시 대댓글을 다는 것은 거부한다(부모는 parent==null인 댓글만 가리킬 수 있음). 댓글과 그 아래 대댓글을 계층으로 표시한다. 수정·삭제는 작성자 본인 또는 ROLE_ADMIN. 삭제는 대댓글 유무와 무관하게 항상 소프트 삭제: 본문 대신 "삭제된 댓글입니다"를 보여주고 하위 대댓글은 계속 노출한다. 삭제된 댓글에는 변경 행위를 차단한다. BaseInitData에 댓글 5개 시드(일부는 대댓글로 깊이 1 시연).

## Acceptance criteria

- [ ] 로그인 회원이 글에 댓글을 작성한다(`@NotBlank`)
- [ ] 최상위 댓글에 대댓글을 작성한다(깊이 1)
- [ ] 대댓글에 대댓글을 시도하면 거부된다(깊이 1 보장)
- [ ] 댓글/대댓글이 계층으로 렌더된다
- [ ] 작성자/ADMIN만 수정·삭제한다
- [ ] 삭제 시 항상 `deleted=true`이며 "삭제된 댓글입니다"로 표시되고 하위 대댓글은 유지된다
- [ ] BaseInitData가 댓글 5개를 시드한다
- [ ] MockMvc 테스트: 댓글·대댓글·깊이1거부·소프트삭제 플레이스홀더·권한차단

## Blocked by

- 04 글 CRUD (`04-post-crud.md`)
