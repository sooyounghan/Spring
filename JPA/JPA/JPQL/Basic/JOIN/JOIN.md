-----
### 조인
-----
- Member
```java
package hellojpa;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;

    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    public void changeTeam(Team team) { // 연관관계 편의 메서드
        this.team = team;
        team.getMembers().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
```

- Team
```java
package hellojpa;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
```

1. 내부 조인 (INNER JOIN)
```java
SELECT m FROM Member m [INNER] JOIN m.team t
```
```java
package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m FROM Member m INNER JOIN m.team t";
            List<Member> result = em.createQuery(query, Member.class).getResultList();

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
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    INNER JOIN
        m.team t */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.username 
        from
            Member m1_0 
        join
            Team t1_0 
                on t1_0.id=m1_0.TEAM_ID
```

2. 외부 조인 (OUTER JOIN)
```java
SELECT m FROM Member m LEFT [OUTER] JOIN m.team t
```
```java
package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
                Team team = new Team();
                team.setName("TeamA");
                em.persist(team);

                Member member = new Member();
                member.setUsername("member");
                member.setAge(10);
                member.setTeam(team);
                em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m FROM Member m LEFT OUTER JOIN m.team t";
            List<Member> result = em.createQuery(query, Member.class).getResultList();

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
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    LEFT OUTER JOIN
        m.team t */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.username 
        from
            Member m1_0
```
  - 💡 Hibernate 버전이 업데이트되면서, 자동으로 최적화 (Left Join을 한 것과 하지 않은 것이 현재 결과가 동일하여 LEFT OUTER JOIN 제거)

```java
package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m FROM Member m LEFT OUTER JOIN m.team t WHERE t.name = :teamName";
            List<Member> result = em.createQuery(query, Member.class)
                    .setParameter("teamName", "teamA")
                    .getResultList();

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
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    LEFT OUTER JOIN
        m.team t 
    WHERE
        t.name = :teamName */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.username 
        from
            Member m1_0 
        left join
            Team t1_0 
                on t1_0.id=m1_0.TEAM_ID 
        where
            t1_0.name=?
```

3. 세타 조인 (Cartessian Product) : 연관관계가 없는 조인
```java
SELECT COUNT(m) FROM MEMBER m, TEAM t WHERE m.username = t.name
```
```java
package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m FROM Member m, Team t WHERE m.username = t.name";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

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
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m,
        Team t 
    WHERE
        m.username = t.name */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.username 
        from 
            Member m1_0, // = Member m1_0 cross join
            Team t1_0    // = Team t1_0
        where
            m1_0.username=t1_0.name
```
   - 💡 Hibernate 버전이 업데이트되면서, 자동으로 최적화
     
4. 💡 ON절을 활용한 조인 (JPA 2.1부터 지원)
   - 조인 대상 필터링
   - 연관 관계 없는 엔티티 외부 조인 (Hiberante 5.1 ~)

5. 조인 대상 필터링
  - 예) 회원과 팀을 조인하면서, 팀 이름이 A인 팀만 조인
  - JPQL
```java
SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'A'
```
  - SQL
```java
SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID = t.id AND t.name = 'A'
```
```java
package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m FROM Member m LEFT JOIN m.team t ON t.name = 'teamA'";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

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
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    LEFT JOIN
        m.team t 
            ON t.name = 'teamA' */ select
                m1_0.id,
                m1_0.age,
                m1_0.TEAM_ID,
                m1_0.username 
        from
            Member m1_0 
        left join
            Team t1_0 
                on t1_0.id=m1_0.TEAM_ID 
                and t1_0.name='teamA'
```

6. 연관관계 없는 엔티티 외부 조인
   - 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
   - JPQL
```java
SELECT m, t FROM Member m LEFT JOIN Team t ON m.username = t.name
```
   - SQL
```java
SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.usernme = t.name
```
```java
package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);
            member.setTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m FROM Member m LEFT JOIN Team t ON m.username = t.name";
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

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
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    LEFT JOIN
        Team t 
            ON m.username = t.name */ select
                m1_0.id,
                m1_0.age,
                m1_0.TEAM_ID,
                m1_0.username 
        from
            Member m1_0 
        left join
            Team t1_0 
                on m1_0.username=t1_0.name
```
