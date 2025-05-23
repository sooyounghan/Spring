-----
### 경로 표현식
-----
1. . (점)을 찍어서 객체 그래프를 탐색하는 것
```java
SELECT m.username → 상태 필드
  FROM Member m
  JOIN m.team t → 단일 값 연관 필드
  JOIN m.orders o → 컬렉션 값 연관 필드
WHERE t.name = '팀A'
```

2. 💡 상태 필드 (State Field) : 단순히 값을 저장하기 위해 필드 (m.username)
   - 경로 탐색의 끝으로, 탐색이 불가
```java
String query = "SELECT m.username FROM Member m"; // 더 이상 경로 탐색 불가

em.createQuery(query, Team.class).getResultList();
```
```
Hibernate: 
    /* SELECT
        m.username 
    FROM
        Member m */ select
            m1_0.username 
        from
            Member m1_0
```

3. 💡 연관 필드 (Association Field) : 연관 관계를 위한 필드
   - 단일 값 연관 필드 : @ManyToOne, @OneToOne 대상이 Entity (예) m.team)
      + 💡 단일 값 연관 경로 : 묵시적 내부 조인 (INNER JOIN) 발생, 탐색 가능
      + 묵시적 내부 조인 예
```java
String query = "SELECT m.team FROM Member m"; // m.team에서 묵시적 내부 조인 발생, 탐색 가능

em.createQuery(query, String.class).getResultList();
```
```java
String query = "SELECT m.team.name FROM Member m"; // m.team에서 묵시적 내부 조인 발생, 탐색 가능

em.createQuery(query, String.class).getResultList();
```
```
Hibernate: 
    /* SELECT
        m.team.name 
    FROM
        Member m */ select
            t1_0.name 
        from
            Member m1_0 
        join
            Team t1_0 
                on t1_0.id=m1_0.TEAM_ID
```

   - 💡 컬렉션 값 연관 필드 : @OneToMany, @ManyToMany 대상이 컬렉션 (예) m.orders)
      + 💡 컬렉션 값 연관 경로 : 묵시적 내부 조인 발생하지만 탐색 불가
      + FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해 탐색 가능
```java
String query = "SELECT t.members FROM Team t"; // t.members에서 묵시적 내부 조인 발생, 탐색 불가

em.createQuery(query, Collection.class).getResultList();
```
```
Hibernate: 
    /* SELECT
        t.members 
    FROM
        Team t */ select
            m1_0.id,
            m1_0.age,
            t1_0.id,
            t1_0.name,
            m1_0.type,
            m1_0.username 
        from
            Team t1_0 
        join
            Member m1_0 
                on t1_0.id=m1_0.TEAM_ID
```

```java
String query = "SELECT m.username FROM Team t JOIN t.members m"; // 명시적 조인을 통해 경로 탐색 가능

em.createQuery(query, String.class).getResultList();
```
```
Hibernate: 
    /* SELECT
        m.username 
    FROM
        Team t 
    JOIN
        t.members m */ select
            m1_0.username 
        from
            Team t1_0 
        join
            Member m1_0 
                on t1_0.id=m1_0.TEAM_ID
```

4. 상태 필드 경로 탐색
   - JPQL
```java
SELECT m.username, m.age FROM Member m
```
   - SQL
```java
SELECT m.username, m.age FROM Member m
```

5. 단일 값 연관 경로 탐색
   - JPQL
```java
SELECT o.member FROM Order o // 묵시적 JOIN 발생
```
  - SQL
```java
SELECT m.*
  FROM Orders o
  INNER JOIN Member m ON o.member_id = m.id
```

-----
### 💡 명시적 조인, 묵시적 조인
-----
1. 명시적 조인 : JOIN 키워드 직접 사용
```java
SELECT m FROM Member m JOIN m.team t
```

2. 묵시적 조인 : 경로 표현식에 의해 묵시적으로 SQL 조인 발생 (내부 조인만 가능)
```java
SELECT m.team FROM Member m
```

-----
### 경로 표현식 예제
-----
```java
SELECT o.member.team FROM Order o
```
1. 성공 (조인 2번 발생(o.member, o.member.team) : 성능 저하)

```java
SELECT t.members FROM Team t
```
2. 성공 (조인 1번 발생 (t.members))

```java
SELECT t.members.username FROM Team t
```
3.  실패 (묵시적 JOIN 발생(t.members)했으나, 컬렉션 값 연관 필드는 탐색 불가(t.members.username))

```java
SELECT m.username FROM Team t JOIN t.members m
```
4. 💡 성공 (FROM 절에 명시적 JOIN 사용, FROM절 별칭 붙여 경로 탐색)

-----
### 💡 경로 탐색을 사용한 묵시적 조인 시 주의사항
-----
1. 항상 내부 조인
2. 💡 컬렉션은 경로 탐색의 끝, 명시적 조인을 통해 별칭을 얻어야 함
3. 경로 탐색은 주로 SELECT, WHERE 절에서 사용하지만, 묵시적 조인으로 인해 SQL의 FROM (JOIN) 절에 영향을 줌

-----
### 실무 조언
-----
1. 가급적 묵시적 조인 대신 명시적 조인 사용
2. 조인은 SQL 튜닝에 중요 포인트
3. 묵시적 조인은 조인이 일어나는 상황을 한눈에 파악하기 어려움
