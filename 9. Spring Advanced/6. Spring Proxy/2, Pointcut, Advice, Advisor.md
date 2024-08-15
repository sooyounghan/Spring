-----
### 💡 포인트컷, 어드바이스, 어드바이저
-----
1. 포인트컷 (Pointcut)
   - 어디에 부가 기능을 적용할지, 어디에 부가 기능을 적용하지 않을지 판단하는 필터링 로직
   - 클래스와 메서드 이름으로 필터링
   - 이름 그대로 어떤 포인트(Point)에 기능을 적용할지, 하지 않을지 잘라서(cut) 구분

2. 어드바이스 (Advice)
   - 프록시가 호출하는 부가 기능
   - 단순하게 프록시 로직이라 생각하면 됨

3. 어드바이저 (Advisor)
   - 단순하게 하나의 포인트컷과 하나의 어드바이스를 가지고 있는 것
   - 포인트컷 1 + 어드바이스 1
  
4. 즉, 부가 기능 로직을 적용해야 하는데, 포인트 컷으로 어디에 적용할지 선택, 어드바이스로 어떤 로직을 적용할 지 선택, 어디에와 어떤 로직을 모두 알고 있는 것이 어드바이저
5. 조언(Advice)을 어디(Pointcut)에 할 것인가?
6. 조언자(Advisor)는 어디(Pointcut)에 조언(Advice)을 해야할지 알고 있음
7. 역할과 책임
   - 이렇게 구분한 것은 역할과 책임을 명확하게 분리한 것
   - 포인트 컷은 대상 여부를 확인하는 필터 역할만 담당
   - 어드바이스는 깔끔하게 부가 기능 로직만 담당
   - 둘을 합치면 어드바이저가 됨
   - 스프링의 어드바이저는 하나의 포인트컷 + 하나의 어드바이스로 구성

8. 전체 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/a57a7d15-2add-4d3a-9e60-8dead6b955e0">
</div>

-----
### 어드바이저 예제
-----
1. 어드바이저는 하나의 포인트 컷과 하나의 어드바이스를 제공
2. 프록시 팩토리를 통해 프록시를 생성할 떄, 어드바이저를 제공하면 어디에 어떤 기능을 제공할 지 알 수 있음
3. AdvisorTest
```java
package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class AdvisorTest {
    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        
        proxy.save();
        proxy.find();
    }
}
```
  - DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
    + Advisor 인터페이스의 가장 일반적인 구현체
    + 생성자를 통해 하나의 어드바이스를 넣어주면 됨
    + 어드바이저에는 하나의 포인트 컷과 하나의 어드바이스로 구성
    + 💡 Pointcut.TRUE : 항상 true를 반환하는 포인트 컷
    + new TimeAdvice() : TimeAdvice 어드바이스 제공
  - proxyFactory.addAdvisor(advisor);
    + 프록시 팩토리에 적용할 어드바이저를 지정
    + 어드바이저는 내부에 포인트컷과 어드바이스를 모두 가지고 있음
    + 💡 따라서 어디에 어떤 부가 기능을 적용해야 할지, 어드바이저 하나로 알 수 있음
    + 💡 프록시 팩토리를 사용할 때 어드바이저는 필수
  - 과거에는 proxyFactory.addAdvice(new TimeAdvice())로 어드바이스를 바로 적용했는데, 이는 편의 메서드
    + 결과적으로 해당 메서드 내부에서 DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice())의 어드바이저가 생성

<div align="center">
<img src="https://github.com/user-attachments/assets/f396bbc2-1619-4174-944f-89def9eb5282">
</div>

  - 실행 결과
```
# save() 호출
19:42:33.547 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeAdvice 실행
19:42:33.547 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
19:42:33.547 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료, resultTime = 0ms

@find() 호출
19:42:33.562 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeAdvice 실행
19:42:33.562 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
19:42:33.562 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료, resultTime = 0ms
```
  - save(), find() 각각 모두 어드바이스가 적용

-----
### 포인트컷 에제
-----
1. save() 메서드는 어드바이스 로직에 적용하지만, find() 메서드에는 어드바이스 로직을 적용하지 않도록 설정
2. 어드바이스에 로직을 추가해서 메서드 이름을 보고 코드를 실행할지 말지 분기를 타도 되지만, 이런 기능이 특화된 포인트컷 이용
3. Pointcut 관련 인터페이스 - 스프링 제공
```java
public interface Pointcut { 
      ClassFilter getClassFilter(); 
      MethodMatcher getMethodMatcher();
}

public interface ClassFilter { 
      boolean matches(Class<?> clazz);
}

public interface MethodMatcher {
      boolean matches(Method method, Class<?> targetClass); 
      //..
}
```
   - 포인트컷은 크게 ClassFilter와 MethodMatcher 둘로 이루어짐
   - 하나는 클래스가 맞는지, 하나는 메서드가 맞는지 확인할 때 사용
   - 둘 다 true를 반환해야 어드바이스 적용 가능

