package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DailyService {

    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    @Data
    public static class MealOrderInfo {
        private Long id;
        private Long prevId;
        private MealCategory category;
    }
}
