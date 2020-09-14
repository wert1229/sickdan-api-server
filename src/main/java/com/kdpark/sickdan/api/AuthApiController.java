package com.kdpark.sickdan.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.Provider;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.AuthProviderException;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.error.exception.PasswordNotCorrectException;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.security.JwtTokenProvider;
import com.kdpark.sickdan.service.MemberService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthApiController {
    public static final String NAVER_REQUEST_URL = "https://openapi.naver.com/v1/nid/me";
    public static final String KAKAO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    public static final String REDIS_TOKEN_MEMBER = "redisTokenStore";

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/v1/signup")
    public void signUpV1(@RequestBody SignUpRequest request) {
        memberService.join(
            Member.builder()
                    .userId(request.getUserId())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode("{noop}" + request.getPassword()))
                    .displayName(request.getDisplayName())
                    .provider(Provider.LOCAL)
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build()
        );
    }

    @PostMapping("/v1/signin")
    public ResponseEntity<Void> signInV1(@RequestBody SignInRequest request) {
        Member member = memberService.findByUserId(request.getUserId());

        if (member == null || member.getProvider() != Provider.LOCAL)
            throw new EntityNotFoundException("멤버를 찾을 수 없음", ErrorCode.ENTITY_NOT_FOUND);
        if (!passwordEncoder.matches("{noop}" + request.getPassword(), member.getPassword()))
            throw new PasswordNotCorrectException("비밀번호 불일치", ErrorCode.INVALID_INPUT_VALUE);

        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(member.getId()), member.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()), member.getRoles());

        return ResponseEntity.ok()
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, accessToken)
                .header(JwtTokenProvider.REFRESH_TOKEN_HEADER, refreshToken)
                .build();
    }

    @PostMapping("/v1/oauth/naver")
    public ResponseEntity<Void> oauthNaverV1(@RequestBody OAuthTokenInfoDto tokenInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenInfo.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(NAVER_REQUEST_URL, HttpMethod.GET, entity, JsonNode.class);

        if (response.getStatusCode().isError() || response.getBody() == null)
            throw new AuthProviderException("naver failed", ErrorCode.EXTERNAL_IO_FAILED);

        ObjectMapper mapper = new ObjectMapper();

        try {
            NaverUserDto info = mapper.treeToValue(response.getBody().get("response"), NaverUserDto.class);
            Member member = memberRepository.findByUserId("naver_" + info.getId());

            if(member == null) {
                Long id = memberService.join(
                        Member.builder()
                                .userId("naver_" + info.getId())
                                .email(info.getEmail())
                                .password(passwordEncoder.encode("{noop}" + UUID.randomUUID()))
                                .displayName(info.name)
                                .provider(Provider.NAVER)
                                .roles(Collections.singletonList("ROLE_USER"))
                                .build()
                );

                member = memberRepository.findById(id);
            }

            String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(member.getId()), member.getRoles());
            String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()), member.getRoles());

            return ResponseEntity.ok()
                    .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, accessToken)
                    .header(JwtTokenProvider.REFRESH_TOKEN_HEADER, refreshToken)
                    .build();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/v1/oauth/kakao")
    public ResponseEntity<Void> oauthKakaoV1(@RequestBody OAuthTokenInfoDto tokenInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenInfo.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(KAKAO_REQUEST_URL, HttpMethod.GET, entity, JsonNode.class);

        if (response.getStatusCode().isError() || response.getBody() == null)
            throw new AuthProviderException("kakao failed", ErrorCode.EXTERNAL_IO_FAILED);

        JsonNode data = response.getBody();
        KakaoUserDto info = new KakaoUserDto();

        try {
            info.setId(data.get("id").asText(""));
            info.setEmail(data.get("kakao_account").get("email").asText(""));
            info.setNickname(data.get("properties").get("nickname").asText(""));
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new AuthProviderException("kakao failed", ErrorCode.EXTERNAL_IO_FAILED);
        }

        Member member = memberRepository.findByUserId("kakao_" + info.getId());

        if(member == null) {
            Long id = memberService.join(
                    Member.builder()
                            .userId("kakao_" + info.getId())
                            .email(info.getEmail())
                            .password(passwordEncoder.encode("{noop}" + UUID.randomUUID()))
                            .displayName(info.getNickname())
                            .provider(Provider.KAKAO)
                            .roles(Collections.singletonList("ROLE_USER"))
                            .build()
            );

            member = memberRepository.findById(id);
        }

        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(member.getId()), member.getRoles());
        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(member.getId()), member.getRoles());

        return ResponseEntity.ok()
                .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, accessToken)
                .header(JwtTokenProvider.REFRESH_TOKEN_HEADER, refreshToken)
                .build();
    }


    @PostMapping("/v1/token/refresh")
    public ResponseEntity<Void> signInV1(@RequestBody Map<String, Object> param) {
        String refreshToken = (String) param.getOrDefault("refreshToken", "");
        if (refreshToken.equals("")) return ResponseEntity.badRequest().build();

        Set<String> blackList = redisTemplate.opsForSet().members(REDIS_TOKEN_MEMBER);
        boolean isBlackListed = blackList != null && blackList.contains(refreshToken);
        if (isBlackListed) return ResponseEntity.badRequest().build();

        try {
            Long memberId = jwtTokenProvider.getUserPk(refreshToken);
            Member member = memberRepository.findById(memberId);
            String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(member.getId()), member.getRoles());

            return ResponseEntity.ok()
                    .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, accessToken)
                    .build();

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @Data
    static class SignUpRequest {
        private String userId;
        private String password;
        private String email;
        private String displayName;
    }

    @Data
    static class SignInRequest {
        private String userId;
        private String password;
    }

    @Data
    static class OAuthTokenInfoDto {
        private String accessToken;
        private String refreshToken;
        private long expiresAt;
        private String tokenType;
    }

    @Data
    static class NaverUserDto {
        private String id;
        private String email;
        private String nickname;
        private String name;
    }

    @Data
    static class KakaoUserDto {
        private String id;
        private String email;
        private String nickname;
    }
}
