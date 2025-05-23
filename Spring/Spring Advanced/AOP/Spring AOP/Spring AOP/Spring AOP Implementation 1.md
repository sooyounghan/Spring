-----
### 스프링 AOP 구현 1
-----
1. 스프링 AOP를 구현하는 일반적인 방법은 @Aspect
2. AspectV1
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class AspectV1 {
    
    @Around("execution(* hello.aop.order..*(..)")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); // Join Point 시그니처
        return joinPoint.proceed();
    }
}
```

  - @Around 애너테이션 값인 execution(* hello.aop.order..*(..)) : 포인트컷
  - @Around 애너테이션의 메서드인 doLog : 어드바이스(Advice)
  - execution(* hello.aop.order..*(..))
    + hello.aop.order 패키지와 그 하위 패키지(..)를 지정하는 AspectJ 포인트컷 표현식
  - 따라서, OrderService, OrderRepository의 모든 메서드는 AOP 적용 대상
  - 참고로, 스프링은 프록시 방식의 AOP를 사용하므로 프록시를 통하는 메서드만 적용 대상

  - 스프링 AOP는 AspectJ 문법을 차용하고, 프록시 방식의 AOP를 제공 (즉, AspectJ를 직접 사용하는 것이 아님)
  - 스프링 AOP를 사용할 때는 @Aspect 애너테이션을 주로 사용하는데, 이 애너테이션도 AspectJ가 제공하는 애너테이션

  - @Aspect를 포함한 org.aspectj 패키지 관련 기능은 aspectjweaver.jar 라이브러리가 제공하는 기능
    + build.gradle에 spring-boot-starter-aop를 포함했는데, 이는 스프링의 AOP 관련 기능과 함께 aspectjweaver.jar도 함께 사용할 수 있도록 의존 관계 포함
    + 그런데, 스프링에서는 AsepctJ가 제공하는 애너테이션이나 관련 인터페이스만 사용하는 것이고, 실제 AspectJ가 제공하는 컴파일, 로드타임 위버 등 사용하는 것이 아님
    + 즉, 스프링에서는 프록시 방식의 AOP만을 사용

3. AopTest
```java
package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.order.aop.AspectV1;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@Import(AspectV1.class)
public class AopTest {

    @Autowired OrderRepository orderRepository;
    @Autowired OrderService orderService;

