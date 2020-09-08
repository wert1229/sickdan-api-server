package com.kdpark.sickdan.domain;

import lombok.AccessLevel;
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
public class Meal {

    @Id @GeneratedValue
    @Column(name = "meal_id")
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private MealCategory category;

    @CreatedDate
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    private LocalDateTime updatedDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "member_id", referencedColumnName = "member_id"),
        @JoinColumn(name = "date", referencedColumnName = "date")
    })
    private Daily daily;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL)
    private List<MealPhoto> photos = new ArrayList<>();

    public static Meal createMeal(String description, MealCategory category) {
        Meal meal = new Meal();
        meal.description = description;
        meal.category = category;
        return meal;
    }

    public void addPhoto(MealPhoto mealPhoto) {
        this.photos.add(mealPhoto);
        mealPhoto.setMeal(this);
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
