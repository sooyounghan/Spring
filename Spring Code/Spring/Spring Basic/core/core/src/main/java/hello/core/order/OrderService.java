package hello.core.order;

public interface OrderService {
    Order createOrder(Long memberId, String itemName, int itemPrice); // 회원이 주문한 주문 제품에 대한 정보 반환
}
