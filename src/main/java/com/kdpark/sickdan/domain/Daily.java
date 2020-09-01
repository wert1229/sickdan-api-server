package com.kdpark.sickdan.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Daily {

    @EmbeddedId
    private DailyId id;

    private String memo;

    private Float bodyWeight;

    private int walkCount;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "daily", cascade = CascadeType.ALL)
    private List<Meal> meals = new ArrayList<>();

    @Builder
    public Daily(DailyId id, String memo, int walkCount, float bodyWeight, Member member) {
        this.id = id;
        this.memo = memo;
        this.walkCount = walkCount;
        this.bodyWeight = bodyWeight;
        setMember(member);
    }

    public void recordMeal(Meal meal) {
        if (meals.size() == 0) meal.setPrevMeal(0L);
        else {
            meal.setPrevMeal(meals.get(meals.size() - 1).getId());
        }

        meals.add(meal);
        meal.setDaily(this);
    }

    public void reorderMeals(Map<Long, Long> map) {
        for (Meal meal : meals) {
            Long afterPrevMealId = map.get(meal.getId());
            if (afterPrevMealId == null || meal.getPrevMeal().equals(afterPrevMealId)) continue;
            meal.setPrevMeal(map.get(meal.getId()));
        }
    }

    private void setMember(Member member) {
        this.member = member;
        member.getDailies().add(this);
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
