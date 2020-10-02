package com.kdpark.sickdan.dto;

import com.kdpark.sickdan.domain.MealCategory;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MealDto {

    @Data
    public static class MealAddRequest {
        @Size(min = 8, max = 8)
        private String date;
        @NotBlank
        private String description;
        @NotNull
        private MealCategory category;
    }

    @Data
    public static class MealEditRequest {
        @NotBlank
        private String description;
    }
}
