package com.kdpark.sickdan.service.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyQueryService {

    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public List<DailyDto> getMonthData(Long memberId, String yyyymm) {
        List<Daily> monthData = dailyRepository.findOneMonth(memberId, yyyymm);

        return monthData.stream()
                .map(d -> new DailyDto(d))
                .collect(Collectors.toList());
    }

    public DailyDto getDayData(Long memberId, String yyyymmdd) {
        DailyId dailyId = new DailyId(memberId, yyyymmdd);
        Daily dayData = dailyRepository.findById(dailyId);

        if (dayData == null) {
            dayData = Daily.builder()
                    .dailyId(dailyId)
                    .build();
        }

        return new DailyDto(dayData);
    }

    @Data
    static public class DailyDto {
        private String date;
        private String memo;
        private int walkCount;
        private float bodyWeight;
        private List<MealDto> meals;

        public DailyDto(Daily daily) {
            this.date = daily.getDailyId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
            this.meals = daily.getMeals().stream()
                    .map(m -> new MealDto(m))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealDto {
        private String description;
        private MealCategory category;

        public MealDto(Meal meal) {
            this.description = meal.getDescription();
            this.category = meal.getCategory();
        }
    }
}
