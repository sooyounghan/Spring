package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne (ManyToOne, OneToOne)
 * Order
 *   - Order -> Member (ManyToOne)
 *   - Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /**
     * V1 : 엔티티 직접 노출
     * - Hibernate5Module 모듈 등록, LAZY = null 처리
     * - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 현재 Loading 전략 : LAZY -> Proxy 객체 생성 후 저장 -> Member 객체에 접근할 때, DB에 접근하여 초기화
        // Proxy 기술 : class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor
        // JPA 입장에서 ByteBuddy에 대한 정보가 없으므로 예외 발생 -> HibernateModule 설치
        List<Order> all = orderRepository.findAllByString(new OrderSearch()); // Order <-> Member 무한 루프 (양방향 연결관계)

        for (Order order : all) {
            order.getMember().getName(); // LAZY 강제 초기화
            order.getDelivery().getAddress(); // LAZY 강제 초기화
        }
        return all;
    }

    /**
     * V2 : 엔티티를 조회해서 DTO 반환 (Fetch Join 미사용)
     *  - 단점 : 지연 로딩이므로 쿼리 N번 호출
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        List<SimpleOrderDto> result = orders.stream()
                                            .map(SimpleOrderDto::new)
                                            .collect(Collectors.toList());

        return result;
    }

    /**
     * V3 : 엔티티를 조회해서 DTO로 변환 (Fetch Join 사용)
     *  - Fetch Join으로 쿼리 1번 호출
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                                              .map(SimpleOrderDto::new)
                                              .collect(Collectors.toList());

        return result;
    }

    /**
     * V4 : JPA에서 DTO로 바로 조회
     *   - 쿼리 1번 조회
     *   - SELECT 절에서 원하는 데이터만 선택해서 조회
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate; // 주문 시간
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
