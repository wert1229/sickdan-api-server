package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.FileReadException;
import com.kdpark.sickdan.repository.MealReposity;
import com.kdpark.sickdan.service.MealService;
import com.kdpark.sickdan.util.S3Util;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MealApiController {

    private final MealService mealService;
    private final MealReposity mealReposity;
    private final S3Util s3Util;

    @PostMapping("/api/v1/meals")
    public void addMeal(@RequestBody MealAddRequest request, @RequestAttribute Long member_id) {
        Daily.DailyId dailyId = new Daily.DailyId(member_id, request.date);
        String description = request.getDescription();
        MealCategory category = request.getCategory();

        mealService.record(dailyId, description, category);
    }

    @PutMapping("/api/v1/meals/{mealId}")
    public void editMeal(@PathVariable Long mealId, @RequestBody Map<String, String> params) {
        String desc = params.get("description");

        mealService.editMeal(mealId, desc);
    }

    @DeleteMapping("/api/v1/meals/{mealId}")
    public void deleteMeal(@PathVariable Long mealId) {
        mealService.delete(mealId);

    }

    @PostMapping("/api/v1/meals/{mealId}/photos")
    public void uploadMealPhoto(@PathVariable Long mealId, @RequestPart MultipartFile file) {
        try {
            String originFileName = file.getOriginalFilename();
            String ext = originFileName.substring(originFileName.lastIndexOf(".") + 1);
            String fileName = String.format("%d_%s.%s", mealId, UUID.randomUUID().toString(), ext);

            String url = s3Util.upload(file, fileName);

            mealService.addPhoto(mealId, originFileName, fileName, file.getSize(), url);
        } catch (IOException e) {
            throw new FileReadException("파일 읽기 실패", ErrorCode.INTERNAL_IO_FAILED);
        }
    }

    @Data
    static class MealAddRequest {
        private String date;
        private String description;
        private MealCategory category;
    }
}
