package hello.order.gauge;

import hello.order.OrderService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class StockConfigV1 {

    @Bean
    public MyStockMetric myStockMetric(OrderService orderService, MeterRegistry registry) {
        return new MyStockMetric(orderService, registry);
    }

    static class MyStockMetric {
        private OrderService orderService;
        private MeterRegistry registry;

        public MyStockMetric(OrderService orderService, MeterRegistry registry) {
            this.orderService = orderService;
            this.registry = registry;
        }

        @PostConstruct
        public void init() {
            Gauge.builder("my.stock", orderService, service -> { // 게이지 생성 (orderService 파라미터 -> Lambda 이용)
                log.info("stock gauge call");
                return service.getStock().get(); // OrderService의 현재 Stock 값을 꺼내서 반환
            }).register(registry); // 메트릭 등록
        }
    }
}
