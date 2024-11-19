-----
### 페이징 API
-----
1. JPA는 페이징을 다음 두 API로 추상화
2. setFirstResult(int startPosition) : 조회 시작 위치 (💡 0부터 시작)
3. setMaxResults(int maxResult) : 조회할 데이터 수
4. 예시
```java
// 페이징 쿼리
String jpql = "SELECT m FROM Member m ORDER BY m.name DESC";

List<Member> resultList = em.createQuery(jpql, Member.class)
                              .setFirstResult(10)
                              .setMaxResults(20)
                              .getResultList();
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
            for(int i = 0; i < 100; i ++) {

                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            List<Member> result = em.createQuery("SELECT m FROM Member m ORDER BY m.age DESC", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            for (Member member1 : result) {
                System.out.println("member = " + member1);
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
        m 
    FROM
        Member m 
    ORDER BY
        m.age DESC */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.username 
        from
            Member m1_0 
        order by
            m1_0.age desc 
        offset
            ? rows 
        fetch
            first ? rows only
result.size() = 10
member = Member{id=99, username='member98', age=98}
member = Member{id=98, username='member97', age=97}
member = Member{id=97, username='member96', age=96}
member = Member{id=96, username='member95', age=95}
member = Member{id=95, username='member94', age=94}
member = Member{id=94, username='member93', age=93}
member = Member{id=93, username='member92', age=92}
member = Member{id=92, username='member91', age=91}
member = Member{id=91, username='member90', age=90}
member = Member{id=90, username='member89', age=89}
```

5. MySQL 방언
```sql
SELECT
    M.ID AS ID,
    M.AGE AS AGE,
    M.TEAM_ID AS TEAM_ID, 
    M.NAME AS NAME
FROM
    MEMBER M 
ORDER BY
    M.NAME DESC LIMIT ?, ?
```

6. Oracle 방언
```sql
SELECT * FROM
    ( SELECT ROW_.*, ROWNUM ROWNUM_ 
FROM
        ( SELECT
            M.ID AS ID, 
            M.AGE AS AGE, 
            M.TEAM_ID AS TEAM_ID, 
            M.NAME AS NAME
FROM MEMBER M 
ORDER BY M.NAME
        ) ROW_ 
WHERE ROWNUM <= ?
    )
WHERE ROWNUM_ > ?
```
