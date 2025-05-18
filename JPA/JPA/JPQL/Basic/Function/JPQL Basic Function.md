-----
### JPQL 기본 (표준) 함수
-----
: 데이터베이스 종류에 상관 없이 사용 가능
1. CONCAT (= || )
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
            member.setUsername("관리자");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT CONCAT('a', 'b') FROM Member m";
            // String query = "SELECT 'a' || 'b' FROM Member m";

            List<String> result = em.createQuery(query, String.class).getResultList();

            for (String s : result) {
                System.out.println("result = " + s);
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
        CONCAT('a', 'b') 
    FROM
        Member m */ select
            ('a'||'b') 
        from
            Member m1_0
result = ab
```

2. SUBSTRING
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
            member.setUsername("관리자");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT SUBSTRING(m.username, 2, 3) FROM Member m";

            List<String> result = em.createQuery(query, String.class).getResultList();

            for (String s : result) {
                System.out.println("result = " + s);
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
        SUBSTRING(m.username, 2, 3) 
    FROM
        Member m */ select
            substring(m1_0.username, 2, 3) 
        from
            Member m1_0

result = 리자
```

3. TRIM
4. LOWER, UPPER
5. LENGTH
6. LOCATE
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
            member.setUsername("관리자");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT LOCATE('de', 'abcdefg') FROM Member m";

            List<Integer> result = em.createQuery(query, Integer.class).getResultList();

            for (Integer s : result) {
                System.out.println("result = " + s);
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
        LOCATE('de', 'abcdefg') 
    FROM
        Member m */ select
            locate('de', 'abcdefg') 
        from
            Member m1_0
result = 4
```

7. ABS, SQRT, MOD
8. SIZE, INDEX (JPA 용도)
  - SIZE : 연관 관계에서 컬렉션의 크기 반환
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
            member.setUsername("관리자");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT SIZE(t.members) FROM Team t";

            List<Integer> result = em.createQuery(query, Integer.class).getResultList();

            for (Integer s : result) {
                System.out.println("result = " + s);
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
        SIZE(t.members) 
    FROM
        Team t */ select
            (select
                count(1) 
            from
                Member m1_0 
            where
                t1_0.id=m1_0.TEAM_ID) 
        from
            Team t1_0
result = 1
```

  - INDEX : @OrderColumn (List 값 타입에서 컬렉션 위치 값을 구할 때 사용)

-----
### 사용자 정의 함수 호출
-----
1. 하이버네이트는 사용 전 방언을 추가해야 함
2. 사용하는 DB 방언을 상속 받고, 사용자 정의 함수를 등록
```java
SELECT FUNTION("group_concat", i.name) FROM Item i
```

3. 💡 Hibernate 6.0 이하
```java
package dialect;

import.org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class MyH2Dialect extends H2Dialect {
    public MyH2Dialect() {
        registerFunction("group_concat", 
               new StandardSQLFunction("group_concat", StandardBasicTypes.STRING));
}
```

5. 💡 Hibernate 6.0 이상
```java
package dialect;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

public class MyH2Dialect implements FunctionContributor {
    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions
                .getFunctionRegistry()
                .registerNamed("group_concat", functionContributions.getTypeConfiguration()
                                                                         .getBasicTypeRegistry()
                                                                         .resolve(StandardBasicTypes.STRING));
    }
}
```

6. Persistence.xml에 사용자 정의 함수 추가 (버전 모두 동일)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2" xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="hello">
        <properties>

            ...

            <!-- 옵션 -->

            ...

            <property name="hibernate.function_contributor" value="dialect.MyH2Dialect"/>
        </properties>
    </persistence-unit>

</persistence>
```

  - Spring Boot 사용 : Application.properties에 사용자 정의 함수 추가
```properties
spring.jpa.properties.hibernate.function.contributor=dialect.MyH2Dialect
```

7. 예시
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
            member.setUsername("관리자");
            member.setAge(10);
            member.setTeam(team);
            member.setType(MemberType.ADMIN);
            em.persist(member);

            em.flush();
            em.clear();

            String query = "SELECT FUNCTION('group_concat', m.username) FROM Member m";
            // = Hibernate에서 가능 : String query = "SELECT group_concat(m.username) FROM Member m";

            List<String> result = em.createQuery(query, String.class).getResultList();

            for (String s : result) {
                System.out.println("result = " + s);
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
        FUNCTION('group_concat', m.username) 
    FROM
        Member m */ select
            group_concat(m1_0.username) 
        from
            Member m1_0

result = 관리자
```
