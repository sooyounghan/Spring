-----
### 데이터베이스 스키마 자동 생성
-----
1. DDL을 애플리케이션 실행 시점에 자동 생성
   - 테이블 중심 → 객체 중심
2. 데이터베이스 방언을 활용해 데이터베이스에 맞는 적절한 DDL 생성
3. 💡 이렇게 생성된 DDL은 개발 장비에서만 사용
4. 💡 생성된 DDL은 운영 서버에서는 사용하지 않거나, 적절히 다듬은 후 사용

-----
### 속성
-----
1. hibernate.hbm2ddl.auto
2. 옵션 : 설명
   - create : 기존 테이블 삭제 후 다시 생성 (DROP + CREATE)
```
<property name="hibernate.hbm2ddl.auto" value="create" />
```
```
Hibernate: 
    drop table if exists Member cascade 
Hibernate: 
    create table Member (
        id bigint not null,
        name varchar(255),
        primary key (id)
    )
```

   - create-drop : CREATE와 같으나 종료 시점에 테이블 DROP
```
<property name="hibernate.hbm2ddl.auto" value="create-drop" />
```
```
Hibernate: 
    select
        m1_0.id,
        m1_0.name 
    from
        Member m1_0 
    where
        m1_0.id=?
Hibernate: 
    drop table if exists Member cascade 
```

   - update : 변경분만 반영 (💡 운영 DB에서는 사용하면 안됨) (삭제는 되지 않으며, 추가만 가능)
```
<property name="hibernate.hbm2ddl.auto" value="update" />
```
```java
@Entity
public class Member {

    @Id
    private Long id;
    private String name;

    ...
}
```
```
Hibernate: 
    create table Member (
        id bigint not null,
        name varchar(255),
        primary key (id)
    )
```
```java
@Entity
public class Member {

    @Id
    private Long id;
    private String name;
    private int age; // 필드 추가
    ...
}
```
```
Hibernate: 
    alter table if exists Member 
       add column age integer not null
```
   - validate : 엔티티와 테이블이 정상 매핑되었는지만 확인
```
<property name="hibernate.hbm2ddl.auto" value="validate" />
```
```java
@Entity
public class Member {

    @Id
    private Long id;
    private String name;
    private int gogo; // 매핑 정보에 없는 컬럼
    ...
}
```
```
Exception in thread "main" jakarta.persistence.PersistenceException: [PersistenceUnit: hello] Unable to build Hibernate SessionFactory
	...
Caused by: org.hibernate.tool.schema.spi.SchemaManagementException: Schema-validation: missing column [gogo] in table [Member]
	...
```

   - none : 사용하지 않음
```
<property name="hibernate.hbm2ddl.auto" value="none" />
```

-----
### 데이터베이스 방언 별로 달라짐 (예) VARCHAR)
-----
1. OracleDialect (Oracle : VARCHAR2)
```
<property name="hibernate.dialect" value="org.hibernate.dialect.OracleDialect"/>
<property name="hibernate.hbm2ddl.auto" value="create" />
```
  - 실행 로그
```
Hibernate: 
    drop table Member cascade constraints
Hibernate: 
    create table Member (
        id number(19,0) not null,
        name varchar2(255 char),
        primary key (id)
    )
```

2. H2Dialect (H2 : VARCHAR)
```
<property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
<property name="hibernate.hbm2ddl.auto" value="create" />
```
  - 실행 로그
```
Hibernate: 
    drop table if exists Member cascade 
Hibernate: 
    create table Member (
        id bigint not null,
        name varchar(255),
        primary key (id)
    )
```

-----
### 💡 주의사항
-----
1. 💡 운영 장비에는 절대 create, create-drop, update를 사용하면 안 됨
2. 개발 초기 단계 : create, update
3. 테스트 서버 : update, validate
4. Staging, 운영 서버 : validate 또는 none

-----
### DDL 생성 기능
-----
1. 제약 조건 추가
  - 예) 회원 이름은 필수, 10자 초과하면 안됨 : @Column(nullable = false, length = 10)

2. 유니크 제약 조건 추가
  - @Table(uniqueConstraints = {@UniqueConstraint(name = "NAME_AGE_UNIQUE", colunmNames = {"NAME", "AGE"})})

3. 💡 DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA 실행 로직에 영향을 주지 않음

4. 예시
```java
@Entity
public class Member {

    ...
    @Column(unique = true, nullable = false, length = 10)
    private String name;
    ...
}
```
```
Hibernate: 
    create table Member (
        id bigint not null,
        name varchar(10) not null unique,
        primary key (id)
    )
```
