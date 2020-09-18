package com.kdpark.sickdan.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {

    @EmbeddedId
    LikeId id;

    @MapsId("likedMemberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liked_member_id", referencedColumnName = "member_id")
    private Member member;

    @MapsId("dailyId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
            @JoinColumn(name = "date", referencedColumnName = "date")
    })
    private Daily daily;

    @Builder
    public Likes(LikeId id, Daily daily, Member member) {
        this.id = id;
        this.daily = daily;
        this.member = member;
    }

    @Embeddable
    @Data
    public static class LikeId implements Serializable {
        private Daily.DailyId dailyId;
        private Long likedMemberId;

        public LikeId() {}

        public LikeId(Daily.DailyId dailyId, Long likedMemberId) {
            this.dailyId = dailyId;
            this.likedMemberId = likedMemberId;
        }
    }
}
