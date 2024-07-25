-----
### 스프링 트랜잭션 추상화
-----
1. 각 데이터 접근 기술들은 트랜잭션을 처리하는 방식에 차이가 존재
2. 예를 들어 JDBC 기술과 JPA 기술은 트랜잭션을 사용하는 코드 자체가 다름
  - JDBC 트랜잭션 코드 예시
```java
public void accountTransfer(String fromId, String toId, int money) throws SQLException {
    Connection con = dataSource.getConnection(); 
    try {
          con.setAutoCommit(false); //트랜잭션 시작
          //비즈니스 로직
          bizLogic(con, fromId, toId, money);
          con.commit(); //성공시 커밋
    } catch (Exception e) {
        con.rollback(); //실패시 롤백
        throw new IllegalStateException(e);
    } finally {
        release(con);
    } 
}
```

  - JPA 트랜잭션 코드 예시
```java
public static void main(String[] args) {
    // Entity Manager Factory 생성
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    EntityManager em = emf.createEntityManager(); // 엔티티 매니저 생성
    EntityTranscaction tx = em.getTransaction(); // 트랜잭션 기능 획득

    try {
      tx.begin(); // 트랜잭션 시작
      logic(em); // 비즈니스 로직
      tx.commit(); // 트랜잭션 커밋
    } catch (Exception e) {
      tx.rollback(); // 트랜잭션 롤백
    } finally {
      em.close(); // 엔티티 매니저 종료
    }
    emf.close(); // 엔티티 매니저 팩토리 종료
}
```

3. 따라서, JDBC 기술을 사용하다가 JPA 기술로 변경하게 되면, 트랜잭션을 사용하는 코드도 모두 변경해야함
4. 스프링은 이런 문제를 해결하기 위해, 트랜잭션 추상화 제공
   - 트랜잭션을 사용하는 입장에서는 스프링 트랜잭션 추상화를 통해 둘을 동일한 방식으로 사용 가능
5. 스프링은 PlatformTransactionManager라는 인터페이스를 통해 트랜잭션 추상화
6. PlatformTransactionManager 인터페이스
```java
package org.springframework.transaction;

public interface PlatformTransactionManager extends TransactionManager {
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    void commit(TransactionStatus status) throws TransactionException; 
    void rollback(TransactionStatus status) throws TransactionException;
}
```
  - 트랜잭션은 트랜잭션 시작 (획득), 커밋, 롤백으로 단순하게 추상화 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/77f73a6b-ec61-46d4-850a-8988362ecb66">
</div>

  - 스프링은 트랜잭션을 추상화해서 제공할 뿐만 아니라, 실무에서 주로 사용하는 데이터 접근 기술에 대한 트랜잭션 매니저 구현체도 제공
  - 이러한 필요한 구현체를 스프링 빈으로 등록하고, 주입 받아서 사용하기만 하면 됨
  - 더불어, 스프링 부트는 어떤 데이터 접근 기술을 사용하는지 자동으로 인식해서 적절한 트랜잭션 매니저를 선택해 스프링 빈으로 등록해주기 때문에, 트랜잭션 매니저를 선택하고 등록하는 과정 생략 가능
  - 예를 들어, JdbcTemplate, MyBatis를 사용하면 DataSourceTransactionManager(JdbcTransactionManager)를 스프링 빈으로 등록
  - JPA를 사용하면 JpaTransactionManager를 스프링 빈으로 등록
  - 참고로, 스프링 5.3부터는 JDBC 트랜잭션을 관리할 때, DataSourceTransactionManager를 상속받아 약간의 기능을 확장한 JdbcTransactionManager를 제공

-----
### 스프링 트랜잭션 사용 방식
-----
1. PlatformTransactionManager를 사용하는 방법은 크게 2가지
2. 선언적 트랜잭션 관리 vs 프로그래밍 방식 트랜잭션 관리
   - 선언적 트랜잭션 관리 (Declarative Transaction Management)
     + @Transactional 애너테이션 하나만 선언해서 매우 편리하게 트랜잭션을 적용하는 것
     + 선언적 트랜잭션 관리는 과거 XML에 설정하기도 했음
     + 이름 그대로 해당 로직에 트랜잭션을 적용하겠다라고 선언하기만 하면, 트랜잭션이 적용되는 방식

   - 프로그래밍 방식 트랜잭션 관리 (Programmatic Transaction Management)
     + 트랜잭션 매니저 또는 트랜잭션 템플릿 등을 사용해 트랜잭션 관련 코드를 직접 작성하는 것

