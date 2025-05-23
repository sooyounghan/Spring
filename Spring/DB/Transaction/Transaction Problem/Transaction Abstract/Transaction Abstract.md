-----
### 트랜잭션 추상화
-----
1. 현재 서비스 계층은 트랜잭션 사용을 위해 JDBC 기술에 의존
2. 향후 JDBC에서 JPA 같은 다른 데이터 접근 기술로 변경하려면, 서비스 계층의 트랜잭션 관련 코드도 모두 함께 수정해야함
3. 구현 기술에 따른 트랜잭션 사용법
   - 트랜잭션은 원자적 단위의 비즈니스 로직 처리하기 위해 사용
   - 구현 기술마다 사용 방법 상이
     + JDBC : conn.setAutoCommit(false)
     + JPA : transaction.begin()

4. JDBC 트랜잭션 예시
```java
public void accountTransfer(String fromId, String toId, int money) throws SQLException {
     Connection conn = dataSource.getConnection();

     try {
         // 트랜잭션 시작
         conn.setAutoCommit(false);

         // 비즈니스 로직
         // Connection Parameter도 포함해서 전송
         bizLogic(conn, fromId, toId, money);

         // 트랜잭션 종료 (커밋)
         conn.commit();
     } catch(Exception e) {
         // 예외 발생 (실패) 하면, Rollback
         conn.rollback();
         throw new IllegalStateException(e);
     } finally {
         release(conn);
     }
}
```

5. JPA 트랜잭션 코드
```java
public static void main(String[] args) {

    // 엔티티 매니저 팩토리 생성 
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");

    EntityManager em = emf.createEntityManager(); // 엔티티 매니저 생성 
    EntityTransaction tx = em.getTransaction(); // 트랜잭션 기능 획득

    try {
        tx.begin(); //트랜잭션 시작
        logic(em);  //비즈니스 로직
        tx.commit();//트랜잭션 커밋 
    } catch (Exception e) {
        tx.rollback(); //트랜잭션 롤백 
    } finally {
        em.close(); //엔티티 매니저 종료 
    }
    emf.close(); //엔티티 매니저 팩토리 종료 

}
```

-----
### JDBC 트랜잭션 의존
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/f3fcc379-ed09-4e9b-95bf-d9db348430a4">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/946a0f3b-4d76-44ed-9188-55f6a3c89055">
</div>

: 이처럼 JDBC 기술을 사용하다가 JPA 기술로 변경하게 되면 서비스 계층의 코드도 JPA 기술을 사용하도록 함께 수정해야함

-----
### 트랜잭션 추상화
-----
1. 따라서, 이 문제를 해결하려면 트랜잭션 기능을 추상화하면 됨
2. 즉, 다음과 같은 인터페이스를 만들어서 사용
```java
// 트랜잭션 추상화 인터페이스
public interface TxManager {
    begin();
    commit();
    rollback();
}
```
  - 트랜잭션은 단순하게, 트랜잭션 시작 / 비즈니스 로직 수행이 끝나면 커밋 또는 롤백 기능만 존재하면 됨

3. 다음과 같이 TxManager 인터페이스를 기반으로 각 기술에 맞는 구현체를 만들면 됨
   - JdbcTxManager : JDBC 트랜잭션 기능을 제공하는 구현체
   - JpaTxManager : JPA 트랜잭션 기능을 제공하는 구현체

-----
### 트랜잭션 추상화와 의존관계
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/b1990e2c-db85-4bcb-aba3-bb9382a8d805">
</div>

1. 서비스는 특정 트랜잭션 기술에 의존하는 것이 아니라, TxManager라는 추상화 된 인터페이스에 의존
2. 따라서, 원하는 구현체를 DI를 통해서 주입하면 됨
3. 예를 들어, JDBC 트랜잭션 기능이 필요하면, JdbcTxManager를 서비스에 주입하고, JPA 트랜잭션 기능으로 변경해야 하면 JpaTxManager를 주입하면 됨
4. 클라이언트인 서비스는 인터페이스에만 의존하고 DI를 사용한 덕분에, OCP 원칙을 지키게 됨
5. 따라서, 트랜잭션을 사용하는 서비스 코드는 전혀 변경하지 않고, 트랜잭션 기술을 마음껏 변경 가능

-----
### 스프링의 트랜잭션 추상화
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/9e8e0049-d117-461c-821b-f86dbd61ad3d">
</div>

1. 스프링 트랜잭션의 추상화 핵심 : PlatformTransactionManager
   - org.springframework.transaction.PlatformTransactionManager

2. 스프링 5.3부터는 JDBC 트랜잭션을 관리할 때, DataSoruceTransactionManager를 상속받아 약간의 기능을 확장한 JdbcTransactionManager를 제공
   - 둘의 기능 차이는 크지 않으므로, 거의 같은 것으로 이해하면 됨

3. PlatformTransactionManager 인터페이스
```java
package org.springframework.transaction;

import org.springframework.lang.Nullable;

public interface PlatformTransactionManager extends TransactionManager {
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException;

    void commit(TransactionStatus status) throws TransactionException;

    void rollback(TransactionStatus status) throws TransactionException;
}
```
  - getTransaction() : 트랜잭션을 시작
    + 기존에 이미 진행중인 트랜잭션이 있는 경우 해당 트랜잭션에 참여할 수 있음 (트랜잭션 참여, 전파)
  - commit() : 트랜잭션 커밋
  - rollback() : 트랜잭션 롤백

4. PlatformTransactionManager 인터페이스와 그 구현체 : 트랜잭션 매니저
