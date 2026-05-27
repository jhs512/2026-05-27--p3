package com.back.domain.post.postComment.repository;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.postComment.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findByPostAndParentIsNullOrderByIdAsc(Post post);
}
