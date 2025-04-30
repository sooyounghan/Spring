-----
### 기본 객체들
-----
1. 타입리프는 기본 객체 제공 (스프링 부트 3.0 이하)
   - ${#request}
   - ${#response}
   - ${#session}
   - ${#servletContext}
   - ${#locale}

2. 스프링 3.0부터 위 객체들은 지원하지 않으며, 사용하게 되면 다음과 같은 오류 발생
```
Caused by: java.lang.IllegalArgumentException: The 'request','session','servletContext' and 'response' expression utility objects 
are no longer available by default for template expressions and their use is not recommended. In cases where they are really needed, they should be manually 
added as context variables.
```

3. 그런데 #request는 HttpServletReqeust 객체가 그대로 제공되므로 데이터를 조회하려면 reqeust.getParameter("data") 처럼 불편하게 접근해야 함
4. 이런 점을 해결하기 위해 편의 객체 제공
   - HTTP 요청 파라미터 접근 : param (예) ${param.paramData})
   - HTTP 세션 접근 : session (예) ${session.sessionData})
   - 스프링 빈 접근 : @ (예) ${@helloBean.hello('Spring!')}

5. 스프링 3.0 미만
  - BasicController
```java
@GetMapping("/basic/objects")
public String basicObjects(HttpSession session) {
    session.setAttribute("sessionData", "Hello Session");
    return "basic/basic-objects";
}

@Component("helloBean")
static class HelloBean {
    public String hello(String data){
        return "Hello " + data;
    }
}
```
  - /resources/templates/basic/basic-objects.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>식 기본 객체 (Expression Basic Objects)</h1>
<ul>
    <li>request = <span th:text="${#request}"></span></li>
    <li>response = <span th:text="${#response}"></span></li>
    <li>session = <span th:text="${#session}"></span></li>
    <li>servletContext = <span th:text="${#servletContext}"></span></li>
    <li>locale = <span th:text="${#locale}"></span></li>
</ul>
<h1>편의 객체</h1>
<ul>
    <li>Request Parameter = <span th:text="${param.paramData}"></span></li>
    <li>session = <span th:text="${session.sessionData}"></span></li>
    <li>spring bean = <span th:text="${@helloBean.hello('Spring!')}"></span></li> 
</ul>
</body> 
</html>
```

6. 스프링 부트 3.0 이상
  - BasicController
```java
@GetMapping("/basic/objects")
public String basicObjects(Model model, 
                           HttpServletRequest request, 
                           HttpServletResponse response,
                           HttpSession session) {
    session.setAttribute("sessionData", "Hello Session");
    model.addAttribute("request", request);
    model.addAttribute("response", response);
    model.addAttribute("serveltContext", servletContext);
    
    return "basic/basic-objects";
}
```

  - /resources/templates/basic/basic-objects.html
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<h1>식 기본 객체 (Expression Basic Objects)</h1>
<ul>
    <li>request = <span th:text="${request}"></span></li>
    <li>response = <span th:text="${response}"></span></li>
    <li>session = <span th:text="${session}"></span></li>
    <li>servletContext = <span th:text="${serveltContext}"></span></li>
    <li>locale = <span th:text="${#locale}"></span></li>
</ul>
<h1>편의 객체</h1>
<ul>
    <li>Request Parameter = <span th:text="${param.paramData}"></span></li>
    <li>session = <span th:text="${session.sessionData}"></span></li>
    <li>spring bean = <span th:text="${@helloBean.hello('Spring!')}"></span></li>
</ul>
</body>
</html>
```

7. 실행 : http://localhost:9090/basic/basic-objects?paramData=HelloParam

<div align="center">
<img src="ttps://github.com/sooyounghan/Spring/assets/34672301/0e1ccb42-2631-4f2c-8959-5ce4156acd8b">
</div>
