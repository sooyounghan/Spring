package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloTraceV1 {
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    // TraceStatus 반환 (시작) : 예) [796bccd9] OrderController.request()
    public TraceStatus begin(String message) {
        TraceId traceId = new TraceId();
        Long startTimeMs = System.currentTimeMillis();

        // 로그 출력
        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()),message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    // TraceStatus 정보를 파라미터로 실행 (종료) : 예) [796bccd9] OrderController.request() time=1016ms
    public void end(TraceStatus status) {
        complete(status, null);
    }

    // 정상적으로 끝나지 않고, 예외가 발생할 경우 : 예) [b7119f27] OrderController.request() time=11ms ex=java.lang.IllegalStateException: 예외 발생!
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }


    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();

        TraceId traceId = status.getTraceId();

        if(e == null) {
            log.info("[{}] {}{} time = {}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        }

        else {
            log.info("[{}] {}{} time = {}ms, ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, e.toString());
        }


    }

    /**
     * level = 0
     * level = 1 |-->
     * level = 2 |   |-->
     *
     * level = 2 |   |<x-
     * level = 1 |<x-|
     */
    private String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < level; i++) {
            sb.append((i == level - 1) ?  "|" + prefix : "|   ");
        }

        return sb.toString();
    }
}
