package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.DailyId;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.service.DailyService;
import com.kdpark.sickdan.service.MealService;
import com.kdpark.sickdan.service.query.DailyQueryService;
import com.kdpark.sickdan.service.query.DailyQueryService.DailyDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DailyApiController {

    private final DailyService dailyService;
    private final MealService mealService;
    private final DailyQueryService dailyQueryService;

    @GetMapping("/api/v1/dailies/month/{yyyymm}")
    public List<DailyDto> getMonthData(@PathVariable String yyyymm, @RequestAttribute String member_id) {
        List<DailyDto> monthData = dailyQueryService.getMonthData(Long.parseLong(member_id), yyyymm);

        return monthData;
    }

    @GetMapping("/api/v1/dailies/day/{yyyymmdd}")
    public DailyDto getDayData(@PathVariable String yyyymmdd, @RequestAttribute String member_id) {
        DailyDto dayData = dailyQueryService.getDayData(Long.parseLong(member_id), yyyymmdd);

        return dayData;
    }

    @PostMapping("/api/v1/meals")
    public void addMeal(@RequestBody MealAddRequest request, @RequestAttribute String member_id) {
        DailyId dailyId = new DailyId(Long.parseLong(member_id), request.date);
        String description = request.getDescription();
        MealCategory category = request.getCategory();

        mealService.record(dailyId, description, category);
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
