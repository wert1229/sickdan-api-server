package com.kdpark.sickdan.dto;

import com.kdpark.sickdan.domain.Comment;
import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Meal;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class DailyDto {
    @Data
    public static class DayInfoUpdateRequest {
        private Integer walkCount;
        private Double bodyWeight;
    }

    @Data
    public static class CommentWriteRequest {
        @NotBlank
        private String description;
        private Long parentId;
    }

    @Data
    static public class MonthDaily {
        private String date;
        private String memo;
        private int walkCount;
        private double bodyWeight;

        public MonthDaily(Daily daily) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
        }
    }

    @Data
    public static class DailyResult<T> {
        private T data;

        public DailyResult(T data) {
            this.data = data;
        }
    }

    @Data
    public static class DailyCommentDto {
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
    public static class DayDailyDto {
        private String date;
        private String memo;
        private Integer walkCount;
        private Double bodyWeight;
        private List<MealDto.MealDaily> meals;
        private Integer commentCount;
        private Integer likeCount;

        public DayDailyDto(Daily daily, DailyCountInfo countInfo) {
            this.date = daily.getId().getDate();
            this.memo = daily.getMemo();
            this.walkCount = daily.getWalkCount();
            this.bodyWeight = daily.getBodyWeight();
            this.commentCount = countInfo.getCommentCount();
            this.likeCount = countInfo.getLikeCount();
            this.meals = daily.getMeals().stream()
                    .sorted(Comparator.comparing(Meal::getCreatedDateTime))
                    .map(MealDto.MealDaily::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class DailyCountInfo {
        private int commentCount;
        private int likeCount;

        @Builder
        public DailyCountInfo(int commentCount, int likeCount) {
            this.commentCount = commentCount;
            this.likeCount = likeCount;
        }
    }
}
