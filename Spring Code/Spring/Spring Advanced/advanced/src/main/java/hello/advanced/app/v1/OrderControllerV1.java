package hello.advanced.app.v1;

import hello.advanced.trace.TraceStatus;
import hello.advanced.trace.hellotrace.HelloTraceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderControllerV1 {
    private final OrderServiceV1 orderService;
    private final HelloTraceV1 trace;

    @GetMapping("/v1/request")
    public String request(String itemId) {

        TraceStatus status = null; // try문 안의 status는 try문 안에서만 유효하므로, null로 초기화
        try {
            status = trace.begin("OrderController.request()"); // Trace 시작할 때, 발생할 수 있는 예외가 있으므로 포함
            orderService.orderItem(itemId); // ex 입력 시, 발생하는 예외를 위한 try-catch
            trace.end(status);
            return "OK";
        } catch (Exception e) {
            trace.exception(status, e);
            throw e; // 예외를 꼭 다시 던져줘야 함 (예외가 터지면, 예외가 나가도록 해줘야 함) (Trace는 애플리케이션 로직에 영향을 미치면 안 됨)
        }
    }
}
