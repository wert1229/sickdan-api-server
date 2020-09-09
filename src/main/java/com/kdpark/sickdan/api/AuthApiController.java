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
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthApiController {
    public static final String NAVER_REQUEST_URL = "https://openapi.naver.com/v1/nid/me";
    public static final String KAKAO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate;
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

        String token = jwtTokenProvider.createToken(String.valueOf(member.getId()), member.getRoles());

        return ResponseEntity.ok().header("X-AUTH-TOKEN", token).build();
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

            String token = jwtTokenProvider.createToken(String.valueOf(member.getId()), member.getRoles());
            return ResponseEntity.ok().header("X-AUTH-TOKEN", token).build();

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

        String token = jwtTokenProvider.createToken(String.valueOf(member.getId()), member.getRoles());
        return ResponseEntity.ok().header("X-AUTH-TOKEN", token).build();
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
