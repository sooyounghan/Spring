-----
### 조인 - 기본 조인
-----
1. 💡 기본 조인
   - 조인의 기본 문법은 첫 번째 파라미터에 조인 대상을 지정
   - 두 번째 파라미터에 별칭(alias)으로 사용할 Q타입 지정
```java
join(조인 대상, 별칭으로 사용할 Q타입)
```
```java
/**
 * 팀 A에 소속된 모든 회원
 */
@Test
public void join() {
    List<Member> result = queryFactory.selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch();

    Assertions.assertThat(result).extracting("username").containsExactly("member1", "member2");
}
```
2. 정리
   - join(), innerJoin() : 내부 조인(Inner Join)
   - leftJoin() : Left 외부 조인 (Left Outer Join)
   - rightJoin() : Right 외부 조인 (Right Outer Join)
   - JPQL의 on과 성능 최적화를 위한 Fetch 조인 제공

3. 세타 조인 : 연관관계가 없는 필드로 조인
```java
/**
 * 세타 조인 : 연관관계가 없는 필드로 조인
 *  - 회원의 이름이 팀 이름과 같은 회원 조회
 */
@Test
public void theta_join() {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    List<Member> result = queryFactory.select(member)
            .from(member, team) // 세타 조인
            .where(member.username.eq(team.name))
            .fetch();
    
    Assertions.assertThat(result).extracting("username").containsExactly("teamA", "teamB");
}
```
  - from 절에 여러 엔티티를 선택해서 세타 조인
  - 💡 외부 조인 불가능 : 다음에 설명할 조인 on을 사용하면 외부 조인 가능

-----
### 조인 - ON절
-----
1. ON절을 활용한 조인 (JPA 2.1부터 지원)
   - 조인 대상 필터링
   - 연관관계 없는 엔티티 외부 조인

2. 조인 대상 필터링
  - 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
```java
/**
 * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
 *    - JPQL : SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'teamA'
 *    - SQL : SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID = t.id AND t.name = 'teamA'
 */
@Test
public void join_on_filtering() {
    List<Tuple> result = queryFactory.select(member, team)
            .from(member)
            .leftJoin(member.team, team).on(team.name.eq("teamA"))
            .fetch();

    for (Tuple tuple : result) {
        System.out.println("tuple = " + tuple);
    }
}
```
  - 결과
```
tuple = [Member(id=1, username=member1, age=10), Team(id=1, name=teamA)]
tuple = [Member(id=2, username=member2, age=20), Team(id=1, name=teamA)]
tuple = [Member(id=3, username=member3, age=30), null]
tuple = [Member(id=4, username=member4, age=40), null]
```
  - 💡 참고
    + ON절을 활용해 대상을 필터링 할 떄, 외부 조인이 아니라 내부 조인(Inner Join)을 사용하면, WHERE 절에서 필터링하는 것과 기능이 동일
    + 따라서, ON절을 활용한 조인 대상 필터링을 사용할 때, 내부조인이면 익숙한 WHERE 절로 해결, 정말 외부 조인이 필요한 경우에만 이 기능 사용
```java
/**
 * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
 *    - JPQL : SELECT m, t FROM Member m LEFT JOIN m.team t ON t.name = 'teamA'
 *    - SQL : SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID = t.id AND t.name = 'teamA'
 */
@Test
public void join_on_filtering() {
    List<Tuple> result = queryFactory.select(member, team)
            .from(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch();

    for (Tuple tuple : result) {
        System.out.println("tuple = " + tuple);
    }
}
```

3. 연관관계 없는 엔티티 외부 조인
   - 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
```java
/**
 * 2. 연관관계 없는 엔티티 외부 조인
 *  - 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
 *  JPQL : SELECT m, t FROM Member m LEFT JOIN Team t ON m.username = t.name
 *  SQL : SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
 */
@Test
public void join_on_no_relation() {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    List<Tuple> result = queryFactory.select(member, team)
            .from(member)
            .leftJoin(team).on(member.username.eq(team.name))
            .fetch();

    for (Tuple tuple : result) {
        System.out.println("tuple = " + tuple);
    }
}
```
  - Hibernate 5.1부터 ON을 사용해서 서로 관계가 없는 필드로 외부 조인하는 기능 추가
  - 물론, 내부 조인도 가능
  - 💡 주의! 문법을 잘 봐야 함 : leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어감
    + 일반 조인 : leftJoin(member.team, team)
    + 💡 ON 조인 : from(member).leftJoin(team).on(Xxx)

  - 결과
```
tuple = [Member(id=1, username=member1, age=10), null]
tuple = [Member(id=2, username=member2, age=20), null]
tuple = [Member(id=3, username=member3, age=30), null]
tuple = [Member(id=4, username=member4, age=40), null]
tuple = [Member(id=5, username=teamA, age=0), Team(id=1, name=teamA)]
tuple = [Member(id=6, username=teamB, age=0), Team(id=2, name=teamB)]
```

-----
### 조인 - 페치 조인
-----
1. Fetch Join은 SQL에서 제공하는 기능이 아님
2. SQL 조인을 활용해서 연관된 엔티티를 SQL 한 번에 조회하는 기능
3. 주로 성능 최적화에 사용하는 방법
4. 페치 조인 미적용 : 지연로딩으로 Member, Team SQL 쿼리 각각 실행
```java
@PersistenceUnit EntityManagerFactory emf;

@Test
public void fetchJoinNo() {
    em.flush();
    em.clear();

    Member findMember = queryFactory.selectFrom(member)
            .where(member.username.eq("member1"))
            .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    Assertions.assertThat(loaded).as("페치 조인 미적용").isFalse();
}
```

5 페치 조인 적용 : 즉시로딩으로 Member, Team SQL 쿼리 조인으로 한 번에 조인
```java
@Test
public void fetchJoinUse() {
    em.flush();
    em.clear();

    Member findMember = queryFactory.selectFrom(member)
            .join(member.team, team).fetchJoin() // Fetch Join
            .where(member.username.eq("member1"))
            .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    Assertions.assertThat(loaded).as("페치 조인 적용").isTrue();
}
```

6. 사용 방법 : join, fetchJoin() 등 조인 기능 뒤 fetchJoin() 추가