3. 프로그래밍 방식의 트랜잭션 관리를 사용하게 되면, 애플리케이션 코드가 트랜잭션이라는 기술 코드와 강하게 결합
4. 선언적 트랜잭션 관리가 프로그래밍 방식에 비해 훨씬 간편하고 실용적이므로, 실무에서는 대부분 선언적 트랜잭션 관리 사용

-----
### 선언적 트랜잭션과 AOP
-----
1. @Transactional를 통한 선언적 트랜잭션 관리 방식을 사용하게 되면, 기본적으로 프록시 방식의 AOP가 적용
2. 프록시 도입 전
<div align="center">
<img src="https://github.com/user-attachments/assets/ff7f0340-5e55-4e45-960c-b22d886b7c6d">
</div>

  - 트랜잭션을 처리하기 위한 프록시가 도입하기 전에는 서비스 로직에서 트랜잭션 직접 시작
  - 서비스 계층의 트랜잭션 사용 코드 예시
```java
// 트랜잭션 시작
TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

try {
    //비즈니스 로직
    bizLogic(fromId, toId, money);
    transactionManager.commit(status); //성공시 커밋 
} catch (Exception e) {
    transactionManager.rollback(status); //실패시 롤백 
    throw new IllegalStateException(e);
}
```

3. 프록시 도입 후
<div align="center">
<img src="https://github.com/user-attachments/assets/97eadb41-9d40-41d0-940a-3f348096332f">
</div>

  - 트랜잭션을 처리하기 위한 프록시를 적용하려면 트랜잭션을 처리하는 객체와 비즈니스 로직을 처리하는 서비스 객체를 명확하게 분리 가능
  - 트랜잭션 프록시 코드 예시
```java
public class TransactionProxy { 
    private MemberService target; 

    public void logic() {

        //트랜잭션 시작
        TransactionStatus status = transactionManager.getTransaction(..); 
    
        try {
            //실제 대상 호출
            target.logic();
            transactionManager.commit(status); //성공시 커밋 
        } catch (Exception e) {
            transactionManager.rollback(status); //실패시 롤백
            throw new IllegalStateException(e);
        }
    }
}
```
  - 트랜잭션 프록시 적용 후 서비스 코드 예시
```java
public class Service {
    public void logic() {
        //트랜잭션 관련 코드 제거, 순수 비즈니스 로직만 남음 
        bizLogic(fromId, toId, money);
    } 
}
```
  - 프록시 도입 전 : 서비스에 비즈니스 로직과 트랜잭션 처리 로직이 함께 섞여있음
  - 프록시 도입 후 : 트랜잭션 프록시가 트랜잭션 처리 로직을 모두 가져가며, 트랜잭션 시작 후 실제 서비스를 대신 호출
    + 트랜잭션 프록시 덕분에 서비스 계층에는 순수한 비즈니스 로직만 남길 수 있음

4. 프록시 도입 후 전체 과정
<div align="center">
<img src="https://github.com/user-attachments/assets/550d5f7f-e3eb-4492-94ae-ddcbce95366f">
</div>

  - 트랜잭션은 커넥션에 con.setAutoCommit(false)를 지정하면서 시작
  - 같은 트랜잭션을 유지하려면, 같은 데이터베이스 커넥션을 사용해야 함
  - 이를 위해 스프링 내부에서는 트랜잭션 동기화 매니저가 사용
  - JdbcTemplate을 포함한 대부분의 데이터 접근 기술들은 트랜잭션을 유지하기 위해 내부에서 트랜잭션 동기화 매니저를 통해 리소스(커넥션)를 동기화

5. 스프링이 제공하는 트랜잭션 AOP
   - 스프링 트랜잭션은 매우 중요한 기능
   - 스프링 트랜잭션 AOP를 처리하기 위한 모든 기능을 제공함, 스프링 부트를 사용하면 트랜잭션 AOP를 처리하기 위해 필요한 스프링 빈들도 자동으로 등록
   - 따라서, 트랜잭션이 필요한 곳에 @Transactional 애너테이션만 붙여주면 됨
   - 스프링의 트랜잭션 AOP는 이 애너테이션을 인식해 트랜잭션을 처리하는 프록시 적용
   - @Transactional : org.springframework.transaction.annotation.Transactional
