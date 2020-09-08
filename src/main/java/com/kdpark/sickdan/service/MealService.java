package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MealReposity;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class MealService {

    private final MealReposity mealReposity;
    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public Long record(Daily.DailyId dailyId, String description, MealCategory category) {
        Daily daily = dailyRepository.findById(dailyId);

        if (daily == null) {
            Member member = memberRepository.findById(dailyId.getMemberId());
            daily = Daily.builder()
                    .id(dailyId)
                    .member(member)
                    .build();

            dailyRepository.save(daily);
        }

        Meal meal = Meal.createMeal(description, category);
        daily.recordMeal(meal);

        return meal.getId();
    }

    public Meal findById(Long mealId) {
        return mealReposity.findById(mealId);
    }

    public void addPhoto(Long mealId, String originFileName, String fileName, Long fileSize, String fileUrl) {
        Meal meal = mealReposity.findById(mealId);

        if (meal == null) throw new EntityNotFoundException("해당 식단을 찾을 수 없습니다", ErrorCode.ENTITY_NOT_FOUND);

        MealPhoto photo = MealPhoto.builder()
                .fileName(fileName)
                .fileOriginName(originFileName)
                .fileSize(fileSize)
                .fileUrl(fileUrl)
                .meal(meal)
                .build();

        meal.addPhoto(photo);
    }

    public void editMeal(Long mealId, String desc) {
        Meal meal = mealReposity.findById(mealId);
        meal.setDescription(desc);
    }
}
