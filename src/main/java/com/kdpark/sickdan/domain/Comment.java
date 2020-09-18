package com.kdpark.sickdan.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    private LocalDateTime updatedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_member_id", referencedColumnName = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
            @JoinColumn(name = "date", referencedColumnName = "date")
    })
    private Daily daily;

    @Builder
    public Comment(Long id, String description, Comment parent, List<Comment> replies, LocalDateTime createdDateTime, LocalDateTime updatedDateTime, Member writer, Daily daily) {
        this.id = id;
        this.description = description;
        this.parent = parent;
        this.replies = replies;
        this.createdDateTime = createdDateTime;
        this.updatedDateTime = updatedDateTime;
        this.writer = writer;
        this.daily = daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }
}
