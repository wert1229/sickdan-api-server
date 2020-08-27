package com.kdpark.sickdan.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class MealPhoto {

    @Id @GeneratedValue
    @Column(name = "photo_id")
    private Long id;

    private String fileOriginName;

    private String fileName;

    private String filePath;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    protected MealPhoto() {}

    public MealPhoto(Long id, String fileName, String filePath, Meal meal) {
        this.id = id;
        this.fileName = fileName;
        this.filePath = filePath;
        setMeal(meal);
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
        meal.getPhotos().add(this);
    }
}
