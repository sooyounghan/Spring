-----
### 빈 스코프 (Bean Scope)
-----
1. 스프링 빈은 스프링 컨테이너의 시작과 함께 생성되어 스프링 컨테이너가 종료될 때까지 유지
2. 이는 스프링 빈이 기본적으로 싱글톤 스코프로 생성되기 때문임
3. 스코프(Scope)는 말 그대로 빈이 존재할 수 있는 범위를 뜻함

-----
### 스프링의 다양한 스코프 지원
-----
1. 싱글톤(Singleton) : 기본 스코프. 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프
2. 프로토타입(Prototype) : 스프링 컨테이너는 프로토타입 빈의 생성과 의존 관계 주입까지만 관여하고 더는 관리하지 않는 매우 짧은 범위의 스코프
3. 웹 관련 스코프
   - request : 웹 요청이 들어오고, 나갈 때 까지 유지되는 스코프
   - session : 웹 세션이 생성되고, 종료될 때 까지 유지되는 스코프
   - application : 웹의 Servlet Context와 같은 범위로 유지되는 스코프
4. 빈 스코프 지정 방법
  - 컴포넌트 스캔 자동 등록
```java
@Scope("prototype")
@Component
public class HelloBean { }
```

  - 수동 등록
```java
@Scope("prototype")
@Bean
PrototypeBean HelloBean() {
    return new HelloBean();
}
```
