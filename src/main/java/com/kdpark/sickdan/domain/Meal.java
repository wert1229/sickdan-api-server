package com.kdpark.sickdan.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meal {

    @Id @GeneratedValue
    @Column(name = "meal_id")
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private MealCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
        @JoinColumn(name = "date", referencedColumnName = "date")
    })
    private Daily daily;

    protected Meal() {}

    public static Meal createMeal(String description, MealCategory category) {
        Meal meal = new Meal();
        meal.description = description;
        meal.category = category;

        return meal;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }
}
