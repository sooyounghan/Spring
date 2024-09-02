package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Member;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {
    @PersistenceContext EntityManager em;

    // 삽입
    public Member save(Member member) {
        em.persist(member);
        return member;
    }


    // 삭제
    public void delete(Member member) {
        em.remove(member);
    }

    // 전체 조회
    public List<Member> findAll() {
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }

    // 단건 조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    // 단건 조회
    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    // 카운트
    public Long count() {
        return em.createQuery("SELECT COUNT(m) FROM Member m", Long.class)
                .getSingleResult();
    }
}
