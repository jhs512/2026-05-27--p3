package com.back.domain.post.post.service;

import com.back.domain.post.post.entity.Post;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostViewService {

    static final String SESSION_KEY = "viewedPostIds";

    /**
     * 같은 세션에서 처음 보는 글이면 조회수를 1 증가시킨다.
     * 동일 세션 재방문/새로고침은 집계하지 않으며, 삭제된 글도 집계하지 않는다.
     */
    @Transactional
    public void viewOnce(Post post, HttpSession session) {
        if (post.isDeleted()) return;

        @SuppressWarnings("unchecked")
        Set<Long> viewed = (Set<Long>) session.getAttribute(SESSION_KEY);
        if (viewed == null) {
            viewed = new HashSet<>();
        }

        if (!viewed.contains(post.getId())) {
            post.increaseViewCount();
            viewed.add(post.getId());
            session.setAttribute(SESSION_KEY, viewed);
        }
    }
}
