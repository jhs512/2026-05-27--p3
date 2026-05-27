package com.back.domain.member.controller;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.security.SecurityUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Getter
    @Setter
    public static class JoinForm {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        @NotBlank
        private String nickname;
    }

    @GetMapping("/join")
    public String joinForm(@ModelAttribute JoinForm joinForm) {
        return "member/join";
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "member/join";
        }

        try {
            memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getNickname());
        } catch (IllegalStateException e) {
            bindingResult.reject("joinFailed", e.getMessage());
            return "member/join";
        }

        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    @Getter
    @Setter
    public static class ModifyForm {
        @NotBlank
        private String nickname;
        // 비밀번호는 비워두면 변경하지 않음
        private String password;
    }

    @GetMapping("/me")
    public String me(@AuthenticationPrincipal SecurityUser user, Model model) {
        Member member = memberService.findById(user.getId()).orElseThrow();
        model.addAttribute("member", member);

        ModifyForm form = new ModifyForm();
        form.setNickname(member.getNickname());
        model.addAttribute("modifyForm", form);

        return "member/me";
    }

    @PostMapping("/me")
    public String modify(
            @AuthenticationPrincipal SecurityUser user,
            @Valid ModifyForm modifyForm,
            BindingResult bindingResult,
            Model model
    ) {
        Member member = memberService.findById(user.getId()).orElseThrow();

        if (bindingResult.hasErrors()) {
            model.addAttribute("member", member);
            return "member/me";
        }

        memberService.modify(member, modifyForm.getNickname(), modifyForm.getPassword());

        return "redirect:/member/me";
    }
}
