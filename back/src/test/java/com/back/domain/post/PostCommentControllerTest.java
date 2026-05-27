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
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PostCommentControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    PostService postService;
    @Autowired
    PostCommentService postCommentService;
    @Autowired
    PostCommentRecommendService postCommentRecommendService;
    @Autowired
    MemberService memberService;

    private Post newPostBy(String username) {
        Member m = memberService.findByUsername(username).orElseThrow();
        return postService.write(m, "글", "내용");
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("댓글 작성 - 상세로 리다이렉트하고 댓글 수 증가")
    void write_comment() throws Exception {
        Post p = newPostBy("user1");
        long before = postCommentService.count();

        mvc.perform(post("/posts/" + p.getId() + "/comments").with(csrf())
                        .param("content", "새 댓글"))
                .andExpect(status().is3xxRedirection());

        assertEquals(before + 1, postCommentService.count());
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("대댓글 작성 - parentId 로 깊이 1 대댓글 생성")
    void write_reply() throws Exception {
        Member m = memberService.findByUsername("user1").orElseThrow();
        Post p = postService.write(m, "글", "내용");
        PostComment top = postCommentService.write(p, m, "최상위", null);
        long before = postCommentService.count();

        mvc.perform(post("/posts/" + p.getId() + "/comments").with(csrf())
                        .param("content", "대댓글")
                        .param("parentId", String.valueOf(top.getId())))
                .andExpect(status().is3xxRedirection());

        // 댓글 1개 생성, 단 대댓글이므로 최상위 댓글 수는 1 그대로
        assertEquals(before + 1, postCommentService.count());
        assertEquals(1, postCommentService.getTopLevelComments(p).size());
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("댓글 소프트 삭제 후 상세 페이지에 '삭제된 댓글입니다' placeholder")
    void soft_delete_placeholder() throws Exception {
        Member m = memberService.findByUsername("user1").orElseThrow();
        Post p = postService.write(m, "글", "내용");
        PostComment c = postCommentService.write(p, m, "지울 댓글", null);

        mvc.perform(post("/comments/" + c.getId() + "/delete").with(csrf()))
                .andExpect(status().is3xxRedirection());

        String body = mvc.perform(get("/posts/" + p.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("삭제된 댓글입니다"));
    }

    @Test
    @WithUserDetails("user2")
    @DisplayName("타인 댓글 삭제 시 403")
    void delete_forbidden_for_non_author() throws Exception {
        Member m = memberService.findByUsername("user1").orElseThrow();
        Post p = postService.write(m, "글", "내용");
        PostComment c = postCommentService.write(p, m, "user1 댓글", null);

        mvc.perform(post("/comments/" + c.getId() + "/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("댓글 추천 토글 - 추천 후 수 증가")
    void recommend_comment() throws Exception {
        Member m = memberService.findByUsername("user1").orElseThrow();
        Post p = postService.write(m, "글", "내용");
        PostComment c = postCommentService.write(p, m, "댓글", null);

        mvc.perform(post("/comments/" + c.getId() + "/recommend").with(csrf()))
                .andExpect(status().is3xxRedirection());

        assertEquals(1, postCommentRecommendService.count(c));
    }
}
