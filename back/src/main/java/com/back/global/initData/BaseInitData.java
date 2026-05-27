package com.back.global.initData;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    private final MemberService memberService;
    private final PostService postService;
    private final PostCommentService postCommentService;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> self.work();
    }

    @Transactional
    public void work() {
        // 회원이 1명이라도 있으면 샘플 데이터 생성 중단
        if (memberService.count() > 0) return;

        // 회원 5명 (system·admin = ADMIN, user1~3 = USER)
        memberService.join("system", "1234", "시스템", "ADMIN");
        Member admin = memberService.join("admin", "1234", "관리자", "ADMIN");
        Member user1 = memberService.join("user1", "1234", "유저1");
        Member user2 = memberService.join("user2", "1234", "유저2");
        Member user3 = memberService.join("user3", "1234", "유저3");

        // 글 5개 (작성자 분산)
        Post post1 = postService.write(user1, "첫 번째 글", "첫 번째 글의 내용입니다.");
        Post post2 = postService.write(user2, "두 번째 글", "두 번째 글의 내용입니다.");
        Post post3 = postService.write(user3, "세 번째 글", "세 번째 글의 내용입니다.");
        postService.write(admin, "공지사항", "관리자가 작성한 공지입니다.");
        postService.write(user1, "다섯 번째 글", "다섯 번째 글의 내용입니다.");

        // 댓글 5개 (일부는 대댓글로 깊이 1 시연)
        PostComment c1 = postCommentService.write(post1, user2, "첫 글 잘 봤습니다.", null);
        postCommentService.write(post1, user3, "저도 동의합니다.", null);
        postCommentService.write(post1, user1, "댓글 감사합니다!", c1); // 대댓글
        postCommentService.write(post2, user1, "두 번째 글 댓글입니다.", null);
        postCommentService.write(post3, admin, "잘 읽었습니다.", null);
    }
}
