package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource 사용, JdbcUtils 사용
 */

@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {
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
            throw e;
        } finally {
            close(conn, pstmt, null); // 자원 반환
        }
    }

    public Member findById(String memberId) throws SQLException {
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
            throw e;
        } finally {
            close(conn, pstmt, rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
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
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    public void delete(String member_id) throws SQLException {
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
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    private Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        log.info("get Connection{}, class={}", connection, connection.getClass());
        return connection;
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }
}
