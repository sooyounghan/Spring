-----
### Named 쿼리 - 정적 쿼리
-----
1. 미리 정의해서 이름을 부여해두고 사용하는 JPQL
2. 정적 쿼리
3. 애너테이션, XML에 정의
4. 💡 애플리케이션 로딩 시점에 초기화 후 재사용
5. 💡 애플리케이션 로딩 시점에 쿼리 검증
  - 로딩 시점에 JPA가 쿼리를 Parsing하여 검증 처리를 하는데, 실패하면 QuerySyntaxException 발생

-----
### Named 쿼리 - 애너테이션
-----
```java
@Entity
@NamedQuery(
  name = "Member.findByUsername",
  query = "SELECT m FROM Member m WHERE m.username = :username")
public class Member {
    ...
}
```

```java
List<Member> members = em.createNamedQuery("Member.findByUsername", Member.class)
        .setParameter("username", "회원1")
        .getResultList();

for (Member member : members) {
    System.out.println("member = " + member);
}
```
```
Hibernate: 
    /* SELECT
        m 
    FROM
        Member m 
    WHERE
        m.username = :username */ select
            m1_0.id,
            m1_0.age,
            m1_0.TEAM_ID,
            m1_0.type,
            m1_0.username 
        from
            Member m1_0 
        where
            m1_0.username=?

member = Member{id=1, username='회원1', age=10, type=null}
```

-----
### Named 쿼리 - XML에 정의
-----
1. META-INF/persistence.xml
```xml
<persistence-unit name="hello">
    <mapping-file>META-INF/ormMember.xml</mapping-file>

    ...
```

2. META-INF/ormMember.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm" version="2.1">

    <named-query name="Member.findByUsername">
        <query><![CDATA[
            SELECT m
            FROM Member m
            WHERE m.username = :username
        ]]></query>
    </named-query>

    <named-query name="Member.count">
        <query>SELECT COUNT(m) FROM Member m</query>
    <named-query>
</entity-mappings>
```

-----
### Named 쿼리 환경에 따른 설정
-----
1. XML이 항상 우선권을 가짐
2. 애플리케이션 운영 환경에 따라 다른 XML을 배포할 수 있음
