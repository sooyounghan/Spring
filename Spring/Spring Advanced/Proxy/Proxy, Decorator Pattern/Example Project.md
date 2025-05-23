-----
### 예제 프로젝트 만들기 V1
-----
1. 다양한 상황에서 프록시 사용법을 이해하기 위해 다음과 같은 기준으로 예제 프로젝트 제작
2. 크게 3가지 상황
   - v1 : 인터페이스와 구현 클래스 (스프링 빈으로 수동 등록)
   - v2 : 인터페이스 없는 구체 클래스 (스프링 빈으로 수동 등록)
   - v3 : 컴포넌트 스캔으로 스프링 빈 자동 등록

3. 실무에서는 스프링 빈으로 등록할 클래스는 인터페이스가 있는 경우도 있고, 없는 경우도 존재
4. 그리고 스프링 빈을 수동으로 직접 등록하는 경우도 있고, 컴포넌트 스캔으로 자동으로 등록하는 경우도 존재

-----
### v1 - 인터페이스와 구현 클래스 - 스프링 빈으로 수동 등록
-----
1. Controller, Service, Repository에 인터페이스를 도입하고, 스프링 빈으로 수동 등록
2. OrderRepositoryV1
```java
package hello.proxy.app.v1;

public interface OrderRepositoryV1 {
    void save(String itemId);
}
```

  - OrderRepositoryV1Impl
```java
package hello.proxy.app.v1;

public class OrderRepositoryV1Impl implements OrderRepositoryV1 {
    @Override
    public void save(String itemId) {
        // 저장 로직
        if(itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생");
        }
        sleep(1000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

3. OrderServiceV1
```java
package hello.proxy.app.v1;

public interface OrderServiceV1 {
    void orderItem(String itemId);
}
```

  - OrderServiceV1Impl
```java
package hello.proxy.app.v1;

public class OrderServiceV1Impl implements OrderServiceV1 {
    private final OrderRepositoryV1 orderRepository;

    public OrderServiceV1Impl(OrderRepositoryV1 orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}
```
    
4. OrderControllerV1
```java
package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping
@ResponseBody
public interface OrderControllerV1 {
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);

    @GetMapping("/v1/no-log")
    String noLog();
}
```
  - @RequestMapping : Spring MVC는 타입에 @Controller 또는 @RequestMapping 애너테이션이 있어야 스프링 컨트롤러로 인식 (스프링 부트 3.0 이하)
    + 스프링 컨트롤러로 인식해야, HTTP URL이 매핑되고 동작
    + 이 애너테이션은 인터페이스에 사용해도 됨
  - @ResponseBody : HTTP 메세지 컨버터를 사용해 응답하며, 이 애너테이션은 인터페이스에 사용해도 됨
  - 💡 @RequestParam("itemId") String itemId : 인터페이스에는 @RequestParam("itemId")의 값을 생략하면, itemId를 컴파일 이후 자바 버전에 따라 인식하지 못할 수 있으므로, 꼭 넣어줘야 함
    + 단, 클래스에는 생략해도 대부분 잘 지원됨
  - request(), noLog() 두 가지 메서드 존재
    + request()는 LogTrace를 적용할 대상
    + noLog()는 단순히 LogTrace를 적용하지 않을 대상

  - 💡 스프링 부트 3.0 변경사항
    + 스프링 부트 3.0 (스프링 프레임워크 6.0)부터는 클래스 레벨에 @RequestMapping이 있어도 스프링 컨트롤러로 인식하지 않음
    + 오직 @Controller가 있어야 스프링 컨트롤러로 인식
    + 참고로, @RestController는 해당 애너테이션 내부에 @Controller를 포함하고 있으므로 인식됨
```java
package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController // 스프링은 @Controller, @RestController가 있어야 스프링 컨트롤러로 인식 (스프링 부트 3.0 이상)
public interface OrderControllerV1 {
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);

    @GetMapping("/v1/no-log")
    String noLog();
}
```
  - 이후 학습할 OrderControllerV2에서도 위와 같이 @RestController 사용
```java
@RestController // 스프링은 @Controller, @RestController가 있어야 스프링 컨트롤러로 인식 (스프링 부트 3.0 이상)
public interface OrderControllerV2 {

}
```
  - OrderControllerV1Impl
```java
package hello.proxy.app.v1;

