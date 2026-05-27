# 세팅, mattpocock/skills 스킬 설치

```bash
mkdir -p projects/p3
cd projects/p3
git int
git remote add origin 리포지터리-주소
npx skills@latest add mattpocock/skills # 전체 설치(심볼릭 링크 X, copy)
msg=001 && git add . && git commit -m "$msg" && git tag $msg && git push origin main && git push origin --tags $msg
```

# CLUADE.md 세팅
```md
# 작업지침
- 한국어 사용
- /caveman 스킬 사용
- 일반적인 작업 흐름 : /grill-with-docs, /to-prd, /to-issue, /tdd and /diagnosis, /improve-codebase-architecture(이 스킬은 필요할 때만 사용)
- 최대한 matt 스킬들을 활용
```

# /setup-matt-pocock-skills 로 하네스 세팅
- `cd projects/p3`
- `claude --dangerously-skip-permissions .`
- `/setup-matt-pocock-skills`
- 이슈관리 : 로컬 마크다운
- 트리아지 라벨 어휘 : 기본
- 도메인 문서 레이아웃 : 단일 컨텍스트
- 진행할까요? : 진행

# 문서 한글화
- CLAUDE.md에서 `## Agent skills` 블록을 h2에서 h1으로 레벨업
> CLUADE.md, docs/**/*.md 파일들을 한글화 해줘

# /grill-with-docs
- back 폴더에 자프링(4.0.6, JDK 25, gradle kts)으로 세팅
- 루트 패키지 : com.back
- 메인 클래스 : com.back.BackApplication
  - @EnableJpaAuditing
  - 모든 엔티티에 작성날짜, 수정날짜 포함(BaseEntity 클래스를 상속받도록)
- h2, h2-console 사용
- 개발 모드에서는 application.yml, application-dev.yml 사용
- 테스트 모드에서는 application.yml, application-test.yml 사용
- 개발 모드에서는 파일 DB 사용(./db_dev.mv.db)
- 테스트 모드에서는 메모리 DB 사용(H2 in-memory)
- 타임리프 사용
- thymeleaf-layout-dialect 사용
- 테일윈드 4.x 플레이 CDN 사용(`<script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>`)
- 데이지 UI 사용(`<link href="https://cdn.jsdelivr.net/npm/daisyui@5" rel="stylesheet" type="text/css" />`)
- 프리텐다드 웹폰트 다이나믹 서브셋
- DEV-TOOLS, LOMBOK, SPRING-DATA-JPA, VALIDATION, SPRING-SECURITY 사용
- 개발 모드에서는 ddl-auto: update
- 테스트 모드에서는 ddl-auto: create
- com.back.global.initData.BaseInitData 에서 baseInitDataApplicationRunner 빈 생성
  - @Transactional
  - 회원이 1명이라도 있으면 샘플 데이터 생성 로직 중단
  - 회원 5명 샘플 데이터 생성
  - 글 5개 샘플 데이터 생성
  - 댓글 5개 샘플 데이터 생성
- 페이지 : 회원가입(username, password, nickname), 내 정보, 로그인, 글 CRUD, 댓글 CRUD, 글/댓글 추천수, 글 조회수