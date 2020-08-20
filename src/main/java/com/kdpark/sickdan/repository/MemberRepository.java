package com.kdpark.sickdan.repository;

import com.kdpark.sickdan.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
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
}
