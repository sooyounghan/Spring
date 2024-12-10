-----
### 검색 조권 쿼리
-----
1. 기본 검색 쿼리
```java
@Test
public void search() {
    Member findMember = queryFactory.selectFrom(member)
            .where(member.username.eq("member1")
                    .and(member.age.eq(10)))
            .fetchOne();

    Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
}
```

  - 검색 조건은 and(), or() 메서드 체인으로 연결 가능
  - 참고 : select, from을 selectFrom으로 합치기 가능

2. JPQL이 제공하는 모든 검색 조건 제공
  - eq(), ne(), not()
```java
member.username.eq("member1") // username = 'member1'
member.username.ne("member1") // username != 'member1'
member.username.eq("member1").not() // username != 'member1'
```

  - isNotNull()
```java
member.username.isNotNull() // 이름이 is not null
```

  - in(), notIn()
```java
member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
```

  - between()
```java
member.age.between(10,30) // between 10, 30
```

  - goe(), gt(), loe(), lt()
```java
member.age.goe(30) // age >= 30
member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30
```

  - like()
```java
member.username.like("member%") // like 검색
member.username.contains("member") // like ‘%member%’ 검색
member.username.startsWith("member") //like ‘member%’ 검색
...`
```

3. AND 조건으로 파라미터 처리
```java
@Test
public void searchAndParam() {
    Member findMember = queryFactory.selectFrom(member)
            .where(member.username.eq("member1"), 
                    (member.age.eq(10)))
            .fetchOne();

    Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
}
```
  - 💡 where()에 파라미터로 검색 조건을 추가하면 AND 조건 추가
  - 💡 이 경우 null 값은 무시 : 메서드 추출을 활용해 동적 쿼리를 깔끔하게 만들 수 있음
