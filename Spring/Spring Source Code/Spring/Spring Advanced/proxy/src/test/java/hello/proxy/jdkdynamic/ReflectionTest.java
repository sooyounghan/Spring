package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {
    @Test
    void reflection0() {
        Hello target = new Hello();

        // 공통 로직 1 시작
        log.info("start");
        String result1 = target.callA(); // 호출하는 메서드만 다름
        log.info("result = {}", result1);
        // 공통 로직 1 종료

        // 공통 로직 2 시작
        log.info("start");
        String result2 = target.callB(); // 호출하는 메서드만 다름
        log.info("result = {}", result2);
        // 공통 로직 2 종료

    }

    @Test
    void reflection1() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 클래스 메타 정보를 획득
        Class<?> classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드 메타 정보 획득
        Method methodCallA = classHello.getMethod("callA");

        // 획득한 메서드 메타 정보를 통해 실제 인스턴스(target) 메서드 호출
        Object result1 = methodCallA.invoke(target);

        log.info("result1 = {}", result1);


        // callB 메서드 메타 정보 획득
        Method methodCallB = classHello.getMethod("callB");

        // 획득한 메서드 메타 정보를 통해 실제 인스턴스(target) 메서드 호출
        Object result2 = methodCallB.invoke(target);

        log.info("result2 = {}", result2);
    }

    @Test
    void reflectionTest2() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 클래스 메타 정보 획득
        Class<?> classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        // callA 메서드
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        // callB 메서드
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    private void dynamicCall(Method method, Object target) throws InvocationTargetException, IllegalAccessException {
        log.info("start");

        Object result = method.invoke(target);

        log.info("result = {}", result);
    }

    @Slf4j
    static class Hello {
        public String callA() {
            log.info("callA");
            return "A";
        }

        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}
