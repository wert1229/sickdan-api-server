package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class DailyService {

    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public void editDaily(Daily.DailyId id, Map<String, Object> params) {
        Daily daily = dailyRepository.findById(id);

        if (params.containsKey("bodyWeight")) daily.setBodyWeight((Double)params.get("bodyWeight"));
        if (params.containsKey("walkCount")) daily.setWalkCount((Integer)params.get("walkCount"));
    }

    @Data
    public static class MealOrderInfo {
        private Long id;
        private Long prevId;
        private MealCategory category;
    }
}
