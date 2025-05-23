-----
### 기본 Q-Type 활용
-----
1. Q클래스 인스턴스를 사용하는 2가지 방법
```java
QMember qMember = new QMember("m"); // 별칭 직접 지정
QMember qMember = QMmber.member; // 기본 인스턴스 사용
```

2. 기본 인스턴스를 static import와 함께 사용
```java
package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import static study.querydsl.entity.QMember.member; // static import

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        ...
    }

    @Test
    public void startQuerydsl() {
        // QMember m = new QMember("m"); // 같은 테이블을 조인해야 하는 경우 사용
        // QMember m = QMember.member; // 그 외의 경우는 기본 인스턴스 사용

        Member findMember = queryFactory.select(member) // static import
                                         .from(member)
                                         .where(member.username.eq("member1")) // 파라미터 바인딩 처리
                                         .fetchOne();

        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }
}
```

  - 다음 조건을 추가하면 실행되는 JPQL을 볼 수 있음
```properties
spring.jpa.properties.hibernate.use_sql_comments=true
```
```
/* select
    member1 
from
    Member member1 
where
    member1.username = ?1 */
select
    m1_0.member_id,
    m1_0.age,
    m1_0.team_id,
    m1_0.username 
from
    member m1_0 
where
    m1_0.username=?
```

  - 💡 참고 :  같은 테이블을 조인해야 하는 경우가 아니면 기본 인스턴스 사용
