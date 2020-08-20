package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members/{id}")
    public MemberInfoDto getMemberV1(@PathVariable Long id) {
        Member member = memberService.findById(id);
        return new MemberInfoDto(member.getId(), member.getEmail(), member.getDisplayName());
    }

    @Data
    static class MemberInfoDto {
        private Long id;
        private String email;
        private String displayName;

        public MemberInfoDto(Long id, String email, String displayName) {
            this.id = id;
            this.email = email;
            this.displayName = displayName;
        }
    }
}
