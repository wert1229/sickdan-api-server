package com.kdpark.sickdan.service.query;

import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.dto.DailyDto;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
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

    public List<DailyDto.MonthDaily> getMonthData(Long memberId, String yyyymm) {
        List<Daily> monthData = dailyRepository.findOneMonth(memberId, yyyymm);

        return monthData.stream()
                .map(DailyDto.MonthDaily::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public DailyDto.DayDailyDto getDayData(Long memberId, String yyyymmdd) {
        Daily.DailyId dailyId = new Daily.DailyId(memberId, yyyymmdd);
        Daily dayData = dailyRepository.findById(dailyId);

        if (dayData == null) {
            Member member = memberRepository.findById(dailyId.getMemberId());
            dayData = Daily.getDefault(member, yyyymmdd);
            dailyRepository.save(dayData);
        }

        DailyDto.DailyCountInfo commentAndLikeCount = dailyRepository.getCommentAndLikeCount(memberId, yyyymmdd);

        return new DailyDto.DayDailyDto(dayData, commentAndLikeCount);
    }

    public List<DailyDto.DailyCommentDto> getDayComments(Long memberId, String yyyymmdd) {
        List<Comment> comments = dailyRepository.getComments(memberId, yyyymmdd);

        return comments.stream()
                .map(DailyDto.DailyCommentDto::new)
                .collect(Collectors.toList());
    }

    public Likes getLike(Long memberId, String yyyymmdd, Long authId) {
        return dailyRepository.getLikeById(new Likes.LikeId(new Daily.DailyId(memberId, yyyymmdd), authId));
    }

}
