-----
### 동적 쿼리
-----
: 동적 쿼리를 해결하는 두 가지 방식
   - BooleanBuilder
   - WHERE 다중 파라미터 사용

-----
### 동적 쿼리 - BooleanBuilder 사용
-----
```java
@Test
public void dynamicQuery_BooleanBuilder() {
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember1(usernameParam, ageParam);
    Assertions.assertThat(result.size()).isEqualTo(1);
}

private List<Member> searchMember1(String usernameCond, Integer ageCond) {
    BooleanBuilder booleanBuilder = new BooleanBuilder(); // 초기값 부여 가능
    
    if(usernameCond != null) {
        booleanBuilder.and(member.username.eq(usernameCond));
    }
    
    if(ageCond != null) {
        booleanBuilder.and(member.age.eq(ageCond));
    }
    
    return queryFactory.selectFrom(member)
                            .where(booleanBuilder) // builder도 and, or로 조합 가능
                            .fetch();
}
```

-----
### 동적 쿼리 - Where 다중 파라미터 사용
-----
```java
@Test
public void dynamicQuery_WhereParam() {
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember2(usernameParam, ageParam);
    Assertions.assertThat(result.size()).isEqualTo(1);
}

private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory.selectFrom(member)
            .where(usernameEq(usernameCond), ageEq(ageCond))
            .fetch();
}

private BooleanExpression usernameEq(String usernameCond) {
    if(usernameCond == null) {
        return null;
    }
    return member.username.eq(usernameCond);
}

private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
}
```
1. 💡 WHERE 조건에 null 값은 무시
2. 메서드를 다른 쿼리에서도 재활용 가능
3. 쿼리 자체의 가독성이 높아짐
4. 조합 가능
```java
private BooleanExpression allEq(String usernameCond, Integer ageCond) {
    return usernameEq(usernameCond).and(ageEq(ageCond));
}
```
  - null 체크는 주의해서 처리해야 함

