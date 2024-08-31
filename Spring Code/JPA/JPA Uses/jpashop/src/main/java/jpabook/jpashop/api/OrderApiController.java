package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * V1 : 엔티티 직접 노출
 *    - 엔티티가 변하면 API 스펙도 변함
 *    - 트랜잭션 안에서 지연 로딩 필요
 *    - 양방향 연관관계 문제
 *
 * V2 : 엔티티를 조회해서 DTO로 변환 (Fetch Join 사용 X)
 *    - 트랜잭션 안에서 지연 로딩 필요
 *
 * V3 : 엔티티를 조회해서 DTO로 변환 (Fetch Join 사용 O)
 *    - 페이징 시에는 N부분을 포기해야 함
 *    - 대신 Batch Fetch Size 옵션을 주면 N -> 1 쿼리로 변경 가능
 *
 * V4 : JPA에서 DTO로 바로 조회, 컬렉션 N 조회 (1 + N Query)
 *    - 페이징 가능
 *
 * V5 : JPA에서 DTO로 바로 조회, 컬렉션 1 조회 최적화 버전 (1 + 1 Query)
 *    - 페이징 가능
 *
 * V6 : JPA에서 DTO로 바로 조회, 플랫 데이터 (1 Query)
 *    - 페이징 불가능
 */

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * V1 : 엔티티 직접 노출
     *   - Hibernate5Module 모듈 등록, LAZY = null 처리
     *   - 양방향 관계 문제 발생 -> @JsonIgnore
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); // LAZY 강제 초기화
            order.getDelivery().getAddress(); // LAZY 강제 초기화

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName()); // LAZY 강제 초기화
        }

        return all;
    }

    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        return result;
    }

    // private final OrderQueryService orderQueryService;

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem();

        for (Order order : orders) {
            System.out.println("order ref = " + order + ", order.id = " + order.getId());
        }
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        return result;
        // return orderQueryService.ordersV3();
    }

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     *  - ToOne 관계만 우선 페치 조인으로 초지ㅓㄱ화
     *  - 컬렉션 관계는 hiberante.default_batch_size, @BatchSize로 최적화
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset, // 페이징 파라미터 : offset = 0
            @RequestParam(value = "limit", defaultValue = "100") int limit) { // 페이징 파라미터 : limit = 100
        // Fetch Join : Order - Member, Delivery 1개의 쿼리
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); // Fetch Join이므로 Paging 가능


        // Order - OrderItem 쿼리 : Order - OrderItem : 총 2개
        // OrderItem - Item 쿼리 : OrderItem 1개당 Item 2개
        // 총 6개의 쿼리
        // 즉 1 + 2 + 4 = 7 (1 + 6 = 1 + N (+ N))개 쿼리 발생
        List<OrderDto> result = orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());

        return result;
    }


    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(Collectors.groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress())
                , Collectors.mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount())
                , Collectors.toList())))
                .entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),  e.getKey().getAddress(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Data
    static class OrderDto {
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

    @Data
    static class OrderItemDto {
        private String itemName; // 상품명
        private int orderPrice; // 주문 가격
        private int count; // 주문 수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}