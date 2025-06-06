-----
### JPQL vs Querydsl
-----
1. 테스트 기본 코드
```java
package study.querydsl;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired EntityManager em;

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
}
```
  - 해당 예제로 실행

2. Querydsl vs JPQL
```java
@Test
public void startJPQL() {
    // member1을 찾기
    Member findMember = em.createQuery("SELECT m FROM Member m WHERE m.username = :username", Member.class)
            .setParameter("username", "member1")
            .getSingleResult();

    Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
}

@Test
public void startQuerydsl() {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    QMember m = new QMember("m");

    Member findMember = queryFactory.select(m)
                                     .from(m)
                                     .where(m.username.eq("member1")) // 파라미터 바인딩 처리
                                     .fetchOne();

    Assertions.assertThat(findMember).isEqualTo("member1");
}
```
  - EntityManager로 JPAQueryFactory 생성
  - Querydsl은 JPQL 빌더
  - 💡 JPQL : 문자 (실행 시점 오류), Querydsl : 코드 (컴파일 시점 오류)
  - 💡 JPQL : 파라미터 바인딩 직접, Querydsl : 파라미터 바인딩 자동 처리

-----
### JPQLQueryFactory를 필드
-----
```java
package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        ...
    }

    ...

    @Test
    public void startQuerydsl() {
        QMember m = new QMember("m");

        Member findMember = queryFactory.select(m)
                                         .from(m)
                                         .where(m.username.eq("member1")) // 파라미터 바인딩 처리
                                         .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}
```
  - 💡 JPAQueryFactory를 필드로 제공하면 동시성의 문제?
    + 동시성 문제는 JPAQueryFactory를 생성할 때 제공하는 EntityManager(em)에 달려있음
    + 스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManager에 접근해도, 트랜잭션마다 별도의 영속성 컨텍스트를 제공하므로, 동시성 문제는 걱정하지 않아도 됨
