package study.data_jpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {
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

    @Modifying(clearAutomatically = true) // 벌크 연산 후 영속성 컨텍스트 초기화
    @Query("UPDATE Member m SET m.age = m.age + 1 WHERE m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.team t")
    List<Member> findMemberFetchJoin();

    // 공통 메서드 오버라이딩
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL + 엔티티 그래프
    // @EntityGraph(attributePaths = {"team"})
    @EntityGraph("Member.all")
    @Query("SELECT m FROM Member m")
    List<Member> findMemberEntityGraph();

    // 메서드 이름 쿼리에서 특히 편리
    @EntityGraph(attributePaths = {"team"})
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"), forCounting = true)
    Page<Member> findReadOnlyByUsername(String name, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    List<UsernameOnly> findProjectionsByUsername(@Param("username") String username);
    // List<UsernameOnlyDto> findProjectionsDtoByUsername(@Param("username") String username);
    <T> List<T> findProjectionsDtoByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "SELECT * FROM member WHERE username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName "
                    + "FROM member m LEFT JOIN team t ON m.team_id = t.team_id",
            countQuery = "SELECT COUNT(*) FROM member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
