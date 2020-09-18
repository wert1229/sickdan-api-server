package com.kdpark.sickdan.service.query;

import com.kdpark.sickdan.domain.*;
import com.kdpark.sickdan.repository.DailyRepository;
import com.kdpark.sickdan.repository.MemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyQueryService {

    private final DailyRepository dailyRepository;
    private final MemberRepository memberRepository;

    public List<MonthDailyDto> getMonthData(Long memberId, String yyyymm) {
        List<Daily> monthData = dailyRepository.findOneMonth(memberId, yyyymm);

        return monthData.stream()
                .map(MonthDailyDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public DayDailyDto getDayData(Long memberId, String yyyymmdd) {
        Daily.DailyId dailyId = new Daily.DailyId(memberId, yyyymmdd);
        Daily dayData = dailyRepository.findById(dailyId);

        if (dayData == null) {
            Member member = memberRepository.findById(dailyId.getMemberId());
            dayData = Daily.getDefault(member, yyyymmdd);
            dailyRepository.save(dayData);
        }

        DailyRepository.DailyCountInfo commentAndLikeCount = dailyRepository.getCommentAndLikeCount(memberId, yyyymmdd);

        return new DayDailyDto(dayData, commentAndLikeCount);
    }

    public List<DailyCommentDto> getDayComments(Long memberId, String yyyymmdd) {
        List<Comment> comments = dailyRepository.getComments(memberId, yyyymmdd);

        return comments.stream()
                .map(DailyCommentDto::new)
                .collect(Collectors.toList());
    }

    public Likes getLike(Long memberId, String yyyymmdd, Long authId) {
        return dailyRepository.getLikeById(new Likes.LikeId(new Daily.DailyId(memberId, yyyymmdd), authId));
    }

    @Data
    static public class DailyCommentDto {
        private Long id;
        private String displayName;
        private String description;
        private String createdDateTime;
        private Long parentCommentId;
        private List<DailyCommentDto> replies;

        public DailyCommentDto(Comment comment) {
            this.id = comment.getId();
            this.displayName = comment.getWriter().getDisplayName();
            this.description = comment.getDescription();
            this.createdDateTime = comment.getCreatedDateTime().toString();
            this.parentCommentId = comment.getParent() != null ?comment.getParent().getId() : null;
            this.replies = comment.getReplies().stream()
                    .map(DailyCommentDto::new)
                    .collect(Collectors.toList());
        }
    }
    @Data
    static public class MonthDailyDto {
        private String date;
        private String memo;
        private int walkCount;
        private double bodyWeight;

        public MonthDailyDto(Daily daily) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
        }
    }

    @Data
    static public class DayDailyDto {
        private String date;
        private String memo;
        private Integer walkCount;
        private Double bodyWeight;
        private List<MealDto> meals;
        private Integer commentCount;
        private Integer likeCount;

        public DayDailyDto(Daily daily, DailyRepository.DailyCountInfo countInfo) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
            this.commentCount = countInfo.getCommentCount();
            this.likeCount = countInfo.getLikeCount();
            this.meals = daily.getMeals().stream()
                    .sorted(Comparator.comparing(Meal::getCreatedDateTime))
                    .map(MealDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealDto {
        private Long id;
        private String description;
        private MealCategory category;
        private List<MealPhotoDto> photos;

        public MealDto(Meal meal) {
            this.id = meal.getId();
            this.description = meal.getDescription();
            this.category = meal.getCategory();
            this.photos = meal.getPhotos().stream()
                    .map(MealPhotoDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealPhotoDto {
        private Long id;
        private String fileOriginName;
        private String fileName;
        private String fileUrl;

        public MealPhotoDto(MealPhoto photo) {
            this.id = photo.getId();
            this.fileOriginName = photo.getFileOriginName();
            this.fileName = photo.getFileName();
            this.fileUrl = photo.getFileUrl();
        }
    }
}
