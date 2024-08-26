package hellojpa;

import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
            Team teamA = new Team();
            teamA.setName("TeamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("TeamB");
            em.persist(teamB);

            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setAge(10);
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setAge(20);
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setAge(30);
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            int resultCount = em.createQuery("UPDATE Member m SET m.age = 20")
                                .executeUpdate();

            em.clear(); // 영속성 컨텍스트 초기화

            // DB 반영된 데이터 영속성 컨텍스트에 저장
            Member updateMember1 = em.find(Member.class, member1.getId());
            Member updateMember2 = em.find(Member.class, member2.getId());
            Member updateMember3 = em.find(Member.class, member3.getId());

            System.out.println("resultCount = " + resultCount);
            System.out.println("member1.getAge() = " + updateMember1.getAge());
            System.out.println("member1.getAge() = " + updateMember1.getAge());
            System.out.println("member1.getAge() = " + updateMember1.getAge());

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
