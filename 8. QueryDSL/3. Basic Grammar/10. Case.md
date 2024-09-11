-----
### CASE 문
-----
1. SELECT, 조건절(WHERE), ORDER BY에서 사용 가능
2. 단순한 조건
```java
@Test
public void basicCase() {
    List<String> result = queryFactory.select(member.age
                    .when(10).then("열살")
                    .when(20).then("스무살")
                    .otherwise("기타"))
                    .from(member)
                    .fetch();

    for (String s : result) {
        System.out.println("s = " + s);
    }
}
```

3. 복잡한 조건
```java
@Test
public void complexCase() {
    List<String> result = queryFactory.select(new CaseBuilder()
                    .when(member.age.between(0, 20)).then("0 ~ 20살")
                    .when(member.age.between(21, 30)).then("21 ~ 30살")
                    .otherwise("기타"))
            .from(member)
            .fetch();

    for (String s : result) {
        System.out.println("s = " + s);
    }
}
```
```
s = 0 ~ 20살
s = 0 ~ 20살
s = 21 ~ 30살
s = 기타
```

4. orderBy에서 Case 문 함께 사용하기 예제
   - 예) 다음과 같이 임의의 순서로 회원을 출력하고 싶다면?
     + 0 ~ 30살이 아닌 회원을 가장 먼저 출력
     + 0 ~ 20살의 회원 출력
     + 21 ~ 30살의 회원 출력

```java
@Test
public void orderByCase() {
    NumberExpression<Integer> rankPath = new CaseBuilder()
            .when(member.age.between(0, 20)).then(2)
            .when(member.age.between(21, 30)).then(1)
            .otherwise(3);

    List<Tuple> result = queryFactory.select(member.username, member.age, rankPath)
            .from(member)
            .orderBy(rankPath.desc())
            .fetch();

    for (Tuple tuple : result) {
        String username = tuple.get(member.username);
        Integer age = tuple.get(member.age);
        Integer rank = tuple.get(rankPath);

        System.out.println("username = " + username + ", age = " + age + ", rank = " + rank);
    }
}
```
```
username = member4, age = 40, rank = 3
username = member1, age = 10, rank = 2
username = member2, age = 20, rank = 2
username = member3, age = 30, rank = 1
```

  - Querydsl은 자바 코드로 작성하기 때문에 rankPath처럼 복잡한 조건을 변수로 선언해서 SELECT절, ORDER BY절에서 함께 사용 가능
