package com.kdpark.sickdan.repository;

import com.kdpark.sickdan.domain.Daily;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyRepository {

    private final EntityManager em;

    public void save(Daily daily) {
        em.persist(daily);
    }

    public Daily findById(Daily.DailyId id) {
        return em.find(Daily.class, id);
    }

    public List<Daily> findOneMonth(Long memberId, String yyyymm) {
        return em.createQuery(
                "select d " +
                        "from Daily d " +
                        "where d.member.id = :memberId " +
                        "and d.id.date like :yyyymm", Daily.class)
                .setParameter("memberId", memberId)
                .setParameter("yyyymm", yyyymm + "%")
                .getResultList();
    }

    public List<Daily> findByDates(Long memberId, List<String> dates) {
        return em.createQuery(
                "select d " +
                        "from Daily d " +
                        "where d.id.memberId = :memberId " +
                        "and d.id.date in (:yyyymm)", Daily.class)
                .setParameter("memberId", memberId)
                .setParameter("dates", dates)
                .getResultList();
    }
}