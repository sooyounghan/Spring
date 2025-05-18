-----
### 트랜잭션 탬플릿
-----
1. 트랜잭션 사용하는 로직을 보면, 같은 패턴이 반복
2. 트랜잭션 사용 코드
```java
// 트랜잭션 시작 (매개변수 : 트랜잭션 디폴트 정의)
TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

try {
    // 비즈니스 로직
    bizLogic(fromId, toId, money);
    // 성공 : 트랜잭션 종료 (커밋)
    transactionManager.commit(status);
} catch(Exception e) {
    // 예외 발생 (실패) 하면, Rollback
    transactionManager.rollback(status);
    throw new IllegalStateException(e);
}
```
  - 트랜잭션 시작 - 비즈니스 로직 실행 - 성공 : 커밋 / 예외가 발생해서 실패 : 롤백
  - 다른 서비스에서 트랜잭션을 시작하려면, try-catch-finally를 포함한 성공 시 커밋, 실패 시 롤백 코드가 반복될 것
  - 이런 형태는 각 서비스에서 반복되고, 달라지는 부분은 비즈니스 로직 뿐임
  - 이럴 때, 템플릿 콜백 패턴을 활용하면 반복 문제 해결 가능

3. 탬플릿 콜백 패턴을 적용하려면 탬플릿을 제공하는 클래스를 작성해야함
4. 스프링은 탬플릿 클래스 TransactionTemplate 제공
```java
public class TransactionTemplate {
    private PlatformTransactionManager transactionManager;

    public <T> T execute(TransactionCallback<T> action){..}
    void executeWithoutResult(Consumer<TransactionStatus> action){..} 
}
```
  - execute() : 응답 값이 있을 때 사용
  - executeWithoutResult() : 응답 값이 없을 때 사용

5. 트랜잭션 탬플릿 사용
```java
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 탬플릿
 */
@Slf4j
public class MemberServiceV3_2 {
    // 트랜잭션 탬플릿 선언
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepository;

    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.txTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        // 반환 값이 없으므로 TransactionTemplate의 executeWithoutResult 사용
        txTemplate.executeWithoutResult((status) -> {
            // 비즈니스 로직
            try {
                bizLogic(fromId, toId, money);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static void validation(Member toMember) {
        if(toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체중 예외 발생");
        }
    }

    private void bizLogic( String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(toId, toMember.getMoney() + money);
    }
}
```
  - TransactionTemplate을 사용하려면 transactionManager가 필요
  - 생성자에서 transactionManager를 주입받으면서 TransactionTemplate 생성

```java
// 반환 값이 없으므로 TransactionTemplate의 executeWithoutResult 사용
txTemplate.executeWithoutResult((status) -> {
    // 비즈니스 로직
    try {
        bizLogic(fromId, toId, money);
    } catch (SQLException e) {
        throw new IllegalStateException(e); // SQLException(체크 예외) -> Unchecked 예외로 전환해서 던짐
    }
});
```
  - 트랜잭션 탬플릿 덕분에 트랜잭션 시작, 커밋하거나 롤백하는 코드 모두 제거
  - 트랜잭션 템플릿의 기본 동작
    + 비즈니스 로직이 정상 수행되면 커밋
    + 💡 Unchecked 예외가 발생하면 롤백하며, 그 외의 경우는 커밋
    + 💡 Checked 예외의 경우에는 커밋하는데, 이 부분은 추후 공부
  - 코드에서 예외를 처리하기 위해 try-catch문이 존재하는데, bizLogic()을 호출하면, SQLException Checked 예외를 넘겨줌
  - 해당 람다에서 Checked 예외를 던질 수 없으므로 Unchecked 예외로 바꾸어 던지도록 예외 전환

6. 테스트 코드
```java
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * 트랜잭션 - 트랜잭션 탬플릿
 */
class MemberServiceV3_2Test {
    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV3 memberRepository;
    private MemberServiceV3_2 memberService;

    @BeforeEach
    void before() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        memberRepository = new MemberRepositoryV3(dataSource);
        // 💡 트랜잭션 매니저 생성 시, DataSource를 넘겨줘야함
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        memberService = new MemberServiceV3_2(transactionManager, memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {
        // Given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberB = new Member(MEMBER_B, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        // When
        memberService.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000);

        // Then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());
        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);
    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        // Given
        Member memberA = new Member(MEMBER_A, 10000);
        Member memberEX = new Member(MEMBER_EX, 10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEX);

        // When
        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(), memberEX.getMemberId(), 2000))
                .isInstanceOf(IllegalStateException.class);

        // Then
        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEX = memberRepository.findById(memberEX.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEX.getMoney()).isEqualTo(10000);
    }
}
```

-----
### 정리
-----
1. 트랜잭션 템플릿 덕분에, 트랜잭션을 사용할 때 반복하는 코드 제거
2. 하지만, 서비스 로직인데, 비즈니스 로직 뿐만 아니라 트랜잭션을 처리하는 기술 로직이 함께 포함되어 있음
3. 애플리케이션을 구성하는 로직을 핵심 기능과 부가 기능으로 구분하자면, 서비스 입장에서 비즈니스 로직은 핵심 기능 / 트랜잭션은 부가 기능
4. 이렇게 비즈니스 로직과 트랜잭션을 처리하는 기술 로직이 한 곳에 존재하면, 두 관심사를 하나의 클래스에서 처리하게 됨
5. 결과적으로, 코드를 유지보수하기 어려워짐
6. 서비스 로직은 가급적 핵심 비즈니스 로직만 있어야하지만, 트랜잭션 기술을 사용하려면 트랜잭션 코드가 있어야 함
