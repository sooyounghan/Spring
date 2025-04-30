-----
### 애플리케이션 구조
-----
1. 가장 단순하면서 많이 사용하는 방법은 3계층으로 나누는 것
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/1c5850e2-0149-4d0d-bf31-c2c64da0e51b">
</div>

2. 프레젠테이션 계층
   - UI와 관련된 처리 담당
   - 웹 요청과 응답
   - 사용자 요청 검증
   - 주 사용 기술 : 서블릿과 HTTP 같은 웹 기술, Spring MVC

3. 서비스 계층
   - 비즈니스 로직 담당
   - 주 사용 기술 : 가급적 특정 기술에 의존하지 않고, 순수 자바 코드로 작성

4. 데이터 접근 계층
   - 실제 데이터베이스에 접근하는 코드
   - 주 사용 기술 : JDBC, JPA, File, Redis, MongoDB, ...

5. 순수한 서비스 계층
   - 핵심 비즈니스 로직이 들어있는 계층
   - 비즈니스 로직은 UI(웹) 관련 부분이 변하고, 데이터 저장 기술을 다른 기술로 변경해도, 최대한 변경 없이 유지되어야 함
   - 즉, 서비스 계층을 특정 기술에 종속적이지 않게 개발해야 함
     + 계층을 나누는 이유도 서비스 계층을 최대한 순수하게 유지하기 위한 목적
     + 기술에 종속적인 부분은 프레젠테이션 계층, 데이터 접근 계층에서 가지고 감
     + 프레젠테이션 계층은 클라이언트가 접근하는 UI와 관련된 기술인 웹, 서블릿, HTTP와 관련된 부분 담당
     + 따라서, 서비스 계층을 UI와 관련된 기술로부터 보호
       * 예) HTTP API를 사용하다가 GPRC 같은 기술로 변경해도 프레젠테이션 계층 코드만 변경, 서비스 계층은 변경하지 않아도 됨
     + 데이터 접근 계층은 데이터를 저장하고 관리하는 기술 담당
     + 따라서, JDBC, JPA와 같은 구체적 데이터 접근 기술로부터 서비스 계층 보호
       * 예) JDBC를 사용하다가 JPA로 변경해도 서비스 계층은 변경하지 않아도 됨
       * 물론, 서비스 계층에서 데이터 계층에 직접 접근하는 것이 아닌, 인터페이스를 제공하고 서비스 계층은 이 인터페이스에 의존하는 것이 좋음
       * 이러한 방법이 서비스 코드의 변경없이 JdbcRepository를 JpaRepository로 변경 가능
   - 서비스 계층이 특정 기술에 종속되지 않으므로, 비즈니스 로직을 유지보수 및 테스트에 용이
   - 즉, 서비스 계층은 가급적 비즈니스 로직만 구현하고, 특정 구현 기술에 직접 의존해서는 안 됨
   - 이렇게 하면, 향후 구현 기술이 변경될 때, 변경의 영향 범위 최소화 가능

-----
### 문제점
-----
1. MemberServiceV1
```java
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {
    private final MemberRepositoryV1 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);

    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }
}
```
  - 특정 기술에 종속적이지 않고, 순수 비즈니스 로직만 존재
  - 특정 기술과 관련된 코드가 거의 없어서 깔끔하고, 유지보수 하기 쉬움
  - 향후 비즈니스 로직의 변경이 필요하면 이 부분만 변경하면 됨

2. 문제점
    - SQLExcpetion이라는 JDBC 기술에 의존
    - 이 부분은 memberRepository에서 던져지는 예외이기 때문에, memberRepository에서 해결해야 함
    - 또한, MemberRepositoryV1라는 구체 클래스에 의존하고 있으므로, MemberRepository 인터페이스를 도입하면, 향후 MemberService의 코드의 변경 없이 다른 구현 기술로 손쉽게 변경 가능

3. MemberServiceV2
```java
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 연동
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

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

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private static void release(Connection conn) {
        if(conn != null) {
            try {
                // 커넥션 풀로 돌아가므로, AutoCommit 기본값인 true으로 변경
                conn.setAutoCommit(true);
                conn.close();
            } catch(Exception e) {
                log.info("error", e);
            }
        }
    }

    private void bizLogic(Connection conn, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(conn, fromId);
        Member toMember = memberRepository.findById(conn, toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }
}
```
  - 트랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작하는 것이 좋음
  - 문제는 트랜잭션을 사용하기 위해 javax.sql.DataSource javax.sql.Connection, javax.sql.SQLException 같은 JDBC 기술에 의존해야 함
  - 💡 트랜잭션을 사용하기 위해 JDBC 기술에 의존
  - 결과적으로 비즈니스 로직보다 JDBC를 사용해서 트랜잭션을 처리하는 코드가 더 많음
  - 이는 JDBC에서 JPA 같은 기술로 바꾸어 사용하게 되면, 서비스 코드 모두 함께 변경해야 함(JPA는 JDBC와 트랜잭션을 사용하는 코드가 다름)
  - 또한, 핵심 비즈니스 로직과 JDBC 기술이 섞여 있어 유지보수 하기 어려움

-----
### 문제 정리
-----
A. 트랜잭션 문제
  1. JDBC 구현 기술이 서비스 계층에 누수되는 문제
     - 트랜잭션 적용을 위해 JDBC 구현 기술이 서비스 계층에 누수
     - 서비스 계층은 순수해야 되며, 구현 기술을 변경해도 서비스 계층 코드는 최대한 유지할 수 있어야함 (변화에 대응)
       + 따라서, 데이터 접근 계층에 JDBC 코드를 집중
       + 물론, 데이터 접근 계층의 구현 기술이 변경될 수 있으니, 데이터 접근 계층은 인터페이스를 제공하는 것이 좋음
     - 서비스 계층은 특정 기술에 종속되지 않아야함. 하지만, 트랜잭션을 적용하면서 서비스 계층에 JDBC 구현 기술 누수

  2. 트랜잭션 동기화 문제
     - 같은 트랜잭션 유지를 위해 커넥션을 파라미터로 넘겨야함
     - 이 때, 파생되는 문제들도 존재
     - 똑같은 기능도 트랜잭션용 기능과 트랜잭션을 유지하도 않아도 되는 기능으로 분리해야 함

  3. 트랜잭션 적용 반복 문제 : 트랜잭션 적용 코드에 try-catch-finally 가 반복

B. 예외 누수 문제
  1. 데이터 접근 계층의 JDBC 구현 기술 예외가 서비스 계층으로 전파
  2. SQLException은 체크 예외이기 때문에, 데이터 접근 계층을 호출한 서비스 계층에서 해당 예외를 잡아서 처리하거나 명시적으로 throws를 통해 던져야 함
  3. SQLException은 JDBC 전용 기술이며, 향후 JPA나 다른 데이터 접근 기술을 사용하면, 그에 맞는 다른 예외로 변경해야하며, 결국 서비스 코드도 수정해야 함

C. JDBC 코드 반복 문제 
  1. 트랜잭션 적용 반복 문제 : 트랜잭션 적용 코드에 try-catch-finally 가 반복
  2. 커넥션을 열고, PreparedStatement를 사용하고, 결과를 매핑하고, 실행하고, 커넥션과 리소스를 정리하는 과정 반복

D. 스프링은 서비스 계층을 순수하게 유지하면서, 이러한 문제를 해결할 수 있는 다양한 방법과 기술 제공
