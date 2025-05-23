-----
### 페치 조인 (Fetch Join)
-----
1. 💡 SQL 조인 종류가 아닌, JPQL에서 성능 최적화를 위해 제공하는 기능
2. 연관된 엔티티나 컬렉션을 SQL에 한 번에 함께 조회하는 기능
3. JOIN FETCH 명령어 사용
```java
Fetch Join :: =

[LEFT [OUTER] | INNER] JOIN FETCH 조인경로
```

-----
### 엔티티 페치 조인 (Entity Fetch Join)
-----
1. 회원을 조회하면서 연관된 팀도 함께 조회 (즉, SQL로 한번에 조회)
   - 💡 Fetch Join으로 회원과 팀을 함께 조회해서 지연 로딩 미발생
   
2. SQL을 보면, 회원 뿐만 아니라 팀(T.*)도 함께 SELECT
3. 
4. JPQL
```java
SELECT m FROM Member m JOIN FETCH Team t
```

4. SQL
```java
SELECT M.*, T.* FROM Member M INNER JOIN TEAM T ON M.TEAM_ID = T.ID
```

<div align="center">
<img src="https://github.com/user-attachments/assets/00639d49-c046-45cb-b9fc-3abc318c9613">
</div>

5. Fetch Join 사용 코드
```java
String jpql = "SELECT m FROM Member m JOIN FETCH m.team";

List<Member> members = em.createQuery(jpql, Member.class)
                          .getResultList();

for(Member member : members) {
    // 💡 Fetch Join으로 회원과 팀을 함께 조회해서 지연 로딩 미발생
    System.out.println("username = " + member.getUsername() + ", " + "teamname = " + member.getTeam.name());
}
```

  - username = 회원1, teamname = 팀A
  - username = 회원2, teamname = 팀A
  - username = 회원3, teamname = 팀B

  - 지연 로딩
```java
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

String query = "SELECT m FROM Member m";

List<Member> result = em.createQuery(query, Member.class).getResultList();

for (Member member : result) {
    System.out.println("username = " + member.getUsername() + ", " + "teamName = " + member.getTeam().getName());
    // 지연 로딩
    // 회원1, 팀A(SQL)
    // 회원2, 팀A(1차 캐시)
    // 회원3, 팀B(SQL)

    // 💡 ... N번 : N + 1 번 (처음 쿼리 SQL 1번 + 100번 조회 N번 = N + 1번 문제 (해결 방법 : FETCH JOIN)
}

tx.commit();
```
```
Hibernate: // 지연 로딩으로 인해 Member 엔티티만 조회
    /* SELECT
        m 
    FROM
        Member m */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0
Hibernate: // Team 단일 컬렉션 값 SQL 실행하여 1차 캐시 저장
    select
        t1_0.id,
        t1_0.name 
    from
        Team t1_0 
    where
        t1_0.id=?

username = 회원1, teamName = TeamA // 단일 컬렉션 값 SQL 실행
username = 회원2, teamName = TeamA // 1차 캐시에 저장된 내역 반환

Hibernate: 
    select
        t1_0.id,
        t1_0.name 
    from
        Team t1_0 
    where
        t1_0.id=?

username = 회원3, teamName = TeamB
```
```java
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

String query = "SELECT m FROM Member m JOIN FETCH m.team"; // Fetch Join

List<Member> result = em.createQuery(query, Member.class).getResultList(); // Fetch Join으로 으로 영속성 컨텍스트에 모든 데이터가 1차 캐시에 저장

for (Member member : result) {
    System.out.println("username = " + member.getUsername() + ", " + "teamName = " + member.getTeam().getName()); // 지연 로딩이 아님
}

tx.commit();
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    JOIN
        
    FETCH
        m.team */ select
            m1_0.id,
            m1_0.age,
            t1_0.id,
            t1_0.name,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0 
        join
            Team t1_0 
                on t1_0.id=m1_0.TEAM_ID

username = 회원1, teamName = TeamA
username = 회원2, teamName = TeamA
username = 회원3, teamName = TeamB
```
-----
### Collection Fetch Join
-----
1. 일대다 관계 (Collection Fetch Join)
2. JPQL
```java
SELECT t FROM Team t JOIN FETCH t.members WEHRE t.name = '팀A'
```

