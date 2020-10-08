package com.kdpark.sickdan.service.query;

import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.dto.DailyDto;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class DailyQueryServiceTest {
    @InjectMocks
    DailyQueryService dailyQueryService;

    @Mock
    DailyRepository dailyRepository;
    @Mock
    MemberRepository memberRepository;

    @Test
    public void 월별조회_정상처리() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        List<Daily> dailies = new ArrayList<>();
        dailies.add(Daily.builder()
                .id(new Daily.DailyId(1L, "20200909"))
                .walkCount(10000)
                .bodyWeight(80.8)
                .memo("test")
                .member(member)
                .build()
        );

        when(dailyRepository.findOneMonth(any(), any()))
                .thenReturn(dailies);
        //when
        List<DailyDto.MonthDaily> monthData = dailyQueryService.getMonthData(any(), any());

        //then
        assertEquals("20200909", monthData.get(0).getDate());
        assertEquals("test", monthData.get(0).getMemo());
        assertEquals(80.8, monthData.get(0).getBodyWeight());
        assertEquals(10000, monthData.get(0).getWalkCount());
    }

    @Test
    public void 월별조회_정상처리_31개데이터() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        List<Daily> dailies = new ArrayList<>();

        int date = 20200901;
        for (int i = 0; i < 31; i++) {
            dailies.add(Daily.builder()
                    .id(new Daily.DailyId(1L, "" + date++))
                    .walkCount(10000)
                    .bodyWeight(80.8)
                    .memo("test")
                    .member(member)
                    .build()
            );
        }

        when(dailyRepository.findOneMonth(any(), any()))
                .thenReturn(dailies);
        //when
        List<DailyDto.MonthDaily> monthData = dailyQueryService.getMonthData(any(), any());

        //then
        date = 20200901;

        assertEquals(31, monthData.size());
        for (int i = 0; i < 31; i++) {
            assertEquals("" + date++, monthData.get(i).getDate());
        }
    }

    @Test
    public void 월별조회_정상처리_데이터없음() throws Exception {
        //given
        List<Daily> dailies = new ArrayList<>();

        when(dailyRepository.findOneMonth(any(), any()))
                .thenReturn(dailies);
        //when
        List<DailyDto.MonthDaily> monthData = dailyQueryService.getMonthData(any(), any());

        //then
        assertEquals(0, monthData.size());
    }

    @Test
    public void 일별조회_정상처리() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();

        Daily daily = Daily.builder()
                .id(new Daily.DailyId(1L, "20200909"))
                .walkCount(10000)
                .bodyWeight(80.8)
                .memo("test")
                .member(member)
                .build();
        daily.recordMeal(Meal.createMeal("meal", MealCategory.BREAKFAST));

        when(dailyRepository.findById(any()))
                .thenReturn(daily);
        when(dailyRepository.getCommentAndLikeCount(any(), any()))
                .thenReturn(new DailyDto.DailyCountInfo(3, 2));

        //when
        DailyDto.DayDailyDto dayData = dailyQueryService.getDayData(1L, "20200909");

        //then
        assertEquals("20200909", dayData.getDate());
        assertEquals(10000, dayData.getWalkCount());
        assertEquals(3, dayData.getCommentCount());
        assertEquals(2, dayData.getLikeCount());
        assertEquals("test", dayData.getMemo());
        assertEquals(1, dayData.getMeals().size());
    }

    @Test
    public void 일별조회_정상처리_없는일자생성() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();
        Daily daily = null;

        when(dailyRepository.findById(any()))
                .thenReturn(daily);
        when(memberRepository.findById(any()))
                .thenReturn(member);
        when(dailyRepository.getCommentAndLikeCount(any(), any()))
                .thenReturn(new DailyDto.DailyCountInfo(0, 0));

        Daily defaultDaily = Daily.getDefault(member, "20200909");

        //when
        DailyDto.DayDailyDto dayData = dailyQueryService.getDayData(1L, "20200909");

        //then
        assertEquals(defaultDaily.getId().getDate(), dayData.getDate());
        assertEquals(defaultDaily.getWalkCount(), dayData.getWalkCount());
        assertEquals(defaultDaily.getBodyWeight(), dayData.getBodyWeight());
        assertEquals(defaultDaily.getMemo(), dayData.getMemo());
        assertEquals(0, dayData.getCommentCount());
        assertEquals(0, dayData.getLikeCount());
    }

    @Test
    public void 일별댓글조회_정상처리() throws Exception {
        //given
        Member member = Member.builder()
                .id(1L)
                .build();
        Member writer = Member.builder()
                .id(2L)
                .displayName("writer")
                .build();

        Daily daily = Daily.builder()
                .id(new Daily.DailyId(1L, "20200909"))
                .walkCount(10000)
                .bodyWeight(80.8)
                .memo("test")
                .member(member)
                .build();

        List<Comment> comments = new ArrayList<>();
        comments.add(Comment.builder()
                .description("test")
                .writer(writer)
                .daily(daily)
                .createdDateTime(LocalDateTime.now())
                .replies(new ArrayList<>())
                .build()
        );

        when(dailyRepository.getComments(any(), any()))
                .thenReturn(comments);
        //when
        List<DailyDto.DailyCommentDto> dayComments = dailyQueryService.getDayComments(1L, "20200909");

        //then
        assertEquals(1, dayComments.size());
        assertEquals("test", dayComments.get(0).getDescription());
        assertEquals("writer", dayComments.get(0).getDisplayName());
    }

    @Test
    public void 일별댓글조회_정상처리_데이터없음() throws Exception {
        //given
        List<Comment> comments = new ArrayList<>();

        when(dailyRepository.getComments(any(), any()))
                .thenReturn(comments);
        //when
        List<DailyDto.DailyCommentDto> dayComments = dailyQueryService.getDayComments(1L, "20200909");

        //then
        assertEquals(0, dayComments.size());
    }
}