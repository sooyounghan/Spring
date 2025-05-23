-----
### 스프링 AOP 구현 6 - 어드바이스 종류
-----
1. 어드바이스 종류
  - @Around : 메서드 호출 전후에 수행하며, 가장 강력한 어드바이스
    + 조인 포인트 실행 여부, 반환 값 변환, 예외 변환 등 가능
  - @Before : 조인 포인트 실행 이전 실행
    + 💡 @Before는 joinPoint 실행 이전 코드 진행 후, 자동적으로 Join Point 실행
```java
@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(JoinPoint joinPoint) {
    // @Before는 joinPoint 실행 이전 코드 진행 후, 자동적으로 Target(Join Point) 실행
    log.info("[before] {}", joinPoint.getSignature());
}
```
```
[aop] [    Test worker] hello.aop.order.aop.AspectV6Advice       : [before] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
```

  - @AfterReturning : 조인 포인트가 정상 완료 후 실행
  - @AfterThrowing : 메서드가 예외를 던지는 경우 실행
  - @After : 조인 포인트가 정상 또는 예외에 관계 없이 실행 (finally)

2. 예제 - AspectV6Advice
```java
package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Slf4j
@Aspect
public class AspectV6Advice {
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // @Before
            log.info("[around][트랜잭션 시작] {}", joinPoint.getSignature());
            Object result = joinPoint.proceed();

            // @AfterReturning
            log.info("[around][트랜잭션 커밋] {}", joinPoint.getSignature());
            return result;
        } catch (Exception e) {
            // @AfterThrowing
            log.info("[around][트랜잭션 롤백] {}", joinPoint.getSignature());
            throw e;
        } finally {
            // @After
            log.info("[around][리소스 릴리즈] {}", joinPoint.getSignature());
        }
    }

    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(JoinPoint joinPoint) {
        // @Before는 joinPoint 실행 이전 코드 진행 후, 자동적으로 Join Point 실행
        log.info("[before] {}", joinPoint.getSignature());
    }

    @AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
    public void doReturn(JoinPoint joinPoint, Object result) {
        log.info("[return] {} return = {}", joinPoint.getSignature(), result); // result 값 변경 불가
    }

    @AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
    public void doThrowing(JoinPoint joinPoint, Exception ex) {
        log.info("[ex] {} message = {}", ex);
    }

    @After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(JoinPoint joinPoint) {
        log.info("[after] {}", joinPoint.getSignature());
    }
}
```
  - @Around를 제외한 나머지 어드바이스들은 @Around가 할 수 있는 일의 일부만 제공
  - @Around 어드바이스만 사용해도 필요한 기능 모두 수행 가능

-----
### 참고 정보 획득
-----
1. 모든 어드바이스는 org.aspectj.lang.JoinPoint를 첫 번째 파라미터에 사용할 수 있음(생략 가능)
2. 💡 단, @Around는 ProceedingJoinPoint를 사용해야 함
   - 참고로, ProceedingJoinPoint는 org.assertj.lang.JoinPoint의 하위 타입
3. JoinPoint 인터페이스의 주요 기능
   - getArgs() : 메서드 인수 반환
   - getThis() : 프록시 객체 반환
   - getTarget() : 대상 객체 반환
   - getSignature() : 조언되는 메서드에 대한 설명 반환
   - toString() : 조언되는 방법에 대한 유용한 설명 인쇄

4. ProceedingJoinPoint 인터페이스의 주요 기능
   - proceed() : 다음 어드바이스나 타겟 호출

5. 추가로 호출 시 전달한 매개변수를 파라미터를 통해서 전달 받을 수 있음

-----
### 어드바이스 종류
-----
1. @Before : 조인 포인트 실행 전
```java
@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(JoinPoint joinPoint) {
   log.info("[before] {}", joinPoint.getSignature());
}
```
  - 💡 @Around와 다르게 작업 흐름 변경할 수 없음
  - @Around는 ProceedingJoinPoint.proceed()를 호출해야 다음 대상 호출
    + 만약 호출하지 않으면, 다음 대상이 호출되지 않음
  - 반면 @Before는 ProceedingJoinPoint.proceed() 자체를 사용하지 않음
    + 메서드 종료 시 자동으로 다음 타겟 호출
    + 물론, 예외가 발생하면 다음 코드가 호출되지 않음

2. @AfterReturning : 메서드 실행이 정상적으로 반환될 때 실행
```java
@AfterReturning(value = "hello.aop.order.aop.Pointcuts.orderAndService()", returning = "result")
public void doReturn(JoinPoint joinPoint, Object result) { // result 객체 변경 불가
    log.info("[return] {} return = {}", joinPoint.getSignature(), result); 
}
```
  - 💡 returning 속성에 사용된 이름은 어드바이스 메서드 매개변수 이름과 일치해야함
  - returning절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행 (타입이 불일치하면, 호출 자체가 되지 않음)
    + 부모 타입을 지정하면 모든 자식 타입은 인정
  - @Around와 다르게 반환되는 객체를 변경할 수 없음
    + 반환 객체를 변경하려면 @Around를 사용해야 함
    + 참고로, 반환 객체를 조작할 수는 있음

3. @AfterThrowing : 메서드 실행이 예외를 던져서 종료될 때 실행
```java
@AfterThrowing(value = "hello.aop.order.aop.Pointcuts.orderAndService()", throwing = "ex")
public void doThrowing(JoinPoint joinPoint, Exception ex) {
    log.info("[ex] {} message = {}", ex);
}
```
  - 💡 throwing 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 함
  - throwing 절에 지정된 타입과 맞는 예외를 대상으로 실행
    + 부모 타입을 지정하면, 모든 자식 타입은 인정

