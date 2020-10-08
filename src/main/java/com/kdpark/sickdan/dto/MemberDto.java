package com.kdpark.sickdan.dto;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.RelationshipStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

public class MemberDto {
    @Data
    public static class FriendSearchResult {
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
    public static class MemberInfo {
        private Long id;
        private String email;
        private String displayName;
        private List<MemberRelationship> relationships;

        public MemberInfo(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.displayName = member.getDisplayName();
            this.relationships = member.getRelationships().stream()
                    .map(MemberRelationship::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class MemberRelationship {
        private Long id;
        private String displayName;
        private String email;
        private RelationshipStatus status;

        public MemberRelationship(com.kdpark.sickdan.domain.MemberRelationship relationship) {
            this.id = relationship.getRelatedMember().getId();
            this.email = relationship.getRelatedMember().getEmail();
            this.displayName = relationship.getRelatedMember().getDisplayName();
            this.status = relationship.getStatus();
        }
    }
}
