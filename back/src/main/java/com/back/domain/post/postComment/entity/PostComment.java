package com.back.domain.post.postComment.entity;

import com.back.domain.member.entity.Member;
import com.back.domain.post.post.entity.Post;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member author;

    // null 이면 최상위 댓글, 값이 있으면 대댓글(깊이 1단계)
    @ManyToOne(fetch = FetchType.LAZY)
    private PostComment parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("id asc")
    @Builder.Default
    private List<PostComment> replies = new ArrayList<>();

    @Lob
    @Column(nullable = false)
    private String content;

    @Builder.Default
    private boolean deleted = false;

    public boolean isReply() {
        return parent != null;
    }

    public void markDeleted() {
        this.deleted = true;
    }

    public boolean isAuthor(long memberId) {
        return author.getId() == memberId;
    }
}
