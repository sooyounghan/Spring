-----
### 정렬
-----
```java
/**
 * 회원 정렬 순서
 * 1. 회원 나이 : 내림차순 (DESC)
 * 2. 회원 이름 : 오름차순 (ASC)
 * 단, 2번에서 회원 이름이 없으면 마지막에 출력 (Nulls Last)
 */
@Test
public void sort() {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    List<Member> result = queryFactory.selectFrom(member)
            .where(member.age.eq(100))
            .orderBy(member.age.desc(), member.username.asc().nullsLast())
            .fetch();

    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);

    Assertions.assertThat(member5.getUsername()).isEqualTo("member5");
    Assertions.assertThat(member6.getUsername()).isEqualTo("member6");
    Assertions.assertThat(memberNull.getUsername()).isNull();
}
```
  - desc(), asc() : 일반 정렬
  - 💡 nullsLast(), nullsFirst() : null 데이터 순서 부여
