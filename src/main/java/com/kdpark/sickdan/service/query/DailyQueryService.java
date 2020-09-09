package com.kdpark.sickdan.service.query;

import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyQueryService {

    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public List<MonthDailyDto> getMonthData(Long memberId, String yyyymm) {
        List<Daily> monthData = dailyRepository.findOneMonth(memberId, yyyymm);

        return monthData.stream()
                .map(MonthDailyDto::new)
                .collect(Collectors.toList());
    }

    public DayDailyDto getDayData(Long memberId, String yyyymmdd) {
        Daily.DailyId dailyId = new Daily.DailyId(memberId, yyyymmdd);
        Daily dayData = dailyRepository.findById(dailyId);

        if (dayData == null) {
            Member member = memberRepository.findById(dailyId.getMemberId());
            dayData = Daily.builder()
                    .id(dailyId)
                    .memo("")
                    .bodyWeight(0.0)
                    .walkCount(0)
                    .member(member)
                    .build();
        }

        return new DayDailyDto(dayData);
    }

    @Data
    static public class MonthDailyDto {
        private String date;
        private String memo;
        private int walkCount;
        private double bodyWeight;

        public MonthDailyDto(Daily daily) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
        }
    }

    @Data
    static public class DayDailyDto {
        private String date;
        private String memo;
        private Integer walkCount;
        private Double bodyWeight;
        private List<MealDto> meals;

        public DayDailyDto(Daily daily) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
            this.meals = daily.getMeals().stream()
                    .sorted(Comparator.comparing(Meal::getCreatedDateTime))
                    .map(MealDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealDto {
        private Long id;
        private String description;
        private MealCategory category;
        private List<MealPhotoDto> photos;

        public MealDto(Meal meal) {
            this.id = meal.getId();
            this.description = meal.getDescription();
            this.category = meal.getCategory();
            this.photos = meal.getPhotos().stream()
                    .map(MealPhotoDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealPhotoDto {
        private Long id;
        private String fileOriginName;
        private String fileName;
        private String fileUrl;

        public MealPhotoDto(MealPhoto photo) {
            this.id = photo.getId();
            this.fileOriginName = photo.getFileOriginName();
            this.fileName = photo.getFileName();
            this.fileUrl = photo.getFileUrl();
        }
    }
}
