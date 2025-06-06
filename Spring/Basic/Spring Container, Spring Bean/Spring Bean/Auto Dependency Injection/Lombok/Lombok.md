-----
### 롬복 (Lombok)
-----
1. 기본 코드
```java
@Component
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) { // Constructor Injection
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
    ...
}
```

  - 생성자가 딱 하나 존재하면, @Autowired 생략 가능
```java
@Component
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;

    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) { // Constructor Injection
        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;
    }
    ...
}
```

2. 롬복 라이브러리 적용 방법
   - build.gradle에 라이브러리 및 환경 추가
```gradle
plugins {
    id 'org.springframework.boot' version '2.3.2.RELEASE'
    id 'io.spring.dependency-management' version '1.0.9.RELEASE' 
    id 'java'
}

group = 'hello'
version = '0.0.1-SNAPSHOT' 
sourceCompatibility = '11'

//lombok 설정 추가 시작
configurations { 
    compileOnly {
        extendsFrom annotationProcessor 
    }
}

//lombok 설정 추가 끝
repositories { 
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter'

    //lombok 라이브러리 추가 시작
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok' 
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    //lombok 라이브러리 추가 끝

    testImplementation('org.springframework.boot:spring-boot-starter-test') { 
        exclude group: 'org.junit.vintage', module: 'junit-vintage-engine' 
    }
}

test { 
    useJUnitPlatform()
}
```
```
A. File → Settings → plugin → lombok 검색 설치 실행 (재시작)
B. File → Settings → Annotation Processor 검색 → Enable annotation processing 체크 (재시작)
C. 임의의 테스트 클래스를 만들고 @Getter, @Setter 확인
```
```java
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HelloLombok {
    private String name;
    private int age;

    public static void main(String[] args) {
        HelloLombok helloLombok = new HelloLombok();
        helloLombok.setName("Hello");
        String name = helloLombok.getName();
        System.out.println("name = " + name);

        System.out.println("helloLombok = " + helloLombok);
    }
}
```

3. Lombok 적용
  - 롬복 라이브러리가 제공하는 @RequiredArgsConstructor 기능을 사용하면 final이 붙은 필드를 모아서 생성자를 자동으로 만들어줌 (실제 호출도 가능)
```java
@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    ...
}
```

4. 롬복이 자바의 애노테이션 프로세서라는 기능을 이용해 컴파일 시점에 생성자 코드를 자동으로 생성
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/b7c65fe1-ffc7-4300-9e34-57ef53662003">
</div>

5. 실제 class를 열어보면, 다음 코드가 추가되어 있음
```java
public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) { // Constructor Injection
    this.memberRepository = memberRepository;
    this.discountPolicy = discountPolicy;
}
```

6. 최근에는 생성자를 딱 하나만 두고, @Autowired를 생략하는 방법을 주로 사용
   - 여기에 Lombok 라이브러리의 @RequiredArgsConstructor를 함께 사용하면, 기능은 다 제공하면서, 코드는 간결하게 작성 가능
