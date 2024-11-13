-----
### JdbcTemplate의 주요 기능
-----
1. JdbcTemplate : 순서 기반 파라미터 바인딩 지원
2. NamedParameterJdbcTempate : 이름 기반 파라미터 바인딩 지원 (권장)
3. SimpleJdbcInsert : INSERT SQL을 편리하게 사용
4. SimpleJdbcCall : 스토어드 프로시저를 편리하게 호출
   - 스토어드 프로시저를 사용하기 위한 SimpleJdbCall에 대한 자세한 내용 : https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc-simple-jdbc-call-1

-----
### JdbcTemplate 사용법 정리
-----
1. 스프링 공식 메뉴얼에 자세히 소개되어 있음
2. https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc-JdbcTemplate

-----
### 조회
-----
1. 단건 조회 - 숫자 조회
```java
int rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_actor", Integer.class);
```
  - 하나의 ROW를 조회할 떄는 queryForObject() 사용
  - 조회 대상이 객체가 아니라 단순 데이터 하나라면 Integer.class, String.class와 같이 지정

2. 단건 조회 - 숫자 조회, 파라미터 바인딩
```java
int countOfActorsNamedJoe = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM t_actor WHERE first_name = ?", Integer.class, "Joe");
```
  - 숫자 하나와 파라미터 바인딩 예시

3. 단건 조회 - 문자 조회
```java
String lastNmae = jdbcTemplate.queryForObjet("SELECT last_name FROM t_actor WHERE id = ?", String.class, 1212L);
```
  - 문자 하나와 파라미터 바인딩 예시

4. 단건 조회 - 객체 조회
```java
Actor actor = jdbcTemplate.queryForObject(
    "SELECT first_name, last_name FROM t_actor WHERE id = ?",
    (resultSet, rowNum) -> {
      Actor newActor = new Actor();
      newActor.setFirstName(resultSet.getString("first_name");
      newActor.setLastName(resultSet.getString("last_name");
      return newActor;
    }, 1212L);
```
  - 객체 하나를 조회
  - 💡 결과를 객체로 매핑해야하므로 RowMapper를 사용해야 함

5. 목록 조회 - 객체
```java
List<Actor> actors = jdbcTemplate.query(
    "SELECT first_name, last_name FROM t_actor",
    (resultSet, rowNum) -> {
      Actor actor = new Actor();
      newActor.setFirstName(resultSet.getString("first_name");
      newActor.setLastName(resultSet.getString("last_name");
      return actor;
});
```
  - 여러 ROW를 조회할 때는 query() 사용
  - 결과를 리스트로 반환
  - 💡 결과를 객체로 매핑해야하므로 RowMapper 사용
  - RowMapper 분리하여 재사용 가능
```java
private final RowMapper<Actor> actorRowMapper = (resultSet, rowNum) -> {
      Actor actor = new Actor();
      newActor.setFirstName(resultSet.getString("first_name");
      newActor.setLastName(resultSet.getString("last_name");
      return actor;
};

public List<Actor> findAllActors() {
  return this.jdbcTemplate.query("SELECT first_name, last_name FROM t_actor", actorRowMapper);
}
```

-----
### 변경 (INSERT, UPDATE, DELETE)
-----
1. 데이터를 변경할 때는 jdbcTemplate.update() 사용
2. 참고로, int값을 반환하는데, SQL 실행 결과에 영향받은 ROW 수
3. 등록
```java
jdbcTemplate.update("INSERT INTO t_actor (first_name, last_name) VALUES (?, ?)", "Leonor", "Watling");
```

4. 수정
```java
jdbcTemplate.update("UPDATE t_actor SET last_name = ? WHERE id = ?", "Banjo", 5276L);
```

5. 삭제
```java
jdbcTemplate.update("DELETE FROM t_actor WHERE id = ?", Long.valueOf(actorId));
```

-----
### 기타 기능
-----
1. 임의의 SQL을 실행할 떄는 execute() 사용
2. 테이블을 생성하는 DDL에 사용 가능
3. DDL
```java
jdbcTemplate.execute("CREATE TABLE mytable (id INTEGER, name VARCHAR(100))");
```

4. 스토어드 프로시저 호출
```java
jdbcTemplate.update("CALL SUPPORT.REFRESH_ACTORS_SUMMARY(?)", Long.valueOf(unionId));
```

-----
### 정리
-----
1. 가장 간단하고 실용적인 방법으로 SQL을 사용하려면 JdbcTemplate 사용
2. JPA와 ORM 기술을 사용하면서 동시에 SQL을 직접 작성해야 할 때, 함께 사용하면 됨
3. 하지만 최대 단점으로, 동적 쿼리를 해결하지 못함
4. 그리고 SQL을 자바 코드로 작성하기 때문에 SQL 라인이 넘어갈 때 마다 문자 더하기를 해줘야 함
5. 동적 쿼리 문제를 해결하면서 동시에 SQL을 편리하게 작성하도록 도와주는 기술이 MyBatis
   - JOOQ도 동적 쿼리 문제를 해결해주지만, 사용자가 많지 않음
