# 01 프로젝트 워킹 스켈레톤 + 공통 레이아웃

Status: ready-for-agent

## Parent

`.scratch/community-board/PRD.md`

## What to build

`back/` 폴더에 Spring Boot 4.0.6 / JDK 25 / Gradle(KTS) 프로젝트를 세워 기동되는 워킹 스켈레톤을 만든다. 루트 패키지 `com.back`, 메인 클래스 `com.back.BackApplication`에 `@EnableJpaAuditing`. 모든 엔티티의 기반이 될 `BaseEntity`(`@MappedSuperclass`: id, createDate, modifyDate를 Auditing으로 자동 관리). 프로파일은 `application.yml`(공통) + `application-dev.yml` + `application-test.yml`로 분리하고, dev는 H2 파일 DB(`./db_dev.mv.db`) + `ddl-auto: update` + h2-console, test는 H2 인메모리 + `ddl-auto: create`. Thymeleaf + thymeleaf-layout-dialect 공통 레이아웃에 Tailwind 4 Play CDN·DaisyUI 5 CDN·Pretendard dynamic subset 웹폰트를 걸고, 그 레이아웃을 사용하는 홈 페이지를 렌더한다. 의존성: DevTools, Lombok, Spring Data JPA, Validation, H2. (Spring Security는 슬라이스 02에서 도입.)

## Acceptance criteria

- [ ] dev 프로파일로 앱이 기동된다
- [ ] `BackApplication`에 `@EnableJpaAuditing` 적용, `BaseEntity` 상속 엔티티의 createDate/modifyDate가 자동 채워진다
- [ ] dev 기동 시 `./db_dev.mv.db` 파일이 생성되고 `/h2-console` 접속이 된다
- [ ] test 프로파일은 인메모리 H2 + `ddl-auto: create`로 동작
- [ ] 홈 페이지가 layout-dialect 공통 레이아웃을 통해 렌더되고 Tailwind/DaisyUI 스타일·Pretendard 폰트가 적용된다
- [ ] `@SpringBootTest` 컨텍스트 로드 테스트가 test 프로파일에서 통과

## Blocked by

None - can start immediately
