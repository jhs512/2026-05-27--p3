# 커뮤니티 게시판 (Community Board)

회원이 글을 쓰고, 글에 댓글을 달고, 글·댓글을 추천하는 커뮤니티 게시판. 글에는 조회수가 쌓인다.

## Language

**Member** (회원):
서비스에 가입해 로그인하는 사람. 글·댓글의 작성자이자 추천의 주체.
_Avoid_: User (Spring Security `UserDetails`와 혼동), Account

**Post** (글):
회원이 작성하는 게시글. 제목과 본문을 가지며, 조회수와 추천을 받는다.
_Avoid_: Article, Board

**PostComment** (댓글):
하나의 Post에 달리는 댓글. 작성자(Member)를 가지며 추천을 받는다. 다른 PostComment를 부모로 가지면 "대댓글"이며, 대댓글의 대댓글은 없다(부모 깊이 1단계로 제한).
_Avoid_: Reply, Comment(단독 사용 시 어디에 달린 댓글인지 불명확)

**Recommend** (추천):
한 Member가 한 Post(또는 PostComment)에 표하는 추천. (Member, 대상) 단위로 유일하며, 다시 누르면 취소되는 토글. 대상의 "추천수"는 Recommend 개수.
_Avoid_: Like, Vote, Up(어휘 일관성 위해 Recommend로 통일)

**조회수** (View count):
Post가 열람된 누적 횟수. Post에 귀속되며 PostComment에는 없다.
