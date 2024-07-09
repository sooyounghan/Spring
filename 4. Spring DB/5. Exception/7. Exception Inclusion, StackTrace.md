-----
### 예외 포함과 스택 트레이스
-----
1. 💡 예외를 전환할 때는 꼭, 기존 예외를 포함해야 함. 그렇지 않으면, StackTrace를 확인할 때 심각한 문제 발생
```java
@Test
void printEx() {
    Controller controller = new Controller();
    try {
        controller.request();
    } catch(Exception e) {
        // e.printStackTrace();
        log.info("ex", e);
    }
}
```

2. 💡 로그를 출력할 때 마지막 파라미터에 예외를 넣어주면, 로그에 스택 트레이스 출력 가능
   - 예) log.info("message={}", "message", ex) : 마지막에 ex를 전달하는 것 확인 가능 (즉, 스택 트레이스에 로그 출력 가능)
   - 예) log.info("ex", ex) : 지금 예시에는 파라미터가 없으므로 예외만 파라미터에 전달하면 스택 트레이스를 로그에 출력 가능

3. System.out에 스택 트레이스를 출력하려면 e.printStackTrace() 사용하면 됨
4. 실무에서는 항상 로그를 사용함

-----
### 기존 예외를 포함하는 경우와 포함하지 않는 경우
-----
```java
public void call() {
    try {
        runSQL();
    } catch (SQLException e) {
        throw new RuntimeSQLException(e);
    }
}
```
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/795534a9-c15c-4fbc-8426-f9a0eec8a09a">
</div>

1. 기존 예외를 포함하는 경우
   - 예외를 포함해서 기존에 발생한 java.sql.SQLException과 스택트레이스를 확인 가능

```java
public void call() {
    try {
        runSQL();
    } catch (SQLException e) {
        throw new RuntimeSQLException(); // 기존 예외 (e) 제외
    }
}
```
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/fb235f5e-1be0-444b-bfc2-27fb83b8abce">
</div>

2. 기존 예외를 포함하지 않는 경우
   - 예외를 포함하지 않아서 기존에 발생한 java.sql.SQLException과 스택 트레이스를 확인 불가
   - 변환한 RuntimeSQLException부터 예외를 확인할 수 있음
   - 즉, 만약 실제 DB를 연동했다면, DB에서 발생한 예외를 확인할 수 없는 심각한 문제 발생

3. 💡 즉, 예외를 전환할 때는 꼭 기존의 예외를 포함해야 함
