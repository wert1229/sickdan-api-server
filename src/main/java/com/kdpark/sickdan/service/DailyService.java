package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public List<String> syncWalkCounts(Long memberId, Map<String, Integer> params) {
        Map<String, Daily> dailyMap = dailyRepository.findByDates(memberId, new ArrayList<>(params.keySet()))
                .stream()
                .collect(Collectors.toMap(
                        daily -> daily.getId().getDate(),
                        daily -> daily));

        List<String> doneDateList = new ArrayList<>();

        for (String date : params.keySet()) {
            Daily daily = dailyMap.get(date);

            if (daily == null) {
                Member member = memberRepository.findById(memberId);
                daily = Daily.builder()
                        .id(new Daily.DailyId(memberId, date))
                        .member(member)
                        .memo("")
                        .bodyWeight(0.0)
                        .walkCount(0)
                        .build();

                dailyRepository.save(daily);
            }

            daily.setWalkCount(params.get(date));

            doneDateList.add(date);
        }

        return doneDateList;
    }

    @Data
    public static class MealOrderInfo {
        private Long id;
        private Long prevId;
        private MealCategory category;
    }
}
