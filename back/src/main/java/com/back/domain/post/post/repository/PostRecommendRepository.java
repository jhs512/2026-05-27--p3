package com.back.domain.post.post.repository;

import com.back.domain.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.entity.PostRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRecommendRepository extends JpaRepository<PostRecommend, Long> {
    boolean existsByPostAndMember(Post post, Member member);

    Optional<PostRecommend> findByPostAndMember(Post post, Member member);

    long countByPost(Post post);
}
