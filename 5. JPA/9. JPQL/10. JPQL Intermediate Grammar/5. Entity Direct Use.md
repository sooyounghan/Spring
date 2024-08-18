-----
### 엔티티 직접 사용 - 기본 키 값
-----
1. 💡 JPQL에서 엔티티를 직접 사용하면, SQL에서 해당 엔티티의 기본 키 값을 사용
   - JPQL
```java
SELECT COUNT(m.id) FROM Member m // Entity의 아이디 사용
SELECT COUNT(m) FROM Member m // Entity 직접 사용
```

  - SQL : JPQL 둘 다 같은 다음 SQL 실행
```java
SELECT COUNT(m.id) AS cnt FROM Member m
```

2. 엔티티를 파라미터로 전달
```java
String jpql = "SELECT m FROM Member m WHERE m = :member";

Member singleResult = em.createQuery(query, Member.class)
        .setParameter("member", member1)
        .getSingleResult();

System.out.println("singleResult = " + singleResult);
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    WHERE
        m = :member */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0 
        where
            m1_0.id=?

singleResult = Member{id=1, username='회원1', age=10, type=null}
```

3. 식별자를 직접 전달
```java
String query = "SELECT m FROM Member m WHERE m.id = :memberId";

Member singleResult = em.createQuery(query, Member.class)
        .setParameter("memberId", member1.getId())
        .getSingleResult();

System.out.println("singleResult = " + singleResult);
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    WHERE
        m.id = :memberId */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0 
        where
            m1_0.id=?

singleResult = Member{id=1, username='회원1', age=10, type=null}
```

4. 실행된 SQL 값
```java
SELECT m.* FROM Member m WHERE m.id = ?
```

-----
### 엔티티 직접 사용 - 외래 키 값
-----
```java
Team team = em.find(Team.class, 1L);
String query = "SELECT m FROM Member m WHERE m.team = :team";

List<Member> resultList = em.createQuery(query, Member.class)
        .setParameter("team", team)
        .getResultList();

for (Member member : resultList) {
    System.out.println("member = " + member);
}
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    WHERE
        m.team = :team */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0 
        where
            m1_0.TEAM_ID=?

member = Member{id=1, username='회원1', age=10, type=null}
member = Member{id=2, username='회원2', age=20, type=null}
```

```java
String query = "SELECT m FROM Member m WHERE m.team.id = :teamId";

List<Member> resultList = em.createQuery(query, Member.class)
        .setParameter("teamId", teamA.getId())
        .getResultList();

for (Member member : resultList) {
    System.out.println("member = " + member);
}
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    WHERE
        m.team.id = :teamId */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0 
        where
            m1_0.TEAM_ID=?
member = Member{id=1, username='회원1', age=10, type=null}
member = Member{id=2, username='회원2', age=20, type=null}
```

: 실행된 SQL
```java
SELECT m.* FROM Member m WHERE m.team id = ?
```
