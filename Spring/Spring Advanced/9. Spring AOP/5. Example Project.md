-----
### 예제 프로젝트
-----
1. OrderRepository
```java
package hello.aop.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class OrderRepository {
    
    public String save(String itemId) {
        log.info("[orderRepository] 실행");
        
        // 저장 로직
        if(itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생");
        }
        
        return "OK";
    }
}
```

2. OrderService
```java
package hello.aop.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    public void orderItem(String itemId) {
        log.info("[orderService] 실행");
        orderRepository.save(itemId);
    }
}
```

3. AopTest
```java
package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class AopTest {

    @Autowired OrderRepository orderRepository;
    @Autowired OrderService orderService;

    @Test
    void aopInfo() {
        log.info("isAopProxy, orderService = {}", AopUtils.isAopProxy(orderService));
        log.info("isAopProxy, orderRepository = {}", AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
    }

    @Test
    void exception() {
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf(IllegalStateException.class);
    }
}
```

  - AopUtils.isAopProxy(...)를 통해 AOP 프록시 적용 확인 가능
    + 현재 AOP 관련 코드를 작성하지 않았으므로 프록시가 적용되지 않고, 결과도 false 반환해야 정상
```
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderService = false
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderRepository = false
```

  - 여기서는 실제 결과 검증 테스트가 아니라 학습 테스트 진행
  - 실행 - success()
```
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
```

<div align="center">
<img src="https://github.com/user-attachments/assets/4d3a6320-8a7a-4d5a-a9a5-513a2aa2ad39">
</div>