public class OrderControllerV1Impl implements OrderControllerV1 {
    private final OrderServiceV1 orderService;

    public OrderControllerV1Impl(OrderServiceV1 orderService) {
        this.orderService = orderService;
    }

    @Override
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "OK";
    }

    @Override
    public String noLog() {
        return "OK";
    }
}
```
  - 컨트롤러 구현체이며, OrderControllerV1 인터페이스에 스프링 MVC 관련 애너테이션이 정의

5. AppV1Config
```java
package hello.proxy.config;

import hello.proxy.app.v1.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppV1Config {
    @Bean
    public OrderControllerV1 orderControllerV1() {
        return new OrderControllerV1Impl(orderServiceV1());
    }

    @Bean
    public OrderServiceV1 orderServiceV1() {
        return new OrderServiceV1Impl(orderRepositoryV1());
    }

    @Bean
    public OrderRepositoryV1 orderRepositoryV1() {
        return new OrderRepositoryV1Impl();
    }
}
```
  - 스프링 빈으로 수동 등록

6. ProxyApplication
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(AppV1Config.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app") //주의
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
```
  - @Import(AppV1Config.class): 클래스를 스프링 빈으로 등록
    + 여기서는 AppV1Config.class를 스프링 빈으로 등록
    + 일반적으로 @Configuration 같은 설정 파일을 등록할 때 사용하지만, 스프링 빈을 등록할 때도 사용 가능

  - @SpringBootApplication(scanBasePackages = "hello.proxy.app")
    + @ComponentScan의 기능과 동일
    + 컴포넌트 스캔 시작 위치를 지정
    + 이 값을 설정하면 해당 패키지와 그 하위 패키지를 컴포넌트 스캔
    + 이 값을 사용하지 않으면 ProxyApplication이 있는 패키지와 그 하위패키지를 스캔
    + 참고로, v3에서 지금 설정한 컴포넌트 스캔 기능을 사용

  - @Configuration을 사용한 수동 빈 등록 설정을 hello.proxy.config 위치에 두고 점진적으로 변경 예정
    + 즉, AppV1Config.class를 @Import를 사용해서 설정하지만, 이후 다른 것을 설정
  - @Configuration은 내부에 @Component 애너테이션을 포함하므로, 컴포넌트 스캔의 대상이 됨
    + 따라서, 컴포넌트 스캔에 의해 hello.proxy.config 위치의 설정 파일들이 스프링 빈으로 자동 등록 되지 않도록 컴포넌트 스캔의 시작 위치를 scanBasePackages=hello.proxy.app으로 설정

  - 스프링 부트 3.0 이상
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(AppV1Config.class)
@SpringBootApplication(scanBasePackages = "hello.proxy.app.v3") //주의
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
```
  - 스프링 부트 3.0부터는 @Controller, @RestController를 사용했는데, 이렇게 하면 내부에 @Component를 가지고 있어서 컴포넌트 스캔의 대상이 됨
  - 따라서, 컴포넌트 스캔도 되고, 빈도 수동으로 직접 등록하게 되면 스프링 컨테이너에 등록 시 충돌 오류 발생
  - hello.proxy.app.v3은 빈을 직접 등록하지 않고 컴포넌트 스캔을 사용하므로 괜찮음

-----
### v2 - 인터페이스가 없는 구체 클래스 - 스프링 빈으로 수동 등록
-----
1. 인터페이스가 없는 Controller, Service, Repository를 스프링 빈으로 수동 등록
2. OrderRepositoryV2
```java
package hello.proxy.app.v2;

