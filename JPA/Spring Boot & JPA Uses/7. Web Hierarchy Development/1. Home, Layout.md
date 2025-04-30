-----
### 홈 화면과 레이아웃
-----
1. 홈 컨트롤러 등록
```java
package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {
    
    @RequestMapping("/")
    public String home() {
        log.info("Home Controller");
        return "home";
    }
}
```

2. 스프링 부트 타임리프 기본 설정
```properties
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
```
  - 스프링 부트 타임리프 viewName 매핑
    + resources:templates/ + {ViewName} + .html
    + resources:templates/home.html

  - 반환된 문자(home)과 스프링 부트 설정 prefix, suffix 정보를 사용해 렌더링할 뷰(html) 찾음
  - 참고 : https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/html/common-application-properties.html

3. 타임리프 템플릿 등록
   - Home.html
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head th:replace="fragments/header :: header">
    <title>Hello</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
<div class="container">
    <div th:replace="fragments/bodyHeader :: bodyHeader" />
    <div class="jumbotron">
        <h1>HELLO SHOP</h1>
        <p class="lead">회원 기능</p>
        <p>
            <a class="btn btn-lg btn-secondary" href="/members/new">회원 가입</a>
            <a class="btn btn-lg btn-secondary" href="/members">회원 목록</a>
        </p>
        <p class="lead">상품 기능</p>
        <p>
            <a class="btn btn-lg btn-dark" href="/items/new">상품 등록</a>
            <a class="btn btn-lg btn-dark" href="/items">상품 목록</a>
        </p>
        <p class="lead">주문 기능</p>
        <p>
            <a class="btn btn-lg btn-info" href="/order">상품 주문</a>
            <a class="btn btn-lg btn-info" href="/orders">주문 내역</a>
        </p>
    </div>
    <div th:replace="fragments/footer :: footer" />
</div> <!-- /container -->
</body>
</html>
```

  - fragments/header.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="header">
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T"
          crossorigin="anonymous">
    <!-- Custom styles for this template -->
    <link href="/css/jumbotron-narrow.css" rel="stylesheet">
    <title>Hello, world!</title>
</head>
```

  - fragments/bodyHeader.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<div class="header" th:fragment="bodyHeader">
    <ul class="nav nav-pills pull-right">
        <li><a href="/">Home</a></li>
    </ul>
    <a href="/"><h3 class="text-muted">HELLO SHOP</h3></a>
</div>
```

  - fragments/footer.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"> 
<div class="footer" th:fragment="footer"> 
    <p>&copy; Hello Shop V2</p>
</div>
```

  - 참고 : Hierarchical-style layouts
    + 예제에서는 뷰 템플릿을 최대한 간단하게 설명
    + header, footer같은 템플릿 파일을 반복해서 포함
    + 참고 : https://www.thymeleaf.org/doc/articles/layouts.html
    + Hierarchical-style layouts을 참고하면 이런 부분도 중복 제거 가능

  - 뷰 템플릿 변경사항을 서버 재시작 없이 즉시 반영하기
    + spring-boot-devtools 추가
    + html 파일 Build → Recompile

4. View 리소스 등록
   - 부트스트랩 사용 (v4.3.1) (https://getbootstrap.com/)
     + resources/static 하위 css, js 추가
     + resources/static/css/jumbotron-narrow.css 추가

   - jumbotron-narrow.css
```css
/* Space out content a bit */ 
body {
    padding-top: 20px; 
    padding-bottom: 20px;
}

/* Everything but the jumbotron gets side spacing for mobile first views */ 
.header,
.marketing,
.footer {
    padding-left: 15px; 
    padding-right: 15px;
}

/* Custom page header */ 
.header {
    border-bottom: 1px solid #e5e5e5; 
}

/* Make the masthead heading the same height as the navigation */ 
.header h3 {
    margin-top: 0; 
    margin-bottom: 0; 
    line-height: 40px; 
    padding-bottom: 19px;
}

/* Custom page footer */ 
.footer {
    padding-top: 19px; 
    color: #777;
    border-top: 1px solid #e5e5e5;
}

/* Customize container */ 
@media (min-width: 768px) {
  .container { 
      max-width: 730px;
  } 
}

.container-narrow > hr { 
    margin: 30px 0;
}

/* Main marketing message and sign up button */ 
.jumbotron {
    text-align: center;
    border-bottom: 1px solid #e5e5e5; 
}

.jumbotron .btn { 
    font-size: 21px; 
    padding: 14px 24px;
}

/* Supporting marketing content */ 
.marketing {
    margin: 40px 0; 
}

.marketing p + h4 { 
    margin-top: 28px;
}

/* Responsive: Portrait tablets and up */ 
@media screen and (min-width: 768px) {

  /* Remove the padding we set earlier */ 
  .header,
  .marketing,
  .footer { 
  padding-left: 0; 
  padding-right: 0;
  }
  /* Space out the masthead */ 
  .header {
  margin-bottom: 30px; 
  }
  /* Remove the bottom border on the jumbotron for visual effect */ 
  .jumbotron {
    border-bottom: 0; 
  }
}
```
