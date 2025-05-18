package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
