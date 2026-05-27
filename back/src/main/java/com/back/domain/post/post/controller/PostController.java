package com.back.domain.post.post.controller;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostRecommendService;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.post.service.PostViewService;
import com.back.domain.post.postComment.service.PostCommentService;
import com.back.global.security.SecurityUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostViewService postViewService;
    private final PostRecommendService postRecommendService;
    private final PostCommentService postCommentService;
    private final MemberService memberService;

    @Getter
    @Setter
    public static class PostForm {
        @NotBlank
        private String title;
        @NotBlank
        private String content;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("postPage", postService.getList(page));
        return "post/list";
    }

    @GetMapping("/write")
    public String writeForm(@ModelAttribute PostForm postForm) {
        return "post/write";
    }

    @PostMapping("/write")
    public String write(
            @AuthenticationPrincipal SecurityUser user,
            @Valid PostForm postForm,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "post/write";
        }
        Member author = currentMember(user);
        Post post = postService.write(author, postForm.getTitle(), postForm.getContent());
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable long id,
            HttpSession session,
            @AuthenticationPrincipal SecurityUser user,
            Model model
    ) {
        Post post = postService.getPost(id);
        postViewService.viewOnce(post, session);

        Member member = currentMember(user);
        model.addAttribute("post", post);
        model.addAttribute("member", member);
        model.addAttribute("postRecommendCount", postRecommendService.count(post));
        model.addAttribute("postRecommended", postRecommendService.isRecommended(post, member));
        model.addAttribute("comments", postCommentService.getTopLevelComments(post));
        return "post/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user,
            Model model
    ) {
        Post post = postService.getPost(id);
        checkCanEdit(post.getAuthor().getId(), user);
        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 글은 수정할 수 없습니다.");
        }

        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setContent(post.getContent());
        model.addAttribute("postForm", form);
        model.addAttribute("postId", id);
        return "post/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user,
            @Valid PostForm postForm,
            BindingResult bindingResult,
            Model model
    ) {
        Post post = postService.getPost(id);
        checkCanEdit(post.getAuthor().getId(), user);

        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id);
            return "post/edit";
        }

        postService.modify(post, postForm.getTitle(), postForm.getContent());
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user
    ) {
        Post post = postService.getPost(id);
        checkCanEdit(post.getAuthor().getId(), user);
        postService.delete(post);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/recommend")
    public String recommend(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user
    ) {
        Post post = postService.getPost(id);
        postRecommendService.toggle(post, currentMember(user));
        return "redirect:/posts/" + id;
    }

    private Member currentMember(SecurityUser user) {
        if (user == null) return null;
        return memberService.findById(user.getId()).orElse(null);
    }

    private void checkCanEdit(long authorId, SecurityUser user) {
        if (user == null) {
            throw new AccessDeniedException("로그인이 필요합니다.");
        }
        if (authorId != user.getId() && !user.isAdmin()) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }
}
