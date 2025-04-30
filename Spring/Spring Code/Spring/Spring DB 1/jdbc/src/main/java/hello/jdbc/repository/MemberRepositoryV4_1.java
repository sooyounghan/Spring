package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.ex.MyDbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * 예외 누수 문제 해결
 * Checked 예외를 Runtime 예외로 변경
 * MemberRepository 인터페이스 사용
 *  - throws SQLException 제거
 */

@Slf4j
public class MemberRepositoryV4_1 implements MemberRepository {
    private final DataSource dataSource;

    public MemberRepositoryV4_1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Member save(Member member) {
        String sql = "INSERT INTO member(member_id, money) VALUES(?, ?)"; // SQL Query

        Connection conn = null; // Connection 객체
        PreparedStatement pstmt = null; // SQL 쿼리 객체

        try {
            conn = getConnection(); // Connection 객체 얻기
            pstmt = conn.prepareStatement(sql); // SQL 쿼리 담기
            pstmt.setString(1, member.getMemberId()); // ? 값 대입
            pstmt.setInt(2, member.getMoney()); // ? 값 대입
            pstmt.executeUpdate();
            return member; // 반환
        } catch (SQLException e) {
            log.error("DB Error", e); // 예외에 대한 에러 로그 출력
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null); // 자원 반환
        }
    }

    @Override
    public Member findById(String memberId) {
        String sql = "SELECT * FROM MEMBER WHERE member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();

            if(rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("Member Not Fount MemberId = " + memberId);
            }
        } catch(SQLException e) {
            log.error("Error", e);
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void update(String memberId, int money) {
        String sql = "UPDATE member SET money = ? WHERE member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={} " + resultSize);
        } catch(SQLException e) {
            log.error("Error", e);
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void delete(String member_id) {
        String sql = "DELETE FROM member WHERE member_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member_id);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={} " + resultSize);
        } catch(SQLException e) {
            log.error("Error", e);
            throw new MyDbException(e);
        } finally {
            close(conn, pstmt, null);
        }
    }

    private Connection getConnection() {
        // 💡 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용
        Connection connection = DataSourceUtils.getConnection(dataSource);

        log.info("get Connection{}, class={}", connection, connection.getClass());
        return connection;
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);

        // 💡 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}
