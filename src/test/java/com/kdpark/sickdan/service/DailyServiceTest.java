package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyServiceTest {
    @InjectMocks
    DailyService dailyService;

    @Mock
    DailyRepository dailyRepository;

    @Mock
    MemberRepository memberRepository;

    @Test
    public void 몸무게_수정() throws Exception {
        //given
        Member member = Member.builder()
                .build();

        Daily daily = Daily.builder()
                .member(member)
                .build();

        when(dailyRepository.findById(new Daily.DailyId(1L, "20200831"))).thenReturn(daily);

        Map<String, Object> params = new HashMap<>();
        params.put("bodyWeight", 70.0f);

        //when
//        dailyService.editDaily(new Daily.DailyId(1L, "20200831"), params);

        //then

    }

    @Test
    public void 걸음수_다중_업데이트() throws Exception {
        //given
        Member member = Member.builder()
                .build();

        List<Daily> dailies = new ArrayList<>();

        dailies.add(Daily.builder()
                .member(member)
                .walkCount(0)
                .id(new Daily.DailyId(1L, "20200909"))
                .build());

        dailies.add(Daily.builder()
                .member(member)
                .walkCount(0)
                .id(new Daily.DailyId(1L, "20200908"))
                .build());

        Map<String, Integer> params = new HashMap<>();

        params.put("20200909", 1000);
        params.put("20200910", 2000);

        when(dailyRepository.findByDates(eq(1L), any())).thenReturn(dailies);
        when(memberRepository.findById(1L)).thenReturn(member);

        //when
        dailyService.syncWalkCounts(1L, params);

        //then

    }
}