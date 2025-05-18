package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제 고려하지 않음.
 * 실무 : ConcurrentHashMap, AtomicLong 사용 고려
 */
public class MemberRepository {
    private static Map<Long, Member> store = new HashMap<Long, Member>();
    private static long sequence = 0L;

    // Singleton Instance
    private static final MemberRepository instance = new MemberRepository();

    // 싱글톤 객체이므로 객체 반환 메서드
    public static MemberRepository getInstance() {
        return instance;
    }

    private MemberRepository() {

    }

    // 회원 정보 저장
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    // 회원의 ID로 회원 정보를 찾음
    public Member findById(Long id) {
        return store.get(id);
    }

    // 모든 회원 정보 반환
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    // 모든 회원 정보 삭제
    public void clearStore() {
        store.clear();
    }
}
