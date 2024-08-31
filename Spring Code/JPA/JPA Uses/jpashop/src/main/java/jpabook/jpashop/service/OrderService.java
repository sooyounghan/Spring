package jpabook.jpashop.service;

import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Member member = memberRepository.findById(memberId).get();
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성 (💡 현재 코드에서 영속화 미실시)
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성 (💡 현재 코드에서 영속화 미실시)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // OrderItem orderItem = new OrderItem(); // 💡 생성 불가 (생성자 protected), 외부로부터 무분별한 엔티티 생성 제한

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        // Order order = new Order(); // 💡 생성 불가 (생성자 protected), 외부로부터 무분별한 엔티티 생성 제한

        // 주문 저장
        // 💡 Delivery, OrderItem은 cascade = CascadeType = ALL (Delivery와 OrderItem은 Order에서만 사용됨. Order는 Private Owner, cascade로 영속성 전이해도 무방)
        // 💡 따라서, order를 영속화하고 DB에 저장하면, 이와 관련된 orderItem / delivery도 영속성 전이로 영속화 되면서, Commit 시점에 DB에 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        // 기존 SQL : 객체와 관련된 값을 모두 가져와서, SQL 쿼리 작성 후 실행
        // 💡 JPA : 엔티티 내 비즈니스 로직을 통해 엔티티 내 비즈니스 로직에 접근해 데이터가 변경되면, Dirty-Checking되어 변경된 감지하여 변경 내역에 대한 UPDATE SQL 실행
        order.cancel();
    }

    /**
     * 주문 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
         return orderRepository.findAll(orderSearch);
    }
}
