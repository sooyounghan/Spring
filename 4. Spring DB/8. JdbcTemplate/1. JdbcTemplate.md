-----
### JdbcTemplate 소개와 설정
-----
1. SQL를 직접 사용하는 경우 스프링이 제공하는 JdbcTemplate을 사용하는 것은 좋음
2. JDBC를 매우 편리하게 사용하도록 도와줌

-----
### JdbcTemplate의 장점
-----
1. 설정의 편리함
   - JdbcTemplate은 spring-jdbc 라이브러리에 포함되어 있는데, 이 라이브러리는 JDBC를 사용할 때 기본으로 사용되는 라이브러리
   - 그리고 복잡한 설정 없이 바로 사용

2. 반복 문제 해결
   - JdbcTemplate은 템플릿 콜백 패턴을 사용해, JDBC를 직접 사용할 때 발생하는 대부분 반복 작업 대신 처리
   - 개발자는 SQL을 작성하고, 전달할 파라미터를 정의하고, 응답 값을 매핑하기만 하면 됨
   - 대부분의 반복 작업 대신 처리
     + 커넥션 획득
     + statement 준비, 실행
     + 결과 반복하도록 루프 실행
     + 커넥션, statement, resultset 종료
     + 트랜잭션을 다루기 위한 커넥션 동기화
     + 예외 발생 시 스프링 예외 변환기 실행

-----
### JdbcTemplate의 단점
-----
: 동적 SQL을 해결하기 어려움

-----
### JdbcTemplate 설정
-----
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf' 
    implementation 'org.springframework.boot:spring-boot-starter-web'

    //JdbcTemplate 추가
    implementation 'org.springframework.boot:spring-boot-starter-jdbc'

    //H2 데이터베이스 추가
    runtimeOnly 'com.h2database:h2'

    compileOnly 'org.projectlombok:lombok' 
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    //테스트에서 lombok 사용
    testCompileOnly 'org.projectlombok:lombok' 
    testAnnotationProcessor 'org.projectlombok:lombok'
}
```
1. 'org.springframework.boot:spring-boot-starter-jdbc'를 추가하면 JdbcTemplate이 들어있는 spring-jdbc가 라이브러리에 포함
2. H2 데이터베이스에 접속해야 하기 때문에, H2 데이터베이스 클라이언트 라이브러리 (Jdbc Driver)도 추가 : runtimeOnly 'com.h2database:h2'

