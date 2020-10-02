package com.kdpark.sickdan.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;


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
    public static class DailyResult<T> {
        private T data;

        public DailyResult(T data) {
            this.data = data;
        }
    }
}
