-----
### JPQL 타입 표현
-----
1. 문자 : 'HELLO', 'She''s'
2. 숫자 : 10L (Long), 10D (Double), 10F (Float)
3. Boolean : TRUE, FALSE
4. ENUM : jpabook.MemberType.Admin (💡 패키지명 포함)
  - MemberType
```java
package hellojpa;

public enum MemberType {
    ADMIN, USER;
}
```
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;

    @Enumerated(EnumType.STRING)
    private MemberType type;

    ...
}
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
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m.username, 'HELLO', TRUE, FALSE FROM Member m WHERE m.type = hellojpa.MemberType.ADMIN";
            List<Object[]> result = em.createQuery(query)
                                         .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
                System.out.println("objects[2] = " + objects[2]);
            }
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
        m.username,
        'HELLO',
        TRUE,
        FALSE 
    FROM
        Member m 
    WHERE
        m.type = hellojpa.MemberType.ADMIN */ select
            m1_0.username,
            'HELLO',
            true,
            false 
        from
            Member m1_0 
        where
            m1_0.type=0

objects[0] = member
objects[1] = HELLO
objects[2] = true
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
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m.username, 'HELLO', TRUE, FALSE FROM Member m WHERE m.type = hellojpa.MemberType.USER";
            List<Object[]> result = em.createQuery(query)
                                         .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
                System.out.println("objects[2] = " + objects[2]);
            }
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
        m.username,
        'HELLO',
        TRUE,
        FALSE 
    FROM
        Member m 
    WHERE
        m.type = hellojpa.MemberType.USER */ select
            m1_0.username,
            'HELLO',
            true,
            false 
        from
            Member m1_0 
        where
            m1_0.type='USER'
```

  - 파라미터 바인딩
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
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m.username, 'HELLO', TRUE, FALSE FROM Member m WHERE m.type = :userType";
            List<Object[]> result = em.createQuery(query)
                                         .setParameter("userType", MemberType.ADMIN)
                                         .getResultList();

            for (Object[] objects : result) {
                System.out.println("objects[0] = " + objects[0]);
                System.out.println("objects[1] = " + objects[1]);
                System.out.println("objects[2] = " + objects[2]);
            }
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

5. 엔티티 타입 : TYPE(m) = Member (상속 관계에서 사용)
    - 예시) Item 클래스 상속 : Book
```java
package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Item;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("김영한");

            em.persist(book);

            em.createQuery("SELECT i FROM Item i WHERE TYPE(i) = Book", Item.class)
                            .getResultList();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
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
        i 
    FROM
        Item i 
    WHERE
        TYPE(i) = Book */ select
            i1_0.ITEM_ID,
            i1_0.DTYPE,
            i1_0.StackQunatity,
            i1_0.createdBy,
            i1_0.createdDate,
            i1_0.lastModifiedBy,
            i1_0.lastModifiedDate,
            i1_0.name,
            i1_0.price,
            i1_0.artist,
            i1_0.etc,
            i1_0.author,
            i1_0.isbn,
            i1_0.actor,
            i1_0.director 
        from
            Item i1_0 
        where
            i1_0.DTYPE='Book'
```

-----
### JPQL 기타
-----
1. SQL과 문법이 같은 식
   - EXISTS, IN
   - AND, OR, NOT
   - ```=, >, >=, <, <=, <>```
   - BETWEEN, LIKE, IS NULL

2. 예시
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
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT m FROM Member m WHERE m.username IS NOT NULL AND m.age BETWEEN 10 AND 20";
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
    WHERE
        m.username IS NOT NULL 
        AND m.age BETWEEN 10 AND 20 */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0 
        where
            m1_0.username is not null 
            and m1_0.age between 10 and 20
```
