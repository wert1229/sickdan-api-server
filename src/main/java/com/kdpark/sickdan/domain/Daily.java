package com.kdpark.sickdan.domain;

import com.kdpark.sickdan.dto.DailyDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@SqlResultSetMapping(
        name="CommentAndLikeCountMapping",
        classes = @ConstructorResult(
                targetClass = DailyDto.DailyCountInfo.class,
                columns = {
                        @ColumnResult(name="commentCount", type = Integer.class),
                        @ColumnResult(name="likeCount", type = Integer.class)
                })
)
public class Daily {

    @EmbeddedId
    private DailyId id;

    private String memo;

    private Double bodyWeight;

    private Integer walkCount;

    @CreatedDate
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    private LocalDateTime updatedDateTime;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "daily", cascade = CascadeType.ALL)
    private List<Meal> meals = new ArrayList<>();

    @OneToMany(mappedBy = "daily", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "daily", cascade = CascadeType.ALL)
    private Set<Likes> likes = new HashSet<>();

    @Builder
    public Daily(DailyId id, String memo, Integer walkCount, Double bodyWeight, Member member) {
        this.id = id;
        this.memo = memo;
        this.walkCount = walkCount;
        this.bodyWeight = bodyWeight;
        setMember(member);
    }

    public void recordMeal(Meal meal) {
        meals.add(meal);
        meal.setDaily(this);
    }

    private void setMember(Member member) {
        this.member = member;
        member.getDailies().add(this);
    }

    public static Daily getDefault(Member member, String date) {
        return Daily.builder()
                .id(new DailyId(member.getId(), date))
                .memo("")
                .bodyWeight(0.0)
                .walkCount(0)
                .member(member)
                .build();
    }

    public void setBodyWeight(Double bodyWeight) {
        this.bodyWeight = bodyWeight;
    }

    public void setWalkCount(int walkCount) {
        this.walkCount = walkCount;
    }

    public void writeComment(Comment comment) {
        this.comments.add(comment);
        comment.setDaily(this);
    }

    public void doLike(Member member) {
        Likes like = Likes.builder()
                .id(new Likes.LikeId(id, member.getId()))
                .daily(this)
                .member(member)
                .build();
        this.getLikes().add(like);
    }

    public void undoLike(Member member) {
        Likes like = Likes.builder()
                .daily(this)
                .member(member)
                .build();
        this.getLikes().remove(like);
    }

    @Embeddable
    @Data
    public static class DailyId implements Serializable {
        private Long memberId;
        private String date;

        public DailyId() {}

        public DailyId(Long memberId, String date) {
            this.memberId = memberId;
            this.date = date;
        }
    }
}