    @Test
    void aopInfo() {
        log.info("isAopProxy, orderService = {}", AopUtils.isAopProxy(orderService));
        log.info("isAopProxy, orderRepository = {}", AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
    }

    @Test
    void exception() {
        Assertions.assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf(IllegalStateException.class);
    }
}
```
  - @Aspect는 에스펙트라는 표식이지 컴포넌트 스캔이 되는 것이 아님
  - 따라서, AsepctV1을 AOP로 사용하려면 스프링 빈에 등록해야 함
  - 스프링 빈에 등록하는 방법
    + @Bean 사용 : 직접 등록
    + @Component : 컴포넌트 스캔을 사용해 자동 등록
    + @Import : 주로 설정파일을 추가할 때 사용 (@Configuration)
    + @Import는 주로 설정 파일을 추가할 때 사용하지만, 이 기능으로 스프링 빈도 등록 가능
    + 테스트에서는 버전을 올려가면서 변경할 예정이어서 간단하게 @Import 기능 사용 예정
  - AopTest에 @Import(AspectV1.class)로 스프링 빈 추가
  - AopUtils.isAopProxy(...)도 프록시가 적용되었으므로 true 반환
```
[aop] [    Test worker] hello.aop.order.aop.AspectV1             : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV1             : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderService = true
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderRepository = true
```

  - 실행 - sucess()
```
[aop] [    Test worker] hello.aop.order.aop.AspectV1             : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV1             : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
```

<div align="center">
<img src="https://github.com/user-attachments/assets/00f59ffc-6df2-488c-b700-402da15b1658">
</div>

-----
### 스프링 AOP 구현2 - 포인트컷 분리
-----
1. @Around에 포인트컷 표현식을 직접 넣을 수 있지만, @Pointcut 애너테이션을 사용해 별도로 분리 가능
2. AspectV2
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV2 {

    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))") // Pointcut Expression
    private void allOrder() {} // Pointcut Signature

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); // Join Point 시그니쳐
        return joinPoint.proceed();
    }
}
```

3. 💡 @Pointcut
   - @Pointcut에 포인트컷 표현식 사용
   - 메서드 이름과 파라미터를 합쳐서 포인트컷 시그니처(Signature)
   - 메서드의 반환 타입은 void
   - 코드 내용은 비워둠
   - 위의 포인트컷 시그니처는 allOrder()
     + 이름 그대로 주문과 관련된 모든 기능을 대상으로 하는 포인트컷
   - @Around 어드바이스에서는 포인트컷을 직접 지정해도 되지만, 포인트컷 시그니처를 사용해도 됨
   - 여기서는 @Around("allOrder()") 사용
   - private, public 같은 접근 제어자는 내부에서만 사용하면 private를 사용해도 되지만, 다른 에스펙트에서 참고하면 public 사용

4. 결과적으로 AspectV1과 같은 기능 수행하나, 이렇게 분리하면 하나의 포인트컷 표현식을 여러 어드바이스에 함께 사용 가능
5. 또한, 다른 클래스에 있는 외부 어드바이스에서도 포인트컷을 함께 사용 가능
6. AopTest 수정
```java
@Slf4j
@SpringBootTest
// @Import(AspectV1.class)
@Import(AspectV2.class)
public class AopTest {

    ...

}
```
  - @Import(AspectV1.class) 주석 처리
  - @Import(AspectV2.class) 추가
  - 실행
```
[aop] [    Test worker] hello.aop.order.aop.AspectV2             : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV2             : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderService = true
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderRepository = true

[aop] [    Test worker] hello.aop.order.aop.AspectV2             : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV2             : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
```

-----
### 스프링 AOP 구현3 - 어드바이스 추가
-----
1. 앞서 로그를 출력하는 기능에 추가로 트랜잭션 적용하는 코드 추가
2. 트랜잭션 기능은 다음과 같이 동작
   - 핵심 로직 실행 직전 트랜잭션 시작
   - 핵심 로직 실행
   - 핵심 로직 실행에 문제가 없으면 커밋
   - 핵심 로직 실행에 예외가 발생하면 롤백

3. AspectV3
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV3 {

    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))") // Pointcut Expression
    private void allOrder() {} // Pointcut Signature

    // 클래스 이름 패턴이 *Service
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService() {}

    @Around("allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); // Join Point 시그니쳐
        return joinPoint.proceed();
    }

    // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
    @Around("allOrder() && allService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}
```
  - allOrder() 포인트컷은 hello.aop.order 패키지와 하위 패키지를 대상으로 함
  - allService() 포인트컷은 타입 이름 패턴이 *Service를 대상으로 함
    + 쉽게 이야기해서, XxxService처럼 Service로 끝나는 것을 대상 (```*Servi*```과 같은 패턴도 가능)
  - 💡 여기서 타입 이름 패턴이라 한 이유는, 클래스 / 인터페이스 모두 적용됨

  - @Around("allOrder() && allService()")
    + 포인트컷은 조합 가능 (&&(AND), ||(OR), !(NOT) 3가지 조합 가능)
    + hello.aop.order 패키지와 하위 패키지이면서 타입 이름 패턴이 *Service인 것을 대상으로 함
    + 결과적으로 doTransaction() 어드바이스는 OrderService에만 적용
    + doLog() 어드바이스는 OrderService, OrderRepository에 모두 적용

4. 포인트컷이 적용된 AOP 결과
   - orderService : doLog(), doTransaction() 어드바이스 적용
   - orderRepository : doLog() 어드바이스 적용

5. AopTest 수정
```java
@Slf4j
@SpringBootTest
// @Import(AspectV1.class)
// @Import(AspectV2.class)
@Import(AspectV3.class)
public class AopTest {

    ...

}
```
  - @Import(AspectV2.class) 주석 처리
  - @Import(AspectV3.class) 추가
  - 실행
```java
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderService = true
[aop] [    Test worker] hello.aop.AopTest                        : isAopProxy, orderRepository = true
```
  - 실행 - success()
```
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)
```
<div align="center">
<img src="https://github.com/user-attachments/assets/09ec6c69-6fb0-46f6-a4a1-8f6d17710eaa">
</div>

6. 진행 순서 분석
   - AOP 적용 전 : 클라이언트 → orderService.orderItem() → orderRepository.save()
   - AOP 적용 후 : 클라이언트 → [doLog() → doTransaction()] → orderService.orderItem() → [doLog()] → orderRepository.save()
   - orderService에는 doLog(), doTransaction() 두 가지 어드바이스가 적용, orderRepository에는 doLog() 하나의 어드바이스만 적용

7. 실행 - exception()
```
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [트랜잭션 롤백] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV3             : [리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)
```
  - 예외 상황에서는 트랜잭션 커밋 대신 트랜잭션 롤백 호출

8. 로그를 남기는 순서가 [doLog() → doTransaction()] 순서로 작동
   - 만약 어드바이스가 적용되는 순서를 변경하고 싶다면?
   - 예를 들어, 실행 시간을 측정해야 하는데, 트랜잭션과 관련된 시간을 제외하고 싶다면, [doTransaction() → doLog()] 이렇게 트랜잭션 이후 로그를 남겨야 함

-----
### 스프링 AOP 구현4 - 포인트컷 참조
-----
1. 포인트컷을 공용으로 사용하기 위해 외부 클래스에 모아두어도 됨
2. 참고로, 외부에서 호출할 때는 포인트컷의 접근 제어자를 public으로 열어둬야 함
3. Pointcuts
```java
package hello.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {

    // hello.aop.order 패키지와 하위 패키지
    @Pointcut("execution(* hello.aop.order..*(..))") // Pointcut Expression
    public void allOrder() {} // Pointcut Signature

    // 클래스 이름 패턴이 *Service
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService() {}

    // allOrder && allService
    @Pointcut("allOrder() && allService()")
    public void orderAndService() {}
}
```
   - orderAndService() : allOrder() 포인트컷과 allService() 포인트컷을 조합해 새로운 포인트컷을 만듬

4. AspectV4Pointcut
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class AspectV4Pointcut {

    @Around("hello.aop.order.aop.Pointcuts.allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature()); // Join Point 시그니쳐
        return joinPoint.proceed();
    }

    // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();
            log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }
}
```
   - 사용하는 방법은 패키지명을 포함한 클래스 이름과 포인트컷 시그니처를 모두 지정
   - 포인트컷을 여러 어드바이스에서 함께 사용할 때 효과적

