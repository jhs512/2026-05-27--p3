package com.back.domain.post;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.service.PostCommentRecommendService;
import com.back.domain.post.postComment.service.PostCommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostCommentRecommendServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    PostCommentService postCommentService;
    @Autowired
    PostCommentRecommendService postCommentRecommendService;
    @Autowired
    MemberService memberService;

    private Member member(String username) {
        return memberService.findByUsername(username).orElseThrow();
    }

    private PostComment newComment(Member author) {
        Post p = postService.write(author, "t", "c");
        return postCommentService.write(p, author, "댓글", null);
    }

    @Test
    @DisplayName("댓글 추천 토글 - 추천/취소")
    void toggle() {
        Member m = member("user1");
        PostComment c = newComment(m);

        assertTrue(postCommentRecommendService.toggle(c, m));
        assertEquals(1, postCommentRecommendService.count(c));
        assertFalse(postCommentRecommendService.toggle(c, m));
        assertEquals(0, postCommentRecommendService.count(c));
    }

    @Test
    @DisplayName("1인 1추천 - 두 회원이면 2")
    void one_per_member() {
        Member m1 = member("user1");
        Member m2 = member("user2");
        PostComment c = newComment(m1);

        postCommentRecommendService.toggle(c, m1);
        postCommentRecommendService.toggle(c, m2);
        assertEquals(2, postCommentRecommendService.count(c));
    }

    @Test
    @DisplayName("자기 댓글 추천 허용")
    void self_recommend_allowed() {
        Member author = member("user1");
        PostComment c = newComment(author);

        assertTrue(postCommentRecommendService.toggle(c, author));
        assertEquals(1, postCommentRecommendService.count(c));
    }

    @Test
    @DisplayName("삭제된 댓글은 추천 불가")
    void cannot_recommend_deleted() {
        Member m = member("user1");
        PostComment c = newComment(m);
        postCommentService.delete(c);

        assertThrows(IllegalStateException.class, () -> postCommentRecommendService.toggle(c, m));
    }
}
