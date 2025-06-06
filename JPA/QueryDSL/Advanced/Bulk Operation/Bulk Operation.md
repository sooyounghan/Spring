-----
### 수정, 삭제 벌크 연산
-----
1. 쿼리 한 번으로 대량 데이터 수정
```java
@Test
public void bulkUpdate() {
    // count : 수정된 행의 수 = 2
    // member1 = 10 -> 비회원 (DB) / 영속성 컨텍스트 = 10
    // member2 = 20 -> 비회원 (DB) / 영속성 컨텍스트 = 20
    // member3 = 30 -> 유지 (DB) / 영속성 컨텍스트 = 30
    // member4 = 40 -> 유지 (DB) / 영속성 컨텍스트 = 40
    long count = queryFactory.update(member)
            .set(member.username, "비회원")
            .where(member.age.lt(28))
            .execute();

    System.out.println("count = " + count);
}
```
```
count = 2
```

2. 기존 숫자에 1 더하기
```java
@Test
public void bulkAdd() {
    long count = queryFactory.update(member)
            .set(member.age, member.age.add(1)) // 빼는 경우 add(-1)
            .execute();

    System.out.println("count = " + count);
}
```
```
/* update
    Member member1 
set
    member1.age = member1.age + ?1 */

update
    member m1_0 
set
    age=(m1_0.age+cast(? as integer))
```
  - 곱하기 : muiltiply(x)

```java
@Test
public void bulkMultiply() {
    long count = queryFactory.update(member)
            .set(member.age, member.age.multiply(2)) // 나누는 경우 소수점
            .execute();

    System.out.println("count = " + count);
}
```

3. 쿼리 한 번으로 대량 데이터 삭제
```java
@Test
public void bulkDelete() {
    long count = queryFactory.delete(member)
            .where(member.age.gt(18))
            .execute();

    System.out.println("count = " + count);
}
```
```
count = 3
```

4. 💡 주의 : JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행 (DB에 바로 쿼리 반영)
   - 따라서, 배치 쿼리를 실행하고 나면, 영속성 컨텍스트를 초기화하는 것이 안전
```java
@Test
public void bulkUpdate() {
    // count : 수정된 행의 수 = 2
    // member1 = 10 -> 비회원 (DB) / 영속성 컨텍스트 = 10
    // member2 = 20 -> 비회원 (DB) / 영속성 컨텍스트 = 20
    // member3 = 30 -> 유지 (DB) / 영속성 컨텍스트 = 30
    // member4 = 40 -> 유지 (DB) / 영속성 컨텍스트 = 40
    long count = queryFactory.update(member)
            .set(member.username, "비회원")
            .where(member.age.lt(28))
            .execute();

    System.out.println("count = " + count);

    em.flush(); 
    em.clear(); // 영속성 컨텍스트 초기화

    List<Member> result = queryFactory.selectFrom(member)
                                      .fetch(); // DB값을 영속성 컨텍스트에 반영

    // member1 = 10 -> 비회원 (DB) / 영속성 컨텍스트 = 비회원
    // member2 = 20 -> 비회원 (DB) / 영속성 컨텍스트 = 비회원
    // member3 = 30 -> 유지 (DB) / 영속성 컨텍스트 = 30
    // member4 = 40 -> 유지 (DB) / 영속성 컨텍스트 = 40
    for(Member member1 : result) {
        System.out.println("member1 = " + member1);
    }
}
```
