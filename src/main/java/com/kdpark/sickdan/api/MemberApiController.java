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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/api/v1/members/{id}")
    public MemberInfoDto getMemberV1(@PathVariable Long id) {
        return memberService.findById(id);
    }

    @GetMapping("/api/v1/members/me")
    public MemberInfoDto getAuthMemberV1(@RequestAttribute Long member_id) {
        return memberService.findById(member_id);
    }

    @GetMapping("/api/v1/members")
    public MemberService.FriendSearchResult searchFriendByEmail(@RequestParam String by,
                                                                @RequestParam String value,
                                                                @RequestAttribute Long member_id) {
        MemberService.FriendSearchResult friendSearchResult = memberService.searchByFilter(by, value, member_id);

        if (friendSearchResult == null)
            throw new EntityNotFoundException("멤버를 찾을 수 없음", ErrorCode.ENTITY_NOT_FOUND);

        return friendSearchResult;
    }

    @PostMapping("/api/v1/members/me/relationships")
    public void requestFriend(@RequestBody Long relatedId, @RequestAttribute Long member_id) {
        memberService.requestFriend(member_id, relatedId);
    }

    @PutMapping("/api/v1/members/me/relationships")
    public void acceptFriend(@RequestBody Long relatedId, @RequestAttribute Long member_id) {
        memberService.acceptFriend(member_id, relatedId);
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
    public Map<String, String> getCode(@RequestAttribute Long member_id) {
        return Collections.singletonMap("code", CryptUtil.encrypt(String.valueOf(member_id)));
    }
}
