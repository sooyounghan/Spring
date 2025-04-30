-----
### 상수, 문자 더하기
-----
1. 상수가 필요하면 Expressions.constant(xxx) 사용
```java
@Test
public void constant() {
    List<Tuple> result = queryFactory.select(member.username, Expressions.constant("A"))
            .from(member)
            .fetch();

    for (Tuple tuple : result) {
        System.out.println("tuple = " + tuple);
    }
}
```
  - 참고 : 위와 같이 최적화가 가능하면 SQL에 constant 값을 넘기지 않아야 함
  - 상수를 더하는 것 처럼 최적화가 어려우면 SQL에 Constant 값을 넘김

```
tuple = [member1, A]
tuple = [member2, A]
tuple = [member3, A]
tuple = [member4, A]
```

2. 문자 더하기 CONCAT
```java
@Test
public void concat() {
    // {username}_{age}
    List<String> result = queryFactory.select(member.username.concat("_").concat(member.age.stringValue()))
            .from(member)
            .where(member.username.eq("member1"))
            .fetch();

    for (String s : result) {
        System.out.println("s = " + s);
    }
}
```
```
s = member1_10
```
  - member.age.stringValue() : 문자가 아닌 다른 타입들은 stringValue()로 문자로 변환 가능
  - 이 방법은 ENUM 처리할 때도 자주 사용
