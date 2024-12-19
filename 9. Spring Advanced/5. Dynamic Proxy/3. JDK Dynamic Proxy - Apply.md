-----
### JDK 동적 프록시 적용
-----
1. JDK 동적 프록시는 인터페이스가 필수 이므로 V1 애플리케이션에만 적용 가능
2. LogTrace를 적용할 수 있는 InvocationHandler 생성
3. LogTraceBasicHandler
```java
package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogicTraceBasicHandler implements InvocationHandler {
    private final Object target;
    private final LogTrace logTrace;

    public LogicTraceBasicHandler(Object target, LogTrace logTrace) {
        this.target = target;
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TraceStatus status = null;
        
        try {
            String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
            status = logTrace.begin(message);
            
            // 로직 호출
            Object result = method.invoke(target, args);
            logTrace.end(status);
            
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```
  - LogTraceBasicHandler는 InvocationHandler 인터페이스를 구현해서 JDK 동적 프록시에서 사용
  - private final Object target : 프록시가 호출할 대상
  - String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
    + LogTrace에서 사용할 메세지
    + 프록시를 직접 개발하면, "ObjectController.request()"와 같이 프록시마다 호출되는 클래스와 메서드 이름을 직접 남김
    + Method를 통해 호출되는 메세지 정보와 클래스 정보를 동적으로 확인 가능하므로 이 정보 사용

4. DynamicProxyBasicConfig (동적 프록시 사용하도록 수동 빈 등록)
```java
package hello.proxy.config.v2_dynamicproxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v2_dynamicproxy.handler.LogicTraceBasicHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class DynamicProxyBasicConfig {
    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1Impl orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));
        OrderControllerV1 proxy = (OrderControllerV1) Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                                                                            new Class[]{OrderControllerV1.class},
                                                                            new LogicTraceBasicHandler(orderController, logTrace));
        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1Impl orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                                                                        new Class[]{OrderServiceV1.class},
                                                                        new LogicTraceBasicHandler(orderService, logTrace));
        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();
        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(OrderRepositoryV1.class.getClassLoader(),
                                                       new Class[]{OrderRepositoryV1.class},
                                                       new LogicTraceBasicHandler(orderRepository, logTrace));

        return proxy;
    }
}
```
  - 이전에는 프록시 클래스를 직접 개발했지만, 이제는 JDK 동적 프록시 기술을 사용해서 각각의 Controller, Service, Repository에 맞는 동적 프록시를 생성해주면 됨
  - LogTraceBasicHandler : 동적 프록시를 만들더라도 LogTrace를 출력하는 로직은 모두 같으므로, 프록시 모두 LogTraceBasicHandler 사용

5. ProxyApplication - 수정
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v1_proxy.ConcreteProxyConfig;
import hello.proxy.config.v1_proxy.InterfaceProxyConfig;
import hello.proxy.config.v2_dynamicproxy.DynamicProxyBasicConfig;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

