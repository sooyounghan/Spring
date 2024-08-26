package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 인터페이스가 다른 인터페이스로 상속될 때는 기존과 동일하게 extends
// JpaRepository<T, ID(PK) Type> = JpaRepository<Member, Long>
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
    Optional<Member> findByName(String name);
}