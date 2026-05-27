package com.back.domain.post;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.service.PostCommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostCommentServiceTest {

    @Autowired
    PostService postService;
    @Autowired
    PostCommentService postCommentService;
    @Autowired
    MemberService memberService;

    private Member user1() {
        return memberService.findByUsername("user1").orElseThrow();
    }

    @Test
    @DisplayName("대댓글은 깊이 1단계까지 허용")
    void reply_depth_1() {
        Member m = user1();
        Post p = postService.write(m, "t", "c");
        PostComment top = postCommentService.write(p, m, "최상위", null);
        PostComment reply = postCommentService.write(p, m, "대댓글", top);
        assertTrue(reply.isReply());
        assertFalse(top.isReply());
    }

    @Test
    @DisplayName("대댓글에 다시 답글 달기는 거부 (깊이 1 보장)")
    void reply_to_reply_rejected() {
        Member m = user1();
        Post p = postService.write(m, "t", "c");
        PostComment top = postCommentService.write(p, m, "최상위", null);
        PostComment reply = postCommentService.write(p, m, "대댓글", top);

        assertThrows(IllegalStateException.class,
                () -> postCommentService.write(p, m, "대대댓글", reply));
    }

    @Test
    @DisplayName("부모 댓글 소프트 삭제 후에도 대댓글은 유지되고 부모도 조회 가능")
    void soft_delete_keeps_replies() {
        Member m = user1();
        Post p = postService.write(m, "t", "c");
        PostComment top = postCommentService.write(p, m, "최상위", null);
        PostComment reply = postCommentService.write(p, m, "대댓글", top);

        postCommentService.delete(top);

        assertTrue(postCommentService.getComment(top.getId()).isDeleted());
        assertFalse(postCommentService.getComment(reply.getId()).isDeleted());
    }

    @Test
    @DisplayName("대댓글이 없는 댓글도 항상 소프트 삭제")
    void delete_is_always_soft() {
        Member m = user1();
        Post p = postService.write(m, "t", "c");
        PostComment c = postCommentService.write(p, m, "댓글", null);

        postCommentService.delete(c);

        assertTrue(postCommentService.getComment(c.getId()).isDeleted());
    }

    @Test
    @DisplayName("삭제된 글에는 댓글 작성 불가")
    void cannot_comment_on_deleted_post() {
        Member m = user1();
        Post p = postService.write(m, "t", "c");
        postService.delete(p);

        assertThrows(IllegalStateException.class,
                () -> postCommentService.write(p, m, "댓글", null));
    }
}
