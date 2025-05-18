-----
### 예제 만들기
-----
1. JdbcTemplate을 사용해서 회원 데이터를 DB에 저장하고 조회하는 간단한 기능
2. Member
```java
package hello.member;

import lombok.Data;

@Data
public class Member {
    private String memberId;
    private String name;

    public Member() {
    }

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }
}
```
  - memberId, name 필드가 있는 간단한 회원 객체
  - 기본 생성자 / memberId, name을 포함하는 생성자 이렇게 2개의 생성자 생성

3. DbConfig
```java
package hello.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DbConfig {
    @Bean
    public DataSource dataSource() {
        log.info("dataSource 빈 등록");
        
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setJdbcUrl("jdbc:h2:mem:test");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        
        return dataSource;
    }
    
    @Bean
    public TransactionManager transactionManager() {
        log.info("transactionManager 빈 등록");
        
        return new JdbcTransactionManager(dataSource());
    }
    
    @Bean
    public JdbcTemplate jdbcTemplate() {
        log.info("jdbcTemplate 빈 등록");
        
        return new JdbcTemplate(dataSource());
    }
}
```
  - JdbcTemplate을 사용해서 회원 데이터를 DB에 보관하고 관리하는 기능
  - DataSource, TransactionManager, JdbcTemplate을 스프링 빈으로 직접 등록
  - 빈 등록이 실제 호출되는지 확인을 위해 로그를 남김
  - DB는 별도의 외부 DB가 아니라 JVM 내부에서 동작하는 메모리 DB 사용
    + 메모리 모드로 동작 옵션 : jdbc:h2:mem:test
  - JdbcTransactionManager는 DataSourceTransactionManager와 같은 것으로 생각하면 됨 (예외 변환 기능이 보강)

4. MemberRepository
```java
package hello.member;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MemberRepository {

    public final JdbcTemplate jdbcTemplate;

    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void initTable() {
        jdbcTemplate.execute("CREATE TABLE member(member_id VARCHAR PRIMARY KEY, name VARCHAR)");
    }

    public void save(Member member) {
        jdbcTemplate.update("INSERT INTO member(member_id, name) VALUES (?, ?)",
                            member.getMemberId(), member.getName());
    }

    public Member find(String memberId) {
        return jdbcTemplate.queryForObject("SELECT member_id, name FROM member WHERE member_id = ?",
                                            BeanPropertyRowMapper.newInstance(Member.class),
                                            memberId);
    }

    public List<Member> findAll() {
        return jdbcTemplate.query("SELECT member_id, name FROM member",
                                  BeanPropertyRowMapper.newInstance(Member.class));
    }
}
```
  - JdbcTemplate을 사용해서 회원을 관리하는 레포지토리
  - DbConfig에서 JdbcTemplate을 빈으로 등록했기 때문에, 바로 주입 받아서 사용 가능
  - initTable : 보통 레포지토리에 테이블을 생성하는 스크립트를 두지 않으나, 예제 단순화를 위해 이곳에 사용
 
5. MemberRepositoryTest
```java
package hello.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Transactional
    @Test
    void memberTest() {
        Member member = new Member("idA", "memberA");

        memberRepository.initTable();
        memberRepository.save(member);

        Member findMember = memberRepository.find(member.getMemberId());
        Assertions.assertThat(findMember.getMemberId()).isEqualTo(member.getMemberId());
        Assertions.assertThat(findMember.getName()).isEqualTo(member.getName());
    }
}
```

6. 정리
   - 회원 데이터를 DB에 보관하고 관리하기 위해 빈으로 등록한 JdbcTemplate, DataSource, TransactionManager가 모두 사용
   - 생각해보면, DB에 데이터를 보관하고 관리하기 위해 이런 객체들을 항상 스프링 빈으로 등록해야하는 번거로움 존재
   - 만약, DB를 사용하는 다른 프로젝트를 진행하면 이러한 객체를 또 스프링 빈으로 등록해야함