3. SQL
```java
SELECT T.*, M.* FROM TEAM T INNER JOIN Member M ON T.ID = M.TEAM_ID
WHERE T.NAME = '팀A'
```
<div align="center">
<img src="https://github.com/user-attachments/assets/030d2d91-fb43-4089-9d9d-6b257cf56c26">
</div>

4. 컬렉션 페치 조인 사용 코드
```java
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

String query = "SELECT t FROM Team t JOIN FETCH t.members";

List<Team> result = em.createQuery(query, Team.class).getResultList();

for (Team team : result) {
    System.out.println("teamName = " + team.getName() + ", " + "size = " + team.getMembers().size());
}
```
```
Hibernate: 
    /* SELECT
        t 
    FROM
        Team t 
    JOIN
        
    FETCH
        t.members  */ select
            t1_0.id,
            m1_0.TEAM_ID,
            m1_0.id,
            m1_0.age,
            m1_0.type,
            m1_0.username,
            t1_0.name 
        from
            Team t1_0 
        join
            Member m1_0 
                on t1_0.id=m1_0.TEAM_ID
teamName = TeamA, size = 2  
teamName = TeamB, size = 1
```

```java
String jpql = "SELECT t FROM Team t JOIN FETCH t.members WHERE t.name = '팀A'";

List<Team> teams = em.createQuery(jpql, Team.class).getResultList();

for(Team team : teams) {
    System.out.println("teamname = " + team.getName() + ", team = " + team);
    for(Member member : team.getMembers()) {
        // 💡 페치 조인을 팀과 회원을 함께 조회해서 지연 로딩 발생 안함
        System.out.println(" -> username = " + member.getUsername() + ", member = " + member);
    }
}
```
```
// Hibernate 6 이상부터는 최적화로 DISTINCT 자동 적용
teamName = TeamA, team = hellojpa.Team@1fc1c7e
-> userName = 회원1, member = hellojpa.Member@5f3f3d00
-> userName = 회원2, member = hellojpa.Member@6b8bdcc6
```

-----
### Fetch Join과 DISTINCT
-----
1. SQL의 DISTINCT는 중복된 결과를 제공하는 명령
2. JPQL의 DISTINCT는 2가지 기능 제공
   - SQL에 DISTINCT 추가
   - 💡 애플리케이션에서 엔티티 중복 제거

3. 예시
```java
SELECT DISTINCT t
FROM Team t JOIN FETCH t.members
WEHRE t.name = '팀A'
```
  - SQL에 DISTINCT를 추가하지만, 데이터가 다르므로 SQL에서 중복제거 실패
<div align="center">
<img src="https://github.com/user-attachments/assets/1b7ff59d-ec43-4662-a868-5f030cbcf744">
</div>

4. DISTINCT가 추가로 애플리케이션 중복 제거 시도
   - 같은 식별자를 가진 Team 엔티티 제거
<div align="center">
<img src="https://github.com/user-attachments/assets/3866de89-7b11-461a-a32c-8c06745cb249">
</div>

  - DISTINCT 추가 시 결과
  - teamname = 팀A, team = Team@0x1000
    + -> username = 회원1, member = Member@0x200
    + -> username = 회원2, member = Member@0x300

5. Hibernate 변경 사항
   - DISTINCT 추가로 애플리케이션 중복 제거 시도
     + 하이버네이트6 부터는 DISTINCT 명령어를 사용하지 않아도 애플리케이션에서 중복 제거가 자동 적용
   - 참고 링크 : https://www.inﬂearn.com/questions/717679

