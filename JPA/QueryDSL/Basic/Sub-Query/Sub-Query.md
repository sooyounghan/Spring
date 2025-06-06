-----
### 서브 쿼리
-----
1. com.querydsl.jpa.JPAExpressions 사용
2. 서브 쿼리 eq 사용
```java
/**
 * 나이가 가장 많은 회원 조회
 */
@Test
public void subQuery() {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(
                    JPAExpressions.select(memberSub.age.max())
                            .from(memberSub)
            ))
            .fetch();
    
    Assertions.assertThat(result).extracting("age").containsExactly(40);
}
```

3. 서브 쿼리 goe 사용
```java
/**
 * 나이가 평균 나이 이상인 회원
 */
@Test
public void subQueryGoe() {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.goe(
                    JPAExpressions.select(memberSub.age.avg())
                            .from(memberSub)
            ))
            .fetch();

    Assertions.assertThat(result).extracting("age").containsExactly(30, 40);
}
```

4. 서브 쿼리 여러 건 처리 IN 사용
```java
/**
 * 서브 쿼리 여러 건 처리, IN 사용
 */
@Test
public void subQueryIn() {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.in(
                    JPAExpressions.select(memberSub.age)
                            .from(memberSub)
                            .where(memberSub.age.gt(10))
            ))
            .fetch();

    Assertions.assertThat(result).extracting("age").containsExactly(20, 30, 40);
}
```

5. SELECT 절에 SubQuery
```java
@Test
public void selectSubQuery() {
    QMember memberSub = new QMember("memberSub");

    List<Tuple> fetch = queryFactory.select(member.username,
                    JPAExpressions.select(memberSub.age.avg())
                            .from(memberSub)
            ).from(member)
            .fetch();

    for (Tuple tuple : fetch) {
        System.out.println("username = " + tuple.get(member.username));
        System.out.println("age = " + tuple.get(JPAExpressions.select(memberSub.age.avg())
                                                                .from(memberSub)));
    }
}
```
```
username = member1
age = 25.0
username = member2
age = 25.0
username = member3
age = 25.0
username = member4
age = 25.0
```

6. static import 활용
```java
import static com.querydsl.jpa.JPAExpressions.*;

/**
 * 나이가 가장 많은 회원 조회
 */
@Test
public void subQuery() {
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
            .selectFrom(member)
            .where(member.age.eq(
                    select(memberSub.age.max())
                            .from(memberSub)
            ))
            .fetch();

    Assertions.assertThat(result).extracting("age").containsExactly(40);
}
```

7. FROM 절의 서브쿼리 한계
   - 💡 JPA JPQL 서브 쿼리의 한계점으로 FROM 절의 서브쿼리 (인라인 뷰)는 지원하지 않음
   - 당연히 Querydsl도 지원하지 않음
   - 💡 하이버네이트 구현체를 사용하면 SELECT 절의 서브 쿼리는 지원
   - Querydsl도 하이버네이트 구현체를 사용하면 SELECT 절의 서브 쿼리를 지원

8. FROM 절의 서브 쿼리 해결 방안
   - 서브쿼리를 JOIN으로 변경 (가능한 상황도 있고, 불가능한 상황도 존재)
   - 애플리케이션에서 쿼리를 2번 분리해서 사용
   - nativeSQL 사용
   
