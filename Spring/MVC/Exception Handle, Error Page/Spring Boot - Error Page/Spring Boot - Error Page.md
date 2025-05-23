-----
### 스프링 부트 - 오류 페이지
-----
1. 예외 처리 페이지를 만들기 위한 과정
   - WebServerCustomizer 생성
   - 예외 종류에 따라 ErrorPage를 추가하고, 예외 처리용 컨트롤러 ErrorPageContorller 생성
2. 스프링 부트는 이런 과정을 모두 기본으로 제공
   - ErrorPage을 자동으로 등록 (이 때, /error라는 경로로 기본 오류 페이지 설정)
     + new ErrorPage("/error") : 상태코드와 예외를 설정하지 않으면 기본 오류 페이지로 사용
     + 서블릿 밖으로 예외가 발생하거나 response.sendError(...)가 호출되면 모든 오류는 /error를 호출하게 됨
   - BasicErrorController라는 스프링 컨트롤러를 자동 등록
     + ErrorPage에서 등록한 /error를 매핑해서 처리하는 컨트롤러

3. ErrorMvcAutoConfiguration라는 클래스가 오류 페이지를 자동으로 등록하는 역할
4. 개발자는 오류 페이지만 등록하면 됨
   - BasicErrorController는 기본적인 로직이 모두 개발되어 있음
   - 오류 페이지 화면만 BasicErrorController가 제공하는 룰과 우선순위에 따라 등록하면 됨
   - 정적 HTML이면 정적 리소스, 뷰 템플릿을 사용해서 동적으로 오류 화면을 만들고 싶으면 뷰 템플릿 경로에 오류 페이지 파일을 만들어서 넣어두기만 하면 됨

5. 뷰 선택 우선 순위 (BasicErrorController의 처리 순서)
   - 뷰 템플릿
     + resources/templates/error/500.html
     + resources/templates/error/5xx.html
   - 정적 리소스(static, public)
     + resources/static/error/400.html
     + resources/static/error/404.html
     + resources/static/error/4xx.html
   - 적용 대상이 없을 때 뷰 이름 (error)
     + resources/templates/error.html

6. 해당 경로 위치에 HTTP 상태 코드 이름의 뷰 파일을 넣어두면 됨
7. 뷰 템플릿이 정적 리소스보다 우선순위가 높고, 404, 500처럼 구체적인 것이 5xx처럼 덜 구체적인 것보다 우선순위가 높음
8. 오류 뷰 템플릿 추가
  - resources/templates/error/4xx.html
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
</head>
<body>
<div class="container" style="max-width: 600px">
    <div class="py-5 text-center">
        <h2>4xx 오류 화면 스프링 부트 제공</h2>
    </div>
    <div>
        <p>오류 화면 입니다.</p>
    </div>
    <hr class="my-4">
</div> <!-- /container -->
</body>
</html>
```

  - resources/templates/error/404.html
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
</head>
<body>
<div class="container" style="max-width: 600px">
    <div class="py-5 text-center">
        <h2>404 오류 화면 스프링 부트 제공</h2>
    </div>
    <div>
        <p>오류 화면 입니다.</p>
    </div>
    <hr class="my-4">
</div> <!-- /container -->
</body>
</html>
```

  - resources/templates/error/500.html
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
</head>
<body>
<div class="container" style="max-width: 600px">
    <div class="py-5 text-center">
        <h2>500 오류 화면 스프링 부트 제공</h2>
    </div>
    <div>
        <p>오류 화면 입니다.</p>
    </div>
    <hr class="my-4">
</div> <!-- /container -->
</body>
</html>
```

```java
package hello.exception.servlet;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Slf4j
@Controller
public class ServletExceptionController {

