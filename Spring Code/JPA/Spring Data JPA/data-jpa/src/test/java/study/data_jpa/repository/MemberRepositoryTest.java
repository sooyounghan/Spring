package study.data_jpa.repository;

import org.junit.jupiter.api.Test; // Junit 5
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
@Rollback(false)
public class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;

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
}