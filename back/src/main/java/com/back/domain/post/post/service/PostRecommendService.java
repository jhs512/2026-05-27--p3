package com.back.domain.post.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.entity.PostRecommend;
import com.back.domain.post.post.repository.PostRecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostRecommendService {

    private final PostRecommendRepository postRecommendRepository;

    /**
     * 추천 토글. 자기추천 허용(작성자==추천자 검증 없음).
     * @return 토글 후 추천 상태(true=추천됨, false=취소됨)
     */
    @Transactional
    public boolean toggle(Post post, Member member) {
        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 글은 추천할 수 없습니다.");
        }

        Optional<PostRecommend> existing = postRecommendRepository.findByPostAndMember(post, member);
        if (existing.isPresent()) {
            postRecommendRepository.delete(existing.get());
            return false;
        }

        postRecommendRepository.save(
                PostRecommend.builder().post(post).member(member).build()
        );
        return true;
    }

    public boolean isRecommended(Post post, Member member) {
        if (member == null) return false;
        return postRecommendRepository.existsByPostAndMember(post, member);
    }

    public long count(Post post) {
        return postRecommendRepository.countByPost(post);
    }
}
