-----
### 스프링 빈 조회 - 상속 관계
-----
1. 부모 타입으로 조회하면, 자식 타입도 함께 조회
2. 따라서, 모든 자바 객체의 최고 부모인 Object 타입으로 조회하면, 모든 스프링 빈 조회
<div align="center">
<img src="https://github.com/sooyounghan/Java/assets/34672301/095371bf-8442-413a-b48e-7cb235284e72">
</div>

```java
package hello.core.beanfind;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationContextExtendsFindTest {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

    @Test
    @DisplayName("부모 타입으로 조회 시 자식이 둘 이상 있으면, 중복 오류 발생")
    void findBeanByParentTypeDuplicate() {
        // DiscountPolicy bean = ac.getBean(DiscountPolicy.class); // NoUniqueBeanDefinitionException 발생
        assertThrows(NoUniqueBeanDefinitionException.class,
                () -> ac.getBean(DiscountPolicy.class));
    }

    @Test
    @DisplayName("부모 타입으로 조회 시 자식이 둘 이상 있으면, 빈 이름을 지정해야 함")
    void findBeanByParentTypeBeanName() {
        DiscountPolicy discountPolicy = ac.getBean("rateDiscountPolicy", DiscountPolicy.class);

        assertThat(discountPolicy).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("특정 하위 타입으로 조회")
    void findBeanBySubType() {
        RateDiscountPolicy bean = ac.getBean(RateDiscountPolicy.class);
        assertThat(bean).isInstanceOf(RateDiscountPolicy.class);
    }

    @Test
    @DisplayName("부모 타입으로 모두 조회")
    void findAllBeanByParentType() {
        Map<String, DiscountPolicy> beansOfType = ac.getBeansOfType(DiscountPolicy.class);

        assertThat(beansOfType.size()).isEqualTo(2);

        for(String key : beansOfType.keySet()) {
            System.out.println("key = " + key + ", value = " + beansOfType.get(key));
        }
    }
    
    @Test
    @DisplayName("부모 타입으로 모두 조회 - Object")
    void findByAllBeanByObjectType() {
        Map<String, Object> beansOfType = ac.getBeansOfType(Object.class);

        for(String key : beansOfType.keySet()) {
            System.out.println("key = " + key + ", value = " + beansOfType.get(key));
        }
    }

    @Configuration
    static class TestConfig {

        @Bean
        public DiscountPolicy rateDiscountPolicy() {
            return new RateDiscountPolicy();
        }

        @Bean
        public DiscountPolicy fixDiscountPolicy() {
            return new FixDiscountPolicy();
        }
    }
}
```
<div align="center">
<img src="https://github.com/sooyounghan/Java/assets/34672301/913e4bdd-d22e-43f1-8ebf-b7438a4a2509">
<img src="https://github.com/sooyounghan/Java/assets/34672301/c6ae67fc-a97f-40ca-b14c-308d5fe1272d">
<img src="https://github.com/sooyounghan/Java/assets/34672301/69ac97ab-29f6-48e1-9ca3-b34993fc665d">
</div>
