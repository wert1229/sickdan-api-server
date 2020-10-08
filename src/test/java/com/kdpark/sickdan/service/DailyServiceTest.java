package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.dto.DailyDto;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class DailyServiceTest {
    @InjectMocks
    DailyService dailyService;

    @Mock
    DailyRepository dailyRepository;

    @Mock
    MemberRepository memberRepository;

    @Test
    public void 일별정보_수정_정상처리_몸무게_걸음수() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Daily daily = mock(Daily.class);

        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when
        DailyDto.DayInfoUpdateRequest request = new DailyDto.DayInfoUpdateRequest();
        request.setBodyWeight(90.0);
        request.setWalkCount(20000);

        dailyService.editDaily(new Daily.DailyId(1L, "20200909"), request);

        //then
        verify(daily, times(1)).setBodyWeight(90.0);
        verify(daily, times(1)).setWalkCount(20000);
    }

    @Test
    public void 일별정보_수정_정상처리_몸무게만() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Daily daily = mock(Daily.class);

        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when
        DailyDto.DayInfoUpdateRequest request = new DailyDto.DayInfoUpdateRequest();
        request.setBodyWeight(90.0);

        dailyService.editDaily(new Daily.DailyId(1L, "20200909"), request);

        //then
        verify(daily, times(1)).setBodyWeight(90.0);
        verify(daily, times(0)).setWalkCount(anyInt());
    }

    @Test
    public void 일별정보_수정_정상처리_걸음수만() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Daily daily = mock(Daily.class);

        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when
        DailyDto.DayInfoUpdateRequest request = new DailyDto.DayInfoUpdateRequest();
        request.setWalkCount(20000);

        dailyService.editDaily(new Daily.DailyId(1L, "20200909"), request);

        //then
        verify(daily, times(0)).setBodyWeight(anyDouble());
        verify(daily, times(1)).setWalkCount(20000);
    }

    @Test
    public void 일별정보_수정_일자정보없음() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Daily daily = null;

        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when
        DailyDto.DayInfoUpdateRequest request = new DailyDto.DayInfoUpdateRequest();
        request.setWalkCount(20000);

        //then
        assertThrows(EntityNotFoundException.class, () ->
                dailyService.editDaily(new Daily.DailyId(1L, "20200909"), request));
    }

    @Test
    public void 일별정보_걸음수_동기화_정상처리() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        List<Daily> dailies = new ArrayList<>();

        dailies.add(Daily.builder()
                .id(new Daily.DailyId(1L, "20200909"))
                .walkCount(0)
                .bodyWeight(80.8)
                .memo("test")
                .member(member)
                .build()
        );
        dailies.add(Daily.builder()
                .id(new Daily.DailyId(1L, "20200910"))
                .walkCount(300)
                .bodyWeight(80.8)
                .memo("test")
                .member(member)
                .build()
        );

        when(dailyRepository.findByDates(any(), anyList()))
                .thenReturn(dailies);

        //when
        Map<String, Integer> params = new HashMap<>();
        params.put("20200909", 10000);
        params.put("20200910", 20000);

        List<String> doneList = dailyService.syncWalkCounts(1L, params);

        //then
        assertEquals(2, doneList.size());
        assertTrue(doneList.contains("20200909"));
        assertTrue(doneList.contains("20200910"));

    }

    @Test
    public void 일별정보_걸음수_동기화_정상처리_일자정보생성() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        List<Daily> dailies = new ArrayList<>();

        dailies.add(Daily.builder()
                .id(new Daily.DailyId(1L, "20200909"))
                .walkCount(0)
                .bodyWeight(80.8)
                .memo("test")
                .member(member)
                .build()
        );

        when(dailyRepository.findByDates(any(), anyList()))
                .thenReturn(dailies);
        when(memberRepository.findById(any()))
                .thenReturn(member);

        //when
        Map<String, Integer> params = new HashMap<>();
        params.put("20200909", 10000);
        params.put("20200910", 20000);

        List<String> doneList = dailyService.syncWalkCounts(1L, params);

        //then
        verify(dailyRepository, times(1)).save(any());
        assertEquals(2, doneList.size());
        assertTrue(doneList.contains("20200909"));
        assertTrue(doneList.contains("20200910"));
    }

    @Test
    public void 댓글작성_1층댓글_정상처리() throws Exception {
        //given
        Member writer = Member.builder()
                .id(2L)
                .build();

        Daily daily = mock(Daily.class);

        when(memberRepository.findById(any()))
                .thenReturn(writer);
        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when
        dailyService.writeComment(any(), "test", null, 2L);

        //then
        verify(daily, times(1)).writeComment(any());
    }

    @Test
    public void 댓글작성_2층댓글_정상처리() throws Exception {
        //given
        Member writer = Member.builder()
                .id(2L)
                .build();

        Daily daily = mock(Daily.class);

        when(memberRepository.findById(any()))
                .thenReturn(writer);
        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when
        dailyService.writeComment(any(), "test", 1L, 2L);

        //then
        verify(dailyRepository, times(1)).getCommentById(anyLong());
        verify(daily, times(1)).writeComment(any());
    }

    @Test
    public void 댓글작성_작성자정보없음_정상처리() throws Exception {
        //given
        Member writer = null;

        Daily daily = mock(Daily.class);

        when(memberRepository.findById(any()))
                .thenReturn(writer);
        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when

        //then
        assertThrows(EntityNotFoundException.class, () ->
                dailyService.writeComment(any(), "test", 1L, 2L));
    }
}