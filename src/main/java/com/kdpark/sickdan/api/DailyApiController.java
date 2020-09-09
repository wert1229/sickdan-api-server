package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.service.DailyService;
import com.kdpark.sickdan.service.query.DailyQueryService;
import com.kdpark.sickdan.service.query.DailyQueryService.DayDailyDto;
import com.kdpark.sickdan.service.query.DailyQueryService.MonthDailyDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class DailyApiController {
    private final DailyQueryService dailyQueryService;
    private final DailyService dailyService;

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

    @GetMapping("/api/v1/members/{memberId}/dailies")
    public List<MonthDailyDto> getMonthData(@PathVariable Long memberId, @RequestParam String yyyymm) {
        List<MonthDailyDto> monthData = dailyQueryService.getMonthData(memberId, yyyymm);

        return monthData;
    }

    @GetMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}")
    public DayDailyDto getDayData(@PathVariable Long memberId, @PathVariable String yyyymmdd) {
        DayDailyDto dayData = dailyQueryService.getDayData(memberId, yyyymmdd);

        return dayData;
    }

    @PutMapping("/api/v1/members/me/dailies/{yyyymmdd}")
    public void editDayData(@PathVariable String yyyymmdd, @RequestAttribute Long member_id,
                            @RequestBody Map<String, Object> params) {

        dailyService.editDaily(new Daily.DailyId(member_id, yyyymmdd), params);
    }

    @PutMapping("/api/v1/members/me/dailies")
    public void editDayData(@RequestAttribute Long member_id, @RequestBody Map<String, Integer> params) {
        dailyService.syncWalkCounts(member_id, params);
    }

    @Data
    static class DailyResult<T> {
        private T data;

        public DailyResult(T data) {
            this.data = data;
        }
    }
}