    @GetMapping("/error-ex")
    public void errorEx() {
        throw new RuntimeException("예외 발생!");
    }

    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 오류!");
    }

    @GetMapping("/error-400")
    public void error400(HttpServletResponse response) throws IOException {
        response.sendError(400, "400 오류!");
    }

    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500, "500 오류!");
    }
}
```

9. 테스트
  - http://localhost:8080/error-404 → 404.html
  - http://localhost:8080/error-400 → 4xx.html (400 오류 페이지가 없지만 4xx가 있음) 
  - http://localhost:8080/error-500 → 500.html
  - http://localhost:8080/error-ex  → 500.html (예외는 500으로 처리)

-----
### BasicErrorController가 제공하는 기본 정보들
-----
1. BasicErrorController 컨트롤러는 다음 정보를 Model에 담아 뷰에 전달
2. 뷰 템플릿은 이 값을 활용해 출력 가능
   - timestamp : Fri Feb 05 00:00:00 KST 2021
   - status : 400
   - error : Bad Request
   - exception : org.springframework.validation.BindException
   - trace : 예외 trace
   - message : Validation failed for objet='data'. Error count: 1
   - errors : Errors(BindingResult)
   - path : 클라이언트 경로 ('/hello')

3. 오류 정보 추가 (resources/templates/error/500.html)
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="utf-8">
</head>
<body>
<div class="container" style="max-width: 600px">
    <div class="py-5 text-center">
        <h2>500 오류 화면 스프링 부트 제공</h2>
    </div>
    <div>
        <p>오류 화면 입니다.</p>
    </div>

    <ul>
        <li>오류 정보</li>
        <ul>
            <li th:text="|timestamp: ${timestamp}|"></li>
            <li th:text="|path: ${path}|"></li>
            <li th:text="|status: ${status}|"></li>
            <li th:text="|message: ${message}|"></li>
            <li th:text="|error: ${error}|"></li>
            <li th:text="|exception: ${exception}|"></li>
            <li th:text="|errors: ${errors}|"></li>
            <li th:text="|trace: ${trace}|"></li>
        </ul>
        </li>
    </ul>
    
    <hr class="my-4">
</div> <!-- /container -->
</body>
</html>
```
  - 오류 관련 내부 정보를 고객에게 노출하는 것은 좋지 않음
  - 고객이 해당 정보를 읽어도 혼란만 더해지고, 보안상 문제가 될 수 있음

 4. 따라서, BasicErrorController 오류 컨트롤러에 다음 오류 정보를 model에 포함할 지 여부 선택 가능 (application.properties)
  - server.error.include-exception=false : exception 포함 여부 (true, false)
  - server.error.include-message=never : message 포함 여부
  - server.error.include-stacktrace=never : trace 포함 여부
  - server.error.include-binding-errors=never : errors 포함 여부
```properties
server.error.include-exception=true
server.error.include-message=on_param
server.error.include-stacktrace=on_param
server.error.include-binding-errors=on_param
```

  - 기본 값이 never인 경우는 다음 3가지 옵션 사용 가능 : never, always, on_param
    + never : 사용하지 않음
    + always : 항상 사용
    + on_param : 파라미터가 있을 때 사용
  - on_param은 파라미터가 있으면 해당 정보를 노출 (디버그 시 문제를 확인하기 위해 사용)
  - 하지만, 이 부분도 개발 서버에서는 사용할 수 있지만, 운영 서버에서는 권장하지 않음
  - on_param으로 설정하고 다음과 같이 HTTP 요청 시 파라미터를 전달하면 해당 정보들이 model에 담겨서 뷰 템플릿에서 출력
```
message=&errors=&trace=
```

5. 테스트 : http://localhost:9090/error-ex?message=&errors=&trace=
6. 💡 하지만, 실무에서 이것들을 노출하면 안 됨 (사용자에게는 오류 화면과 고객이 이해할 수 있는 간단한 오류 메세지를 보여주고, 오류는 서버에 로그를 남겨서 확인해야 함)
7. 스프링 부트 오류 관련 옵션
   - server.error.whitelabel.enabled=true : 오류 화면을 찾지 못하면, 스프링 whitelabel 오류 페이지 적용
   - server.error.path=/error : 오류 페이지 경로, 스프링이 자동 등록하는 서블릿 글로벌 오류 페이지 경로와 BasicErrorController 오류 컨트롤러 경로에 함께 사용

8. 확장 포인트
   - 에러 공통 처리 컨트롤러의 기능을 변경하고 싶으면 ErrorController 인터페이스를 상속받아서 구현
   - 또는, BasicErrorController를 상속받아서 기능을 추가하면 됨
