-----
### 예제 만들기
-----
1. @Trace 애너테이션으로 로그 출력
2. @Retry 애너테이션으로 예외 발생 시 재시도하기
3. 먼저 AOP를 적용할 예제 생성
   - ExamRepository
```java
package hello.aop.exam;

import org.springframework.stereotype.Repository;

@Repository
public class ExamRepository {
    private static int seq = 0;

    /**
     * 5번에 1번 실패하는 요청
     */
    public String save(String itemId) {
        seq++;

        if(seq % 5 == 0) {
            throw new IllegalStateException("예외 발생");
        }

        return "OK";
    }
}
```
  - 5번에 1번 정도 실패하는 저장소
  - 간혈적으로 실패할 경우 재시도하는 AOP가 있으면 편리

  - ExamService
```java
package hello.aop.exam;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;

    public void request(String itemId) {
        examRepository.save(itemId);
    }
}
```

  - ExampTest
```java
package hello.aop.exam;

import hello.aop.exam.ExamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExamTest {
    @Autowired ExamService examService;

    @Test
    void test() {
        for(int i = 0; i < 5; i++) {
            examService.request("data" + i);
        }
    }
}
```
   - 실행하면, 테스트가 5번째 루프를 실행할 때, 레포지토리 위치에서 예외가 발생하면서 실패
