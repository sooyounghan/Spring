-----
### 예제 도메인 모델
-----
1. 엔티티 클래스
<div align="center">
<img src="https://github.com/user-attachments/assets/c31a8230-253f-42f5-97bf-347a95f8fac8">
</div>

2. ERD
<div align="center">
<img src="https://github.com/user-attachments/assets/cf4e56f0-c6b4-4edf-9e82-b0463328874a">
</div>

3. Member 엔티티
```java
package study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "username", "age"})
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 생성자
    public Member(String username) {
        this(username, 0);
    }

    public Member(String username, int age) {
        this(username, age, null);
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;

        if(team != null) {
            changeTeam(team);
        }
    }

    // 연관관계 편의 메서드
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
```
  - 롬복 설명
    + @Setter : 실무에서는 가급적 Setter 사용하지 않기
    + @NoArgsConstructor(access = AccessLevel.PROTECTED) : 기본 생성자 방지 (JPA 스펙 상 PROTECTED까지 개방)
    + @ToString : 가급적 내부 필드만 사용 (연관관계가 없는 필드만 사용)
  - changeTeam() : 양방향 연관관계 한 번에 처리 (연관관계 편의 메서드)

4. Team 엔티티
```java
package study.querydsl.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();

    // 생성자
    public Team(String name) {
        this.name = name;
    }
}
```
  - Member와 Team은 양방향 연관관계
  - Member.team은 연관관계의 주인
  - Team.members는 연관관계의 주인이 아님
  - 💡 따라서, Member.team이 데이터베이스 외래키 값 변경, 반대편은 읽기만 가능

5. 데이터 확인 테스트
```java
package study.querydsl.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
// @Commit
class MemberTest {
    @Autowired EntityManager em;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        // 초기화
        em.flush();
        em.clear();

        // 확인
        List<Member> members = em.createQuery("SELECT m FROM Member m", Member.class)
                                    .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("-> member.team = " + member.getTeam());
        }
    }
}
```
  - 가급적 순수 JPA로 동작 확인
  - DB 테이블 결과 확인
  - 지연 로딩 동작 확인