4. @After
```java
@After(value = "hello.aop.order.aop.Pointcuts.orderAndService()")
public void doAfter(JoinPoint joinPoint) {
    log.info("[after] {}", joinPoint.getSignature());
}
```
  - 메서드 실행이 종료되면 실행 (= finally 생각)
  - 정상 및 예외 반환 조건 모두 처리
  - 일반적으로 리소스 해제하는 데 사용

5. @Around
```java
@Around("hello.aop.order.aop.Pointcuts.orderAndService()")
public Object doTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
    try {
        // @Before
        log.info("[around][트랜잭션 시작] {}", joinPoint.getSignature());
        Object result = joinPoint.proceed();

        // @AfterReturning
        log.info("[around][트랜잭션 커밋] {}", joinPoint.getSignature());
        return result;
    } catch (Exception e) {
        // @AfterThrowing
        log.info("[around][트랜잭션 롤백] {}", joinPoint.getSignature());
        throw e;
    } finally {
        // @After
        log.info("[around][리소스 릴리즈] {}", joinPoint.getSignature());
    }
}
```
  - 메서드의 실행의 주변에서 실행
  - 메서드 실행 전후에 작업 수행
  - 가장 강력한 어드바이스
    + 조인 포인트 실행 여부 선택, joinPoint.proceed() 호출 여부 선택
    + 전달 값 변환 : joinPoint.proceed(args[])
    + 반환 값 변환
    + 예외 변환
    + 트랜잭션 처럼 try ~ catch ~ finally 모두 들어가는 구문 처리 가능
  - 💡 어드바이스 첫 번쨰 파라미터는 ProceedingJoinPoint 사용해야 함
  - proceed()를 통해 대상 실행 및 여러 번 실행 가능 (재시도)

6. AopTest - 변경
```java
@Slf4j
@SpringBootTest
// @Import(AspectV1.class)
// @Import(AspectV2.class)
// @Import(AspectV3.class)
// @Import(AspectV4Pointcut.class)
// @Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class})
@Import(AspectV6Advice.class)
public class AopTest {

    ...

}
```
  - @Import({AspectV5Order.LogAspect.class, AspectV5Order.TxAspect.class}) 주석 처리
  - @Import(AspectV6Advice.class) 추가

7. 실행
```
[aop] [    Test worker] hello.aop.order.aop.AspectV6Advice       : [around][트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV6Advice       : [before] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.OrderService             : [orderService] 실행
[aop] [    Test worker] hello.aop.order.OrderRepository          : [orderRepository] 실행
[aop] [    Test worker] hello.aop.order.aop.AspectV6Advice       : [return] void hello.aop.order.OrderService.orderItem(String) return = null
[aop] [    Test worker] hello.aop.order.aop.AspectV6Advice       : [after] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV6Advice       : [around][트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)
[aop] [    Test worker] hello.aop.order.aop.AspectV6Advice       : [around][리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)
```

<div align="center">
<img src="https://github.com/user-attachments/assets/6b286832-12f6-4f80-a3dd-1e86bca22586">
</div>

8. 순서
   - 스프링 5.2.7 버전부터 동일한 @Aspect 안에서 동일한 조인포인트 우선순위 정함 
   - 💡 실행 순서 : @Around → @Before → @After → @AfterReturning → @AfterThrowing (위 조건)
   - 어드바이스가 적용되는 순서는 위와 같지만, 호출 순서와 리턴 순서는 반대
   - 물론, @Aspect 안 동일 종류의 어드바이스가 2개 있으면 순서가 보장되지 않음
     + 이 경우, @Aspect를 분리하고, @Order 적용

9. @Around 외 다른 어드바이스가 존재하는 이유
```java
@Around("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(ProceedingJoinPoint joinPoint) {
   log.info("[before] {}", joinPoint.getSignature());
}
```
  - 이 코드의 문제점은 타겟을 호출하지 않는 문제 존재 (부가 기능만 실행)
  - 하지만, 이 코드를 개발한 의도는 타겟 실행 전 로그를 출력하는 것
  - @Around는 항상 joinPoint.proceed()를 호출해야 함
  - 만약, 실수로 호출하지 않으면 타겟이 호출되지 않는 치명적인 버그 발생

```java
@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(JoinPoint joinPoint) {
   log.info("[before] {}", joinPoint.getSignature());
}
```
  - @Before는 joinPoint.proceed()를 호출하는 고민을 하지 않아도 됨
  - @Around가 가장 넓은 기능을 제공하는 것은 맞지만, 실수 가능성이 존재
  - 반면, @Before, @After 같은 어드바이스는 기능은 적지만 실수할 가능성이 낮고, 코드도 단순
  - 💡 가장 중요한 점은, 바로 이 코드를 작성한 의도가 명확하게 드러남 (@Before 애너테이션을 보면, 타겟 실행 전 한정해 어떤 일을 하는 코드인지 드러남)

10. 좋은 설계는 제약 존재
    - @Around만으로 충분히 좋지만, 제약을 둠으로써 실수를 미연에 방지
    - 즉, 일종의 가이드 역할을 함
    - 만약, @Around를 사용했으나, 중간에 누군가가 해당 코드를 수정해 호출하지 않으면 큰 문제 발생
    - 처음부터, @Before를 사용했다면 문제 자체가 발생하지 않음
    - 제약 덕분에 역할이 명확해짐
    - 다른 개발자도, 코드를 보고 고민해야 하는 범위가 줄어들고, 코드의 의도도 파악하기 쉬움
