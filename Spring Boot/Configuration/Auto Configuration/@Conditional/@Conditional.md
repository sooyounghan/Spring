-----
### @Conditional
-----
1. 앞서 만든 메모리 조회 기능을 항상 사용하는 것이 아니라 특정 조건일때만 해당 기능이 활성화되도록 설정
   - 예를 들어서, 개발 서버에서 확인 용도로 해당 기능을 사용하고, 운영 서버에서는 해당 기능을 사용하지 않도록 하는 것
   - 핵심은 소스 코드를 고치지 않고 가능하게 하는 것
     + 프로젝트를 빌드해서 나온 빌드 파일을 개발 서버에도 배포하고, 같은 파일을 운영 서버에도 배포해야 함
   - 같은 소스 코드인데 특정 상황일 때만, 특정 빈들을 등록해서 사용하도록 도와주는 기능 : @Conditional
   - 참고로 이 기능은 스프링 부트 자동 구성에서 자주 사용

2. 💡 즉, 이름 그대로 특정 조건을 만족하는가, 만족하지 않는가를 구별하는 기능
3. 이 기능을 사용하려면 먼저 Condition 인터페이스를 구현해야 함
4. Condition 인터페이스
```java
package org.springframework.context.annotation;

public interface Condition {
    boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata);
}
```

  - matches() 메서드
    + true를 반환하면 조건에 만족해서 동작
    + false를 반환하면 동작하지 않음

  - ConditionContext : 스프링 컨테이너, 환경 정보 등 담고 있음
  - AnnotatedTypeMetadata : 애너테이션 메타 정보를 담고 있음

5. Condition 인터페이스를 구현해서 다음과 같이 자바 시스템 속성이 memory=on 이라고 되어있을 때만, 메모리 기능이 동작
```
##VM Options
#java -Dmemory=on -jar proejct.jar
```

6. MemoryCondition
```java
package memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Slf4j
public class MemoryCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        // -DMmoery=on
        String memory = context.getEnvironment().getProperty("memory");

        log.info("memory = {}", memory);
        return "on".equals(memory);
    }
}
```
  - 환경 정보에 memory=on이라고 되어 있는 경우에만 true 반환

7. MemoryConfig 수정
```java
package hello.config;

import memory.MemoryCondition;
import memory.MemoryController;
import memory.MemoryFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(MemoryCondition.class) // 추가
public class MemoryConfig {

    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder());
    }

    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
}
```
  - @Conditional(MemoryCondition.class)
    + MemoryConfig의 적용 여부는 @Conditional에 지정한 MemoryCondition의 조건에 따라 달라짐
    + MemoryCondition의 matches()를 실행해보고, 그 결과가 true이면 MemoryConfig는 정상 동작
    + 따라서, memoryController, memoryFinder가 빈으로 등록
    + 만약, MemoryCondition의 실행 결과가 false이면, MemoryConfig는 무효화되며, memoryController와 memoryFinder 빈은 등록되지 않음

  - 아무 조건을 주지 않고 실행
```
Whitelabel Error Page
```
  - memory=on을 설정하지 않았으므로 동작하지 않음
  - 로그를 통해서 MemoryCondition 조건이 실행된 부분 확인 가능 (결과는 false)
```
[           main] memory.MemoryCondition                   : memory = null
```

  - memory=on으로 조건을 주고 실행
<div align="center">
<img src="https://github.com/user-attachments/assets/120b215e-ebcd-42cc-829b-cd600e68e7bb">
</div>

  - VM 옵션을 추가하는 경우, -Dmemory=on를 사용
  - 실행
```
{
    "used": 27061952,
    "max": 2111832064
}
```
  - 로그 결과
```
[           main] memory.MemoryCondition                   : memory = on
[           main] memory.MemoryFinder                      : init memoryFinder
[nio-9090-exec-1] memory.MemoryController                  : memory = Memory{used=27061952, max=2111832064}
```
  - 스프링이 로딩되는 과정은 복잡해서 MemoryCondition이 여러번 호출될 수 있음
  - 💡 참고 : 스프링은 외부 설정을 추상화해서 Enviornment로 통합
    + 따라서, 다양한 외부 환경 설정을 Enviornment 하나로 읽어들일 수 있음
```
# VM Options
# java -Dmemory=on -jar project.jar -Dmemory=on

# Program arguments
# -- 가 있으면 스프링이 환경 정보로 사용
# java -jar project.jar --memory=on --memory=on

# application.properties
# application.properties에 있으면 환경 정보로 사용 memory=on
```

-----
### @Conditional - 다양한 기능
-----
1. 스프링은 이미 필요한 Condition 인터페이스 구현체를 만들어둠
2. MemoryConfig - 수정
```java
package hello.config;

import memory.MemoryCondition;
import memory.MemoryController;
import memory.MemoryFinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
// @Conditional(MemoryCondition.class)
@ConditionalOnProperty(name = "memory", havingValue = "on")
public class MemoryConfig {

    @Bean
    public MemoryController memoryController() {
        return new MemoryController(memoryFinder());
    }

    @Bean
    public MemoryFinder memoryFinder() {
        return new MemoryFinder();
    }
}
```

  - @Conditional(MemoryCondition.class) 주석 처리
  - @ConditionalOnProperty(name = "memory", havingValue = "on")
    + 환경 정보가 memory=on이라는 조건에 맞으면 동작, 그렇지 않으면 동작하지 않음
  - 실행하면 앞서 만든 것과 동일하게 작동

3. @ConditionalOnProperty
```java
package org.springframework.boot.autoconfigure.condition;

@Conditional(OnPropertyCondition.class)
public @interface ConditionalOnProperty {...}
```
  - @ConditionalOnProeprty도 동일하게 내부에 @Conditional 사용
  - 그리고 그 안에는 Condition 인터페이스를 구현한 OnPropertyCondition를 가지고 있음

4. 💡 @ConditionalOnXxx
   - 스프링은 @Conditional과 관련해 편리하게 사용할 수 있도록 수 많은 @ConditionalOnXxx 제공
   - @ConditionalOnClass, @ConditionalOnMissingClass : 클래스가 있는 경우 동작, 나머지는 그 반대
   - @ConditionalOnBean, @ConditionalOnMissingBean : 빈이 등록되어 있는 경우, 나머지는 그 반대
   - @ConditionalOnProperty : 환경 정보가 있는 경우 동작
   - @ConditionalOnResource : 리소스가 있는 경우 동작
   - @ConditionalOnWebApplication, @ConditionalOnNotWebApplication : 웹 애플리케이션인 경우 동작, 나머지는 그 반대
   - @ConditionalOnExpression : SpEL 표현식에 만족하는 경우 동작
   - 공식 메뉴얼 : https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration.condition-annotations
   - @ConditionalOnXxx는 주로 스프링 부트 자동 구성에 사용
     + 자동 구성 클래스들을 열어서 소스 코드를 확인해보면 @ConditionalOnXxx가 아주 많이 사용되는 것 확인 가능
     + JdbcTemplateAutoConfiguration, DataSourceTransactionManagerAutoConfiguration, DataSourceAutoConfiguration

5. 참고 : @Conditional 자체는 스프링 부트가 아니라 스프링 프레임워크의 기능 (본래는 스프링 부트 기능이었음)
   - 스프링 부트는 이 기능을 확장해서 @ConditionalXxx를 제공
