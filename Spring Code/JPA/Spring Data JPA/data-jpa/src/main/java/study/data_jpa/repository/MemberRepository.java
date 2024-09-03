package study.data_jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("SELECT m FROM Member m WHERE m.username = :username AND m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("SELECT m.username FROM Member m")
    List<String> findUsernameList();

    @Query("SELECT new study.data_jpa.dto.MemberDto(m.id, m.username, t.name) FROM Member m JOIN m.team t")
    List<MemberDto> findMemberDto();

    @Query("SELECT m FROM Member m WHERE m.username = :name")
    Member findMembers(@Param("name") String username);

    @Query("SELECT m  FROM Member m WHERE m.username IN :names")
    List<Member> findByNames(Collection<String> names);

    List<Member> findListByUsername(String username); // Collection
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalMemberByUsername(String username); // 단건 - Optional

    Page<Member> findByAge(int age, Pageable pageable); // Page
    Slice<Member> findSliceByAge(int age, Pageable pageable); // Slice (limit + 1)
    List<Member> findListByAge(int age, Pageable pageable); // List

    @Query(
            value = "SELECT m FROM Member m LEFT JOIN m.team t", // 일반 쿼리 : 조인 쿼리
            countQuery = "SELECT COUNT(m.username) FROM Member m" // 카운터 쿼리 
    )
    Page<Member> findMemberAllCountBy(Pageable pageable);

    List<Member> findTop3By();
}
