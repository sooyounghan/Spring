package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.data_jpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {

    @PersistenceContext private EntityManager em;

    // 삽입
    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    // 삭제
    public void delete(Team team) {
        em.remove(team);
    }

    // 전체 조회
    public List<Team> findAll() {
        return em.createQuery("SELECT t FROM Team t", Team.class)
                .getResultList();
    }

    // 단건 조회
    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    // 카운트
    public Long count() {
        return em.createQuery("SELECT COUNT(t) FROM Team t", Long.class)
                .getSingleResult();
    }
}
