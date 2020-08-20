package com.kdpark.sickdan.repository;

import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.DailyId;
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

    public Daily findById(DailyId id) {
        return em.find(Daily.class, id);
    }

    public Daily findOneDay(Long memberId, String yyyymmdd) {
        List<Daily> resultList = em.createQuery(
                "select d " +
                        "from Daily d " +
                        "where d.member.id = :memberId " +
                        "and d.dailyId.date = :yyyymmdd", Daily.class)
                .setParameter("memberId", memberId)
                .setParameter("yyyymmdd", yyyymmdd)
                .getResultList();

        return resultList.isEmpty() ? null : resultList.get(0);
    }

    public List<Daily> findOneMonth(Long memberId, String yyyymm) {
        return em.createQuery(
                "select d " +
                        "from Daily d " +
                        "where d.member.id = :memberId " +
                        "and d.dailyId.date like :yyyymm", Daily.class)
                .setParameter("memberId", memberId)
                .setParameter("yyyymm", yyyymm + "%")
                .getResultList();
    }
}