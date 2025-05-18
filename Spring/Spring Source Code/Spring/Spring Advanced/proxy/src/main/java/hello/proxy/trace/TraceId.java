package hello.proxy.trace;

import java.util.UUID;

public class TraceId {

    private String id; // Trace ID
    private int level; // Trace 깊이

    // 생성자 (처음)
    public TraceId() {
        this.id = createId();
        this.level = 0;
    }

    // 다음, 이전 깊이에 대한 생성자
    private TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    // TraceId 생성
    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8); // UUID 앞의 8자리만 사용
    }

    // 다음 깊이 TraceId 생성
    public TraceId createNextId() {
        return new TraceId(id, level + 1);
    }

    // 이전 깊이 TraceId 생성
    public TraceId createPreviousId() {
        return new TraceId(id, level - 1);
    }

    // 첫 번째 레벨인지 확인 메서드
    public boolean isFirstLevel() {
        return level == 0;
    }

    // Getter
    public String getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }
}
