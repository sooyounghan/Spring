-----
### View 환경 설정
-----
1. thymeleaf 템플릿 엔진
   - 공식 사이트 : https://www.thymeleaf.org/
   - 스프링 공식 튜토리얼 : https://spring.io/guides/gs/serving-web-content/
   - 스프링 부트 메뉴얼 : https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/boot-features-developing-web-applications.html#boot-features-spring-mvc-template-engines
   - 스프링 부트 thymeleaf viewName 매핑 : resources:templates/ + {viewName} + .html
2. jpabook.jpashop.HelloController
```java
package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "hello!");
        return "hello";
    }
}
```

3. thymeleaf 템플릿 엔진 동작 확인 (hello.html)
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
    <p th:text="'안녕하세요. ' + ${data}" >안녕하세요. 손님</p>
</body>
</html>
```
  - 위치 : resources/templates/hello.html

4. index.html (static/index.html)
```html
<!DOCTYPE HTML>
<html>
<head>
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
Hello
<a href="/hello">hello</a>
</body>
</html>
```

5. 참고 : spring-boot-devtools 라이브러리를 추가하면, html 파일을 컴파일만 해주면, 서버 재시작 없이 View 파일 변경 가능
```gradle
implementation 'org.springframework.boot:spring-boot-devtools'
```
```
[jpashop] [  restartedMain] jpabook.jpashop.JpashopApplication
```
  - 정상적으로 devtools 작동 됨을 '  restartedMain'으로 확인 가능
  
   - IntelliJ 컴파일 방법 : Build - Recomplie (또는 Ctrl + Shift + F9)

