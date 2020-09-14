package com.kdpark.sickdan.domain;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Entity
@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQuery(name="Member.relationships", query=
        "select m " +
        "from Member m " +
        "where m in (" +
            "select mr.relatingMember " +
            "from MemberRelationship mr " +
            "where mr.relatedMember.id = :memberId) " +
        "or m in (" +
            "select mr.relatedMember " +
            "from MemberRelationship mr " +
            "where mr.relatingMember.id = :memberId)")
public class Member implements UserDetails {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String userId;

    private String password;

    private String email;

    private String displayName;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Daily> dailies = new ArrayList<>();

    @OneToMany(mappedBy = "relatingMember", cascade = CascadeType.ALL)
    private List<MemberRelationship> relationships = new ArrayList<>();

    protected Member() {}

    @Builder
    public Member(Long id, String userId, String email, String password, String displayName, Provider provider, List<String> roles) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.provider = provider;
        this.roles = roles;
    }

    public void requestFriend(Member requested) {
        relationships.add(
                MemberRelationship.builder()
                        .id(new MemberRelationship.MemberRelationshipId(this.getId(), requested.getId()))
                        .relatingMember(this)
                        .relatedMember(requested)
                        .status(RelationshipStatus.REQUESTING)
                        .build()
        );

        requested.getRelationships().add(
                MemberRelationship.builder()
                        .id(new MemberRelationship.MemberRelationshipId(requested.getId(), this.getId()))
                        .relatingMember(requested)
                        .relatedMember(this)
                        .status(RelationshipStatus.REQUESTED)
                        .build()
        );
    }

    public void acceptFriend(Member requesting) {
        relationships.forEach(relationship -> {
            if (relationship.getId().getRelatedId().equals(requesting.getId()))
                relationship.setFriend();
        });

        requesting.getRelationships().forEach(relationship -> {
            if (relationship.getId().getRelatedId().equals(this.getId()))
                relationship.setFriend();
        });
    }

    //==스프링 시큐리티 관련==//
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return String.valueOf(this.id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
