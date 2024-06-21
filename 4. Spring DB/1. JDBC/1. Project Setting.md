-----
### 프로젝트 생성 환경
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/9365f179-7d79-4eff-b6b2-64fec19474a8">
</div>

1. build.gradle
   - 테스트에서도 Lombok을 사용하기 위해 코드 추가
```java
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.3.1'
	id 'io.spring.dependency-management' version '1.1.5'
}

group = 'hello'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-jdbc'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

	// 💡 Test에서 Lombok 사용
	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'
}

tasks.named('test') {
	useJUnitPlatform()
}
```

  - 이 설정을 추가해줘야 테스트 코드에서 @Slfj4 같은 롬복 애너테이션 사용 가능

2. 동작 확인
   - 기본 메인 클래스 실행 (JdbcApplication.main())
   - 콘솔에 'Started JdbcApplication' 로그가 보이면 성공
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/310115e1-8845-4b60-bb61-600b7d84b0d2">
</div>

-----
### H2 데이터베이스 설정
-----
1. 입문편에서 작성해놨던 H2 데이터베이스 설정 참조
2. 테이블 생성
   - 테이블 관리를 위해 프로젝트 루트에 sql/schema.sql 파일 생성
```sql
drop table member if exists cascade;
create table member (
	member_id varchar(10),
	money integer not null default 0,

	primary key(member_id)
);

INSERT INTO member(member_id, money) VALUES('hi1', 10000);
INSERT INTO member(member_id, money) VALUES('hi2', 20000);
```

3. member 테이블에 저장된 데이터 확인
```sql
SELECT * FROM member;
```
