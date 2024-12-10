-----
### 결과 조회
-----
1. fetch() : 리스트 조회, 데이터가 없으면 빈 리스트 반환
2. fetchOne() : 단 건 조회
   - 결과가 없으면 null
   - 결과가 둘 이상 : com.querydsl.core.NonUniqueResultException
3. fetchFirst() : limit(1), fetchOne()
4. fetchResults() : 페이징 정보 포함, totalCount 쿼리 추가 실행
5. fetchCount() : count 쿼리로 변경해서 count 수 조회
```java
@Test
public void resultFetch() {
    // List
    List<Member> fetch = queryFactory.selectFrom(member)
                                        .fetch();

    // 단 건
    Member fetchOne = queryFactory.selectFrom(member)
                                    .fetchOne();

    // 처음 한 건 조회
    Member fetchFirst = queryFactory.selectFrom(member)
                                    .fetchFirst();

    // 페이징에서 사용
    QueryResults<Member> results = queryFactory.selectFrom(member)
                                                .fetchResults();

    List<Member> content = results.getResults();
    long total = results.getTotal();


    // COUNT 쿼리로 변경
    long count = queryFactory.selectFrom(member)
                             .fetchCount();
}
```
