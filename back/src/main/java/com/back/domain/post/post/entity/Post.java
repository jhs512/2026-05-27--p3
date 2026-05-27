package com.back.domain.post.post.entity;

import com.back.domain.member.entity.Member;
import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member author;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Builder.Default
    private int viewCount = 0;

    @Builder.Default
    private boolean deleted = false;

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void markDeleted() {
        this.deleted = true;
    }

    public boolean isAuthor(long memberId) {
        return author.getId() == memberId;
    }
}
