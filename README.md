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
