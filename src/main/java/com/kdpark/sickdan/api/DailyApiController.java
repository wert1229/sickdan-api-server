package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Likes;
import com.kdpark.sickdan.service.DailyService;
import com.kdpark.sickdan.service.query.DailyQueryService;
import com.kdpark.sickdan.service.query.DailyQueryService.DailyCommentDto;
import com.kdpark.sickdan.service.query.DailyQueryService.DayDailyDto;
import com.kdpark.sickdan.service.query.DailyQueryService.MonthDailyDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    public DailyResult<List<String>> editDayData(@RequestBody Map<String, Integer> params, @RequestAttribute Long member_id) {
        List<String> doneList = dailyService.syncWalkCounts(member_id, params);

        return new DailyResult<>(doneList);
    }

    @GetMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/comments")
    public List<DailyCommentDto> getDayComments(@PathVariable Long memberId, @PathVariable String yyyymmdd) {
        return dailyQueryService.getDayComments(memberId, yyyymmdd);
    }

    @PostMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/comments")
    public void writeComment(@PathVariable Long memberId, @PathVariable String yyyymmdd,
                             @RequestBody CommentWriteRequest request, @RequestAttribute Long member_id) {

        dailyService.writeComment(new Daily.DailyId(memberId, yyyymmdd),
                request.getDescription(), request.getParentId(), member_id);
    }

    @PostMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes")
    public void doLike(@PathVariable Long memberId, @PathVariable String yyyymmdd, @RequestAttribute Long member_id) {
        dailyService.doLike(new Daily.DailyId(memberId, yyyymmdd), member_id);
    }

    @DeleteMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes")
    public void undoLike(@PathVariable Long memberId, @PathVariable String yyyymmdd, @RequestAttribute Long member_id) {
        dailyService.undoLike(new Daily.DailyId(memberId, yyyymmdd), member_id);
    }

    @GetMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes/me")
    public Map<String, Boolean> isLiked(@PathVariable Long memberId, @PathVariable String yyyymmdd, @RequestAttribute Long member_id) {
        Likes like = dailyQueryService.getLike(memberId, yyyymmdd, member_id);
        return Collections.singletonMap("isLiked", like != null);
    }

    @Data
    static class CommentWriteRequest {
        private String description;
        private Long parentId;
    }

    @Data
    static class DailyResult<T> {
        private T data;

        public DailyResult(T data) {
            this.data = data;
        }
    }
}