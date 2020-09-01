package com.kdpark.sickdan.service;

import com.kdpark.sickdan.api.DailyApiController;
import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DailyService {

    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public void reorderMeals(Long member_id, String yyyymmdd, List<MealOrderInfo> orderList) {
        Daily daily = dailyRepository.findById(new Daily.DailyId(member_id, yyyymmdd));

        Map<Long, Long> map = orderList.stream().collect(Collectors.toMap(MealOrderInfo::getId, MealOrderInfo::getPrevId));
        daily.reorderMeals(map);
    }

    @Data
    public static class MealOrderInfo {
        private Long id;
        private Long prevId;
    }
}
