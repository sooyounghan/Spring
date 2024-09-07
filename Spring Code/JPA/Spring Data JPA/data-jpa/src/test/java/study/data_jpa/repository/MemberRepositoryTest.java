package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import net.bytebuddy.dynamic.Transformer;
import org.assertj.core.api.Assertions;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.Test; // Junit 5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void testFindUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String username : usernameList) {
            System.out.println("username = " + username);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.changeTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMembers = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member findMember : findMembers) {
            System.out.println("findMember = " + findMember);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findMembers = memberRepository.findListByUsername("AAA");

        for (Member findMember : findMembers) {
            System.out.println("findMember = " + findMember);
        }

        List<Member> findMembers2 = memberRepository.findListByUsername("CCC"); // 빈 컬렉션 반환
        System.out.println("findMembers2.size() = " + findMembers2.size());

        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println("findMember = " + findMember);

        Member findMember2 = memberRepository.findMemberByUsername("CCC"); // JPA : 예외 반환, Spring Data JPA : null 반환
        System.out.println("findMember2 = " + findMember2);

        Optional<Member> findOptionalMember = memberRepository.findOptionalMemberByUsername("AAA");
        System.out.println("findOptionalMember = " + findOptionalMember.get());

        Optional<Member> findOptionalMember2 = memberRepository.findOptionalMemberByUsername("CCC");
        System.out.println("findOptionalMember2 = " + findOptionalMember2); // Optional.empty
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest); // Page
        // Member Page -> MemberDto Page (map)
        Page<MemberDto> dtoPage = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest); // Slice : limit(3) + 1 = 4개 요청 (Count X) - 다음 페이지 여부
        List<Member> list = memberRepository.findListByAge(age, pageRequest); // List

        // then
        List<Member> content = page.getContent(); // 조회된 데이터
        long totalElements = page.getTotalElements();// = totalCount (조회된 데이터 수)

        // Page<Member>
        assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        assertThat(page.getTotalPages()).isEqualTo(2); // 페이지 번호 (1-3 : 1페이지, 4-5 : 2페이지)
        assertThat(page.isFirst()).isTrue(); // 첫 번쨰 항목(페이지) 인가?
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 존재하는가?

        // Slice<Member>
        // assertThat(slice.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        // assertThat(slice.getTotalPages()).isEqualTo(2); // 페이지 번호 (1-3 : 1페이지, 4-5 : 2페이지)
        assertThat(slice.isFirst()).isTrue(); // 첫 번쨰 항목(페이지) 인가?
        assertThat(slice.hasNext()).isTrue(); // 다음 페이지가 존재하는가?

        // List<Member>
        // assertThat(list.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        // assertThat(list.getTotalPages()).isEqualTo(2); // 페이지 번호 (1-3 : 1페이지, 4-5 : 2페이지)
        // assertThat(list.isFirst()).isTrue(); // 첫 번쨰 항목(페이지) 인가?
        // assertThat(list.hasNext()).isTrue(); // 다음 페이지가 존재하는가?
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        // em.flush(); // DB에 반영 (UPDATE SQL 같은 연산은 해당 쿼리를 미리 전송)
        // em.clear(); // 영속성 컨텍스트 초기화

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when (N + 1)
        // SELECT Member (1)
        List<Member> members = memberRepository.findAll();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); // Proxy
            System.out.println("member.team = " + member.getTeam().getName()); // LAZY(N) - Proxy 초기화 (SELECT Team)
        }
    }

    @Test
    public void findMemberFetchJoin() {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findMemberFetchJoin();
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); // 순수 Team Entity
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void findEntityGraphMember() {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        // when
        List<Member> members = memberRepository.findEntityGraphByUsername("member1");
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass()); // 순수 Team Entity
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        // given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");

        em.flush(); // UPDATE 쿼리 미실행
    }

    @Test
    public void lock() {
        // given
        memberRepository.save(new Member("member1", 10));
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // When
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // Then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // When
        // Probe
        Member member = new Member("m1"); // 도메인 객체 자체가 검색 조건
        Team team = new Team("teamA"); // 내부 조인으로 teamA 가능
        member.setTeam(team);

        // ExampleMatcher 생성 (여기서는 age 프로퍼티 무시)
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");// age라는 속성이 있으면 무시하고 매칭

        Example<Member> example = Example.of(member, matcher);// Probe : 도메인 객체 member가 검색 조건 (단, 위의 matcher 적용)

        List<Member> result = memberRepository.findAll(example);

        // Then
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // When
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnly usernameOnly : result) {
            System.out.println("usernameOnly = " + usernameOnly);
        }

        // Then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void projections2() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // When
        List<UsernameOnlyDto> result = memberRepository.findProjectionsDtoByUsername("m1", UsernameOnlyDto.class);

        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnlyDto = " + usernameOnlyDto.getUsername());
        }

        // Then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void projections3() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // When
        List<NestedClosedProjections> result = memberRepository.findProjectionsDtoByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("nestedClosedProjections = " + nestedClosedProjections.getUsername());
            System.out.println("nestedClosedProjections = " + nestedClosedProjections.getTeam().getName());
        }

        // Then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void nativeQuery() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // When
        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("result = " + result);
    }

    @Test
    public void nativeQueryProjection() {
        // Given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // When
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection.username = " + memberProjection.getUsername());
            System.out.println("memberProjection.teamName = " + memberProjection.getTeamName());
        }
    }
}