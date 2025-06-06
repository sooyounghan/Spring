-----
### JDK 동적 프록시
-----
1. 프록시를 적용하기 위해 적용 대상의 숫자만큼 프록시 클래스를 만들었음
2. 하지만, 프록시 클래스의 기본 코드와 흐름 거의 같고, 프록시를 어떤 대상에 적용하는가 정도만 차이 존재
   - 즉, 프록시의 로직은 같은데, 적용 대상만 차이가 있는 것
3. 이 문제를 해결하는 것이 JDK 동적 프록시
4. 동적 프록시 기술을 사용하면 직접 프록시 클래스를 만들지 않아도 됨
   - 즉, 프록시 객체를 동적으로 런타임에 대신 만들어 줌
   - 그리고, 동적 프록시에 원하는 실행 로직 지정 가능
5. 💡 JDK 동적 프록시는 인터페이스를 기반으로 프록시를 동적으로 만들어주므로, 인터페이스가 필수

-----
### 기본 예제 코드
-----
1. 간단히 A, B 클래스를 만드는데, JDK 동적 프록시는 인터페이스가 필수 이므로, 인터페이스와 구현체로 구분
2. AInterface (테스트 코드(src/test)에 위치)
```java
package hello.proxy.jdkdynamic.code;

public interface AInterface {
    String call();
}
```

3. AImpl (테스트 코드(src/test)에 위치)
```java
package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AImpl implements AInterface {
    @Override
    public String call() {
        log.info("A 호출");
        return "A";

    }
}
```

4. BInterface (테스트 코드(src/test)에 위치)
```java
package hello.proxy.jdkdynamic.code;

public interface BInterface {
    String call();
}
```

5. BImpl (테스트 코드(src/test)에 위치)
```java
package hello.proxy.jdkdynamic.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BImpl implements BInterface {
    @Override
    public String call() {
        log.info("B 호출");
        return "B";
    }
}
```

-----
### JDK 동적 프록시 - InvocationHandler
------
1. JDK 동적 프록시에 사용할 로직은 InvocationHandler 인터페이스를 구현해서 작성하면 됨
2. 💡 JDK 동적 프록시가 제공하는 InvocationHandler
```java
package java.lang.reflect;

public interface InvocationHandler {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
```

  - Object proxy : 프록시 자신
  - Method method : 호출한 메서드
  - Object[] args : 메서드를 호출할 때 전달한 인수

3. TimeInvocationHandler (테스트 코드(src/test)에 위치)
```java
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
```
  - TimeInvocationHandler는 InvocationHandler 인터페이스를 구현
  - 이렇게 함으로, JDK 프록시에 적용할 공통 로직 개발 가능
  - Object target : 동적 프록시가 호출할 대상
  - method.invoke(target, args) : 리플렉션을 사용해 target 인스턴스의 메서드 실행 (args 메서드 호출 시 넘겨줄 인수)

4. JdkDynamicProxyTest
```java
package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {
    @Test
    void dynamicA() {
        AInterface target = new AImpl();

        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        // 동적 프록시 생성 (Return : Object -> Type-Casting)
        AInterface proxy = (AInterface) Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

        proxy.call();
        log.info("target Class = {}", target.getClass());

        log.info("proxy Class = {}", proxy.getClass());
    }

    @Test
    void dynamicB() {
        BInterface target = new BImpl();

        TimeInvocationHandler handler = new TimeInvocationHandler(target);

        // 동적 프록시 생성 (Return : Object -> Type-Casting)
        BInterface proxy = (BInterface) Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

        proxy.call();
        log.info("target Class = {}", target.getClass());

        log.info("proxy Class = {}", proxy.getClass());
    }
}
```

  - TimeInvocationHandler handler = new TimeInvocationHandler(target); : 동적 프록시에 적용할 핸들러 로직
  - Object proxy = Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);
    + 동적 프록시는 java.lang.reflect.Proxy를 통해 생성 가능
    + 💡 클래스 로더 정보, 인터페이스, 핸들러 로직을 넣어주면 됨
    + 해당 인터페이스를 기반으로 동적 프록시를 생성하고, 그 결과 반환

  - 출력 결과
