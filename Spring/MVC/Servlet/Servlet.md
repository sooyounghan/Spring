-----
### 서블릿 (Servlet)
-----
1. Servlet은 Tomcat 같은 웹 애플리케이션 서버를 직접 설치하고, 그 위에 Servlet 코드를 클래스 파일로 빌드해서 올린 뒤, Tomcat 서버를 실행
2. 하지만, 이 과정은 매우 번거로움
3. Spring Boot는 Tomcat 서버를 내장하고 있으므로,  Tomcat 서버 설치 없이 편리하게 Servlet 코드 실행 가능

-----
### Spring Boot 서블릿 환경 구성
-----
1. @ServletComponentScan : 스프링 부트는 서블릿을 직접 등록해서 사용할 수 있도록, @ServletComponentScan 지원
```java
package hello.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan // Servlet 자동 등록
@SpringBootApplication
public class ServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}
}
```


2. 현재 패키지를 포함해, 하위 패키지까지 Servlet를 찾아, 자동으로 Servlet 등록

-----
### 서블릿 등록하기
-----
1. 서블릿 등록 코드
```java
package hello.servlet.basic;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("HelloServlet.service");
        System.out.println("request = " + request);
        System.out.println("response = " + response);

        String username = request.getParameter("username");
        System.out.println("username = " + username);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("hello " + username);
    }
}
```

<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/aa305547-30c1-4f1b-ad81-37fa1f70dffc">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/d3838fe8-506c-4a97-98a8-d78b01e7e8c3">
</div>

2. @WebServlet 애너테이션
   - name : 서블릿 이름
   - urlPatterns : URL 매핑
   - 두 개의 값은 중복되면 안 됨

3. HTTP 요청을 통해 매핑된 URL이 호출되면, 서블릿 컨테이너는 다음 메서드 실행
```java
protected void service(HttpServletRequest request, HttpServletResponse response)
```

4. 예) 웹 브라우저 실행
   - http://localhost:9090/hello?username=%EA%B9%80
   - 브라우저 출력 : hello 김

-----
### HTTP 요청 메세지 로그 확인
-----
1. 설정 추가 (application.properties)
```java
logging.level.org.apache.coyote.http11=debug(or trace)
```
  - Spring Boot 3.2 이상은 debug 대신 trace을 사용해야 로그 출력

<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/26c27eef-876c-48d2-9672-8ca76f2a242d">
</div>

2. 참고로, 운영 서버에서 이렇게 모든 요청 정보를 다 남기면 성능저하 발생 가능 (개발 단계에서만 적용)

-----
### 서블릿 컨테이너 동작 방식 설명
-----
1. 내장 톰캣 서버 생성
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/0bf26827-dc1a-48d9-a850-577a2e32fcd2">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/a8435100-acf0-4dea-b4e0-3ec98f54bd69">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/61dc50bb-95cc-44d4-a055-d2611d12a748">
</div>

2. HTTP 응답에서 Content-Length는 웹 애플리케이션 서버가 자동으로 생성

-----
### welcome 페이지 추가
-----
1. 개발할 내용을 편리하게 참조할 수 있도록 welcome 페이지 추가 (src/webapp 디렉토리 생성 후, src/webapp/index.html)
2. webapp 경로에 index.html를 두면, http://localhost:port-number 호출 시 index.html 페이지가 열림
  - index.html
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<ul>
    <li><a href="basic.html">서블릿 basic</a></li>
</ul>
</body>
</html>
```

<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/b6b6c637-3ac2-4739-ad2f-c2d81b4d9992">
</div>

3. basic.html
```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<ul>
    <li>hello 서블릿
        <ul>
            <li><a href="/hello?username=servlet">hello 서블릿 호출</a></li>
        </ul>
    </li>
    <li>HttpServletRequest
        <ul>
            <li><a href="/request-header">기본 사용법, Header 조회</a></li>
            <li>HTTP 요청 메시지 바디 조회
                <ul>
                    <li><a href="/request-param?username=hello&age=20">GET - 쿼리
                        파라미터</a></li>
                    <li><a href="/basic/hello-form.html">POST - HTML Form</a></
                    li>
                    <li>HTTP API - MessageBody -> Postman 테스트</li>
                </ul>
            </li>
        </ul>
    </li>
    <li>HttpServletResponse
        <ul>
            <li><a href="/response-header">기본 사용법, Header 조회</a></li>
            <li>HTTP 응답 메시지 바디 조회
                <ul>
                    <li><a href="/response-html">HTML 응답</a></li>
                    <li><a href="/response-json">HTTP API JSON 응답</a></li>
                </ul>
            </li>
        </ul>
    </li>
</ul>
</body>
</html>
```
