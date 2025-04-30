package hello.order.v1;

import hello.order.OrderService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OrderServiceV1 implements OrderService {

    private final MeterRegistry registry;
    private AtomicInteger stock = new AtomicInteger(100);

    public OrderServiceV1(MeterRegistry registry) {
        this.registry = registry;
    }

    /**
     * 💡 my.order 메트릭 : 주문 수(ordr) + 취소 수(cancel) 같이 존재
     * - 💡 태그를 통해 order면, 해당 카운터 값 (주문 수) 증가
     * - 💡 태그를 통해 cancel이면, 해당 카운터 값 (취소 수) 증가
     * - 💡 결과적으로, 주문 수와 취소 수를 태그 없이 전체 my.order로 조회하면 주문 수 + 취소 수 = 전체 물량
     * - 💡 tag 필터를 통해 조회하면 각 주문 수와 취소 수
     */
    @Override
    public void order() {
        log.info("주문");
        stock.decrementAndGet();

        Counter counter = Counter.builder("my.order") // 메트릭 이름
                .tag("class", this.getClass().getName()) // 클래스 이름을 tag로 넣음
                .tag("method", "order") // 메서드 이름을 tag로 넣음
                .description("order") // 설명
                .register(registry);

        counter.increment(); // 해당 메트릭 값 1 증가
    }

    @Override
    public void cancel() {
        log.info("취소");
        stock.incrementAndGet();

        Counter.builder("my.order") // tag로 구분 가능하므로 메트릭은 동일하게 설정 가능
                .tag("class", this.getClass().getName())
                .tag("method", "cancel")
                .description("cancel")
                .register(registry)
                .increment(); // 메서드 체이닝 가능
    }

    @Override
    public AtomicInteger getStock() {
        return stock;
    }
}
