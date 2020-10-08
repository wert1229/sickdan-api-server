package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Likes;
import com.kdpark.sickdan.dto.DailyDto;
import com.kdpark.sickdan.service.DailyService;
import com.kdpark.sickdan.service.query.DailyQueryService;
import com.kdpark.sickdan.dto.DailyDto.DailyCommentDto;
import com.kdpark.sickdan.dto.DailyDto.DayDailyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@Validated
@RequiredArgsConstructor
public class DailyApiController {
    private final DailyQueryService dailyQueryService;
    private final DailyService dailyService;

    @GetMapping("/api/v1/members/me/dailies")
    public List<DailyDto.MonthDaily> getMonthData(@RequestParam @Size(min = 6, max = 6) String yyyymm,
                                                  Principal principal) {
        String memberId = principal.getName();

        return dailyQueryService.getMonthData(Long.parseLong(memberId), yyyymm);
    }

    @GetMapping("/api/v1/members/me/dailies/{yyyymmdd}")
    public DayDailyDto getDayData(@PathVariable @Size(min = 8, max = 8) String yyyymmdd,
                                  Principal principal) {
        String memberId = principal.getName();

        return dailyQueryService.getDayData(Long.parseLong(memberId), yyyymmdd);
    }

    @GetMapping("/api/v1/members/{memberId}/dailies")
    public List<DailyDto.MonthDaily> getMonthData(@PathVariable @Min(1) Long memberId,
                                                  @RequestParam @Size(min = 6, max = 6) String yyyymm) {

        return dailyQueryService.getMonthData(memberId, yyyymm);
    }

    @GetMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}")
    public DayDailyDto getDayData(@PathVariable @Min(1) Long memberId,
                                  @PathVariable @Size(min = 8, max = 8) String yyyymmdd) {

        return dailyQueryService.getDayData(memberId, yyyymmdd);
    }

    @PutMapping("/api/v1/members/me/dailies/{yyyymmdd}")
    public void editDayData(@PathVariable @Size(min = 8, max = 8) String yyyymmdd,
                            @RequestBody DailyDto.DayInfoUpdateRequest request,
                            Principal principal) {

        String memberId = principal.getName();
        dailyService.editDaily(new Daily.DailyId(Long.parseLong(memberId), yyyymmdd), request);
    }

    @PutMapping("/api/v1/members/me/dailies/walkcounts")
    public DailyDto.DailyResult<List<String>> editDayData(@RequestBody Map<String, Integer> params,
                                                          Principal principal) {

        String memberId = principal.getName();
        List<String> doneList = dailyService.syncWalkCounts(Long.parseLong(memberId), params);

        return new DailyDto.DailyResult<>(doneList);
    }

    @GetMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/comments")
    public List<DailyCommentDto> getDayComments(@PathVariable @Min(1) Long memberId,
                                                @PathVariable @Size(min = 8, max = 8) String yyyymmdd) {
        return dailyQueryService.getDayComments(memberId, yyyymmdd);
    }

    @PostMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/comments")
    public void writeComment(@PathVariable @Min(1) Long memberId,
                             @PathVariable @Size(min = 8, max = 8) String yyyymmdd,
                             @RequestBody @Valid DailyDto.CommentWriteRequest request,
                             Principal principal) {

        String writerMemberId = principal.getName();
        dailyService.writeComment(new Daily.DailyId(memberId, yyyymmdd),
                request.getDescription(), request.getParentId(), Long.parseLong(writerMemberId));
    }

    @PostMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes")
    public void doLike(@PathVariable @Min(1) Long memberId,
                       @PathVariable @Size(min = 8, max = 8) String yyyymmdd,
                       Principal principal) {
        String writerMemberId = principal.getName();
        dailyService.doLike(new Daily.DailyId(memberId, yyyymmdd), Long.parseLong(writerMemberId));
    }

    @DeleteMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes")
    public void undoLike(@PathVariable @Min(1) Long memberId,
                         @PathVariable @Size(min = 8, max = 8) String yyyymmdd,
                         Principal principal) {
        String writerMemberId = principal.getName();
        dailyService.undoLike(new Daily.DailyId(memberId, yyyymmdd), Long.parseLong(writerMemberId));
    }

    @GetMapping("/api/v1/members/{memberId}/dailies/{yyyymmdd}/likes/me")
    public Map<String, Boolean> isLiked(@PathVariable @Min(1) Long memberId,
                                        @PathVariable @Size(min = 8, max = 8) String yyyymmdd,
                                        Principal principal) {
        String writerMemberId = principal.getName();
        Likes like = dailyQueryService.getLike(memberId, yyyymmdd, Long.parseLong(writerMemberId));
        return Collections.singletonMap("isLiked", like != null);
    }
}