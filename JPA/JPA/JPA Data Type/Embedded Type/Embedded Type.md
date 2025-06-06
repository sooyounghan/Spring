-----
### 임베디드 타입
-----
1. 새로운 값 타입을 직접 정의할 수 있는데, JPA는 임베디드 타입(Embedded Type)이라 함
2. 💡 주로 기본 값 타입을 모아 만들어서 복합 값 타입이라고 함
  - 임베디드 타입 역시 int, String과 같은 값 타입
3. 예시
  - 회원 엔티티는 이름, 근무 시작일, 근무 종료일, 주소 도시, 주소 번지, 주소 우편 번호를 가짐
    + 근무 시작일 / 근무 종료일과 주소 도시 / 주소 번지 / 주소 우편 번호를 묶을 수 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/1712d20f-8dec-4c41-93fa-58e1d55b18b0">
</div>

  - 회원 엔티티는 이름, 근무 기간, 집 주소를 가짐 (추상화 가능) → 임베디드 타입
<div align="center">
<img src="https://github.com/user-attachments/assets/89802091-e456-4e93-a45e-f663df79bd04">
</div>

<div align="center">
<img src="https://github.com/user-attachments/assets/89001ee9-fc76-4040-8942-58fa439e841c">
</div>

4. 💡 사용법
   - @Embeddedable : 값 타입을 정의하는 곳에 표시
   - @Embedded : 값 타입을 사용하는 곳에 표시
   - 기본 생성자 필수

5. 장점
   - 재사용
   - 높은 응집도
   - Period.isWork()처럼 해당 값 타입만 사용하는 의미 있는 메소드 제작 가능
   - 💡 임베디드 타입을 포함한 모든 값 타입은, 값 타입을 소유한 엔티티에 생명주기 의존

6. 임베디드 타입과 테이블 매핑
<div align="center">
<img src="https://github.com/user-attachments/assets/0f55fa2d-03a6-4736-b58a-1e7cad7c18aa">
</div>

  - Member
```java
package hellojpa;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // Period
    @Embedded
    private Period workPeriod;

    // Address
    @Embedded
    private Address homeAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
```

  - Period
```java
package hellojpa;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class Period {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Period() { // 기본 생성자 필수
    }

    public Period(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
```

  - Address
```java
package hellojpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;

    public Address() { // 기본 생성자 필수
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
```

```java
package hellojpa;

import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction(); // 트랜잭션
        tx.begin(); // 트랜잭션 시작

        try {
            Member member = new Member();
            member.setUsername("hello");
            member.setHomeAddress(new Address("city", "street", "zipcode"));
            member.setWorkPeriod(new Period(LocalDateTime.now(), LocalDateTime.now()));

            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}
```
```
Hibernate: 
    create table Member (
        MEMBER_ID bigint not null,
        endDate timestamp(6),
        startDate timestamp(6),
        USERNAME varchar(255),
        city varchar(255),
        street varchar(255),
        zipcode varchar(255),
        primary key (MEMBER_ID)
    )

...

Hibernate: 
    /* insert for
        hellojpa.Member */insert 
    into
        Member (city, street, zipcode, USERNAME, endDate, startDate, MEMBER_ID) 
    values
        (?, ?, ?, ?, ?, ?, ?)
```
  - 💡 임베디드 타입은 엔티티의 값일 뿐임
  - 💡 임베디드 타입을 사용하기 전과 후에 매핑하는 테이블은 같음
  - 객체와 테이블을 아주 세밀하게 (find-grained) 매핑하는 것이 가능
  - 잘 설계한 ORM 애플리케이션은 매핑한 테이블 수보다 클래스의 수가 더 많음

7. 임베디드 타입과 연관관계
<div align="center">
<img src="https://github.com/user-attachments/assets/33c58c32-eced-4bb9-82c1-139c8a99e830">
</div>

  - 임베디드 타입은 입베디드 타입을 가질 수 있음 (Address - Zipcode)
  - 임베디드 타입은 엔티티 타입을 가질 수 있음 (PhoneNumber - PhoneEntity)
