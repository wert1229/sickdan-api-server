package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.service.DailyService;
import com.kdpark.sickdan.service.MealService;
import com.kdpark.sickdan.service.query.DailyQueryService;
import com.kdpark.sickdan.service.query.DailyQueryService.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.UUID;

@Log4j2
@RestController
@RequiredArgsConstructor
public class DailyApiController {

    private final DailyService dailyService;
    private final MealService mealService;
    private final DailyQueryService dailyQueryService;

    @GetMapping("/api/v1/members/me/dailies")
    public List<MonthDailyDto> getMonthData(@RequestParam String yyyymm, @RequestAttribute Long member_id) {
        List<MonthDailyDto> monthData = dailyQueryService.getMonthData(member_id, yyyymm);
        //dockerTest
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
    public void uploadMealPhoto(@PathVariable Long mealId, @RequestPart MultipartFile files, HttpSession session) {
        try {
            String originFileName = files.getOriginalFilename();
            String filename = String.format("%s_%s", UUID.randomUUID().toString(), originFileName);
            String savePath = System.getProperty("user.dir") + "/src/main/resources/static/images";

            if (!new File(savePath).exists()) new File(savePath).mkdir();

            String filePath = savePath + "/" + filename;
            files.transferTo(new File(filePath));

        } catch(Exception e) {
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
