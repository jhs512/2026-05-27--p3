# 세팅

```bash
mkdir -p projects/p3
cd projects/p3
git int
git remote add origin 리포지터리-주소
npx skills@latest add mattpocock/skills # 전체 설치(심볼릭 링크 X, copy)
msg=001 && git add . && git commit -m "$msg" && git tag $msg && git push origin main && git push origin --tags $msg
```