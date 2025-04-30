-----
### 프로젝션 (Projection)
-----
1. SELECT 절에 조회할 대상을 지정하는 것
2. 프로젝션 대상 : Entity Type, Embedded Type, Scalar Type(숫자, 문자 등 기본 데이터 타입)
   - SELECT m FROM Member m : Entity Projection
     + 💡 Entity Projection으로 조회된 값들은 영속성 컨텍스트에서 관리
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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            // Entity Projection
            List<Member> result = em.createQuery("SELECT m FROM Member m", Member.class)
                                    .getResultList();  
            
            Member findMember = result.get(0); 
            findMember.setAge(20); // 영속성 컨텍스트에 반영되어 관리
            
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
        Member m */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.username 
        from
            Member m1_0

Hibernate: 
    /* update
        for hellojpa.Member */update Member 
    set
        age=?,
        TEAM_ID=?,
        username=? 
    where
        id=?
```
   - SELECT m.team FROM Member m : Entity Proejction (Member에 연관된 Team Entity)

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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            // Entity Projection (Member - Team JOIN)
            // 묵시적 조인
            // List<Team> result = em.createQuery("SELECT m.team FROM Member m", Team.class).getResultList();
            // 명시적 조인
            List<Team> result = em.createQuery("SELECT t FROM Member m JOIN m.team t", Team.class)
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
Hibernate: // Member - Team JOIN
    /* SELECT
        m.team 
    FROM
        Member m */ select
            t1_0.id,
            t1_0.name 
        from
            Member m1_0 
        join
            Team t1_0 
                on t1_0.id=m1_0.TEAM_ID
```

   - SELECT o.address FROM Order o : Embedded Type Projection (💡 임베디드 값 타입은 소속된 타입 명시해야 되는 단점 존재)
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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            // Embedded Type Projection
            List<Address> result = em.createQuery("SELECT o.address FROM Order o", Address.class)
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
        o.address 
    FROM
        
    Order o */ select
        o1_0.city,
        o1_0.street,
        o1_0.zipcode from
            ORDERS o1_0
```

   - SELECT m.username, m.age FROM Member m : Scalar Type Projection
     + DISTINCT로 중복 제거
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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            // Scalar Type Projection
            em.createQuery("SELECT DISTINCT m.username, m.age FROM Member m")
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
        DISTINCT m.username,
        m.age 
    FROM
        Member m */ select
            distinct m1_0.username,
            m1_0.age 
        from
            Member m1_0
```


-----
### 여러 값 조회
-----
```java
SELECT m.username, m.age FROM Member m
```
1. Query 타입으로 조회
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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            List resultList = em.createQuery("SELECT DISTINCT m.username, m.age FROM Member m")
                    .getResultList();

            Object o = resultList.get(0);
            Object[] result = (Object[]) o;

            System.out.println("username = " + result[0]);
            System.out.println("age = " + result[1]);

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
        DISTINCT m.username,
        m.age 
    FROM
        Member m */ select
            distinct m1_0.username,
            m1_0.age 
        from
            Member m1_0

username = member1
age = 10
```
2. Object[] 타입으로 조회
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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            List<Object[]> resultList = em.createQuery("SELECT DISTINCT m.username, m.age FROM Member m")
                    .getResultList();

            Object[] result = resultList.get(0);

            System.out.println("username = " + result[0]);
            System.out.println("age = " + result[1]);

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
        DISTINCT m.username,
        m.age 
    FROM
        Member m */ select
            distinct m1_0.username,
            m1_0.age 
        from
            Member m1_0

username = member1
age = 10
```

3. 💡 new 명령어로 조회
   - 단순 값을 DTO로 바로 조회
```java
SELECT new hellojpa.MemberDTO(m.username, m.age) FROM Member m
```
   - MemberDTO
```java
package hellojpa;

public class MemberDTO {
    private String username;
    private int age;

    public MemberDTO(String username, int age) {
        this.username = username;
        this.age = age;
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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();

            List<MemberDTO> resultList = em.createQuery("SELECT DISTINCT new hellojpa.MemberDTO(m.username, m.age) FROM Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = resultList.get(0);

            System.out.println("username = " + memberDTO.getUsername());
            System.out.println("age = " + memberDTO.getAge());

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
        DISTINCT new hellojpa.MemberDTO(m.username, m.age) 
    FROM
        Member m */ select
            distinct m1_0.username,
            m1_0.age 
        from
            Member m1_0

username = member1
age = 10
```
  - 패키지 명을 포함한 전체 클래스 명 입력
  - 💡 순서와 타입이 일치하는 생성자 필요
