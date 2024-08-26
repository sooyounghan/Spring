package hello.proxy.pureproxy.proxy.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheProxy implements Subject {

    private Subject target; // 호출해야되는 대상 : 실제 객체
    private String cacheValue; // 캐시하는 데이터

    public CacheProxy(Subject target) {
        this.target = target;
    }

    @Override
    public String operation() {
        log.info("프록시 호출");

        if(cacheValue == null) {
            cacheValue = target.operation(); // 실제 객체 반환 값을 캐시 데이터에 저장
        }
        return cacheValue; // 캐시 데이터 반환
    }
}
