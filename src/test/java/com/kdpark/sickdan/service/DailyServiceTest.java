package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.repository.DailyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyServiceTest {
    @InjectMocks
    DailyService dailyService;

    @Mock
    DailyRepository dailyRepository;

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
        dailyService.editDaily(new Daily.DailyId(1L, "20200831"), params);

        //then

    }
}