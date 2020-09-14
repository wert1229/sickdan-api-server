package com.kdpark.sickdan.repository;

import com.kdpark.sickdan.domain.Member;
import com.kdpark.sickdan.domain.MemberRelationship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void saveMember(Member member) {
        em.persist(member);
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Member findByUserId(String userId) {
        List<Member> result = em.createQuery(
                "select m " +
                        "from Member m " +
                        "where m.userId = :userId", Member.class)
                .setParameter("userId", userId)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public Member findByEmail(String email) {
        List<Member> result = em.createQuery(
                "select m " +
                        "from Member m " +
                        "where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public void saveRelation(MemberRelationship relationship){
        em.persist(relationship);
    }

    public List<MemberRelationship> getRelationshipsByMemberId(Long memberId) {
        return em.createNamedQuery("Member.relationships", MemberRelationship.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
