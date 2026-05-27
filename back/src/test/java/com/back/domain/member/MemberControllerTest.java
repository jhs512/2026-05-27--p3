package com.back.domain.member;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MemberControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 - username/password/nickname 으로 가입, 비밀번호 BCrypt 해시")
    void join() throws Exception {
        mvc.perform(post("/member/join").with(csrf())
                        .param("username", "newbie")
                        .param("password", "pw1234")
                        .param("nickname", "뉴비"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));

        Member m = memberRepository.findByUsername("newbie").orElseThrow();
        assertEquals("뉴비", m.getNickname());
        assertNotEquals("pw1234", m.getPassword());
        assertTrue(passwordEncoder.matches("pw1234", m.getPassword()));
    }

    @Test
    @DisplayName("중복 username 가입 거부 - 폼 재렌더(리다이렉트 없음)")
    void join_duplicate() throws Exception {
        mvc.perform(post("/member/join").with(csrf())
                        .param("username", "user1")
                        .param("password", "pw1234")
                        .param("nickname", "중복"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("가입 입력 검증 실패 - 빈 값이면 폼 재렌더")
    void join_validation() throws Exception {
        mvc.perform(post("/member/join").with(csrf())
                        .param("username", "")
                        .param("password", "")
                        .param("nickname", ""))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 성공 - 인증 세션 생성 후 / 로 리다이렉트")
    void login_success() throws Exception {
        mvc.perform(formLogin("/member/login").user("user1").password("1234"))
                .andExpect(authenticated())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호면 미인증")
    void login_fail() throws Exception {
        mvc.perform(formLogin("/member/login").user("user1").password("wrong"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("비로그인 보호 페이지 접근 시 로그인으로 리다이렉트")
    void protected_redirect() throws Exception {
        mvc.perform(get("/member/me"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/member/login"));
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("내 정보 - 닉네임 수정")
    void modify_nickname() throws Exception {
        mvc.perform(post("/member/me").with(csrf())
                        .param("nickname", "변경된닉")
                        .param("password", ""))
                .andExpect(redirectedUrl("/member/me"));

        Member m = memberRepository.findByUsername("user1").orElseThrow();
        assertEquals("변경된닉", m.getNickname());
    }

    @Test
    @WithUserDetails("user1")
    @DisplayName("내 정보 - 비밀번호 변경 시 새 BCrypt 해시 저장")
    void modify_password() throws Exception {
        mvc.perform(post("/member/me").with(csrf())
                        .param("nickname", "유저1")
                        .param("password", "newpw99"))
                .andExpect(redirectedUrl("/member/me"));

        Member m = memberRepository.findByUsername("user1").orElseThrow();
        assertTrue(passwordEncoder.matches("newpw99", m.getPassword()));
    }
}
