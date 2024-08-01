-----
### JPA 구동 방식
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/e6addf8a-c897-4d3e-af2f-22c22ccbe957">
</div>

  - JPA는 /META-INF/persistence.xml의 설정 정보를 읽고 조회
  - Persistence 클래스로부터 시작해, EntityManagerFactory를 클래스를 생성
  - 필요할 때마다 EntityManager를 생성

  - JpaMain 
```java
package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        // Code

        em.close();
        emf.close();
    }
}
```

-----
### 객체와 테이블을 생성하고 매핑
-----
1. SQL로 Member Table 생성
```sql
CREATE TABLE Member (
  id BIGINT NOT NULL,
  name VARCHAR(255),
  PRIMARY KEY (id)
);
```

2. JPA를 통해 객체와 테이블을 생성하고 매핑
```java
package hellojpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
// @Table(name = "USER") // DB의 USER 테이블과 Mapping, 없으면 클래스명과 동일한 테이블명에 Mapping
public class Member {

    @Id
    private Long id;
    
    // @Column(name = "username") // DB의 Mapping된 테이블에서 username Column에 Mapping, 해당 테이블의 해당 필드명과 동일한 Column에 Mapping
    private String name;

    // Getter, Setter
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
  - @Entity : JPA가 관리할 객체
  - @Id : 데이터베이스 PK와 매핑

3. JpaMain
```java
package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        // 회원 등록
        try {
            Member member = new Member();
            member.setId(1L);
            member.setName("HelloA"); // Member 테이블에 (id, name) = (1L, "HelloA") 데이터 삽입

            em.persist(member); // 데이터 저장

            tx.commit(); // 트랜잭션 커밋
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }


        // 회원 조회 및 수정
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();

        try {
            // 회원 조회
            Member findMember = em.find(Member.class, 1L); // Member 테이블에서 id = 1L인 데이터 조회
            System.out.println("findMember.id = " + findMember.getId()); // 1L
            System.out.println("findMember.name = " + findMember.getName()); // HelloA

            // 회원 수정
            findMember.setName("HelloJPA"); // HelloA를 HelloJPA로 변경 (persist 사용 안해도, 커밋 시점에 변경된 상태 적용)

            tx.commit(); // 트랜잭션 커밋
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        // 회원 삭제
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();

        try {
            Member findMember = em.find(Member.class, 1L); 
            em.remove(findMember); // id가 1L인 데이터 삭제

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
  - 실행 로그
```
Hibernate: 
    create table Member (
        id bigint not null,
        name varchar(255),
        primary key (id)
    )
// 생성 시점에 한 번만 생성 (persistence.xml 속성 참고)
...

Hibernate: 
    /* insert for
        hellojpa.Member */
    insert into
        Member (name, id) 
    values
        (?, ?)

...

Hibernate: 
    select
        m1_0.id,
        m1_0.name 
    from
        Member m1_0 
    where
        m1_0.id=?

findMember.id = 1
findMember.name = HelloJPA

...

Hibernate: 
    select
        m1_0.id,
        m1_0.name 
    from
        Member m1_0 
    where
        m1_0.id=?

...

Hibernate: 
    /* update
        for hellojpa.Member */
    update Member 
    set
        name=? 
    where
        id=?

...

Hibernate: 
    /* delete for hellojpa.Member */
    delete from
        Member 
    where
        id=?
```

-----
### 주의사항
-----
1. 💡 EntityManagerFactory는 하나만 생성해서, 애플리케이션 전체에서 공유
2. 💡 EntityManager는 Thread 간에 공유하지 않음 (사용하고 버려야 함)
3. 💡 JPA의 모든 데이터 변경은 트랜잭션 안에서 실행

-----
### JPQL
-----
1. 가장 단순한 조회방법
   - EntityManger.find()
   - 객체 그래프 탐색 (a.getB().getC())

2. 예) 회원 전체 조회
```java
// 회원 조회 (JPQL)
em = emf.createEntityManager();
tx = em.getTransaction();
tx.begin();
try {
    // JPQL로 회원 전체 조회
    List<Member> result = em.createQuery("SELECT m FROM Member m", Member.class) 
                         .getResultList(); // Member Entity 선택
    for (Member member : result) {
        System.out.println("member.name = " + member.getName());
    }
    tx.commit();
} catch (Exception e) {
    tx.rollback();
} finally {
    em.close();
}
```

3. 실행 로그
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m */
        select
            m1_0.id,
            m1_0.name 
        from
            Member m1_0

member.name = HelloJPA
member.name = HelloB
```

4. JPA를 사용하면 Entity 객체를 중심으로 개발
5. 문제는 검색 쿼리
   - 검색을 할 때도, Table이 아닌 Entity 객체를 대상으로 검색
   - 하지만, 모든 DB 데이터를 객체로 변환해서 검색하는 것은 불가능
   - 애플리케이션이 필요한 데이터만 DB에서 불러오려면, 검색 조건이 필요한 SQL 필요
6. JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
   - SQL과 문법 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원

7. 💡 JPQL은 Entity 객체를 대상으로 쿼리 (즉, Table이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리)
   - 💡 SQL을 추상화해서 특정 데이터베이스 SQL에 의존하지 않음 (즉, 데이터베이스 방언이 바뀌어도 JPQL 자체를 변경할 필요가 없으며, 데이터베이스 방언에 맞게 변경)
   - 즉, 객체 지향 SQL

8. 💡 SQL은 데이터베이스 테이블을 대상으로 쿼리
