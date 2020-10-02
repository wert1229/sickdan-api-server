package com.kdpark.sickdan.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.Provider;
import com.kdpark.sickdan.dto.SignDto;
import com.kdpark.sickdan.dto.TestSignDto;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.security.JwtTokenProvider;
import com.kdpark.sickdan.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static com.kdpark.sickdan.api.AuthApiController.NAVER_REQUEST_URL;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthApiController.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class AuthApiControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MemberService memberService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private RedisTemplate<String, String> redisTemplate;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void 회원가입_로컬_정상처리() throws Exception {
        SignDto.SignUpRequest request = new SignDto.SignUpRequest();
        request.setUserId("userid");
        request.setPassword("1234");
        request.setEmail("wert@nav.com");
        request.setDisplayName("ddd");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
    }

    @Test
    public void 회원가입_로컬_아이디_빈값() throws Exception {
        SignDto.SignUpRequest request = new SignDto.SignUpRequest();
        request.setUserId(" ");
        request.setPassword("1234");
        request.setEmail("wert@nav.com");
        request.setDisplayName("ddd");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 회원가입_로컬_이메일_빈값() throws Exception {
        SignDto.SignUpRequest request = new SignDto.SignUpRequest();
        request.setUserId("userid");
        request.setPassword("1234");
        request.setEmail(" ");
        request.setDisplayName("ddd");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 회원가입_로컬_닉네임_빈값() throws Exception {
        SignDto.SignUpRequest request = new SignDto.SignUpRequest();
        request.setUserId("userid");
        request.setEmail("wert@nav.com");
        request.setPassword("1234");
        request.setDisplayName(" ");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 회원가입_로컬_이메일_형식틀림() throws Exception {
        SignDto.SignUpRequest request = new SignDto.SignUpRequest();
        request.setUserId("userid");
        request.setEmail("wertnavcom");
        request.setPassword("1234");
        request.setDisplayName("ddd");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 회원가입_로컬_회원_아이디_중복() throws Exception {
        SignDto.SignUpRequest request = new SignDto.SignUpRequest();
        request.setUserId("userid");
        request.setPassword("1234");
        request.setEmail("wert@nav.com");
        request.setDisplayName("ddd");

        when(memberRepository.findByUserId(any()))
                .thenReturn(Member.builder().build());

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_로컬_정상처리() throws Exception {
        SignDto.SignInRequest request = new SignDto.SignInRequest();
        request.setUserId("userid");
        request.setPassword("1234");

        when(memberRepository.findByUserId("userid"))
                .thenReturn(Member.builder()
                        .userId("userid")
                        .password(passwordEncoder.encode("{noop}1234"))
                        .roles(Collections.singletonList("ROLE_USER"))
                        .provider(Provider.LOCAL)
                        .build());

        when(jwtTokenProvider.createAccessToken(any(), anyList())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(any(), anyList())).thenReturn("refreshToken");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtTokenProvider.ACCESS_TOKEN_HEADER))
                .andExpect(header().exists(JwtTokenProvider.REFRESH_TOKEN_HEADER));
    }

    @Test
    public void 로그인_로컬_아이디_빈값() throws Exception {
        SignDto.SignInRequest request = new SignDto.SignInRequest();
        request.setUserId(" ");
        request.setPassword("1234");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_로컬_패스워드_빈값() throws Exception {
        SignDto.SignInRequest request = new SignDto.SignInRequest();
        request.setUserId("userid");
        request.setPassword(" ");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_로컬_존재하지않는_회원() throws Exception {
        SignDto.SignInRequest request = new SignDto.SignInRequest();
        request.setUserId("null");
        request.setPassword("1234");

        when(memberRepository.findByUserId(any())).thenReturn(null);

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_로컬_비밀번호_불일치() throws Exception {
        SignDto.SignInRequest request = new SignDto.SignInRequest();
        request.setUserId("userid");
        request.setPassword("1234");

        when(memberRepository.findByUserId("userid"))
        .thenReturn(Member.builder()
                .userId("userid")
                .password(passwordEncoder.encode("{noop}5678"))
                .provider(Provider.LOCAL)
                .build());

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_로컬_OAuth아이디로_시도() throws Exception {
        SignDto.SignInRequest request = new SignDto.SignInRequest();
        request.setUserId("userid");
        request.setPassword("1234");

        when(memberRepository.findByUserId("userid"))
                .thenReturn(Member.builder()
                        .userId("userid")
                        .password(passwordEncoder.encode("{noop}1234"))
                        .provider(Provider.NAVER)
                        .build());

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_네이버_엑세스토큰_빈값() throws Exception {
        SignDto.OAuthTokenInfo tokenInfo = new SignDto.OAuthTokenInfo();
        tokenInfo.setAccessToken(" ");

        String content = objectMapper.writeValueAsString(tokenInfo);

        mvc.perform(post("/v1/oauth/naver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_네이버_네이버엑세스_실패() throws Exception {
        SignDto.OAuthTokenInfo tokenInfo = new SignDto.OAuthTokenInfo();
        tokenInfo.setAccessToken("SomeValidToken");

        when(restTemplate.exchange(eq(NAVER_REQUEST_URL), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));

        String content = objectMapper.writeValueAsString(tokenInfo);

        mvc.perform(post("/v1/oauth/naver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_네이버_Json파싱_실패() throws Exception {
        SignDto.OAuthTokenInfo tokenInfo = new SignDto.OAuthTokenInfo();
        tokenInfo.setAccessToken("SomeValidToken");

        TestSignDto.WrongFormatNaverUser wrongFormatUser = new TestSignDto.WrongFormatNaverUser();
        Map<String, Object> map = new HashMap<>();
        map.put("response", wrongFormatUser);

        ResponseEntity<JsonNode> response = new ResponseEntity<>(objectMapper.convertValue(map, JsonNode.class), HttpStatus.OK);

        when(restTemplate.exchange(eq(NAVER_REQUEST_URL), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(response);

        String content = objectMapper.writeValueAsString(tokenInfo);

        mvc.perform(post("/v1/oauth/naver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 로그인_네이버_신규생성_정상처리() throws Exception {
        SignDto.OAuthTokenInfo tokenInfo = new SignDto.OAuthTokenInfo();
        tokenInfo.setAccessToken("SomeValidToken");

        SignDto.NaverUser naverUser = new SignDto.NaverUser();
        Map<String, Object> map = new HashMap<>();
        map.put("response", naverUser);

        ResponseEntity<JsonNode> response = new ResponseEntity<>(objectMapper.convertValue(map, JsonNode.class), HttpStatus.OK);

        when(restTemplate.exchange(eq(NAVER_REQUEST_URL), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(response);

        when(memberService.join(any())).thenReturn(1L);

        when(memberRepository.findById(anyLong()))
                .thenReturn(Member.builder()
                        .id(1L)
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build());

        when(jwtTokenProvider.createAccessToken(any(), anyList())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(any(), anyList())).thenReturn("refreshToken");

        String content = objectMapper.writeValueAsString(tokenInfo);

        mvc.perform(post("/v1/oauth/naver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtTokenProvider.ACCESS_TOKEN_HEADER))
                .andExpect(header().exists(JwtTokenProvider.REFRESH_TOKEN_HEADER));
    }

    @Test
    public void 로그인_네이버_조회_정상처리() throws Exception {
        SignDto.OAuthTokenInfo tokenInfo = new SignDto.OAuthTokenInfo();
        tokenInfo.setAccessToken("SomeValidToken");

        SignDto.NaverUser naverUser = new SignDto.NaverUser();
        Map<String, Object> map = new HashMap<>();
        map.put("response", naverUser);

        ResponseEntity<JsonNode> response = new ResponseEntity<>(objectMapper.convertValue(map, JsonNode.class), HttpStatus.OK);

        when(restTemplate.exchange(eq(NAVER_REQUEST_URL), eq(HttpMethod.GET), any(), eq(JsonNode.class)))
                .thenReturn(response);

        when(memberRepository.findByUserId(any()))
                .thenReturn(Member.builder()
                        .id(1L)
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build());

        when(jwtTokenProvider.createAccessToken(any(), anyList())).thenReturn("accessToken");
        when(jwtTokenProvider.createRefreshToken(any(), anyList())).thenReturn("refreshToken");

        String content = objectMapper.writeValueAsString(tokenInfo);

        mvc.perform(post("/v1/oauth/naver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtTokenProvider.ACCESS_TOKEN_HEADER))
                .andExpect(header().exists(JwtTokenProvider.REFRESH_TOKEN_HEADER));
    }

    @Test
    public void 엑세스토큰_재발급_리프레쉬토큰_빈값() throws Exception {
        SignDto.RefreshRequest request = new SignDto.RefreshRequest();
        request.setRefreshToken(" ");

        String content = objectMapper.writeValueAsString(request);

        mvc.perform(post("/v1/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 엑세스토큰_재발급_블랙리스트() throws Exception {
        SignDto.RefreshRequest request = new SignDto.RefreshRequest();
        request.setRefreshToken("validRefreshToken");

        String content = objectMapper.writeValueAsString(request);

        Set<String> blacklist = new HashSet<>();
        blacklist.add("validRefreshToken");

        SetOperations setOperations = mock(SetOperations.class);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(blacklist);

        mvc.perform(post("/v1/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 엑세스토큰_재발급_만료된_리프레쉬토큰() throws Exception {
        SignDto.RefreshRequest request = new SignDto.RefreshRequest();
        request.setRefreshToken("validRefreshToken");

        String content = objectMapper.writeValueAsString(request);

        Set<String> blacklist = new HashSet<>();

        SetOperations setOperations = mock(SetOperations.class);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(blacklist);

        when(jwtTokenProvider.getUserPk(anyString()))
                .thenThrow(mock(ExpiredJwtException.class));

        mvc.perform(post("/v1/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void 엑세스토큰_재발급_정상처리() throws Exception {
        SignDto.RefreshRequest request = new SignDto.RefreshRequest();
        request.setRefreshToken("validRefreshToken");

        String content = objectMapper.writeValueAsString(request);

        Set<String> blacklist = new HashSet<>();

        SetOperations setOperations = mock(SetOperations.class);

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.members(anyString())).thenReturn(blacklist);

        when(memberRepository.findById(any()))
                .thenReturn(Member.builder()
                        .id(1L)
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build());

        when(jwtTokenProvider.createAccessToken(any(), anyList())).thenReturn("accessToken");

        mvc.perform(post("/v1/token/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(header().exists(JwtTokenProvider.ACCESS_TOKEN_HEADER));
    }
}