// @Import(AppV1Config.class)
// @Import({AppV1Config.class, AppV2Config.class})
// @Import(InterfaceProxyConfig.class)
// @Import(ConcreteProxyConfig.class)
@Import(DynamicProxyBasicConfig.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app.v3") //주의
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

	@Bean
	public LogTrace logTrace() {
		return new ThreadLocalLogTrace();
	}
}
```
  - @Import(DynamicProxyBasicConfig.class) : 동적 프록시 설정을 @Import 하고 실행

6. 그림 정리
   - 클래스 의존 관계 - 직접 프록시 사용
<div align="center">
<img src="https://github.com/user-attachments/assets/d8ffe441-559b-493c-8c16-3a66a5d2773e">
</div>

   - 클래스 의존 관계 - JDK 동적 프록시 사용
<div align="center">
<img src="https://github.com/user-attachments/assets/25ff79be-dda7-4594-ae11-554660225e0e">
</div>

   - 런타임 객체 의존 관계 - 직접 프록시 사용
<div align="center">
<img src="https://github.com/user-attachments/assets/5f7d4142-7fba-4dab-b1d9-e29748ac4a06">
</div>

   - 런타임 객체 의존 관계 - JDK 동적 프록시 사용
<div align="center">
<img src="https://github.com/user-attachments/assets/73d1d12c-723e-4e00-8d03-400c9fc616f0">
</div>   

7. 남은 문제
   - no-log를 실행해도 동적 프록시 적용
   - LogTraceBasicHandler가 실행되기 때문에 로그가 남음
```
[advanced] [nio-9090-exec-5] h.p.trace.logtrace.ThreadLocalLogTrace   : [68e625bd] OrderControllerV1.noLog()
[advanced] [nio-9090-exec-5] h.p.trace.logtrace.ThreadLocalLogTrace   : [68e625bd] OrderControllerV1.noLog() time = 2ms
```
   - 이 부분을 로그가 남지 않도록 처리

-----
### 메세지 이름 필터 기능 추가
-----
1. http://localhost:9090/v1/no-log : 요구사항에 의해 no-log 호출 시, 로그가 남으면 안 됨
2. 따라서, 메서드 이름을 기준으로 특정 조건을 만족할 때만 로그를 남기는 기능 개발
3. LogTraceFilterHandler
```java
package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogicTraceFilterHandler implements InvocationHandler {
    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns;

    public LogicTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 메서드 이름 필터
        String methodName = method.getName();
        
        // 예) save, request, request*, *request
        if(!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            // 패턴과 매치되지 않음 (쯕, 여기서는 no-log()면, 로그에 출력하지 않아야하므로 실제 target 메서드 호출)
            return method.invoke(target, args);
        }
        
        TraceStatus status = null;

        try {
            String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
            status = logTrace.begin(message);

            // 로직 호출
            Object result = method.invoke(target, args);
            logTrace.end(status);

            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
```
  - LogTraceFilterHandler는 기존 기능에 다음 기능 추가
    + 특정 메서드 이름이 매칭 되는 경우에만 LogTrace 로직 실행
    + 이름이 매칭되지 않으면 실제 로직 바로 호출

  - 💡 스프링이 제공하는 PatternMatchUtils.simpleMatch(...)를 사용하면, 단순한 매칭 로직을 쉽게 적용 가능
    + xxx : xxx가 정확히 매칭되면 참
    + xxx* : xxx로 시작하면 참
    + *xxx : xxx로 끝나면 참
    + ```*xxx*``` : xxx가 있으면 참

  - String[] patterns : 적용할 패턴은 생성자를 통해 외부에서 받음

4. DynamicProxyFilterConfig
```java
package hello.proxy.config.v2_dynamicproxy;

import hello.proxy.app.v1.*;
import hello.proxy.config.v2_dynamicproxy.handler.LogicTraceFilterHandler;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
public class DynamicProxyFilterConfig {
    private static final String[] PATTERNS = {"request*", "order*", "save*"};
    @Bean
    public OrderControllerV1 orderControllerV1(LogTrace logTrace) {
        OrderControllerV1Impl orderController = new OrderControllerV1Impl(orderServiceV1(logTrace));
        OrderControllerV1 proxy = (OrderControllerV1) Proxy.newProxyInstance(OrderControllerV1.class.getClassLoader(),
                                                                            new Class[]{OrderControllerV1.class},
                                                                            new LogicTraceFilterHandler(orderController, logTrace, PATTERNS));
        return proxy;
    }

    @Bean
    public OrderServiceV1 orderServiceV1(LogTrace logTrace) {
        OrderServiceV1Impl orderService = new OrderServiceV1Impl(orderRepositoryV1(logTrace));
        OrderServiceV1 proxy = (OrderServiceV1) Proxy.newProxyInstance(OrderServiceV1.class.getClassLoader(),
                                                                        new Class[]{OrderServiceV1.class},
                                                                        new LogicTraceFilterHandler(orderService, logTrace, PATTERNS));
        return proxy;
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1(LogTrace logTrace) {
        OrderRepositoryV1 orderRepository = new OrderRepositoryV1Impl();
        OrderRepositoryV1 proxy = (OrderRepositoryV1) Proxy.newProxyInstance(OrderRepositoryV1.class.getClassLoader(),
                                                       new Class[]{OrderRepositoryV1.class},
                                                        new LogicTraceFilterHandler(orderRepository, logTrace, PATTERNS));

        return proxy;
    }
}
```

  - private static final String[] PATTERNS = {"request*", "order*", "save*"};
    + 적용할 패턴이며, request, order, save로 시작하는 메서드에 로그를 남김
  - LogicTraceFilterHandler : 필터 기능이 있는 핸들러 사용하며, 핸들러에 적용 패턴도 넣어줌

5. ProxyApplication - 추가
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import hello.proxy.config.v1_proxy.ConcreteProxyConfig;
import hello.proxy.config.v1_proxy.InterfaceProxyConfig;
import hello.proxy.config.v2_dynamicproxy.DynamicProxyBasicConfig;
import hello.proxy.config.v2_dynamicproxy.DynamicProxyFilterConfig;
import hello.proxy.trace.logtrace.LogTrace;
import hello.proxy.trace.logtrace.ThreadLocalLogTrace;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

// @Import(AppV1Config.class)
// @Import({AppV1Config.class, AppV2Config.class})
// @Import(InterfaceProxyConfig.class)
// @Import(ConcreteProxyConfig.class)
// @Import(DynamicProxyBasicConfig.class)
@Import(DynamicProxyFilterConfig.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app.v3") //주의
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

	@Bean
	public LogTrace logTrace() {
		return new ThreadLocalLogTrace();
	}
}
```
  - @Import(DynamicProxyFilterConfig.class) : 설정 추가
  - 실행하면 no-log가 사용하는 noLog() 메서드에는 로그가 남지 않음

-----
### JDK 동적 프록시의 한계
-----
1. 인터페이스가 필수인 것
2. V2 애플리케이션 처럼 인터페이스 없이 클래스만 존재하는 경우에 동적 프록시 적용 방법은 CGLIB라는 바이트코드를 조작하는 특별 라이브러리 사용해야 함
