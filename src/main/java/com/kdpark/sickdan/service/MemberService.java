package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.MemberRelationship;
import com.kdpark.sickdan.domain.RelationshipStatus;
import com.kdpark.sickdan.dto.MemberDto;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.error.exception.InvalidParameterException;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.util.CryptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Long join(Member member) {
        memberRepository.saveMember(member);
        return member.getId();
    }

    public MemberDto.MemberInfo findById(Long memberId) {
        Member member = memberRepository.findById(memberId);
        return new MemberDto.MemberInfo(member);
    }

    public void getRelationships(Long memberId) {
        List<MemberRelationship> relationships = memberRepository.getRelationshipsByMemberId(memberId);
    }

    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public MemberDto.FriendSearchResult searchByFilter(String by, String value, Long member_id) {
        Member findMember;

        if (by.equals("email")) {
            findMember = memberRepository.findByEmail(value);
        } else if (by.equals("code")) {
            String id = CryptUtil.decrypt(value);
            if ("".equals(id))
                throw new InvalidParameterException("code value cannot be decrypted", ErrorCode.INVALID_INPUT_VALUE);

            findMember = memberRepository.findById(Long.parseLong(id));
        } else {
            throw new InvalidParameterException("filter 'by' value is invalid", ErrorCode.INVALID_INPUT_VALUE);
        }

        if (findMember == null) throw new EntityNotFoundException("검색결과없음", ErrorCode.ENTITY_NOT_FOUND);

        Member member = memberRepository.findById(member_id);

        if (findMember.getId().equals(member_id)) {
            return MemberDto.FriendSearchResult.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .displayName(member.getDisplayName())
                    .status(RelationshipStatus.SELF)
                    .build();
        }

        Map<Member, MemberRelationship> map = member.getRelationships().stream()
                .collect(Collectors.toMap(MemberRelationship::getRelatedMember, relationship -> relationship));

        MemberDto.FriendSearchResult result = MemberDto.FriendSearchResult.builder()
                .id(findMember.getId())
                .email(findMember.getEmail())
                .displayName(findMember.getDisplayName())
                .status(map.containsKey(findMember) ? map.get(findMember).getStatus() : RelationshipStatus.NONE)
                .build();

        return result;
    }

    public void requestFriend(Long relatingMemberId, Long relatedMemberId) {
        Member relatingMember = memberRepository.findById(relatingMemberId);
        Member relatedMember = memberRepository.findById(relatedMemberId);

        relatingMember.requestFriend(relatedMember);
    }

    public void acceptFriend(Long relatingMemberId, Long relatedMemberId) {
        Member relatingMember = memberRepository.findById(relatingMemberId);
        Member relatedMember = memberRepository.findById(relatedMemberId);

        relatingMember.acceptFriend(relatedMember);
    }

}
