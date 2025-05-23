-----
### OSIV(Open Session In View)와 성능 최적화
-----
1. Open Session In View : Hibernate
2. Open EntityManager In View : JPA
3. 관례상 OSIV라고 함

-----
### OSIV ON
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/1ed7681a-7f66-4e8a-be3a-94da73feb430">
</div>

1. spring.jpa.open-in-view: true (기본값)
```
[jpashop] [  restartedMain] JpaBaseConfiguration$JpaWebConfiguration : spring.jpa.open-in-view is enabled by default. Therefore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-in-view to disable this warning
```

2. 이 기본값을 뿌리면서 애플리케이션 시작 시점에 warn 로그를 남기는 것은 이유가 있음
3. 💡 OSIV 전략은 트랜잭션 시작처럼(최초 데이터베이스 커넥션 시작 시점부터) API 응답이 끝날 때 까지 영속성 컨테스트와 데이터베이스 커넥션 유지
  - 따라서, 지금까지 View Template이나 API 컨트롤러에 지연 로딩이 가능했던 것
  - 💡 지연 로딩은 영속성 컨텍스트가 살아있어야 가능하고, 영속성 컨텍스트는 기본적으로 데이터베이스 커넥션을 유지
  - 즉, 이러한 점 자체가 큰 장점임

4. 그런데, 이 전략은 너무 오랜시간 동안 데이터베이스 커넥션 리소스를 사용하므로, 실시간 트래픽이 중요한 애플리케이션에서는 커넥션이 모자를 수 있고, 이는 장애로 이어짐 (치명적인 단점)
5. 예를 들어 컨트롤러에서 외부 API를 호출하면 외부 API 대기 시간 만큼 커넥션 리소스를 반환하지 못하고, 유지해야 함

-----
### OSIV OFF
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/8c1707db-f1b7-43db-b8a9-89d85fdc718c">
</div>

1. spring.jpa.open-in-view: false (OSIV 종료)
2. OSIV를 끄면, 트랜잭션을 종료할 때 영속성 컨텍스트를 닫고, 데이터베이스 커넥션도 반환
3. 따라서, 커넥션 리소스를 낭비하지 않음
4. 💡 OSIV를 끄면, 모든 지연 로딩을 트랜잭션 안에서 처리해야 함
   - 따라서, 지금까지 작성한 많은 지연 로딩 코드를 트랜잭션 안으로 넣어야 하는 단점 존재
   - 그리고 View Template에 지연로딩이 작동하지 않음
   - 결론적으로, 트랜잭션이 끝나기 전에 지연 로딩을 강제로 호출해둬야 함

-----
### 커멘드와 쿼리 분리
-----
1. 실무에서 OSIV를 끈 상태로 복잡성을 관리하는 좋은 방법이 존재 : Command와 Query를 분리하는 것
   - 참고 : https://en.wikipedia.org/wiki/Command–query_separation

2. 보통 비즈니스 로직은 특정 엔티티 몇 개를 등록하거나 수정하는 것이므로 성능이 크게 문제가 되지 않음
3. 그런데, 복잡한 화면을 출력하기 위한 쿼리는 화면에 맞추어 성능을 최적화하는 것이 중요
4. 하지만, 그 복잡성에 비해 핵심 비즈니스에 큰 영향을 주는 것은 아님
5. 그래서 크고 복잡한 애플리케이션을 개발한다면, 이 둘의 관심사를 명확하게 분리하는 선택은 유지보수 관점에서 충분히 의미가 있음
6. 즉, 다음처럼 분리하는 것
  - OrderService
    + OrderService : 핵심 비즈니스 로직
    + OrderQueryService : 화면에서 API에 맞춘 서비스 (주로 읽기 전용 트랜잭션 사용)

7. 보통 서비스 계층에서 트랜잭션을 유지
8. 두 서비스 모두 트랜잭션을 유지하면서 지연 로딩 사용 가능
9. 되도록이면, 고객 서버의 실시간 API는 OSIV를 끄고, ADMIN처럼 커넥션을 많이 사용하지 않는 곳은 OSIV 작동

10. 예시
  - OrderApiController
```java
private final OrderQueryService orderQueryService;

...

@GetMapping("/api/v3/orders")
public List<OrderDto> ordersV3() {
    return orderQueryService.ordersV3();
}
```

  - OrderQueryService
```java
package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        return result;
    }
}
```

  - OrderDto
```java
package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderDto {
    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
        orderId = order.getId();
        name = order.getMember().getName();
        orderDate = order.getOrderDate();
        orderStatus = order.getStatus();
        address = order.getDelivery().getAddress();

        // order.getOrderItems().forEach(o -> o.getItem().getName());
        // orderItems = order.getOrderItems(); // Entity -> 초기화를 통해 풀어줌

        orderItems = order.getOrderItems().stream()
                .map(OrderItemDto::new)
                .collect(Collectors.toList());
    }
}
```

  - OrderItemDto
```java
package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemDto {
    private String itemName; // 상품명
    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

    public OrderItemDto(OrderItem orderItem) {
        itemName = orderItem.getItem().getName();
        orderPrice = orderItem.getOrderPrice();
        count = orderItem.getCount();
    }
}
```
