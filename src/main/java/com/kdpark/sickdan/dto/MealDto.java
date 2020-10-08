package com.kdpark.sickdan.dto;

import com.kdpark.sickdan.domain.Meal;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.domain.MealPhoto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

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

    @Data
    static class MealDaily {
        private Long id;
        private String description;
        private MealCategory category;
        private List<MealDailyPhoto> photos;

        public MealDaily(Meal meal) {
            this.id = meal.getId();
            this.description = meal.getDescription();
            this.category = meal.getCategory();
            this.photos = meal.getPhotos().stream()
                    .map(MealDailyPhoto::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    static class MealDailyPhoto {
        private Long id;
        private String fileOriginName;
        private String fileName;
        private String fileUrl;

        public MealDailyPhoto(MealPhoto photo) {
            this.id = photo.getId();
            this.fileOriginName = photo.getFileOriginName();
            this.fileName = photo.getFileName();
            this.fileUrl = photo.getFileUrl();
        }
    }

    @Data
    public static class MealOrderInfo {
        private Long id;
        private Long prevId;
        private MealCategory category;
    }
}
