-----
### 집합 - 집합 함수
-----
```java
/**
 * JPQL
 * select
 *    COUNT(m),   // 회원수
 *    SUM(m.age), // 나이 합
 *    AVG(m.age), // 평균 나이
 *    MAX(m.age), // 최대 나이
 *    MIN(m.age)  // 최소 나이
 * from Member m
 */
@Test
public void aggregation() {
    List<Tuple> result = queryFactory.select(
                    member.count(),
                    member.age.sum(),
                    member.age.avg(),
                    member.age.max(),
                    member.age.min()
                    )
                            .from(member)
                            .fetch();


    Tuple tuple = result.get(0);
    Assertions.assertThat(tuple.get(member.count())).isEqualTo(4);
    Assertions.assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    Assertions.assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    Assertions.assertThat(tuple.get(member.age.max())).isEqualTo(40);
    Assertions.assertThat(tuple.get(member.age.min())).isEqualTo(10);
}
```
  - JPQL이 제공하는 모든 집합 함수 제공
  - tuple은 프로젝션과 결과 반환에서 설명

-----
### 집합 - GroupBy 사용
-----
```java
/**
 * 팀의 이름과 각 팀의 평균 연령
 */
@Test
public void group() throws Exception {
    List<Tuple> result = queryFactory.select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch();

    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);

    Assertions.assertThat(teamA.get(team.name)).isEqualTo("teamA");
    Assertions.assertThat(teamA.get(member.age.avg())).isEqualTo(15);

    Assertions.assertThat(teamB.get(team.name)).isEqualTo("teamB");
    Assertions.assertThat(teamB.get(member.age.avg())).isEqualTo(35);
}
```
1. groupBy : 그룹화된 결과를 제한하려면 having 사용
2. groupBy(), having() 예시
```java
...

  .groupBy(item.price)
  .having(item.price.gt(1000))

...
```
