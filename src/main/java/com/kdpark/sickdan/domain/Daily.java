package com.kdpark.sickdan.domain;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
//@Table(
//    uniqueConstraints = {
//        @UniqueConstraint(
//            columnNames = {"member_id", "date"}
//        )
//    }
//)
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Daily {

    @EmbeddedId
    private DailyId dailyId;
//    @Id @GeneratedValue
//    @Column(name = "daily_id")
//    private Long id;

//    private String date;

    private String memo;

    private Float bodyWeight;

    private int walkCount;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "daily", cascade = CascadeType.ALL)
    private List<Meal> meals = new ArrayList<>();

    protected Daily() {}

    @Builder
    public Daily(DailyId dailyId, String memo, int walkCount, float bodyWeight, Member member) {
        this.dailyId = dailyId;
        this.memo = memo;
        this.walkCount = walkCount;
        this.bodyWeight = bodyWeight;
        this.member = member;
    }

    public void recordMeal(Meal meal) {
        meals.add(meal);
        meal.setDaily(this);
    }

    private void setMember(Member member) {
        this.member = member;
        member.getDailies().add(this);
    }
}
