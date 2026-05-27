package com.back.domain.post;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostRecommendService;
import com.back.domain.post.post.service.PostService;
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
class PostRecommendServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    PostRecommendService postRecommendService;
    @Autowired
    MemberService memberService;

    private Member member(String username) {
        return memberService.findByUsername(username).orElseThrow();
    }

    @Test
    @DisplayName("추천 토글 - 누르면 추천, 다시 누르면 취소")
    void toggle() {
        Member m = member("user1");
        Post p = postService.write(m, "t", "c");

        assertEquals(0, postRecommendService.count(p));

        assertTrue(postRecommendService.toggle(p, m));   // 추천
        assertEquals(1, postRecommendService.count(p));
        assertTrue(postRecommendService.isRecommended(p, m));

        assertFalse(postRecommendService.toggle(p, m));  // 취소
        assertEquals(0, postRecommendService.count(p));
        assertFalse(postRecommendService.isRecommended(p, m));
    }

    @Test
    @DisplayName("1인 1추천 - 같은 회원은 추천수 0/1만 오감, 두 회원이면 2")
    void one_per_member() {
        Member m1 = member("user1");
        Member m2 = member("user2");
        Post p = postService.write(m1, "t", "c");

        postRecommendService.toggle(p, m1);
        postRecommendService.toggle(p, m1); // 다시 -> 취소
        assertEquals(0, postRecommendService.count(p));

        postRecommendService.toggle(p, m1);
        postRecommendService.toggle(p, m2);
        assertEquals(2, postRecommendService.count(p));
    }

    @Test
    @DisplayName("자기추천 허용 - 작성자가 자기 글을 추천 가능")
    void self_recommend_allowed() {
        Member author = member("user1");
        Post p = postService.write(author, "t", "c");

        assertTrue(postRecommendService.toggle(p, author));
        assertEquals(1, postRecommendService.count(p));
    }

    @Test
    @DisplayName("삭제된 글은 추천 불가")
    void cannot_recommend_deleted() {
        Member m = member("user1");
        Post p = postService.write(m, "t", "c");
        postService.delete(p);

        assertThrows(IllegalStateException.class, () -> postRecommendService.toggle(p, m));
    }
}