```
[Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 실행
[Test worker] INFO hello.proxy.jdkdynamic.code.AImpl -- A 호출
[Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 종료 resultTime = 1

[Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- target Class = class hello.proxy.jdkdynamic.code.AImpl
[Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- proxy Class = class jdk.proxy3.$Proxy11
```
  - 프록시 정상 수행

5. 생성된 JDK 동적 프록시
   - proxy Class = class jdk.proxy3.$Proxy11 : 동적으로 생성된 프록시 클래스 정보
   - 직접 만든 클래스가 아닌 JDK 동적 프록시가 이름 그대로 동적으로 만들어준 프록시
   - 이 프록시는 TimeInvocationHandler 로직 실행

6. 실행 순서
<div align="center">
<img src="https://github.com/user-attachments/assets/cb1c4165-4f30-41fe-8c20-ad290963dc4c">
</div>

  - 클라이언트는 JDK 동적 프록시의 call() 실행
  - JDK 동적 프록시는 InvocationHandler.invoke() 호출
    + InvocationHandler가 구현체로 있으므로, TimeInvocationHandler.invoke() 호출
  - TimeInvocationHandler가 내부 로직을 수행하고, method.invoke(target, args)를 호출해서 target인 실제 객체(AImpl)를 호출
  - AImpl 인스턴스의 call() 실행
  - AImpl 인스턴스의 call()의 실행이 끝나면 TimeInvocationHandler로 응답이 돌아옴
    + 시간 로그를 출력하고, 결과 반환

7. 동적 프록시 클래스 정보
   - dynamicA()와 dynamicB() 둘을 동시에 함께 실행하면 JDK 동적 프록시가 각 다른 동적 프록시 클래스로 만듬
```
[Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 실행
[Test worker] INFO hello.proxy.jdkdynamic.code.AImpl -- A 호출
[Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 종료 resultTime = 0
[Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- target Class = class hello.proxy.jdkdynamic.code.AImpl
[Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- proxy Class = class jdk.proxy3.$Proxy11 // dynamicA
 
[Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 실행
[Test worker] INFO hello.proxy.jdkdynamic.code.BImpl -- B 호출
[Test worker] INFO hello.proxy.jdkdynamic.code.TimeInvocationHandler -- TimeProxy 종료 resultTime = 1
[Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- target Class = class hello.proxy.jdkdynamic.code.BImpl
[Test worker] INFO hello.proxy.jdkdynamic.JdkDynamicProxyTest -- proxy Class = class jdk.proxy3.$Proxy12 // dynamicB
```

8. 정리
   - 예제를 보면, AImpl, BImpl 각각 프록시를 만들지 않고, 프록시는 JDK 동적 프록시를 사용해서 동적으로 만들고 TimeInvocationHandler는 공통으로 사용
   - JDK 동적 프록시 기술 덕분에 적용 대상 만큼 프록시 객체를 만들지 않아도 됨
   - 그리고 같은 부가 기능 로직을 한 번만 구현해서 공통 적용 가능
   - 즉, 적용 대상이 100개일지라도, 동적 프록시를 통해 생성하고 각 필요한 InvocationHandler만 만들어서 넣어주면 됨
   - 결과적으로 프록시 클래스를 수 없이 만들어야 하는 문제 해결, 부가 기능 로직도 하나의 클래스에 모아서 단일 책임 원칙(SRP)도 지킬 수 있게 됨

9. JDK 동적 프록시 도입 전 / 후 - 직접 프록시 생성
   - JDK 동적 프록시 도입 전 (클래스 의존 관계)
<div align="center">
<img src="https://github.com/user-attachments/assets/8cb5a342-8f66-48f8-abeb-b4ca63633940">
</div>

  - JDK 동적 프록시 도입 후 (클래스 의존 관계)
<div align="center">
<img src="https://github.com/user-attachments/assets/d8dec2d3-4e4a-450f-9d7a-34977018e328">
</div>

  - JDK 동적 프록시 도입 전 / 후 (런타임 객체 의존 관계)
<div align="center">
<img src="https://github.com/user-attachments/assets/1b6e4283-7f3d-4099-afc8-3bbed7468a6c">
</div>
