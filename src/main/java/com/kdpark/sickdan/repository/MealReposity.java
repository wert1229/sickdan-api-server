package com.kdpark.sickdan.repository;

import com.kdpark.sickdan.domain.Meal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class MealReposity {

    private final EntityManager em;

    public void save(Meal meal) {
        em.persist(meal);
    }

    public Meal findById(Long id) {
        return em.find(Meal.class, id);
    }
}
