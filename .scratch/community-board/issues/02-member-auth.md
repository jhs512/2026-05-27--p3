# 02 회원 가입·로그인·로그아웃 + 샘플회원 시드

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

회원 가입·로그인·로그아웃을 end-to-end로 구현한다. `Member` 엔티티(`username` 불변, `password`, `nickname`, `role`[USER/ADMIN], `BaseEntity` 상속). 가입 시 username 유일성 검사 + BCrypt 해시 저장. Spring Security 폼 로그인 + 세션, `BCryptPasswordEncoder` 빈, Member 기반 `UserDetailsService`, USER/ADMIN 권한, 공개 경로(홈·가입·로그인·정적·h2-console) 허용 설정. 가입 페이지(username/password/nickname), 로그인 페이지, 로그아웃. `com.back.global.initData.BaseInitData`의 `baseInitDataApplicationRunner`(`@Transactional`): 회원이 1명이라도 있으면 중단, 없으면 회원 5명 생성(`system`·`admin`=ADMIN, `user1`·`user2`·`user3`=USER). 이 슬라이스에서 spring-security 의존성을 추가한다.

## Acceptance criteria

- [ ] username/password/nickname으로 가입되며 입력 검증(`@NotBlank`)이 걸린다
- [ ] 이미 존재하는 username으로는 가입이 거부된다
- [ ] 비밀번호가 BCrypt로 해시되어 저장된다(평문 아님)
- [ ] 폼 로그인 성공 시 인증 세션이 생성되고 로그아웃이 동작한다
- [ ] 비인증 사용자가 보호 페이지 접근 시 로그인 페이지로 리다이렉트된다
- [ ] BaseInitData가 첫 기동에 회원 5명을 시드하고, 회원이 있으면 재실행하지 않는다
- [ ] MockMvc 통합 테스트(`@Transactional` 롤백, 샘플 회원 픽스처 활용): 가입·중복거부·로그인·로그아웃

## Blocked by

- 01 프로젝트 워킹 스켈레톤 (`01-walking-skeleton.md`)
