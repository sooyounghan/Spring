-----
### 프로젝트 환경
-----
1. Java 17 이상
2. IDE : IntelliJ 또는 Eclipse 설치

-----
### 프로젝트 설정
-----
1. build.gradle 확인
```gradle
plugins {
    id 'java'
    id 'war'
}

group = 'hello'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    //서블릿
    implementation 'jakarta.servlet:jakarta.servlet-api:6.0.0'
}

tasks.named('test') {
    useJUnitPlatform()
}
```
  - id 'war' : 톰캣 같은 웹 애플리케이션 서버 (WAS) 위에서 동작하는 WAR 파일을 만들어주는 플러그인
  - jakarta.servlet-api : 서블릿을 사용할 때 필요한 라이브러리

2. 주의
   - 스프링 부트 3.2부터는 Build and run using에 IntelliJ IDEA를 선택하면 몇 가지 오류 발생
   - 따라서, 스프링 부트 3.2를 사용한다면 Gradle 선택
<div align="center">
<img src="https://github.com/user-attachments/assets/6293700a-4e8c-4403-b6f8-68a0e056e531">
</div>

   - Windows : File → Settings(Ctrl+Alt+S)
   - Mac : IntelliJ IDEA → Preferences
   - Preferences → Build, Execution, Deployment → Build Tools → Gradle
   - 빨간색 박스의 Build and run using를 Gradle로 선택
   - 빨간색 박스의 Build tests using를 Gradle로 선택
   - 빨간색 박스 Gradle JVM을 새로 설치한 자바 17 또는 그 이상으로 지정

-----
### 간단한 HTML 등록 및 서블릿 등록
-----
1. 웹 서버가 정적 리소스를 잘 전달하는지 확인을 위해 HTML 하나 생성
   - /src/main 하위에 webapp 이라는 폴더 생성
   - 다음 HTML 파일 생성 : /src/main/webapp/index.html
```html
<html>
<body>index html</body>
</html>
```

2. 서블릿 등록
   - 전체 설정이 잘 동작하는지 확인하기 위해 간단한 서블릿 하나 생성
   - 웹 서버를 통해 이 서블릿이 실행되어야 함
   - TestServlet 등록
```java
package hello.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * http://localhost:9090/test
 */
@WebServlet(urlPatterns = "/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("TestServlet.service");
        resp.getWriter().println("test");
    }
}
```
  - /test로 요청이 오면 이 서블릿이 실행
  - TestServlet.service를 로그에 출력
  - test를 응답 : 웹 브라우저에 요청하면 이 서블릿이 실행되고 화면에 test가 출력되어야 함

3. 이 서블릿을 실행하려면 톰캣 같은 웹 애플리케이션 서버(WAS)에 이 코드를 배포해야함
   
