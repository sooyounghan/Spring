package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter @Setter
// 💡 @NoArgsConstructor(access = AccessLevel.PROTECTED) // protect OrderItem()과 동일
// 💡 access = AccessLevel.PRIVATE도 존재하지만, 엔티티 Proxy 사용으로 인해 PROTECTED만 거의 사용
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; // 주문 가격
    private int count; // 주문 수량

    protected OrderItem() { // 💡 생성자 protected 설정 : 외부에서 생성자 사용 불가하도록 설정
        // 💡 의도치 않는 엔티티 생성 방지 가능 (JPA 스펙)
    }

    /**
     * 생성 메서드
     */
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count); // 주문된 수량 만큼 재고 감소
        return orderItem;
    }

    // == 비즈니스 로직 == //
    public void cancel() {
        getItem().addStock(count); // 주문 수량만큼 재고 수량 원복
    }

    // == 조회 로직 == //

    /**
     * 주문 상품 전체 가격 조회
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount(); // 전체 가격 = 주문가격 * 주문수량
    }
}
