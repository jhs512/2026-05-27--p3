package com.back.domain.post.postComment.service;

import com.back.domain.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;

    @Transactional
    public PostComment write(Post post, Member author, String content, PostComment parent) {
        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 글에는 댓글을 달 수 없습니다.");
        }
        if (parent != null) {
            // 대댓글의 대댓글 금지 (깊이 1단계)
            if (parent.isReply()) {
                throw new IllegalStateException("대댓글에는 답글을 달 수 없습니다.");
            }
            if (!parent.getPost().getId().equals(post.getId())) {
                throw new IllegalStateException("부모 댓글이 해당 글의 댓글이 아닙니다.");
            }
            if (parent.isDeleted()) {
                throw new IllegalStateException("삭제된 댓글에는 답글을 달 수 없습니다.");
            }
        }

        PostComment comment = PostComment.builder()
                .post(post)
                .author(author)
                .parent(parent)
                .content(content)
                .build();
        return postCommentRepository.save(comment);
    }

    public PostComment getComment(long id) {
        return postCommentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("댓글이 존재하지 않습니다."));
    }

    public List<PostComment> getTopLevelComments(Post post) {
        return postCommentRepository.findByPostAndParentIsNullOrderByIdAsc(post);
    }

    @Transactional
    public void modify(PostComment comment, String content) {
        if (comment.isDeleted()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }
        comment.setContent(content);
    }

    @Transactional
    public void delete(PostComment comment) {
        // 대댓글 유무와 무관하게 항상 소프트 삭제 (ADR-0001)
        comment.markDeleted();
    }

    public long count() {
        return postCommentRepository.count();
    }
}
