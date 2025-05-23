-----
### 콜백 (Callback)
-----
1. ContextV2는 변하지 않는 템플릿 역할을 함
2. 그리고 변하는 부분은 파라미터로 넘어온 Strategy의 코드를 실행해서 처리
3. 이처럼 다른 코드의 인수로서 넘겨주는 실행 가능한 코드를 콜백(Callback)이라 함
4. 💡 콜백
   - 프로그래밍에서 콜백(Callback) 또는 콜애프터 함수(Call-After Function)는 다른 코드의 인수로서 넘겨주는 실행 가능한 코드
   - 콜백을 넘겨받는 코드는 이 콜백을 필요에 따라 즉시 실행할 수 있고, 아니면 나중에 실행할 수 있음

5. 💡 즉, 쉽게 말해 콜백은 코드가 호출(call)되는데 코드를 넘겨준 곳의 뒤(back)에서 실행된다는 뜻
   - ContextV2 예제에서 콜백은 Strategy
   - 클라이언트에서 직접 Strategy를 실행하는 것이 아니라, 클라이언트가 ContextV2.execute(...)를 실행할 때, Streategy를 넘겨주고, ContextV2 뒤에서 Strategy가 실행

6. 자바 언어에서 콜백
   - 자바 언어에서 실행 가능한 코드를 인수로 넘기려면 객체가 필요
   - 자바 8부터는 람다를 사용할 수 있음
   - 자바 8이전에는 보통 하나의 메소드를 가진 인터페이스를 구현하고, 주로 익명 내부 클래스를 사용
   - 최근에는 주로 람다 사용

-----
### 템플릿 콜백 패턴
-----
1. 스프링에서 ContextV2와 같은 방식의 전략 패턴을 템플릿 콜백 패턴이라 함
2. 전략 패턴에서 Context가 템플릿 역할, Strategy 부분이 콜백으로 넘어온다고 생각하면 됨
3. 💡 참고로 템플릿 콜백 패턴은 GOF 패턴은 아니고, 스프링 내부에서 이런 방식을 자주 사용하므로, 스프링 안에서만 이렇게 부름
   - 전략 패턴에서 템플릿과 콜백 부분이 강조된 패턴이라 생각
4. 스프링에서는 JdbcTemplate, RestTemplate, TransactionTemplate, RedisTemplate처럼 다양함 템플릿 콜백 패턴 사용
   - 스프링에서 이름에 XxxTemplate이 있다면, 템플릿 콜백 패턴으로 만들어진것으로 보면 됨
<div align="center">
<img src="https://github.com/user-attachments/assets/8d600443-45a4-4c15-9f43-cb74207efffe">
</div>

-----
### 예제
-----
1. 템플릿 콜백 패턴 구현 (ContextV2와 내용은 같고, 이름만 다름)
   - Context → Template
   - Strategy → Callback

2. Callback 인터페이스 (테스트 코드(src/test)에 위치)
```java
package hello.advanced.trace.strategy.code.template;

public interface Callback {
    void call();
}
```
  - 콜백 로직을 전달할 인터페이스

3. TimeLogTemplate (테스트 코드(src/test)에 위치)
```java
package hello.advanced.trace.strategy.code.template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeLogTemplate {
    public void execute(Callback callback) {
        long startTime = System.currentTimeMillis();

        // 비즈니스 로직 실행
        callback.call();
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("resultTime = {}", resultTime);
    }
}
```

4. TemplateCallbackTest
```java
package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.template.Callback;
import hello.advanced.trace.strategy.code.template.TimeLogTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TemplateCallbackTest {
    /**
     * 템플릿 콜백 패턴 - 익명 내부 클래스
     */
    @Test
    void callbackV1() {
        TimeLogTemplate template = new TimeLogTemplate();
        template.execute(new Callback() {
            @Override
            public void call() {
                log.info("비즈니스 로직1 실행");
            }
        });
        
        template.execute(new Callback() {
            @Override
            public void call() {
                log.info("비즈니스 로직2 실행");
            }
        });
    }

    /**
     * 템플릿 콜백 패턴 - 람다
     */
    @Test
    void callbackV2() {
        TimeLogTemplate template = new TimeLogTemplate();
        template.execute(() -> log.info("비즈니스 로직1 실행"));
        template.execute(() -> log.info("비즈니스 로직2 실행"));
    }
}
```
  - 별도의 클래스를 만들어서 전달해도 되지만, 콜백을 사용할 경우 익명 내부 클래스나 람다 사용하는 것이 편리
  - 물론 여러 곳에서 함께 사용되는 경우에는 재사용을 위해 콜백을 별도 클래스로 만들어도 됨

-----
### 적용
-----
1. TraceCallback 인터페이스
```java
package hello.advanced.trace.callback;

public interface TraceCallback<T> {
    T call();
}
```
  - 콜백을 전달하는 인터페이스
  - ```<T>``` 제네릭을 사용 : 콜백의 반환 타입 정의

