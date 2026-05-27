package com.back.domain.post.postComment.controller;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.service.PostCommentRecommendService;
import com.back.domain.post.postComment.service.PostCommentService;
import com.back.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PostCommentController {

    private final PostService postService;
    private final PostCommentService postCommentService;
    private final PostCommentRecommendService postCommentRecommendService;
    private final MemberService memberService;

    @PostMapping("/posts/{postId}/comments")
    public String write(
            @PathVariable long postId,
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId
    ) {
        if (content == null || content.isBlank()) {
            return "redirect:/posts/" + postId;
        }

        Post post = postService.getPost(postId);
        Member author = currentMember(user);
        PostComment parent = (parentId != null) ? postCommentService.getComment(parentId) : null;
        postCommentService.write(post, author, content, parent);
        return "redirect:/posts/" + postId;
    }

    @GetMapping("/comments/{id}/edit")
    public String editForm(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user,
            Model model
    ) {
        PostComment comment = postCommentService.getComment(id);
        checkCanEdit(comment.getAuthor().getId(), user);
        model.addAttribute("comment", comment);
        return "comment/edit";
    }

    @PostMapping("/comments/{id}/edit")
    public String edit(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user,
            @RequestParam String content
    ) {
        PostComment comment = postCommentService.getComment(id);
        checkCanEdit(comment.getAuthor().getId(), user);
        postCommentService.modify(comment, content);
        return "redirect:/posts/" + comment.getPost().getId();
    }

    @PostMapping("/comments/{id}/delete")
    public String delete(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user
    ) {
        PostComment comment = postCommentService.getComment(id);
        checkCanEdit(comment.getAuthor().getId(), user);
        long postId = comment.getPost().getId();
        postCommentService.delete(comment);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/recommend")
    public String recommend(
            @PathVariable long id,
            @AuthenticationPrincipal SecurityUser user
    ) {
        PostComment comment = postCommentService.getComment(id);
        postCommentRecommendService.toggle(comment, currentMember(user));
        return "redirect:/posts/" + comment.getPost().getId();
    }

    private Member currentMember(SecurityUser user) {
        return memberService.findById(user.getId()).orElseThrow();
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
