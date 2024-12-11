-----
### SQL Function 호출
-----
1. JPA와 같이 Dialect에 등록된 내용만 호출 가능
2. member를 M으로 변경하는 replace 함수 사용
```java
@Test
public void sqlFunction() {
    List<String> result = queryFactory.select(Expressions.stringTemplate(
                    "function('replace', {0}, {1}, {2})",
                    member.username, "member", "M"))
            .from(member)
            .fetch();

    for (String s : result) {
        System.out.println("s = " + s);
    }
}
```
```
/* select
    function('replace', member1.username, ?1, ?2) 
from
    Member member1 */
select
    replace(m1_0.username, ?, ?) 
from
    member m1_0

s = M1
s = M2
s = M3
s = M4
```

3. 소문자로 변경해서 비교
```java
@Test
public void sqlFunction2() {
    List<String> result = queryFactory.select(member.username)
            .from(member)
            .where(member.username.eq(
                    Expressions.stringTemplate(
                            "function('lower', {0})",
                            member.username)))
            .fetch();

    for (String s : result) {
        System.out.println("s = " + s);
    }
}
```
```
/* select
    member1.username 
from
    Member member1 
where
    member1.username = function('lower', member1.username) */

select
    m1_0.username 
from
    member m1_0 
where
    m1_0.username=lower(m1_0.username)

s = member1
s = member2
s = member3
s = member4
```

  - lower 같은 ANSI 표준 함수들은 Querydsl이 상당 부분 내장하고 있음
```java
@Test
public void sqlFunction2() {
    List<String> result = queryFactory.select(member.username)
            .from(member)
            .where(member.username.eq(member.username.lower()))
            .fetch();

    for (String s : result) {
        System.out.println("s = " + s);
    }
}
