package com.back.domain.post.postComment.service;

import com.back.domain.member.entity.Member;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.entity.PostCommentRecommend;
import com.back.domain.post.postComment.repository.PostCommentRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostCommentRecommendService {

    private final PostCommentRecommendRepository postCommentRecommendRepository;

    /**
     * 댓글 추천 토글. 자기추천 허용.
     * @return 토글 후 추천 상태(true=추천됨, false=취소됨)
     */
    @Transactional
    public boolean toggle(PostComment postComment, Member member) {
        if (postComment.isDeleted()) {
            throw new IllegalStateException("삭제된 댓글은 추천할 수 없습니다.");
        }

        Optional<PostCommentRecommend> existing =
                postCommentRecommendRepository.findByPostCommentAndMember(postComment, member);
        if (existing.isPresent()) {
            postCommentRecommendRepository.delete(existing.get());
            return false;
        }

        postCommentRecommendRepository.save(
                PostCommentRecommend.builder().postComment(postComment).member(member).build()
        );
        return true;
    }

    public boolean isRecommended(PostComment postComment, Member member) {
        if (member == null) return false;
        return postCommentRecommendRepository.existsByPostCommentAndMember(postComment, member);
    }

    public long count(PostComment postComment) {
        return postCommentRecommendRepository.countByPostComment(postComment);
    }
}
