package hello.hellospring.repository;

import hello.hellospring.domain.Member;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class JpaMemberRepository implements MemberRepository {

    private final EntityManager em; // JPA는 EntitiyManager로 동작

    public JpaMemberRepository(EntityManager em) { // Dependency Injection
        this.em = em;
    }

    public Member save(Member member) {
        em.persist(member); // persist : 영구하다, 영속하다 -> em.persist(Object);
        return member;
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);  // em.find(Class, Column); [PK는 조회 가능]
        return Optional.ofNullable(member);
    }

    public List<Member> findAll() {
        // JPQL 객체지향 쿼리 언어 사용
        // em.createQuery("JPQL", Class) : 객체 (Entity 대상)를 대상으로 쿼리를 날림 => SQL로 번역
        // 결과 List 반환 : getResultList()
        return em.createQuery("select m from Member m", Member.class).getResultList();
        // "SELECT m FROM MEMBER m" : Member Entity 대상으로 조회 (m : Member)
        // SELECT를 특정 컬럼이 아닌 Member Entity로 조회
    }

    public Optional<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name).getResultList();
        // setParameter : Entity Parameter 지정
        return result.stream().findAny();
    }
}