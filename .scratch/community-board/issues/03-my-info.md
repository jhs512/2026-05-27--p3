# 03 내 정보 조회·수정

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

로그인 회원의 "내 정보" 페이지를 만든다. username·nickname·가입일을 조회하고, nickname과 password를 수정할 수 있다. username은 수정 수단을 제공하지 않는다(불변). 본인만 접근·수정 가능.

## Acceptance criteria

- [ ] 내 정보 페이지에 username·nickname·가입일이 표시된다
- [ ] nickname 수정이 반영된다
- [ ] password 변경 시 새 BCrypt 해시로 저장되고, 변경 후 새 비밀번호로 로그인된다
- [ ] username을 바꿀 수단이 없다(불변)
- [ ] 비로그인 접근 시 로그인으로 유도된다
- [ ] MockMvc 테스트(`@Transactional` 롤백, 샘플 회원 활용): nickname 수정, password 변경 후 재로그인

## Blocked by

- 02 회원 가입·로그인·로그아웃 (`02-member-auth.md`)
