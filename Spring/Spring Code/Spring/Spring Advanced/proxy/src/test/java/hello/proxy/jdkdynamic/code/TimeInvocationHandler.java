package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class TimeInvocationHandler implements InvocationHandler {
    private final Object target; // Proxy는 항상 호출할 실제 객체 존재

    public TimeInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = method.invoke(target, args);// 매개변수 까지 처리

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 resultTime = {}", resultTime);
        return result;
    }
}
