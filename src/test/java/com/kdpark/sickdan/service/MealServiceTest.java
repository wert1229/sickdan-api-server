package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Meal;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MealReposity;
import com.kdpark.sickdan.repository.MemberRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class MealServiceTest {
    @InjectMocks
    MealService mealService;

    @Mock
    MealReposity mealReposity;
    @Mock
    DailyRepository dailyRepository;
    @Mock
    MemberRepository memberRepository;

    @Test
    public void 식단기록_정상처리() throws Exception {
        //given
        Daily daily = mock(Daily.class);

        when(dailyRepository.findById(any()))
                .thenReturn(daily);

        //when
        mealService.record(any(), "test", MealCategory.BREAKFAST);

        //then
        verify(daily, times(1)).recordMeal(any());
    }

    @Test
    public void 식단기록_정상처리_일자정보생성() throws Exception {
        //given
        Member member = Member.builder()
                .id(2L)
                .build();

        Daily daily = null;

        when(dailyRepository.findById(any()))
                .thenReturn(daily);
        when(memberRepository.findById(any()))
                .thenReturn(member);

        //when
        mealService.record(new Daily.DailyId(1L, "20200909"), "test", MealCategory.BREAKFAST);

        //then
        verify(dailyRepository, times(1)).save(any());
    }

    @Test
    public void 식단_사진추가_정상처리() throws Exception {
        //given
        Meal meal = mock(Meal.class);

        when(mealReposity.findById(any()))
                .thenReturn(meal);

        //when
        mealService.addPhoto(1L, "originName", "fileName", 10000L, "url");

        //then
        verify(meal, times(1)).addPhoto(any());
    }

    @Test
    public void 식단_사진추가_식단정보없음() throws Exception {
        //given
        Meal meal = null;

        when(mealReposity.findById(any()))
                .thenReturn(meal);

        //when

        //then
        assertThrows(EntityNotFoundException.class, () ->
                mealService.addPhoto(1L, "originName", "fileName", 10000L, "url"));
    }

}