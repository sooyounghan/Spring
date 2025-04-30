-----
### 컨테이너에 등록된 모든 빈 조회
-----
```java
package hello.core.beanfind;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextInfoTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

    @Test
    @DisplayName("모든 빈 출력하기")
    void findAllBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ac.getBean(beanDefinitionName);

            System.out.println("name = " + beanDefinitionName + " object = " + bean);
        }
    }

    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            //Role ROLE_APPLICATION: 직접 등록한 애플리케이션 빈
            //Role ROLE_INFRASTRUCTURE: 스프링이 내부에서 사용하는 빈
            if(beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);

                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }
        }
    }
}
```
<div align="center">
<img src="https://github.com/sooyounghan/JavaScript/assets/34672301/a7cb2571-772d-4f8b-ab95-33baf224bc27">
<img src="https://github.com/sooyounghan/JavaScript/assets/34672301/19cb3154-b672-49f0-b094-11c04824fe4c">
</div>

1. 모든 빈 출력하기
  - 스프링 내부적 확장을 위한 기반 빈까지 출력
  - 즉, 실행하면 스프링에 등록된 모든 빈 정보를 출력
  - ac.getBeanDefintionNames() : 스프링에 등록된 모든 빈 이름 조회 (String[])
  - ac.getBean() : 빈 이름으로 빈 객체(인스턴스) 조회
  - appConfig 또한 스프링 빈으로 등록

2. 애플리케이션 빈 출력하기
   - 스프링이 내부에서 사용하는 빈은 제외하고, 등록한 빈만 출력
   - 스프링 내부에서 사용하는 빈은 getRole()로 구분 가능
     + ROLE_APPLICATION : 일반적으로 사용자가 정의한 빈
     + ROLE_INFRASTRUCTURE : 스프링이 내부에서 사용하는 빈
