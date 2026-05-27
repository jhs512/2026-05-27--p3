package com.back.domain.post;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PostControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    PostService postService;
    @Autowired
    MemberService memberService;
    @Autowired
    PostRepository postRepository;

    private Post createPostBy(String username, String title) {
        Member m = memberService.findByUsername(username).orElseThrow();
        return postService.write(m, title, "내용");
    }

    @Test
    @DisplayName("글 목록은 비로그인도 조회 가능")
    void list_public() throws Exception {
        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("post/list"));
    }

    @Test
    @DisplayName("글 상세는 비로그인도 조회 가능")
    void detail_public() throws Exception {
        Post p = createPostBy("user1", "공개글");
        mvc.perform(get("/posts/" + p.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("post/detail"));
    }

    @Test
    @DisplayName("비로그인 글쓰기 폼 접근 시 로그인으로 리다이렉트")
    void write_form_requires_login() throws Exception {
        mvc.perform(get("/posts/write"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("로그인 회원 글 작성")
    void write() throws Exception {
        long before = postService.count();
        mvc.perform(post("/posts/write").with(csrf())
                        .param("title", "새 글 제목")
                        .param("content", "새 글 내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/posts/*"));
        assertEquals(before + 1, postService.count());
    }

    @Test
    @WithUserDetails("user2")
    @DisplayName("타인의 글 수정 시 403")
    void edit_forbidden_for_non_author() throws Exception {
        Post p = createPostBy("user1", "user1글");
        mvc.perform(post("/posts/" + p.getId() + "/edit").with(csrf())
                        .param("title", "탈취")
                        .param("content", "x"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("admin")
    @DisplayName("ADMIN은 타인 글도 삭제 가능 (소프트 삭제)")
    void admin_can_delete() throws Exception {
        Post p = createPostBy("user1", "삭제대상");
        mvc.perform(post("/posts/" + p.getId() + "/delete").with(csrf()))
                .andExpect(redirectedUrl("/posts"));

        Post reloaded = postRepository.findById(p.getId()).orElseThrow();
        assertTrue(reloaded.isDeleted());
        assertTrue(postRepository.existsById(p.getId())); // 물리 삭제 아님
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("작성자 본인 소프트 삭제 후 목록·상세에 '삭제된 글' placeholder")
    void soft_delete_placeholder() throws Exception {
        Post p = createPostBy("user1", "내글");
        mvc.perform(post("/posts/" + p.getId() + "/delete").with(csrf()))
                .andExpect(redirectedUrl("/posts"));

        String detail = mvc.perform(get("/posts/" + p.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(detail.contains("삭제된 글"));
    }

    @Test
    @DisplayName("조회수는 같은 세션에서 1회만 증가, 새 세션은 다시 증가")
    void view_count_once_per_session() throws Exception {
        Post p = createPostBy("user1", "조회수글");
        assertEquals(0, p.getViewCount());

        MockHttpSession session = new MockHttpSession();
        mvc.perform(get("/posts/" + p.getId()).session(session)).andExpect(status().isOk());
        mvc.perform(get("/posts/" + p.getId()).session(session)).andExpect(status().isOk());

        assertEquals(1, postRepository.findById(p.getId()).orElseThrow().getViewCount());

        MockHttpSession session2 = new MockHttpSession();
        mvc.perform(get("/posts/" + p.getId()).session(session2)).andExpect(status().isOk());

        assertEquals(2, postRepository.findById(p.getId()).orElseThrow().getViewCount());
    }
}
