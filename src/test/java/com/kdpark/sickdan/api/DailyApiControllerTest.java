package com.kdpark.sickdan.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdpark.sickdan.dto.DailyDto;
import com.kdpark.sickdan.security.JwtTokenProvider;
import com.kdpark.sickdan.service.DailyService;
import com.kdpark.sickdan.service.query.DailyQueryService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DailyApiController.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class DailyApiControllerTest {
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
    private DailyQueryService dailyQueryService;
    @MockBean
    private DailyService dailyService;

    private String getAccessToken(Long id) {
        return jwtTokenProvider.createAccessToken(String.valueOf(id), Collections.singletonList("ROLE_USER"));
    }

    @Test
    public void 월별데이터_인증회원_조회_정상처리() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies")
                .param("yyyymm", "202009")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 월별데이터_인증회원_조회_날짜빈값() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies")
                .param("yyyymm", "")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 월별데이터_인증회원_조회_파라미터없음() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 월별데이터_인증회원_조회_날짜길이초과() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies")
                .param("yyyymm", "20200919")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 월별데이터_인증회원_조회_날짜길이미달() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies")
                .param("yyyymm", "2020")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 일별데이터_인증회원_조회_정상처리() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies/20200919")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_인증회원_조회_날짜길이초과() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies/2020091999")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 일별데이터_인증회원_조회_날짜길이미달() throws Exception {
        mvc.perform(get("/api/v1/members/me/dailies/202009")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 월별데이터_지정회원_조회_정상처리() throws Exception {
        mvc.perform(get("/api/v1/members/2/dailies")
                .param("yyyymm", "202009")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_지정회원_조회_정상처리() throws Exception {
        mvc.perform(get("/api/v1/members/2/dailies/20200919")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_몸무게_걸음수_수정() throws Exception {
        DailyDto.DayInfoUpdateRequest request = new DailyDto.DayInfoUpdateRequest();
        request.setBodyWeight(70.5);
        request.setWalkCount(10000);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(put("/api/v1/members/me/dailies/20200919")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_몸무게_걸음수_수정_빈값() throws Exception {
        DailyDto.DayInfoUpdateRequest request = new DailyDto.DayInfoUpdateRequest();

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(put("/api/v1/members/me/dailies/20200919")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_걸음수_동기화_정상처리() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("20200101", 10000);
        map.put("20200102", 20000);

        String content = objectMapper.writeValueAsString(map);

        mvc.perform(put("/api/v1/members/me/dailies/walkcounts")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_댓글_조회_정상처리() throws Exception {
        mvc.perform(get("/api/v1/members/1/dailies/20200909/comments")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_댓글_조회_날짜길이미달() throws Exception {
        mvc.perform(get("/api/v1/members/1/dailies/202009/comments")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 일별데이터_댓글_조회_날짜길이초과() throws Exception {
        mvc.perform(get("/api/v1/members/1/dailies/2020092222/comments")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 일별데이터_댓글_작성_1계층_정상처리() throws Exception {
        DailyDto.CommentWriteRequest request = new DailyDto.CommentWriteRequest();
        request.setDescription("desc");
        request.setParentId(null);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/members/2/dailies/20200903/comments")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_댓글_작성_2계층_정상처리() throws Exception {
        DailyDto.CommentWriteRequest request = new DailyDto.CommentWriteRequest();
        request.setDescription("desc");
        request.setParentId(1L);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/members/2/dailies/20200903/comments")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void 일별데이터_댓글_작성_내용빈값() throws Exception {
        DailyDto.CommentWriteRequest request = new DailyDto.CommentWriteRequest();
        request.setDescription("");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/api/v1/members/2/dailies/20200903/comments")
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, getAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}