public class OrderRepositoryV2 {
    public void save(String itemId) {
        // 저장 로직
        if(itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생");
        }
        sleep(1000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

3. OrderServiceV2
```java
package hello.proxy.app.v2;

public class OrderServiceV2 {
    private final OrderRepositoryV2 orderRepository;

    public OrderServiceV2(OrderRepositoryV2 orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}
```

4. OrderRepositoryV2
```java
package hello.proxy.app.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping
@ResponseBody
public class OrderControllerV2 {
    private final OrderServiceV2 orderService;

    public OrderControllerV2(OrderServiceV2 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v2/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "OK";
    }

    @GetMapping("/v2/no-log")
    public String noLog() {
        return "OK";
    }
}
```
  - @RequestMapping : 스프링 MVC는 타입에 @Controller 또는 @RequestMapping 애너테이션이 있어야 스프링 컨트롤러로 인식
    + 스프링 컨트롤러로 인식해야 HTTP URL이 매핑되고 동작
    + 여기서는, @Controller를 사용하지 않고, @RequestMapping 애너테이션 사용한 이유는 @Controller를 사용하면 자동 컴포넌트 스캔 대상이 됨
    + 여기서는 컴포넌트 스캔을 통한 자동 빈 등록이 아닌 수동 빈 등록을 하는 것이 목표이므로 컴포넌트 스캔과 관계 없는 @RequestMapping을 타입에 사용
    
  - 스프링 부트 3.0 이상
```java
package hello.proxy.app.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OrderControllerV2 {
    private final OrderServiceV2 orderService;

    public OrderControllerV2(OrderServiceV2 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v2/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "OK";
    }

    @GetMapping("/v2/no-log")
    public String noLog() {
        return "OK";
    }
}
```

5. AppV2Config
```java
package hello.proxy.config;

import hello.proxy.app.v2.OrderControllerV2;
import hello.proxy.app.v2.OrderRepositoryV2;
import hello.proxy.app.v2.OrderServiceV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppV2Config {
    @Bean
    public OrderControllerV2 orderControllerV2() {
        return new OrderControllerV2(orderServiceV2());
    }

    @Bean
    public OrderServiceV2 orderServiceV2() {
        return new OrderServiceV2(orderRepositoryV2());
    }

    @Bean
    public OrderRepositoryV2 orderRepositoryV2() {
        return new OrderRepositoryV2();
    }
}
```
  - 수동 빈 등록을 위한 설정

6. ProxyApplication
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

// @Import(AppV1Config.class)
@Import({AppV1Config.class, AppV2Config.class})
@SpringBootApplication(scanBasePackages = "hello.proxy.app.v3") //주의
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
```
  - 기존 : @Import(AppV1Config.class)
  - 변경 : @Import({AppV1Config.class, AppV2Config.class})
  - @Import 안에 배열로 등록하고 싶은 설정 파일 다양하게 추가 가능

-----
### v3 - 컴포넌트 스캔으로 스프링 빈 자동 등록
-----
1. OrderRepositoryV3
```java
package hello.proxy.app.v3;

import org.springframework.stereotype.Repository;

@Repository // Repository 컴포넌트 스캔
public class OrderRepositoryV3 {
    public void save(String itemId) {
        // 저장 로직
        if(itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생");
        }
        sleep(1000);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

2. OrderServiceV3
```java
package hello.proxy.app.v3;

import org.springframework.stereotype.Service;

@Service // Service 컴포넌트 스캔
public class OrderServiceV3 {
    private final OrderRepositoryV3 orderRepository;

    public OrderServiceV3(OrderRepositoryV3 orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}
```

3. OrderControllerV3
```java
package hello.proxy.app.v3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OrderControllerV3 {
    private final OrderServiceV3 orderService;

    public OrderControllerV3(OrderServiceV3 orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/v3/request")
    public String request(String itemId) {
        orderService.orderItem(itemId);
        return "OK";
    }

    @GetMapping("/v3/no-log")
    public String noLog() {
        return "OK";
    }
}
```

4. ProxyApplication
```java
package hello.proxy;

import hello.proxy.config.AppV1Config;
import hello.proxy.config.AppV2Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

// @Import(AppV1Config.class)
@Import({AppV1Config.class, AppV2Config.class})
@SpringBootApplication(scanBasePackages = "hello.proxy.app.v3") //주의
public class ProxyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

}
```
  - @SpringBootApplication(scanBasePackages = "hello.proxy.app.v3")를 사용
  - 각각 @RestController, @Service, @Reposiotry 애너테이션을 가지고 있으므로 컴포넌트 스캔의 대상이 됨
   
