-----
### 벌크 연산
-----
1. 재고가 10개 미만인 모든 상품의 가격을 10% 상승하려면?
2. JPA 변경 감지 기능으로 실행하려면 너무 많은 SQL 실행
   - 재고가 10개 미만인 상품을 리스트로 조회
   - 상품 엔티티의 가격을 10% 증가
   - 트랜잭션 커밋 시점에 변경 감지가 동작
3. 변경된 데이터가 100건이라면 100번의 UPDATE SQL 실행

-----
### 벌크 연산 예제
-----
1. 쿼리 한 번으로 여러 테이블 ROW 변경 (Entity)
2. 💡 executeUpdate()의 결과는 영향받은 엔티티 수 반환
3. UPDATE, DELETE 지원
4. INSERT(INSERT INTO .. SELECT), Hibernate 지원
```java
String query = "UPDATE Product p SET p.price = p.price * 1.1 WEHRE p.stockAmount < :stockAmount";

int resultCount = em.createQuery(query)
                    .setParameter("stockAmount", 10)
                    .executeUpdate();
```

5. 예제
```java
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

// flush 자동 호출 (COMMIT, QUERY 전송)
int resultCount = em.createQuery("UPDATE Member m SET m.age = 20")
                    .executeUpdate();

System.out.println("resultCount = " + resultCount);
```
```
Hibernate: 
    /* UPDATE
        Member m 
    SET
        m.age = 20 */ update Member 
    set
        age=20
resultCount = 3
```

-----
### 벌크 연산 주의
-----
1. 💡 벌크 연산은 영속성 컨텍스트를 무시하고, 데이터베이스에 직접 쿼리
```java
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

// Flush가 되어, DB에 반영되나 영속성 컨텍스트에는 미 반영
int resultCount = em.createQuery("UPDATE Member m SET m.age = 20")
                    .executeUpdate();

System.out.println("resultCount = " + resultCount);

// 영속성 컨텍스트에는 미 반영
System.out.println("member1.getAge() = " + member1.getAge());
System.out.println("member1.getAge() = " + member2.getAge());
System.out.println("member1.getAge() = " + member3.getAge());
```
```
Hibernate: 
    /* UPDATE
        Member m 
    SET
        m.age = 20 */ update Member 
    set
        age=20
resultCount = 3
member1.getAge() = 10
member1.getAge() = 20
member1.getAge() = 30
```
   - 데이터 정합성이 깨질 수 있음

2. 💡 해결 방법 2가지
   - 벌크 연산 먼저 실행
   - 벌크 연산 수행 후 영속성 컨텍스트 초기화
```java
int resultCount = em.createQuery("UPDATE Member m SET m.age = 20")
                    .executeUpdate();

em.clear(); // 영속성 컨텍스트 초기화

// DB 반영된 데이터 영속성 컨텍스트에 저장
Member updateMember1 = em.find(Member.class, member1.getId());
Member updateMember2 = em.find(Member.class, member2.getId());
Member updateMember3 = em.find(Member.class, member3.getId());

System.out.println("resultCount = " + resultCount);
System.out.println("member1.getAge() = " + updateMember1.getAge());
System.out.println("member1.getAge() = " + updateMember1.getAge());
System.out.println("member1.getAge() = " + updateMember1.getAge());
```
```
Hibernate: 
    /* UPDATE
        Member m 
    SET
        m.age = 20 */ update Member 
    set
        age=20

Hibernate: 
    select
        m1_0.id,
        m1_0.age,
        m1_0.TEAM_ID,
        m1_0.type,
        m1_0.username 
    from
        Member m1_0 
    where
        m1_0.id=?
Hibernate: 
    select
        m1_0.id,
        m1_0.age,
        m1_0.TEAM_ID,
        m1_0.type,
        m1_0.username 
    from
        Member m1_0 
    where
        m1_0.id=?
Hibernate: 
    select
        m1_0.id,
        m1_0.age,
        m1_0.TEAM_ID,
        m1_0.type,
        m1_0.username 
    from
        Member m1_0 
    where
        m1_0.id=?

resultCount = 3
member1.getAge() = 20
member1.getAge() = 20
member1.getAge() = 20
```
