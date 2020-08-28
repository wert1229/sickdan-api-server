package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.service.MealService;
import com.kdpark.sickdan.service.S3Service;
import com.kdpark.sickdan.service.query.DailyQueryService;
import com.kdpark.sickdan.service.query.DailyQueryService.DayDailyDto;
import com.kdpark.sickdan.service.query.DailyQueryService.MonthDailyDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
public class DailyApiController {

    private final MealService mealService;
    private final DailyQueryService dailyQueryService;
    private final S3Service s3Service;

    @GetMapping("/api/v1/members/me/dailies")
    public List<MonthDailyDto> getMonthData(@RequestParam String yyyymm, @RequestAttribute Long member_id) {
        List<MonthDailyDto> monthData = dailyQueryService.getMonthData(member_id, yyyymm);

        return monthData;
    }

    @GetMapping("/api/v1/members/me/dailies/{yyyymmdd}")
    public DayDailyDto getDayData(@PathVariable String yyyymmdd, @RequestAttribute Long member_id) {
        DayDailyDto dayData = dailyQueryService.getDayData(member_id, yyyymmdd);

        return dayData;
    }

    @GetMapping("/api/v1/members/{memberId}}/dailies")
    public List<MonthDailyDto> getMonthData(@PathVariable Long memberId, @RequestParam String yyyymm) {
        List<MonthDailyDto> monthData = dailyQueryService.getMonthData(memberId, yyyymm);

        return monthData;
    }

    @GetMapping("/api/v1/members/{memberId}}/dailies/{yyyymmdd}")
    public DayDailyDto getDayData(@PathVariable Long memberId, @PathVariable String yyyymmdd) {
        DayDailyDto dayData = dailyQueryService.getDayData(memberId, yyyymmdd);

        return dayData;
    }

    @PostMapping("/api/v1/meals")
    public void addMeal(@RequestBody MealAddRequest request, @RequestAttribute Long member_id) {
        Daily.DailyId dailyId = new Daily.DailyId(member_id, request.date);
        String description = request.getDescription();
        MealCategory category = request.getCategory();

        mealService.record(dailyId, description, category);
    }

    @PostMapping("/api/v1/meals/{mealId}/photos")
    public void uploadMealPhoto(@PathVariable Long mealId, @RequestPart MultipartFile file) {
        try {
            String originFileName = file.getOriginalFilename();
            String ext = originFileName.substring(originFileName.lastIndexOf(".") + 1);
            String fileName = String.format("%d_%s.%s", mealId, UUID.randomUUID().toString(), ext);

            String url = s3Service.upload(file, fileName);

            //TODO: DB 저장
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    static class DailyResult<T> {
        private T data;

        public DailyResult(T data) {
            this.data = data;
        }
    }

    @Data
    static class MealAddRequest {
        private String date;
        private String description;
        private MealCategory category;
    }
}