5. AopTest - 수정
```java
@Slf4j
@SpringBootTest
// @Import(AspectV1.class)
// @Import(AspectV2.class)
// @Import(AspectV3.class)
@Import(AspectV4Pointcut.class)
public class AopTest {

        ...

}
```
   - @Import(AspectV3.class) 주석 처리
   - @Import(AspectV4Pointcut.class) 추가

-----
### 스프링 AOP 구현5 - 어드바이스 순서
-----
1. 어드바이스는 기본적으로 순서를 보장하지 않음
2. 💡 순서를 지정하고 싶으면 @Aspect 적용 단위로 org.springframework.core.annotation.@Order 애너테이션 적용
   - 문제는 어드바이스 단위가 아니라 클래스 단위로 적용할 수 있음
   - 따라서, 하나의 에스펙트에 여러 어드바이스가 있다면, 순서를 보장 받을 수 없음
3. 따라서, 에스펙트를 별도의 클래스로 분리해야 함
4. 현재 로그를 남기는 순서 : [doLog() → doTransaction()]이 순서로 남음 (JVM이나 실행 환경에 따라 달라질 수 있음)
   - 로그를 남기는 순서를 바꾸어서 [doTransaction() → doLog()] 트랜잭션이 먼저 처리되고, 이후 로그가 남기도록 변경

7. AspectV5Order
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

@Slf4j
@Aspect
public class AspectV5Order {

    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
            log.info("[log] {}", joinPoint.getSignature()); // Join Point 시그니쳐
            return joinPoint.proceed();
        }
    }

    @Aspect
    @Order(1)
    public static class TxAspect {
        // hello.aop.order 패키지와 하위 패키지 이면서 클래스 이름 패턴이 *Service
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
            try {
                log.info("[트랜잭션 시작] {}", joinPoint.getSignature());
                Object result = joinPoint.proceed();
                log.info("[트랜잭션 커밋] {}", joinPoint.getSignature());
                return result;
            } catch (Exception e) {
                log.info("[트랜잭션 롤백] {}", joinPoint.getSignature());
                throw e;
            } finally {
                log.info("[리소스 릴리즈] {}", joinPoint.getSignature());
            }
        }
    }
}
```
   - 하나의 에스펙트 안에 있던 어드바이스를 LogAspect, TxAspect 에스펙트로 각각 분리
   - 각 에스펙트에 @Order 애너테이션을 통해 실행 순서 적용
   - 참고로, 숫자가 작을 수록 먼저 실행

8. AopTest - 변경
```java
@Slf4j
@SpringBootTest
// @Import(AspectV1.class)
// @Import(AspectV2.class)
// @Import(AspectV3.class)
// @Import(AspectV4Pointcut.class)
@Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class})
public class AopTest {

    ...

}
```
   - @Import(AspectV4Pointcut.class) 주석 처리
   - @Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class}) 추가

9. 실행
```
[aop] [    Test worker] hello.aop.order.aop.AspectV5Order        : [트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV5Order        : [log] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV5Order        : [log] String hello.aop.order.OrderRepository.save(String)
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV5Order        : [트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV5Order        : [리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)
```
<div align="center">
<img src="https://github.com/user-attachments/assets/142d8b39-a2f4-42e5-8e36-92f0ddc8efa2">
</div>