-----
### 페치 조인과 일반 조인의 차이
-----
1. 일반 조인 실행 시, 연관된 엔티티를 함께 조회하지 않음
2. JPQL
```java
SELECT t
FROM Team t JOIN t.members 
WHERE t.name = '팀A'
```
```java
String query = "SELECT t FROM Team t JOIN t.members";

List<Team> result = em.createQuery(query, Team.class).getResultList();

System.out.println("result = " + result.size());
for (Team team : result) {
    System.out.println("teamName = " + team.getName() + ", " + "team = " + team);
    for (Member member : team.getMembers()) {
        System.out.println("-> userName = " + member.getUsername() + ", member = " + member);
    }
}
```
```
Hibernate: // Team 객체의 필드 값만 조회 (Member와 JOIN 되었을지라도)
    /* SELECT
        t 
    FROM
        Team t 
    JOIN
        t.members */ select
            t1_0.id,
            t1_0.name 
        from
            Team t1_0 
        join
            Member m1_0 
                on t1_0.id=m1_0.TEAM_ID

result = 2
teamName = TeamA, team = hellojpa.Team@394fb736

Hibernate: // t.members 지연 로딩
    select
        m1_0.TEAM_ID,
        m1_0.id,
        m1_0.age,
        m1_0.type,
        m1_0.username 
    from
        Member m1_0 
    where
        m1_0.TEAM_ID=?
-> userName = 회원1, member = hellojpa.Member@27358a19
-> userName = 회원2, member = hellojpa.Member@22865072

teamName = TeamB, team = hellojpa.Team@4fc6e776
Hibernate: // t.members 지연 로딩
    select
        m1_0.TEAM_ID,
        m1_0.id,
        m1_0.age,
        m1_0.type,
        m1_0.username 
    from
        Member m1_0 
    where
        m1_0.TEAM_ID=?
-> userName = 회원3, member = hellojpa.Member@4601047
```

3. SQL
```java
SELECT T.*
FROM TEAM T
INNER JOIN MEMBER M ON T.ID = M.TEAM_ID
WHERE T.NAME = '팀A'
```

3. 💡 JPQL은 결과를 반환할 때 연관관계를 고려하지 않음
   - 단지, SELECT 절에 지정한 엔티티만 조회
   - 여기서는 팀 엔티티만 조회하고, 회원 엔티티는 조회하지 않음

4. 💡 페치 조인을 사용할 때만, 연관된 엔티티도 함께 조회 (💡 = 즉시 로딩)
5. 페치 조인은 객체 그래프를 SQL 한번에 조회하는 개념

6. 페치 조인 실행 예시
  - JPQL
```java
SELECT t
FROM Team m JOIN FETCH t.members
WHERE t.name = '팀A'
```
```java
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

String query = "SELECT t FROM Team t JOIN FETCH t.members";

List<Team> result = em.createQuery(query, Team.class).getResultList();

System.out.println("result = " + result.size());
for (Team team : result) {
    System.out.println("teamName = " + team.getName() + ", " + "team = " + team);
    for (Member member : team.getMembers()) {
        System.out.println("-> userName = " + member.getUsername() + ", member = " + member);
    }
}
```

```
Hibernate: 
    /* SELECT
        t 
    FROM
        Team t 
    JOIN
        
    FETCH
        t.members */ select
            t1_0.id,
            m1_0.TEAM_ID,
            m1_0.id,
            m1_0.age,
            m1_0.type,
            m1_0.username,
            t1_0.name 
        from
            Team t1_0 
        join
            Member m1_0 
                on t1_0.id=m1_0.TEAM_ID

result = 2
teamName = TeamA, team = hellojpa.Team@11399548
-> userName = 회원1, member = hellojpa.Member@64c25a62
-> userName = 회원2, member = hellojpa.Member@16a6dc21
teamName = TeamB, team = hellojpa.Team@7871d261
-> userName = 회원3, member = hellojpa.Member@59f45950
```

4. SQL
```java
SELECT T.*, M.* FROM TEAM T INNER JOIN MEMBER M ON T.ID = M.TEAM_ID
WHERE T.NAME = '팀A'
```
