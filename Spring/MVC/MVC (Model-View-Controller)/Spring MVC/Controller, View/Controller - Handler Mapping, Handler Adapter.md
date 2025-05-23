------
### Controller 인터페이스
------
1. 과거 버전의 스프링 컨트롤러
```java
org.springframework.web.servlet.mvc.Controller
```
```java
public interface Controller {
    ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
```

2. Controller 인터페이스는 @Controller 애너테이션과 전혀 다름

3. OldController 구현
```java
package hello.servlet.web.springmvc.old;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

@Component("/spring-mvc/old-controller")
public class oldController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("oldController.handleRequest");
        return null;
    }
}
```

  - @Component : 이 컨트롤러는 /spring-mvc/old-controller 라는 이름의 스프링 빈으로 등록
  - 💡 따라서, 빈의 이름으로 URL을 매핑
  - 실행 : http://localhost:9090/spring-mvc/old-controller
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/0b34821c-9dc2-494b-b865-f5a454386264">
</div>

-----
### 컨트롤러 호출 구조
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/089d825d-d4fa-40e0-af80-a7f21734a1a5">
</div>

1. HandlerMapping (핸들러 매핑)
   - 핸들러 매핑에서 이 컨트롤러를 찾을 수 있어야 함
   - 예) 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑 필요

2. HandlerAdapter (핸들러 어댑터)
   - 핸들러 매핑을 통해서 찾은 핸들러를 실행할 수 있는 핸들러 어댑터가 필요
   - 예) Controller 인터페이스를 실행할 수 있는 핸들러 어댑터를 찾고 실행해야 함

3. 스프링은 이미 필요한 핸들러 매핑과 핸들러 어댑터를 대부분 구현해둠
4. 스프링 부트가 자동 등록하는 핸들러 매핑과 핸들러 어댑터 (실제로는 더 많지만, 중요한 부분 위주 표시)
  - HandlerMapping
```
0 = RequestMappingHandlerMapping   : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용 
1 = BeanNameUrlHandlerMapping      : 스프링 빈의 이름으로 핸들러를 찾는다.
```

  - HandlerAdapter
```
0 = RequestMappingHandlerAdapter   : 애노테이션 기반의 컨트롤러인 @RequestMapping에서 사용 
1 = HttpRequestHandlerAdapter      : HttpRequestHandler 처리
2 = SimpleControllerHandlerAdapter : Controller 인터페이스(애노테이션X, 과거에 사용) 처리
```

5. 핸들러 매핑도, 핸들러 어댑터도 모두 순서대로 찾고, 만약 없으면 다음 순서로 넘어감

-----
### 순서
-----
1. 핸들러 매핑으로 핸들러 조회
   - HandlerMapping을 순서대로 실행해, 핸들러를 찾음
   - 이 경우 빈 이름으로 핸들러를 찾아야 하기 떄문에, 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping이 실행에 성공
   - 핸들러인 OldController 반환

2. 핸들러 어댑터 조회
   - HandlerAdapter의 supports()를 순서대로 호출
   - SimpleControllerHanlderAdapter가 Conroller 인터페이스를 지원하므로 대상이 됨

3. 핸들러 어댑터 실행
   - DispatcherServlet이 조회한 SimpleControllerHandlerApdater가 실행하면서 핸들러 정보도 함께 넘겨 줌
   - SimpleControllerHandlerAdapter는 핸들러인 OldController를 내부에서 실행하고, 그 결과를 반환

-----
### 정리 - OldController 핸들러 매핑, 어댑터
-----
1. HandlerMapping = BeanNameUrlHanlderMapping
2. HandlerAdapter = SimpleControllerHandlerAdapter

-----
### HttpRequestHandler
-----
1. 서블릿과 가장 유사한 형태의 핸들러
```java
public interface HttpRequestHandler {
    void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
```

2. 구현
```java
package hello.servlet.web.springmvc.old;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import java.io.IOException;

@Component("/spring-mvc/request-handler")
public class MyHttpRequestHandler implements HttpRequestHandler {
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MyHttpRequestHandler.handleRequest");
    }
}
```

  - 실행 : http://localhost:9090/spring-mvc/request-handler
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/f2447d29-f128-4546-aed0-ec84f9f2af14">
</div>

-----
### 순서
-----
1. 핸들러 매핑으로 핸들러 조회
   - HandlerMapping을 순서대로 실행해, 핸들러를 찾음
   - 이 경우 빈 이름으로 핸들러를 찾아야 하기 때문에, 이름 그대로 빈 이름으로 핸들러를 찾아주는 BeanNameUrlHandlerMapping가 실행에 성공
   - 핸들러인 MyHttpRequestHandler 반환

2. 핸들러 어댑터 조회
   - HandlerAdapter의 supports()를 순서대로 조회
   - HttpRequestHandlerAdatper가 HttpRequestHandler 인터페이스를 지원하므로 대상이 됨

3. 핸들러 어댑터 사용
   - DispatcherServlet이 조회한 HttpRequestHandlerAdapter를 실행하면서 핸들러 정보도 함께 넘겨줌
   - HttpRequestHandlerAdapter는 핸들러인 MyHttpRequestHandler를 내부에서 실행하고, 그 결과를 반환

-----
### 정리 - MyHttpRequestHandler 핸들러 매핑, 어댑터
-----
1. HandlerMapping = BeanNameUrlHandlerMapping
2. HanlderAdapter = HttpRequestHandlerAdapter

-----
### @RequestMapping
-----
1. 가장 우선순위가 높은 핸들러 매핑과 핸들링 어댑터
2. RequestMappingHandlerMapping, RequestMappingHandlerAdapter
   - @RequestMapping의 앞글자를 따서 만든 이름
   - 현재 스프링에서 주로 사용하는 애너테이션 기반의 컨트롤러를 지원하는 매핑과 어댑터
