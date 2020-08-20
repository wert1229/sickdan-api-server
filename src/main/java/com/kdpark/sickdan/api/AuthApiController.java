package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.UserNotFoundException;
import com.kdpark.sickdan.security.JwtTokenProvider;
import com.kdpark.sickdan.service.CustomUserDetailService;
import com.kdpark.sickdan.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @PostMapping("/v1/signup")
    public void signUpV1(@RequestBody SignUpRequest request) {
        memberService.join(
            Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode("{noop}" + request.getPassword()))
                .displayName(request.getDisplayName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build()
        );
    }

    @PostMapping("/v1/signin")
    public ResponseEntity signInV1(@RequestBody SignInRequest request) {
        Member member = memberService.findByEmail(request.getEmail());

        if (member == null)
            throw new UserNotFoundException("멤버를 찾을 수 없음", ErrorCode.ENTITY_NOT_FOUND);
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword()))
            throw new IllegalArgumentException("비밀번호 불일치");

        String token = jwtTokenProvider.createToken(String.valueOf(member.getId()), member.getRoles());

        return ResponseEntity.ok().header("X-AUTH-TOKEN", token).build();
    }

    @Data
    static class SignUpRequest {
        private String email;
        private String password;
        private String displayName;
    }

    @Data
    static class SignInRequest {
        private String email;
        private String password;
    }
}
