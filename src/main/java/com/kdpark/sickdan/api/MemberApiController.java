package com.kdpark.sickdan.api;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.service.MemberService;
import com.kdpark.sickdan.service.MemberService.MemberInfoDto;
import com.kdpark.sickdan.util.CryptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/api/v1/members/{memberId}")
    public MemberInfoDto getMemberV1(@PathVariable @Min(1) Long memberId) {
        return memberService.findById(memberId);
    }

    @GetMapping("/api/v1/members/me")
    public MemberInfoDto getAuthMemberV1(Principal principal) {
        String memberId = principal.getName();
        return memberService.findById(Long.parseLong(memberId));
    }

    @GetMapping("/api/v1/members")
    public MemberService.FriendSearchResult searchFriendByEmail(@RequestParam @NotBlank String by,
                                                                @RequestParam @NotBlank String value,
                                                                Principal principal) {
        String memberId = principal.getName();
        MemberService.FriendSearchResult friendSearchResult = memberService.searchByFilter(by, value, Long.parseLong(memberId));

        if (friendSearchResult == null)
            throw new EntityNotFoundException("멤버를 찾을 수 없음", ErrorCode.ENTITY_NOT_FOUND);

        return friendSearchResult;
    }

    @PostMapping("/api/v1/members/me/relationships")
    public void requestFriend(@RequestBody Long relatedId, Principal principal) {
        String memberId = principal.getName();
        memberService.requestFriend(Long.parseLong(memberId), relatedId);
    }

    @PutMapping("/api/v1/members/me/relationships")
    public void acceptFriend(@RequestBody Long relatedId, Principal principal) {
        String memberId = principal.getName();
        memberService.acceptFriend(Long.parseLong(memberId), relatedId);
    }

    @GetMapping("/api/v1/members/exist")
    public Map<String, Boolean> checkDuplicate(@RequestParam String userId) {
        Map<String, Boolean> result = new HashMap<>();
        Member member = memberRepository.findByUserId(userId);

        if (member == null) result.put("exist", false);
        else result.put("exist", true);

        return result;
    }

    @GetMapping("/api/v1/members/me/code")
    public Map<String, String> getCode(Principal principal) {
        String memberId = principal.getName();
        return Collections.singletonMap("code", CryptUtil.encrypt(memberId));
    }
}
