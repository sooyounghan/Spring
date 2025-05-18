-----
### 페이징
-----
1. 조회 건수 제한
```java
@Test
public void paging1() {
    List<Member> result = queryFactory.selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1) // 0부터 시작 (Zero Index)
            .limit(2) // 최대 2건 조회
            .fetch();

    Assertions.assertThat(result.size()).isEqualTo(2);
}
```

2. 전체 조회 수가 필요하면?
```java
@Test
public void paging2() {
    QueryResults<Member> queryResults = queryFactory.selectFrom(member)
            .orderBy(member.username.desc())
            .offset(1) // 0부터 시작 (Zero Index)
            .limit(2) // 최대 2건 조회
            .fetchResults();

    Assertions.assertThat(queryResults.getTotal()).isEqualTo(4); // 전체 조회 개수
    Assertions.assertThat(queryResults.getLimit()).isEqualTo(2); // Limit 개수
    Assertions.assertThat(queryResults.getOffset()).isEqualTo(1); // Offset 위치
    Assertions.assertThat(queryResults.getResults()).isEqualTo(2); // 결과 개수
}
```
  - COUNT 쿼리가 실행되므로 주의
  - 실무에서 페이징 쿼리를 작성할 때, 데이터를 조회하는 쿼리는 여러 테이블을 조인해야 하지만, COUNT 쿼리는 조인이 필요 없는 경우도 존재
     + 그런데, 이렇게 자동화된 COUNT 쿼리는 원본 쿼리와 같이 모두 조인을 해버리므로 성능이 안 나올 수 있음
     + COUNT 쿼리에 조인이 필요없는 성능 최적화가 필요하다면, COUNT 전용 쿼리를 별도로 작성해야 함
  
