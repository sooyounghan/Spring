-----
### Bean Validation
-----
1. 검증 기능을 매번 코드로 작성하는 것은 상당히 번거로움
2. 특히, 특정 필드에 대한 검증 로직은 대부분 빈 값인지 아닌지, 특정 크기를 넘는지 아닌지와 같이 매우 일반적인 로직
3. 예시
```java
public class Item {
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    @NotNull
    @Max(9999)
    private Integer quantity;

    //...
}
```

4. 이런 검증 로직을 모든 프로젝트에 적용할 수 있도록 공통화하고 표준화한 것
5. 즉, 이를 잘 활용하면 애너테이션 하나로 검증 로직을 매우 편리하게 적용 가능
6. Bean Validation이란 특정한 구현체가 아닌 Bean Validation 2.0(JSR-380)이라는 기술 표준
   - 즉, 검증 애너테이션과 여러 인터페이스의 모음
   - 이를 구현한 기술 중 일반적으로 사용하는 구현체는 Hibernate Validator
   - Hibernate Validator 관련 링크
     + 공식 사이트 : https://hibernate.org/validator/
     + 공식 메뉴얼 : https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/
     + 검증 애너테이션 모음 : https://docs.jboss.org/hibernate/validator/6.2/reference/en-US/html_single/#validator-defineconstraints-spec
    
-----
### Bean Validator 의존 관계 추가
-----
1. 먼저, 스프링과 통합하지 않고, 순수한 Bean Validator 사용하는 것부터 시작
2. 의존 관계 추가
   - Bean Validator를 사용하려면 다음 의존 관계 추가 (build.gradle)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```
  - spring-boot-starter-validation 의존 관계를 추가하면 라이브러리가 추가
3. Jakarta Bean Validation
  - jakarta.validation-api : Bean Validation 인터페이스
  - hibernate-validator : 구현체

4. Item - Bean Validation 애너테이션 적용
```java
package hello.itemservice.domain.item;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class Item {
    private Long id; // 상품 ID

    @NotBlank
    private String itemName; // 상품명

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price; // 상품 가격

    @NotNull
    @Max(9999)
    private Integer quantity; // 상품 수량


    public Item() {

    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```
  - @NotBlank : 빈값 + 공백만 있는 경우를 허용하지 않음
  - @NotNull : null을 허용하지 않음
  - @Range(min = 1000, max = 1000000) : 범위 안의 값이어야 함
  - @Max(9999) : 최대 9999까지만 허용

5. javax(jakarta).validation.constraints.NotNull / org.hibernate.validator.constraints.Range
   - javax(jakarta).vadliation으로 시작 : 특정 구현에 관계없이 제공되는 표준 인터페이스
   - org.hiberante.validator으로 시작 : Hibernate Valiator 구현체를 사용할 때만 제공되는 검증 기능
   - 대부분 Hibernate Validator를 사용함 (스프링 부트도 기본적으로 넣어줌)

6. 테스트 코드 작성 (BeanValidationTest - Bean Validation)
```java
package hello.itemservice.validation;

import hello.itemservice.domain.item.Item;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class BeanValidationTest {
    @Test
    void beanValidation() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Item item = new Item();
        item.setItemName(" ");
        item.setPrice(0);
        item.setQuantity(10000);

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        for (ConstraintViolation<Item> violation : violations) {
            System.out.println("violation = " + violation);
            System.out.println("violation.getMessage() = " + violation.getMessage());
        }
    }
}
```

  - 검증기 생성 : 다음과 같이 검증기를 생성 (이후, 스프링과 통합하면 직접 이런 코드는 작성하지 않음)
```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
```

  - 검증 실행 : 검증 대상(item)을 직접 검증기에 넣고 그 결과를 받음
    + Set에는 ConstraintViolation 이라는 검증 오류가 담김
    + 따라서, 결과가 비어 있으면 검증 오류가 없는 것
```java
Set<ConstraintViolation<Item>> violations = validator.validate(item);
```

  - 실행 결과
```java
violation = ConstraintViolationImpl{interpolatedMessage='공백일 수 없습니다', propertyPath=itemName, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{jakarta.validation.constraints.NotBlank.message}'}
violation.getMessage() = 공백일 수 없습니다
violation = ConstraintViolationImpl{interpolatedMessage='9999 이하여야 합니다', propertyPath=quantity, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{jakarta.validation.constraints.Max.message}'}
violation.getMessage() = 9999 이하여야 합니다
violation = ConstraintViolationImpl{interpolatedMessage='1000에서 1000000 사이여야 합니다', propertyPath=price, rootBeanClass=class hello.itemservice.domain.item.Item, messageTemplate='{org.hibernate.validator.constraints.Range.message}'}
violation.getMessage() = 1000에서 1000000 사이여야 합니다
```

  - ConstraintViolation 출력 결과를 보면, 검증 오류가 발생한 객체 / 필드 / 메세지 정보 등 다양한 정보 확인 가능