```java
public class Address {
    private String city;
    private String street;
    private String zipcode;

    private PhoneEntity phoneEntity; // PhoneEnity : Entity

    ...
}
```

8. @AttritubeOverride : 속성 재정의
   - 한 엔티티에서 같은 값 타입을 사용하면?
```java
package hellojpa;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // Period
    @Embedded
    private Period workPeriod;

    // Address
    @Embedded
    private Address homeAddress;

    // Address
    @Embedded
    private Address workAddress; // 중복
}
```
```
Exception in thread "main" jakarta.persistence.PersistenceException: [PersistenceUnit: hello] Unable to build Hibernate SessionFactory
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.persistenceException(EntityManagerFactoryBuilderImpl.java:1591)
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1512)
	at org.hibernate.jpa.HibernatePersistenceProvider.createEntityManagerFactory(HibernatePersistenceProvider.java:55)
	at jakarta.persistence.Persistence.createEntityManagerFactory(Persistence.java:80)
	at jakarta.persistence.Persistence.createEntityManagerFactory(Persistence.java:55)
	at hellojpa.JpaMain.main(JpaMain.java:11)
Caused by: org.hibernate.MappingException: Column 'city' is duplicated in mapping for entity 'hellojpa.Member' (use '@Column(insertable=false, updatable=false)' when mapping multiple properties to the same column)
	at org.hibernate.mapping.Value.checkColumnDuplication(Value.java:197)
	at org.hibernate.mapping.MappingHelper.checkPropertyColumnDuplication(MappingHelper.java:249)
	at org.hibernate.mapping.Component.checkColumnDuplication(Component.java:278)
	at org.hibernate.mapping.MappingHelper.checkPropertyColumnDuplication(MappingHelper.java:249)
	at org.hibernate.mapping.PersistentClass.checkColumnDuplication(PersistentClass.java:935)
	at org.hibernate.mapping.PersistentClass.validate(PersistentClass.java:678)
	at org.hibernate.mapping.RootClass.validate(RootClass.java:273)
	at org.hibernate.boot.internal.MetadataImpl.validate(MetadataImpl.java:497)
	at org.hibernate.internal.SessionFactoryImpl.<init>(SessionFactoryImpl.java:273)
	at org.hibernate.boot.internal.SessionFactoryBuilderImpl.build(SessionFactoryBuilderImpl.java:450)
	at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1507)
	... 4 more
```
   - 컬럼명이 중복됨
   - @AttributeOverride (하나), @AttributeOverrides (다수)를 사용해서 컬럼명 속성을 재정의
```java
@Embedded
@AttributeOverrides({
        @AttributeOverride(name="startDate", column=@Column(name = "EMP_START")),
	@AttributeOverride(name="endDate", column=@Column(name = "EMP_END"))
})
```
```java
package hellojpa;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    // Period
    @Embedded
    private Period workPeriod;

    // Address
    @Embedded
    private Address homeAddress;

    // Address
    @Embedded
    @AttributeOverrides(value = {@AttributeOverride(name = "city", column = @Column(name = "WORK_CITY")),
                                @AttributeOverride(name = "street", column = @Column(name = "WORK_STREET")),
                                @AttributeOverride(name = "zipcode", column = @Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;

    ...
}
```
```
Hibernate: 
    create table Member (
        MEMBER_ID bigint not null,
        endDate timestamp(6),
        startDate timestamp(6),
        USERNAME varchar(255),
        WORK_CITY varchar(255),
        WORK_STREET varchar(255),
        WORK_ZIPCODE varchar(255),
        city varchar(255),
        street varchar(255),
        zipcode varchar(255),
        primary key (MEMBER_ID)
    )
```

9. 임베디드 타입의 값이 null이면, 매핑한 컬럼 값은 모두 null
```java
// Period
@Embedded
private Period workPeriod = null;
```
  - 관련된 매핑 컬럼 값 모두 null 값 입력