4. AdvisorTest - advisorTest2() 추가
```java
package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;

@Slf4j
public class AdvisorTest {
    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    @Test
    @DisplayName("직접 만든 Pointcut")
    void advisorTest2() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());
        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    static class MyPointcut implements Pointcut {

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    static class MyMethodMatcher implements MethodMatcher {

        private String matchName = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            boolean result = method.getName().equals(matchName);
            log.info("포인트 컷 호출 method = {}, target Class = {}", method.getName(), targetClass);
            log.info("포인트 컷 결과 = {}", result);
            return result;
        }

        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }
}
```
   - MyPointcut
     + 직접 구현한 포인트 컷
     + Pointcut 인터페이스 구현
     + 현재 메서드 기준으로 로직을 적용하면 됨
     + 클래스 필터는 항상 true를 반환, 메서드 비교 기능은 MyMethodMatcher를 사용

   - MyMethodMatcher
     + 직접 구현한 MethodMatcher
     + MethodMatcher 인터페이스를 구현
     + matches() : 이 메서드에 method, targetClass 정보가 넘어옴. 이 정보로 어드바이스를 적용할지, 적용하지 않을지 판단
     + 여기서는 메서드 이름이 save인 경우, true를 반환하도록 판단 로직 적용
     + 💡 isRuntime(), matches(... args) : isRuntime()의 값이 참이면 matches(... args) 메서드가 대신 호출
       * 동적으로 넘어오는 매개변수를 판단 로직으로 사용 가능
       * 💡 isRunitme()이 false인 경우 클래스의 정적 정보만 사용하므로, 스프링 내부에서 캐싱을 통해 성능 향상이 가능
       * 💡 그러나, isRuntime()이 true이면, 매개변수가 동적으로 변경된다고 가정하므로 캐싱하지 않음

   - DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());
     + 어드바이저에 직접 구현한 포인트컷 사용

```
# save() 호출
[Test worker] INFO hello.proxy.advisor.AdvisorTest -- 포인트 컷 호출 method = save, target Class = class hello.proxy.common.service.ServiceImpl
[Test worker] INFO hello.proxy.advisor.AdvisorTest -- 포인트 컷 결과 = true
[Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeAdvice 실행
[Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
[Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료, resultTime = 0ms

# find() 호출
[Test worker] INFO hello.proxy.advisor.AdvisorTest -- 포인트 컷 호출 method = find, target Class = class hello.proxy.common.service.ServiceImpl
[Test worker] INFO hello.proxy.advisor.AdvisorTest -- 포인트 컷 결과 = false
[Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
```
   - 실행 결과를 보면 save() 호출할 때는 어드바이스 적용
   - find()를 호출할 때는 어드바이스가 적용되지 않음

5. 그림 정리
<div align="center">
<img src="https://github.com/user-attachments/assets/d9dee173-eadd-4979-9dd2-8cbf04057717">
</div>

   - 클라이언트가 프록시의 save() 호출
   - 포인트컷에 Service 클래스의 save() 메서드에 어드바이스를 적용해도 될지 확인
   - 포인트컷이 true를 반환, 따라서 어드바이스를 호출해 부가 기능 적용
   - 이후 실제 인스턴스의 save() 호출

<div align="center">
<img src="https://github.com/user-attachments/assets/380bdf71-599a-42d9-aafa-f4a82d0b97ff">
</div>

   - 클라이언트가 프록시의 find() 호출
   - 포인트컷에 Service 클래스의 find() 메서드에 어드바이스를 적용해도 될지 확인
   - 포인트컷이 false 반환, 따라서 어드바이스를 호출하지 않고, 부가 기능도 적용하지 않음
   - 실제 인스턴스 호출

-----
### 스프링이 제공하는 포인트컷
-----
1. 스프링이 제공하는 NameMatchMethodPointcut 사용해서 구현
2. AdvisorTest - advisorTest3() 추가
```java
 @Test
 @DisplayName("스프링이 제공하는 Pointcut")
 void advisorTest3() {
     ServiceInterface target = new ServiceImpl();
     ProxyFactory proxyFactory = new ProxyFactory(target);

     NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
     pointcut.setMappedName("save"); // save인 경우에만 적용

     DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());
     proxyFactory.addAdvisor(advisor);

     ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

     proxy.save();
     proxy.find();
 }
```

3. NameMatchMethodPointcut 사용 코드
```java
NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
pointcut.setMappedName("save"); // save인 경우에만 적용
```
   - NameMatchMethodPointcut을 생성하고, setMappedName(...)으로 메서드 이름을 지정하면 포인트 컷

4. 실행 결과
```
# save() 호출
[Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeAdvice 실행
[Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
[Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료, resultTime = 0ms

# find() 호출
[Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
```
   - save() 호출 : 어드바이스 적용
   - find() 호출 : 어드바이스 미적용

