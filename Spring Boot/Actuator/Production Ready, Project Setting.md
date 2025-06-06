-----
### 프로덕션 준비 기능
-----
1. 애플리케이션을 개발할 때, 요구사항만 개발하는 것이 아님
2. 💡 서비스를 실제 운영 단계에 올리게 되면, 개발자들이 해야되는 또 다른 중요한 업무가 있는데, 바로 서비스에 문제가 없는지 모니터링하고 지표들을 삼아서 감시하는 활동
3. 운영 환경에서 서비스를 할 때 필요한 이런 기능들을 프로덕션 준비 기능
    - 💡 쉽게 이야기해서 프로덕션을 운영에 배포할 때, 준비해야하는 비 기능적 요소들을 뜻함
    - 지표(Metric) - CPU 사용량 등 , 추적(Trace) - 코드 문제 및 호출 파악 등, 감사(Audition) - 로그인 이력 기록 등
    - 모니터링 - 지표 모니터링 등
    
4. 좀 더 구체적으로 설명하자면, 애플리케이션이 현재 살아있는지, 로그 정보는 정상 설정 되었는지, 커넥션 풀은 얼마나 사용되고 있는지 확인할 수 있어야 함
5. 스프링 부트가 제공하는 액추에이터는 이런 프로덕션 준비 기능을 매우 편리하게 사용할 수 있는 다양한 편의 기능들을 제공
6. 더 나아가 마이크로미터, 프로메테우스, 그라파나 같은 최근 유행하는 모니터링 시스템과 매우 쉽게 연동할 수 있는 기능 제공
7. 참고로 액추에이터(Actuator)는 시스템을 움직이거나 제어할 때 쓰이는 기계 장치

-----
### 프로젝트 설정
-----
1. build.gradle
```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.0.2'
    id 'io.spring.dependency-management' version '1.1.0'
}

group = 'hello'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-actuator' // actuator 추가

    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // test lombok 사용
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
}

tasks.named('test') {
    useJUnitPlatform()
}
```
  - 스프링 부트에서 Spring Boot Actuator, Spring Web, Spring Data JPA, H2 Database, Lombok 라이브러리 선택
  - 테스트 코드에서 Lombok을 사용할 수 있도록 설정 추가
  - Spring Boot Actuator 라이브러리 추가 부분 확인
