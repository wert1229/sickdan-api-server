package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Meal;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MealReposity;
import com.kdpark.sickdan.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    public void 식단추가() throws Exception {
        //given
        Member member = Member.builder()
                .build();

        Daily daily = Daily.builder()
                .member(member)
                .build();

        when(dailyRepository.findById(new Daily.DailyId(1L, "20200831"))).thenReturn(daily);

        //when
        mealService.record(new Daily.DailyId(1L, "20200831"), "test", MealCategory.BREAKFAST);

        //then
    }

    @Test
    public void 식단추가_Daily없을시() throws Exception {
        //given
        Member member = Member.builder()
                .build();
        Daily.DailyId dailyId = new Daily.DailyId(1L, "20200831");
        Daily daily = Daily.builder()
                .id(dailyId)
                .member(member)
                .build();

        when(dailyRepository.findById(dailyId)).thenReturn(null);
        when(memberRepository.findById(1L)).thenReturn(member);

        //when
        mealService.record(dailyId, "test", MealCategory.BREAKFAST);

        //then
        verify(dailyRepository, times(1)).save(any(Daily.class));
    }

    @Test
    public void 사진추가() throws Exception {
        //given
        when(mealReposity.findById(1L)).thenReturn(Meal.createMeal("testMeal", MealCategory.BREAKFAST));

        //when
        mealService.addPhoto(1L, "test", "test", 100L, "test.com");

        //then
    }

    @Test
    public void 사진추가_식단없음() throws Exception {
        //given

        //when

        //then
        assertThrows(EntityNotFoundException.class, () ->
                mealService.addPhoto(-1L, "test", "test", 100L, "test.com"));
    }
}