package com.kdpark.sickdan.service;

import com.kdpark.sickdan.api.DailyApiController;
import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.dto.DailyDto;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DailyService {

    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public void editDaily(Daily.DailyId id, DailyDto.DayInfoUpdateRequest request) {
        Daily daily = dailyRepository.findById(id);

        if (request.getBodyWeight() != null) daily.setBodyWeight(request.getBodyWeight());
        if (request.getWalkCount() != null) daily.setWalkCount(request.getWalkCount());
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
                daily = Daily.getDefault(member, date);
                dailyRepository.save(daily);
            }

            daily.setWalkCount(params.get(date));

            doneDateList.add(date);
        }

        return doneDateList;
    }

    public void writeComment(Daily.DailyId dailyId, String description, Long parentId, Long member_id) {
        Daily daily = dailyRepository.findById(dailyId);
        Member writer = memberRepository.findById(member_id);
        Comment parent = null;

        if (parentId != null)
            parent = dailyRepository.getCommentById(parentId);

        Comment comment = Comment.builder()
                .description(description)
                .writer(writer)
                .parent(parent)
                .build();

        daily.writeComment(comment);
    }

    public void doLike(Daily.DailyId dailyId, Long member_id) {
        Member liker = memberRepository.findById(member_id);
        Daily daily = dailyRepository.findById(dailyId);

        daily.doLike(liker);
    }

    public void undoLike(Daily.DailyId dailyId, Long member_id) {
        Member liker = memberRepository.findById(member_id);
        Daily daily = dailyRepository.findById(dailyId);

        daily.undoLike(liker);
    }

    @Data
    public static class MealOrderInfo {
        private Long id;
        private Long prevId;
        private MealCategory category;
    }
}
