package com.kdpark.sickdan;

import com.kdpark.sickdan.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = Member.builder()
                    .email("wert1229@naver.com")
                    .password("{noop}1234")
                    .displayName("wert")
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

            em.persist(member);

            Daily daily1 = Daily.builder()
                    .dailyId(new DailyId(member.getId(), "20200815"))
                    .bodyWeight(75)
                    .walkCount(10000)
                    .member(member)
                    .build();
            daily1.recordMeal(Meal.createMeal("pizza1", MealCategory.BREAKFAST));
            daily1.recordMeal(Meal.createMeal("rice1", MealCategory.LUNCH));
            daily1.recordMeal(Meal.createMeal("chicken1", MealCategory.DINNER));
            daily1.recordMeal(Meal.createMeal("icecream1", MealCategory.DINNER));

            em.persist(daily1);


            Daily daily2 = Daily.builder()
                    .dailyId(new DailyId(member.getId(), "20200816"))
                    .bodyWeight(76)
                    .walkCount(9000)
                    .member(member)
                    .build();

            daily2.recordMeal(Meal.createMeal("pizza2", MealCategory.BREAKFAST));
            daily2.recordMeal(Meal.createMeal("rice2", MealCategory.LUNCH));
            daily2.recordMeal(Meal.createMeal("chicken2", MealCategory.DINNER));
            daily2.recordMeal(Meal.createMeal("icecream2", MealCategory.DINNER));

            em.persist(daily2);
        }
    }
}
