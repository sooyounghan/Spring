package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class MemberRepository {
    private static Map<Long, Member> store = new HashMap<>(); // static 사용
    private static Long sequence = 0L;

    // 회원 1명 저장
    public Member save(Member member) {
        member.setId(++sequence);
        log.info("save : member = {}", member);
        store.put(member.getId(), member);
        return member;
    }

    // ID를 통해 회원 찾기
    public Member findById(Long id) {
        return store.get(id);
    }

    // Login ID를 통한 회원 찾기
    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream()
                .filter(member -> member.getLoginId().equals(loginId))
                .findFirst();
    }

    // 전체 회원 찾기
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    // 전체 회원 삭제
    public void clearStore() {
        store.clear();
    }
}