5. 스프링이 제공하는 포인트컷 (대표적인 포인트컷)
   - NameMatchMethodPointcut : 메서드 이름을 기반으로 매칭. 내부에는 PatternMatchUtils를 사용
     + 예) ```*xxx*``` 허용
   - JdkRegexMethodPointcut : JDK 정규 표현식 기반으로 포인트컷 매칭
   - TruePointcut : 항상 참 반환
   - AnnotationMathcingPointcut : 애너테이션으로 매칭
   - AspectJExpressionPointcut : aspectJ 표현식으로 매칭

6. 💡💡💡 ASpectJExpressionPointcut
   - 실무에서 사용하기도 편리하고 기능도 가장 많음

-----
### 여러 어드바이저 함꼐 사용
-----
1. MultiAdvisorTest
```java
package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class MultiAdvisorTest {

    @Test
    @DisplayName("여러 프록시")
    void multiAdvisorTest1() {
        // client -> proxy2(advisor2) -> proxy1(advisor1) -> target

        // Proxy1 생성 (target -> proxy1)
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory1 = new ProxyFactory(target);
        DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
        proxyFactory1.addAdvisor(advisor1);
        ServiceInterface proxy1 = (ServiceInterface) proxyFactory1.getProxy();

        // Proxy2 생성 (proxy2 -> proxy1)
        ProxyFactory proxyFactory2 = new ProxyFactory(proxy1);
        DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());
        proxyFactory2.addAdvisor(advisor2);
        ServiceInterface proxy2 = (ServiceInterface) proxyFactory2.getProxy();

        // 실행
        proxy2.save();
        proxy2.find();
    }

    @Slf4j
    static class Advice1 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice1 호출");
            return invocation.proceed();
        }
    }

    @Slf4j
    static class Advice2 implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            log.info("advice2 호출");
            return invocation.proceed();
        }
    }
}
```

2. 동작 흐름
<div align="center">
<img src="https://github.com/user-attachments/assets/65ac1dfd-a6c3-4417-b384-46929f439130">
</div>

3. 실행 결과
```
19:34:44.312 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice2 -- advice2 호출
19:34:44.312 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice1 -- advice1 호출
19:34:44.312 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출

19:34:44.312 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice2 -- advice2 호출
19:34:44.312 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice1 -- advice1 호출
19:34:44.328 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
```

   - 포인트컷은 advisor1, advisor2 모두 항상 true를 반환하도록 설정했으므로, 둘 다 어드바이스 적용

4. 여러 프록시의 문제
   - 프록시를 2번 생성해야 되는 문제 존재
   - 만약, 적용해야 하는 어드바이저가 10개라면 10개의 프록시를 생성해야 함

-----
### 하나의 프록시, 여러 어드바이저
-----
1. 스프링은 이 문제를 해결하기 위해 하나의 프록시에 여러 어드바이저를 적용할 수 있도록 만들어둠
<div align="center">
<img src="https://github.com/user-attachments/assets/d915ad1b-3d22-41c1-a6a5-f75be26cb945">
</div>

2. MultiAdvisorTest - multiAdvisorTest2() 추가
```java
@Test
@DisplayName("하나의 프록시, 여러 어드바이저")
void multiAdvisorTest2() {
  // client -> proxy -> advisor2 -> advisor1 -> target
  DefaultPointcutAdvisor advisor1 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice1());
  DefaultPointcutAdvisor advisor2 = new DefaultPointcutAdvisor(Pointcut.TRUE, new Advice2());

  // Proxy 생성 
  ServiceInterface target = new ServiceImpl();
  ProxyFactory proxyFactory = new ProxyFactory(target);
  
  // 💡 proxy -> advisor2 -> advisor1 이므로 순서 주의
  proxyFactory.addAdvisor(advisor2);
  proxyFactory.addAdvisor(advisor1);
  ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();

  // 실행
  proxy.save();
  proxy.find();
}
```
   - 프록시 팩토리에 원하는 만큼 addAvisor()를 통해 어드바이저 등록
   - 💡 등록하는 순서대로 advisor가 호출 (여기서는 advisor2, advisor1 순서로 등록)

3. 실행 흐름
<div align="center">
<img src="https://github.com/user-attachments/assets/a01fc43c-d1c7-4d79-84f6-fc1e6077ebc4">
</div>

```
19:42:12.828 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice2 -- advice2 호출
19:42:12.844 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice1 -- advice1 호출
19:42:12.844 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출

19:42:12.844 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice2 -- advice2 호출
19:42:12.844 [Test worker] INFO hello.proxy.advisor.MultiAdvisorTest$Advice1 -- advice1 호출
19:42:12.844 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
```
   - advisor2, advisor1 순서대로 호출

4. 💡 여러 프록시를 사용할 때와 비교하면 결과는 같지만, 성능은 더 좋음
5. 💡💡💡 스프링 AOP 적용 수 만큼 프록시가 생성된다고 착각할 수 있지만, 스프링은 AOP를 적용할 때 최적화를 진행해서 하나의 프록시만 만들고, 하나의 프록시에 여러 어드바이저를 적용
6. 💡💡💡 즉, 하나의 target에 여러 AOP가 적용되어도, 스프링의 AOP는 target마다 하나의 프록시만 생성
