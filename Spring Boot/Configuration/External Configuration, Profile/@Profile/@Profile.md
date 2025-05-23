-----
### @Profile
-----
1. 프로필과 외부 설정을 사용해 각 환경마다 설정값을 다르게 적용 가능
2. 그런데 설정값이 다른 정도가 아니라 각 환경마다 서로 다른 빈을 등록해야 한다면?
   - 예를 들어서, 결제 기능을 붙여야 하는데, 로컬 개발 환경에서는 실제 결제가 발생하면 문제가 되니 가짜 결제 기능이 있는 스프링 빈 등록
   - 운영 환경에서는 실제 결제 기능을 제공하는 스프링을 등록한다고 가정

3. PayClient
```java
package hello.pay;

public interface PayClient {
    void pay(int money);
}
```
  - DI를 적극 활용하기 위해 인터페이스 사용

4. LocalPayClient
```java
package hello.pay;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalPayClient implements PayClient {
    @Override
    public void pay(int money) {
        log.info("로컬 결제 money = {}", money);
    }
}
```
  - 로컬 개발 환경에서는 실제 결제를 하지 않음

5. ProdPayClient
```java
package hello.pay;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProdPayClient implements PayClient {
    @Override
    public void pay(int money) {
        log.info("운영 결제 money = {}", money);
    }
}
```
  - 운영 환경에서는 실제 결제를 시도

6. OrderService
```java
package hello.pay;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final PayClient payClient;

    public void order(int money) {
        // 주문 로직 후, 결제
        payClient.pay(money);
    }
}
```
  - PayClient 사용 부분
  - 상황에 따라 LocalPayClient나 ProdPayClient를 주입 받음

7. PayConfig
```java
package hello.pay;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class PayConfig {

    @Bean
    @Profile("default")
    public LocalPayClient localPayClient() {
        log.info("LocalPayClient 빈 등록");
        return new LocalPayClient();
    }

    @Bean
    @Profile("prod")
    public ProdPayClient prodPayClient() {
        log.info("ProdPayClient 빈 등록");
        return new ProdPayClient();
    }
}
```
  - @Profile 애너테이션을 사용하면 해당 프로필이 활성화 된 경우에만 빈 등록
    + defualt 프로필 (기본값) 활성화 : LocalPayClient를 빈으로 등록
    + prod 프로필 활성화 : ProdPayClient를 빈으로 등록

8. RunOrder
```java
package hello.pay;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRunner implements ApplicationRunner {

    private final OrderService orderService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        orderService.order(1000);
    }
}
```
  - 💡 ApplicationRunner 인터페이스를 사용하면 스프링 빈 초기화가 모두 끝나고 애플리케이션 로딩이 완료되는 시점에 run(args) 메서드 호출
    
9. ExternalReadApplication 변경
```java
package hello;

import hello.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

// @Import(MyDataSourceEnvConfig.class)
// @Import(MyDataSourceValueConfig.class)
// @Import(MyDataSourceConfigV1.class)
// @Import(MyDataSourceConfigV2.class)
@Import(MyDataSourceConfigV3.class)
@SpringBootApplication(scanBasePackages = {"hello.datasource", "hello.pay"})
public class ExternalReadApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExternalReadApplication.class, args);
    }

}
```
  - 컴포넌트 스캔 부분에 hello.pay 패키지 추가

10. 프로필 없이 실행
```
2024-09-16T19:17:03.113+09:00  INFO 10248 --- [           main] hello.ExternalReadApplication            : No active profile set, falling back to 1 default profile: "default"
2024-09-16T19:17:04.200+09:00  INFO 10248 --- [           main] hello.pay.PayConfig                      : LocalPayClient 빈 등록
...
2024-09-16T19:17:04.561+09:00  INFO 10248 --- [           main] hello.pay.LocalPayClient                 : 로컬 결제 money = 1000
```
  - 프로필 없이 실행하면 default 프로필 사용
  - default 프로필이 사용되면 LocalPayClient가 빈으로 등록되는 것 확인 가능

11. prod 프로필 실행
  - --spring.profiles.active=prod 프로필 활성화 적용
```
2024-09-16T19:18:22.489+09:00  INFO 16588 --- [           main] hello.ExternalReadApplication            : The following 1 profile is active: "prod"
2024-09-16T19:18:23.565+09:00  INFO 16588 --- [           main] hello.pay.PayConfig                      : ProdPayClient 빈 등록
...
2024-09-16T19:18:24.276+09:00  INFO 16588 --- [           main] hello.pay.ProdPayClient                  : 운영 결제 money = 1000
```
  - prod 프로필 적용
  - prod 프로필이 시작되면 ProdPayClient가 빈으로 등록되는 것 확인 가능

12. 💡 @Profile
```java
package org.springframework.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

...
@Conditional({ProfileCondition.class})
public @interface Profile {
    String[] value();
}
```
  - @Profile은 특정 조건에 따라 해당 빈을 등록할지 말지 결정
  - 즉, @Conditional(ProfileCondition.class)를 통해 이를 결정하고, 해당 코드에 존재
  - 즉, 스프링은 @Conditional 기능을 활용해 개발자가 더 편리하게 사용할 수 있는 @Profile 기능 제공

13. 정리
    - @Profile을 사용하면 각 환경 별로 외부 설정 값을 분리하는 것을 넘어, 등록되는 스프링 빈도 분리 가능
    
