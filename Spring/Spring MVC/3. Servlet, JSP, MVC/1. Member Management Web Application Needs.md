-----
### 회원 관리 웹 애플리케이션 요구사항
-----
1. 회원 정보
   - 이름 : username
   - 나이 : age

2. 기능 요구사항
   - 회원 저장
   - 회원 목록 조회

3. 회원 도메인 모델
```java
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
```

4. 테스트 코드
```java
package hello.servlet.domain.member;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryTest {
    MemberRepository memberRepository = MemberRepository.getInstance();

    // 테스트 완료 후 처리
    @AfterEach
    void afterEach() {
        memberRepository.clearStore();
    }

    // 회원 정보 저장 테스트
    @Test
    void save() {
        // Given
        Member member = new Member("hello", 20);

        // When
        Member savedMember = memberRepository.save(member);

        // Then
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    // 모든 회원 정보 저장 테스트
    @Test
    void findAll() {
        // Given
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // When
        List<Member> result = memberRepository.findAll();

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }
}
```

  - 회원을 저장하고, 목록을 조회하는 테스트 작성
  - 각 테스트가 끝날 때, 다음 테스트에 영향을 주지 않도록 각 테스트의 저장소를 clearStore()를 호출해 초기화 
