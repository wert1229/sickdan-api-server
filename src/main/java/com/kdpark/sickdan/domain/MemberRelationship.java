package com.kdpark.sickdan.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
public class MemberRelationship {

//    @Id @GeneratedValue
//    @Column(name = "relationship_id")
//    private Long id;

    @EmbeddedId
    private MemberRelationshipId id;

    @MapsId("relatingId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relating_member", referencedColumnName = "member_id")
    private Member relatingMember;

    @MapsId("relatedId")
    @ManyToOne
    @JoinColumn(name = "related_member", referencedColumnName = "member_id")
    private Member relatedMember;

    @Enumerated(EnumType.STRING)
    private RelationshipStatus status;

    protected MemberRelationship() {}

    @Builder
    public MemberRelationship(MemberRelationshipId id, Member relatingMember, Member relatedMember, RelationshipStatus status) {
        this.id = id;
        this.relatingMember = relatingMember;
        this.relatedMember = relatedMember;
        this.status = status;
    }

    public void setFriend() {
        this.status = RelationshipStatus.FRIEND;
    }

    @Embeddable
    @Data
    public static class MemberRelationshipId implements Serializable {
        private Long relatingId;
        private Long relatedId;

        public MemberRelationshipId() {}

        public MemberRelationshipId(Long relatingId, Long relatedId) {
            this.relatingId = relatingId;
            this.relatedId = relatedId;
        }
    }
}
