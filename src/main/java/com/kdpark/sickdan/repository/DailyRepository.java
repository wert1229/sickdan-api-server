package com.kdpark.sickdan.repository;

import com.kdpark.sickdan.domain.Comment;
import com.kdpark.sickdan.domain.Daily;
import com.kdpark.sickdan.domain.Likes;
import com.kdpark.sickdan.dto.DailyDto;
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
                        "and d.id.date in (:dates)", Daily.class)
                .setParameter("memberId", memberId)
                .setParameter("dates", dates)
                .getResultList();
    }

    public DailyDto.DailyCountInfo getCommentAndLikeCount(Long memberId, String yyyymmdd) {
        return (DailyDto.DailyCountInfo) em.createNativeQuery(
                "select " +
                        "    (select count(comment_id) " +
                        "     from   comment c " +
                        "     where  c.member_id = d.member_id " +
                        "     and    c.date = d.date) as commentCount, " +
                        "    (select count(*) " +
                        "     from   likes l " +
                        "     where  l.member_id = d.member_id " +
                        "     and    l.date = d.date) as likeCount " +
                        "from  daily d " +
                        "where member_id = :member_id " +
                        "and   date = :date", "CommentAndLikeCountMapping")
                .setParameter("member_id", memberId)
                .setParameter("date", yyyymmdd)
                .getSingleResult();
    }

    public List<Comment> getComments(Long memberId, String yyyymmdd) {
        return em.createQuery(
                "select c " +
                        "from Comment c " +
                        "join fetch c.writer " +
                        "left join fetch c.parent " +
                        "where c.daily.id.memberId = :member_id " +
                        "and   c.daily.id.date = :date " +
                        "and   c.parent.id is null", Comment.class)
                .setParameter("member_id", memberId)
                .setParameter("date", yyyymmdd)
                .getResultList();
    }

    public Comment getCommentById(Long id) {
        return em.find(Comment.class, id);
    }

    public Likes getLikeById(Likes.LikeId id) {
        return em.find(Likes.class, id);
    }

    public void undoLike(Likes likes) {
        em.remove(likes);
    }

}