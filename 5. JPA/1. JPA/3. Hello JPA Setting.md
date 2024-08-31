-----
### H2 데이터베이스
-----
1. http://www.h2database.com
2. 실습용 DB
3. 가벼움 (1.5M)
4. 웹용 쿼리툴 제공
5. MySQL, Oracle 데이터베이스 시뮬레이션 가능
6. SEQUENCE, AUTO INCREMENT 기능 지원

-----
### Maven
-----
1. https://maven.apache.org/
2. JAVA 라이브러리, 빌드 관리
3. 라이브러리 자동 다운로드 및 의존성 관리
4. 현재는 Gradle 사용 추세

-----
### 참고 - 프로젝트 생성 (강의 참고)
-----
1. 자바 8 이상
2. 메이븐 설정
   - groupId : jpa-basic
   - artifactId : ex1-hello-jpa
   - version : 1.0.0

3. 라이브러리 추가 - pom.xml (ex1-hello-jpa)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>jpa-basic</groupId>
    <artifactId>ex1-hello-jpa</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- JPA 하이버네이트 -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>6.4.2.Final</version>
        </dependency>

        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.1</version>
        </dependency>

        <!-- H2 데이터베이스 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>

        <!-- logback -->
<!--
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.14</version>
        </dependency>
-->
    </dependencies>

</project>

```

4. JPA 설정하기 - persistence.xml (src/resources/META-INF)
  - JPA 설정 파일
  - /META-INF/persistence.xml 위치
  - persistence-unit name으로 이름 지정
  - jakarta.persistence로 시작 : JPA 표준 속성
  - hibernate로 시작 : 하이버네이트 전용 속성
```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2" xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="hello">
        <properties>
            <!-- 필수 속성 -->
            <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="jakarta.persistence.jdbc.user" value="sa"/>
            <property name="jakarta.persistence.jdbc.password" value=""/>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:tcp://localhost/~/test"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

            <!-- 옵션 -->
            <!-- JPA로 작성된 쿼리 확인 -->
            <property name="hibernate.show_sql" value="true"/>

            <!-- JPA로 작성된 쿼리를 형식에 맞추어 출력 -->
            <property name="hibernate.format_sql" value="true"/>

            <!-- JPA로 작성된 쿼리에 대해 /* */를 통해 자세히 출력-->
            <property name="hibernate.use_sql_comments"  value="true"/>

            <!-- 자동으로 DDL 생성 -->
            <!-- <property name="hibernate.hbm2ddl.auto" value="create" /> -->
        </properties>
    </persistence-unit>

</persistence>
```
   - 예시
```
Hibernate: 
    /* insert for
        hellojpa.Member */insert 
    into
        Member (name, id) 
    values
        (?, ?)
```

-----
### 데이터베이스 방언
-----
1. JPA는 특정 데이터베이스에 종속되지 않음
2. 각각의 데이터베이스가 제공하는 SQL 문법과 함수는 조금씩 다름
   - 가변문자 : MySQL은 VARCHAR, Oracle은 VARCHAR2
   - 문자열을 자르는 함수 : SQL 표준은 SUBSTRING(), Oracle은 SUBSTR()
   - 페이징 : MySQL은 LIMIT, Oracle은 ROWNUM
3. 방언 : SQL 표준을 지키지 않는 특정 데이터베이스만의 고유한 기능

<div align="center">
<img src="https://github.com/user-attachments/assets/d849d0b5-36ee-4ec3-897f-c702ac47193b">
</div>

4. hibernate.dialect 속성에 지정
   - H2 : org.hibernate.dialect.H2Dialect
   - Oracle 10g : org.hibernate.dialect.Oracle10gDialect
   - MySQL : org.hibernate.dialect.MySQL5(InnoDB)Dialect (버전 5)
   - 하이버네이트는 40가지 이상 데이터베이스 방언 지원
