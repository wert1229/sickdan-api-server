package com.kdpark.sickdan.service;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.MemberRelationship;
import com.kdpark.sickdan.domain.RelationshipStatus;
import com.kdpark.sickdan.error.common.ErrorCode;
import com.kdpark.sickdan.error.exception.EntityNotFoundException;
import com.kdpark.sickdan.repository.MemberRepository;
import com.kdpark.sickdan.util.CryptUtil;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    public MemberInfoDto findById(Long memberId) {
        Member member = memberRepository.findById(memberId);
        return new MemberInfoDto(member);
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

    public FriendSearchResult searchByFilter(String by, String value, Long member_id) {
        Member findMember;

        if (by.equals("email")) {
            findMember = memberRepository.findByEmail(value);
        } else if (by.equals("code")) {
            String id = CryptUtil.decrypt(value);
            if ("".equals(id)) return null;
            findMember = memberRepository.findById(Long.parseLong(id));
        } else {
            // TODO
            throw new EntityNotFoundException("검색결과없음", ErrorCode.ENTITY_NOT_FOUND);
        }

        if (findMember == null) throw new EntityNotFoundException("검색결과없음", ErrorCode.ENTITY_NOT_FOUND);

        Member member = memberRepository.findById(member_id);

        if (findMember.getId().equals(member_id)) {
            return FriendSearchResult.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .displayName(member.getDisplayName())
                    .status(RelationshipStatus.SELF)
                    .build();
        }

        Map<Member, MemberRelationship> map = member.getRelationships().stream()
                .collect(Collectors.toMap(MemberRelationship::getRelatedMember, relationship -> relationship));

        FriendSearchResult result = FriendSearchResult.builder()
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

    @Data
    static public class FriendSearchResult {
        private Long id;
        private String email;
        private String displayName;
        private RelationshipStatus status;

        @Builder
        public FriendSearchResult(Long id, String email, String displayName, RelationshipStatus status) {
            this.id = id;
            this.email = email;
            this.displayName = displayName;
            this.status = status;
        }
    }

    @Data
    static public class MemberInfoDto {
        private Long id;
        private String email;
        private String displayName;
        private List<MemberRelationshipDto> relationships;

        public MemberInfoDto(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.displayName = member.getDisplayName();
            this.relationships = member.getRelationships().stream()
                    .map(r -> new MemberRelationshipDto(r))
                    .collect(Collectors.toList());
        }
    }

    @Data
    static public class MemberRelationshipDto {
        private Long id;
        private String displayName;
        private String email;
        private RelationshipStatus status;

        public MemberRelationshipDto(MemberRelationship relationship) {
            this.id = relationship.getRelatedMember().getId();
            this.email = relationship.getRelatedMember().getEmail();
            this.displayName = relationship.getRelatedMember().getDisplayName();
            this.status = relationship.getStatus();
        }
    }
}
