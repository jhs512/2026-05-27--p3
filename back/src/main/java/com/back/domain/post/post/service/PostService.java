package com.back.domain.post.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private static final int PAGE_SIZE = 10;

    private final PostRepository postRepository;

    @Transactional
    public Post write(Member author, String title, String content) {
        Post post = Post.builder()
                .author(author)
                .title(title)
                .content(content)
                .build();
        return postRepository.save(post);
    }

    public Page<Post> getList(int page) {
        return postRepository.findAllByOrderByIdDesc(PageRequest.of(page, PAGE_SIZE));
    }

    public Post getPost(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("글이 존재하지 않습니다."));
    }

    @Transactional
    public void modify(Post post, String title, String content) {
        if (post.isDeleted()) {
            throw new IllegalStateException("삭제된 글은 수정할 수 없습니다.");
        }
        post.setTitle(title);
        post.setContent(content);
    }

    @Transactional
    public void delete(Post post) {
        // 소프트 삭제: 물리 삭제하지 않고 deleted 플래그만 설정 (ADR-0001)
        post.markDeleted();
    }

    public long count() {
        return postRepository.count();
    }
}