2. TraceTemplate
```java
package hello.advanced.trace.callback;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.logtrace.LogTrace;

public class TraceTemplate {
    private final LogTrace logTrace;

    public TraceTemplate(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    public <T> T execute(String message, TraceCallback<T> callback) {
    // 지네릭 메서드 제네릭 <T>(메서드 내 작용)와 매개변수 TraceCallback<T> callback (callback의 타입이 TraceCallback<T>)의 제네릭은 서로 다른 제네릭
        TraceStatus status = null;

        try {
            status = logTrace.begin(message);

            // 로직 호출
            T result = callback.call();

            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```
  - TraceTemplate는 템플릿 역할
  - execute(...)를 보면 message 데이터와 콜백인 TraceCallback callback을 전달 받음
  - ```<T>``` 제네릭 사용 : 반환 타입 정의

3. v4 → v5 복사
  - hello.advanced.app.v5 패키지 생성 
  - 복사
    + v4.OrderControllerV4 → v5.OrderControllerV5 
    + v4.OrderServiceV4 → v5.OrderServiceV5
    + v4.OrderRepositoryV4 → v5.OrderRepositoryV5 

  - 코드 내부 의존관계를 클래스를 V5으로 변경
    + OrderControllerV5 : OrderServiceV4 → OrderServiceV5
    + OrderServiceV5 : OrderRepositoryV4 → OrderRepositoryV5 
  
  - OrderControllerV5 매핑 정보 변경
    + @GetMapping("/v5/request")

  - TraceTemplate 을 사용하도록 코드 변경

6. OrderControllerV5
```java
package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceCallback;
import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderControllerV5 {
    private final OrderServiceV5 orderService;
    private final TraceTemplate traceTemplate;

    public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
        this.orderService = orderService;
        this.traceTemplate = new TraceTemplate(trace);
    }

    @GetMapping("/v5/request")
    public String request(String itemId) {

        return traceTemplate.execute("OrderController.request()", new TraceCallback<String>() {
            @Override
            public String call() {
                orderService.orderItem(itemId);
                return "OK";
            }
        });
    }
}
```
```java
private final TraceTemplate traceTemplate;

...
// @RequiredArgsConstructor 사용하지 않고, 생성자로 주입 (하나이므로 @Autowired 적용)
public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
    this.orderService = orderService;
    this.traceTemplate = new TraceTemplate(trace);
}
```
  - this.traceTemplate = new TraceTemplate(trace) : trace의 의존관계 주입을 받으면서 필요한 TraceTemplate 템플릿 생성
  - 참고로 TraceTemplate를 처음부터 스프링 빈으로 등록하고 주입받아도 됨 (선택적인 부분)
  - template.execute(..., new TraceCallback() {...}) : 템플릿을 실행하면서 콜백 전달 (여기서는 콜백으로 익명 내부 클래스 사용)
    
7. OrderServiceV5
```java
package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceV5 {
    private final OrderRepositoryV5 orderRepository;
    private final TraceTemplate traceTemplate;

    public OrderServiceV5(OrderRepositoryV5 orderRepository, LogTrace trace) {
        this.orderRepository = orderRepository;
        this.traceTemplate = new TraceTemplate(trace);
    }

    public void orderItem(String itemId) {
        traceTemplate.execute("OrderService.orderItem()", () -> {
                orderRepository.save(itemId);
                return null;
            }
        );
    }
}
```
  - template.execute(..., new TraceCallback(){...}) : 템플릿을 실행하면서 콜백 전달. 여기서는 콜백으로 람다 전달

8. OrderRepositoryV5
```java
package hello.advanced.app.v5;

import hello.advanced.trace.callback.TraceTemplate;
import hello.advanced.trace.logtrace.LogTrace;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryV5 {
    private final TraceTemplate traceTemplate;

    public OrderRepositoryV5(LogTrace trace) {
        this.traceTemplate = new TraceTemplate(trace);
    }

    public void save(String itemId) {

        traceTemplate.execute("OrderRepository.save()", () -> {
            // 저장 로직
            if (itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생");
            }
            sleep(1000);
            return null;
        });
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

9. 실행 로그
```
[09f720d5] OrderController.request()
[09f720d5] |-->OrderService.orderItem()
[09f720d5] |   |-->OrderRepository.save()
[09f720d5] |   |<--OrderRepository.save() time = 1010ms
[09f720d5] |<--OrderService.orderItem() time = 1014ms
[09f720d5] OrderController.request() time = 1026ms
```

-----
### 정리
-----
1. 템플릿 메서드, 전략 패턴, 템플릿 콜백 패턴을 진행하면서 변하는 코드와 변하지 않는 코드를 분리
2. 최종적으로 템플릿 콜백 패턴을 적용하고 콜백으로 람다를 사용해 코드 사용 최소화
3. 한계
   - 현재까지 방식은 아무리 최적화를 해도 결국 로그 추적기를 적용하기 위해 원본 코드를 수정해야함
   - 클래스가 수백개이면 수백개를 더 힘들게 수정하는가, 조금 덜 힘들게 수정하는가의 차이가 있을 뿐, 본질적으로는 코드를 다 수정해야됨
  
