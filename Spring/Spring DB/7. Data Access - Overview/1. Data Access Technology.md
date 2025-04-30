-----
### 데이터 접근 기술 진행 방식
-----
1. 적용 데이터 접근 기술
   - JdbcTemplate
   - MyBatis
   - JPA, Hibernate
   - Spring Data JPA
   - Querydsl

2. SQLMapper
   - JdbcTemplate
   - MyBatis

3. ORM 관련 기술
   - JPA, Hibernate
   - Spring Data JPA
   - Querydsl

-----
### SQLMapper 주요 기능
-----
1. SQL만 작성하면 해당 SQL 결과를 객체로 편리하게 Mapping
2. JDBC를 직접 사용할 때 발생하는 여러 가지 중복을 제거해주며, 기타 여러 가지 편리한 기능 제공

-----
### ORM 주요 기능
-----
1. JdbcTemplate이나 MyBatis 같은 SQL Mapper 기술은 SQL을 직접 작성해야 함
2. JPA를 사용하면 기본적인 SQL은 JPA가 대신 작성하고 처리해줌
3. 즉, 저장하고 싶은 객체를 마치 자바 컬렉션에 저장하고 조회하듯 사용하면, ORM 기술이 데이터베이스에 해당 객체를 저장하고 조회
4. JPA는 자바 진영 ORM 기술, Hibernate는 JPA에서 가장 많이 사용하는 구현체
   - 자바에서 ORM를 사용할 때는 JPA 인터페이스를 사용하고, 그 구현체로 하이버네이트를 사용
5. 스프링 데이터 JPA, Querydsl은 JPA를 더 편리하게 사용할 수 있도록 도와주는 프로젝트
