package com.kdpark.sickdan.domain;

import lombok.Builder;
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

    private String fileUrl;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    protected MealPhoto() {}

    @Builder
    public MealPhoto(Long id, String fileOriginName, String fileName, String fileUrl, Long fileSize, Meal meal) {
        this.id = id;
        this.fileOriginName = fileOriginName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        setMeal(meal);
    }

    public void setMeal(Meal meal) {
        this.meal = meal;
        meal.getPhotos().add(this);
    }
}
