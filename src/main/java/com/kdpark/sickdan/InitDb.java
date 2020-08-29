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
//        initService.dbInit1();
        // github hook test
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
                    .id(new Daily.DailyId(member.getId(), "20200815"))
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
                    .id(new Daily.DailyId(member.getId(), "20200816"))
                    .bodyWeight(76)
                    .walkCount(9000)
                    .member(member)
                    .build();

            daily2.recordMeal(Meal.createMeal("pizza2", MealCategory.BREAKFAST));
            daily2.recordMeal(Meal.createMeal("rice2", MealCategory.LUNCH));
            daily2.recordMeal(Meal.createMeal("chicken2", MealCategory.DINNER));
            daily2.recordMeal(Meal.createMeal("icecream2", MealCategory.DINNER));

            em.persist(daily2);


            Member member2 = Member.builder()
                    .email("wert1220@naver.com")
                    .password("{noop}1234")
                    .displayName("wert1")
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

            em.persist(member2);

            Daily daily3 = Daily.builder()
                    .id(new Daily.DailyId(member2.getId(), "20200815"))
                    .bodyWeight(50)
                    .walkCount(20000)
                    .member(member2)
                    .build();
            daily3.recordMeal(Meal.createMeal("pizza3", MealCategory.BREAKFAST));
            daily3.recordMeal(Meal.createMeal("rice3", MealCategory.LUNCH));
            daily3.recordMeal(Meal.createMeal("chicken3", MealCategory.DINNER));
            daily3.recordMeal(Meal.createMeal("icecream3", MealCategory.DINNER));

            em.persist(daily3);


            Daily daily4 = Daily.builder()
                    .id(new Daily.DailyId(member2.getId(), "20200816"))
                    .bodyWeight(52)
                    .walkCount(19000)
                    .member(member2)
                    .build();

            daily4.recordMeal(Meal.createMeal("pizza4", MealCategory.BREAKFAST));
            daily4.recordMeal(Meal.createMeal("rice4", MealCategory.LUNCH));
            daily4.recordMeal(Meal.createMeal("chicken4", MealCategory.DINNER));
            daily4.recordMeal(Meal.createMeal("icecream4", MealCategory.DINNER));

            em.persist(daily4);

            MemberRelationship relating = MemberRelationship.builder()
                    .id(new MemberRelationship.MemberRelationshipId(member.getId(), member2.getId()))
                    .relatingMember(member)
                    .relatedMember(member2)
                    .status(RelationshipStatus.REQUESTING)
                    .build();

            MemberRelationship related = MemberRelationship.builder()
                    .id(new MemberRelationship.MemberRelationshipId(member2.getId(), member.getId()))
                    .relatingMember(member2)
                    .relatedMember(member)
                    .status(RelationshipStatus.REQUESTED)
                    .build();

            em.persist(relating);
            em.persist(related);

            Member member3 = Member.builder()
                    .email("wert1228@naver.com")
                    .password("{noop}1234")
                    .displayName("requestTest")
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

            em.persist(member3);
        }

    }
}
