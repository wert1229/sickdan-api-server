package com.kdpark.sickdan.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdpark.sickdan.domain.MealCategory;
import com.kdpark.sickdan.dto.MealDto;
import com.kdpark.sickdan.security.JwtTokenProvider;
import com.kdpark.sickdan.service.MealService;
import com.kdpark.sickdan.util.S3Util;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MealApiController.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class MealDailyApiControllerTest {
    @TestConfiguration
    static class AdditionalConfig {
        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            return new JwtTokenProvider();
        }
    }

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private S3Util s3Util;
    @MockBean
    private MealService mealService;

    private String getAccessToken(Long id) {
        return jwtTokenProvider.createAccessToken(String.valueOf(id), Collections.singletonList("ROLE_USER"));
    }

    @Test
    public void 식단_추가_정상처리() throws Exception {
        MealDto.MealAddRequest request = new MealDto.MealAddRequest();
        request.setDate("20200909");
        request.setDescription("desc");
        request.setCategory(MealCategory.BREAKFAST);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/meals")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 식단_추가_날짜틀림() throws Exception {
        MealDto.MealAddRequest request = new MealDto.MealAddRequest();
        request.setDate("2020090909");
        request.setDescription("desc");
        request.setCategory(MealCategory.BREAKFAST);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/meals")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 식단_추가_내용빈값() throws Exception {
        MealDto.MealAddRequest request = new MealDto.MealAddRequest();
        request.setDate("20200909");
        request.setDescription("");
        request.setCategory(MealCategory.BREAKFAST);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/meals")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 식단_추가_카테고리빈값() throws Exception {
        MealDto.MealAddRequest request = new MealDto.MealAddRequest();
        request.setDate("20200909");
        request.setDescription("desc");
        request.setCategory(null);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/meals")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 식단_내용수정_정상처리() throws Exception {
        MealDto.MealEditRequest request = new MealDto.MealEditRequest();
        request.setDescription("desc");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(put("/api/v1/meals/1")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 식단_내용수정_내용빈값() throws Exception {
        MealDto.MealEditRequest request = new MealDto.MealEditRequest();
        request.setDescription("");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(put("/api/v1/meals/1")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 식단_사진추가_정상처리() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "originalname",
                "text/plain", "test data".getBytes());

        mvc.perform(multipart("/api/v1/meals/1/photos")
                .file(file)
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 식단_사진추가_S3_IO실패() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "originalname",
                "text/plain", "test data".getBytes());

        when(s3Util.upload(any(), any())).thenThrow(IOException.class);

        mvc.perform(multipart("/api/v1/meals/1/photos")
                .file(file)
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

}