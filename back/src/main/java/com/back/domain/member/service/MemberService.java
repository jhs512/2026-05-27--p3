package com.back.domain.member.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member join(String username, String password, String nickname) {
        return join(username, password, nickname, "USER");
    }

    @Transactional
    public Member join(String username, String password, String nickname, String role) {
        memberRepository.findByUsername(username).ifPresent(m -> {
            throw new IllegalStateException("이미 사용 중인 username입니다.");
        });

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .role(role)
                .build();

        return memberRepository.save(member);
    }

    @Transactional
    public void modify(Member member, String nickname, String password) {
        member.setNickname(nickname);
        if (password != null && !password.isBlank()) {
            member.setPassword(passwordEncoder.encode(password));
        }
    }

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findById(long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }
}
