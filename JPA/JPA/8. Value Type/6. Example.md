-----
### 값 타입 매핑 예제
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/987aef13-6f38-4d2c-b5f0-f9657a93a502">
</div>

1. Address 값 타입
```java
package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Address {
    private String city;
    private String street;
    private String zipcode;

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZipcode() {
        return zipcode;
    }

    private void setCity(String city) {
        this.city = city;
    }

    private void setStreet(String street) {
        this.street = street;
    }

    private void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) && Objects.equals(street, address.street) && Objects.equals(zipcode, address.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode);
    }
}
```

  - Member
```java
package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
```

  - Delivery
```java
package jpabook.jpashop.domain;

import jakarta.persistence.*;

@Entity
public class Delivery {
    @Id @GeneratedValue
    private Long id;

    @Embedded
    private Address address;

    private DeliveryStatus status;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;
}
```
```
Hibernate: 
    create table Member (
        MEMBER_ID bigint not null,
        createdDate timestamp(6),
        lastModifiedDate timestamp(6),
        city varchar(255),
        createdBy varchar(255),
        lastModifiedBy varchar(255),
        name varchar(255),
        street varchar(255),
        zipcode varchar(255),
        primary key (MEMBER_ID)
    )

...

Hibernate: 
    create table Delivery (
        status tinyint check (status between 0 and -1),
        id bigint not null,
        city varchar(255),
        street varchar(255),
        zipcode varchar(255),
        primary key (id)
```
