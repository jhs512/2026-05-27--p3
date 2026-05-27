package com.back.domain.post.postComment.repository;

import com.back.domain.member.entity.Member;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.entity.PostCommentRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentRecommendRepository extends JpaRepository<PostCommentRecommend, Long> {
    boolean existsByPostCommentAndMember(PostComment postComment, Member member);

    Optional<PostCommentRecommend> findByPostCommentAndMember(PostComment postComment, Member member);

    long countByPostComment(PostComment postComment);
}
