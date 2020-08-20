package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MealReposity;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MealService {

    private final MealReposity mealReposity;
    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public Long record(DailyId dailyId, String description, MealCategory category) {
        Daily daily = dailyRepository.findById(dailyId);

        // TODO: this is for test. rewrite it
        if (daily == null) {
            Member member = memberRepository.findById(dailyId.getMemberId());
            daily = Daily.builder()
                    .dailyId(dailyId)
                    .member(member)
                    .build();
        }

        Meal meal = Meal.createMeal(description, category);
        daily.recordMeal(meal);

        dailyRepository.save(daily);

        return meal.getId();
    }
}
