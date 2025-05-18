-----
### CGLIB : Code Generator LIBrary
-----
1. 바이트코드를 조작해서 동적으로 클래스를 생성하는 기술을 제공하는 라이브러리
2. 💡 인터페이스가 없어도 구체 클래스만 가지고 동적 프록시 만들어낼 수 있음
3. 원래는 외부 라이브러리이나, 스프링 프레임워크가 스프링 내부 소스 코드에 포함
   - 스프링을 사용한다면, 별도의 외부 라이브러리 추가 없이 사용 가능
4. 직접 CGLIB를 사용하는 경우가 별로 없고, 스프링의 ProxyFactory라는 것이 이 기술을 편리하게 사용하는 것을 도와줌

-----
### 공통 예제 코드
-----
1. 인터페이스의 구현이 있는 서비스 클래스 - ServiceInterface, ServiceImpl
  - ServiceInterface (테스트 코드(src/test)에 위치)
```java
package hello.proxy.common.service;

public interface ServiceInterface {
    void save();
    void find();
}
```
  
  - ServiceImpl (테스트 코드(src/test)에 위치)
```java
package hello.proxy.common.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceImpl implements ServiceInterface {
    @Override
    public void save() {
        log.info("save 호출");  
    }

    @Override
    public void find() {   
        log.info("find 호출");
    }
}
```

2. 구체 클래스만 있는 서비스 클래스 - ConcreteService (테스트 코드(src/test)에 위치)
```java
package hello.proxy.common.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcreteService {
    public void call() {
        log.info("ConcreteService 호출");
    }
}
```

-----
### CGLIB 코드
-----
1. JDK 동적 프록시에서 실행 로직을 위해 InvocationHandler를 제공했듯이, CGLIB에서는 MethodInterceptor를 제공
2. MethodInterceptor - CGLIB 제공
```java
package org.springframework.cglib.proxy;

import java.lang.reflect.Method;

public interface MethodInterceptor extends Callback {
    Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;
}
```
  - Object obj : CGLIB가 적용된 객체
  - Method method : 호출된 메서드
  - Obejct[] args : 메서드를 호출하면서 전달된 인수
  - MethodProxy proxy : 메서드 호출에 사용

3. TimeMethodInterceptor (테스트 코드(src/test)에 위치)
```java
package hello.proxy.common.cglib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class TimeMethodInterceptor implements MethodInterceptor {
    private final Object target;

    public TimeMethodInterceptor(Object target) {
        this.target = target;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("TimeProxy 실행");
        long startTime = System.currentTimeMillis();

        Object result = methodProxy.invoke(target, args);

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("TimeProxy 종료 resultTime = {}", resultTime);
        return result;
    }
}
```
  - TimeMethodInterceptor는 MethodInterceptor 인터페이스를 구현해서 CGLIB 프록시의 실행 로직을 정의
  - JDK 동적 프록시를 설명할 때 같은 코드
  - Object target : 프록시가 호출할 실제 대상
  - methodProxy.invoke(target, args) : 실제 대상을 동적으로 호출
    + 참고로, method를 사용해도 되지만, CGLIB는 성능상 MethodProxy methodProxy 사용하는 것 권장

4. CglibTest
```java
package hello.proxy.common.cglib;

import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {

    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ConcreteService.class);
        enhancer.setCallback(new TimeMethodInterceptor(target));

        ConcreteService proxy = (ConcreteService) enhancer.create();

        log.info("target Class = {}", target.getClass());
        log.info("proxy Class = {}", proxy.getClass());

        proxy.call();
    }
}
```
  - ConcreteService는 인터페이스가 없는 구체 클래스이므로, 여기에 CGLIB를 사용해 프록시 생성
  - Enhancer : CGLIB는 Enhancer를 사용해 프록시 생성
  - enhancer.setSuperclass(ConcreteService.class) : CGLIB는 구체 클래스를 상속 받아서 프록시 생성 가능
    + 어떤 구체 클래스를 상속 받을지 지정
  - enhancer.setCallback(new TimeMethodInterceptor(target)) : 프록시에 적용할 실행 로직 할당
  - Object enhancer.create() : 프록시를 생성
    + 앞서 설정한 enhancer.setSuperclass(ConcreteService.class)에서 지정한 클래스를 상속받아 프록시가 만들어짐
    + 따라서, Type-Casting 가능

5. JDK 동적 프록시는 인터페이스를 구현(implements)해서 프록시를 만듬
6. CGLIB는 구체 클래스를 상속(extends)해서 프록시를 만듬
7. 실행 결과
```
[Test worker] INFO hello.proxy.common.cglib.CglibTest -- target Class = class hello.proxy.common.service.ConcreteService
[Test worker] INFO hello.proxy.common.cglib.CglibTest -- proxy Class = class hello.proxy.common.service.ConcreteService$$EnhancerByCGLIB$$48bd19d7
[Test worker] INFO hello.proxy.common.cglib.TimeMethodInterceptor -- TimeProxy 실행
[Test worker] INFO hello.proxy.common.service.ConcreteService -- ConcreteService 호출
[Test worker] INFO hello.proxy.common.cglib.TimeMethodInterceptor -- TimeProxy 종료 resultTime = 38
```

8. CGLIB가 생성한 프록시 클래스 이름
   - CGLIB를 통해 생성된 클래스의 이름 : ConcreteService$$EnhancerByCGLIB$$48bd19d7
   - CGLIB가 동적으로 생성하는 클래스 이름의 규칙
     + 대상클래스$$EnhancerByCGLIB$$임의코드

9. 그림으로 정리
    - CGLIB 클래스 및 런타임 객체 의존 관계
<div align="center">
<img src="https://github.com/user-attachments/assets/70a02a66-ce4e-4753-aaf4-4ab5512238fa">
</div>

<div align="center">
<img src="https://github.com/user-attachments/assets/95a10d80-3e8b-4231-ae4d-6e5ece6ab3f1">
</div>

10. 💡 CGLIB 제약
    - 클래스 기반 프록시는 상속을 사용하므로 몇가지 제약 존재
    - 부모 클래스의 생성자 체크 : CGLIB는 자식 클래스를 동적으로 생성하므로 기본 생성자가 필요
    - 클래스에 fianl 키워드가 붙으면 상속 불가 : CGLIB는 예외 발생
    - 메서드에 final 키워드가 붙으면 해당 메서드 오버라이딩 불가 : CGLIB에서는 프록시 로직이 동작하지 않음

11. CGLIB를 사용하면 인터페이스가 없는 V2 애플리케이션에 동적 프록시 적용 가능
    - 그러나 당장 적용하기에는 V2 애플리케이션에 기본 생성자 추가 및 의존 관계를 setter를 사용해 주입하면 CGLIB 적용 가능
    - 하지만, ProxyFactory를 통해서 CGLIB를 적용하면 이런 단점 해결하고, 또 편리하게 해줌

12. 남은 문제
    - 인터페이스가 있는 경우는 JDK 동적 프록시, 그렇지 않은 경우 CGLIB를 적용하려면 어떻게 해야하나?
    - 두 기술을 함께 사용할 때, 부가 기능을 제공하기 위해 JDK 동적 프록시가 제공하는 InvocationHandler와 CGLIB가 제공하는 MethodInterceptor를 각각 중복으로 만들어서 관리?
    - 특정 조건에 맞을 때 프록시 로직을 적용하는 기능도 공통으로 제공되어있다면?
