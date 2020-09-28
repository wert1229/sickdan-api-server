package com.kdpark.sickdan.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.Provider;
import com.kdpark.sickdan.dto.SignDto;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.*;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.security.JwtTokenProvider;
import com.kdpark.sickdan.service.MemberService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
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
    public void signUpV1(@Valid @RequestBody SignDto.SignUpRequest request) {
        Member member = memberRepository.findByUserId(request.getUserId());

        if (member != null) throw new EntityDuplicatedException("이미 존재하는 회원 아이디", ErrorCode.MEMBER_DUPLICATED);

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
    public ResponseEntity<Void> signInV1(@Valid @RequestBody SignDto.SignInRequest request) {
        Member member = memberRepository.findByUserId(request.getUserId());

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
    public ResponseEntity<Void> oAuthNaverV1(@Valid @RequestBody SignDto.OAuthTokenInfo tokenInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenInfo.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(NAVER_REQUEST_URL, HttpMethod.GET, entity, JsonNode.class);

        if (response.getStatusCode().isError() || response.getBody() == null)
            throw new OAuthProviderException("naver failed", ErrorCode.EXTERNAL_IO_FAILED);

        ObjectMapper mapper = new ObjectMapper();

        try {
            SignDto.NaverUser info = mapper.treeToValue(response.getBody().get("response"), SignDto.NaverUser.class);
            Member member = memberRepository.findByUserId("naver_" + info.getId());

            if(member == null) {
                Long id = memberService.join(
                        Member.builder()
                                .userId("naver_" + info.getId())
                                .email(info.getEmail())
                                .password(passwordEncoder.encode("{noop}" + UUID.randomUUID()))
                                .displayName(info.getName())
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
            throw new OAuthProviderException("naver json failed", ErrorCode.EXTERNAL_IO_FAILED);
        }
    }

    @PostMapping("/v1/oauth/kakao")
    public ResponseEntity<Void> oauthKakaoV1(@Valid @RequestBody SignDto.OAuthTokenInfo tokenInfo) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenInfo.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(KAKAO_REQUEST_URL, HttpMethod.GET, entity, JsonNode.class);

        if (response.getStatusCode().isError() || response.getBody() == null)
            throw new OAuthProviderException("kakao failed", ErrorCode.EXTERNAL_IO_FAILED);

        JsonNode data = response.getBody();
        SignDto.KakaoUser info = new SignDto.KakaoUser();

        try {
            info.setId(data.get("id").asText(""));
            info.setEmail(data.get("kakao_account").get("email").asText(""));
            info.setNickname(data.get("properties").get("nickname").asText(""));
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new OAuthProviderException("kakao failed", ErrorCode.EXTERNAL_IO_FAILED);
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
    public ResponseEntity<Void> signInV1(@Valid @RequestBody SignDto.RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        Set<String> blacklist = redisTemplate.opsForSet().members(REDIS_TOKEN_MEMBER);
        boolean isBlackListed = blacklist != null && blacklist.contains(refreshToken);
        if (isBlackListed) throw new BanishedTokenException("블랙리스트 된 리프레쉬토큰", ErrorCode.BANISHED_REFRESH_TOKEN);

        try {
            Long memberId = jwtTokenProvider.getUserPk(refreshToken);
            Member member = memberRepository.findById(memberId);
            String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(member.getId()), member.getRoles());

            return ResponseEntity.ok()
                    .header(JwtTokenProvider.ACCESS_TOKEN_HEADER, accessToken)
                    .build();

        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
