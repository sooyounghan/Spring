-----
### 직접 만든 MVC 프레임워크 구조
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/953756e7-d23f-4709-a335-c261092a3265">
</div>

-----
### Spring MVC 구조
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/cd6810d9-e8d0-493d-9f7c-7ef247a8c08e">
</div>

-----
### 비교
-----
1. FrontConroller → DispatcherServlet
2. handlerMapping → HandlerMapping
3. MyHandlerAdapter → HandlerAdapter
4. ModelView → ModelAndView
5. viewResolver → ViewResolver
6. MyView → View

------
### DispatcherServlet
-----
```java
org.framework.web.servlet.DispatcherServlet
```
1. 스프링 MVC도 프론트 컨트롤러 패턴으로 구현되어 있음
2. 스프링의 MVC의 프론트 컨트롤러 : 디스패처 서블릿(Dispatcher Servlet)

-----
### DispatcherServlet 서블릿 등록
-----
1. DispatcherServlet도 부모 클래스에서 HttpServlet을 상속 받아서 사용하고, 서블릿으로 동작
   - DispatcherSerlvet → FrameworkServlet → HttpServletBean → HttpServlet
2. 스프링 부트는 DispatcherServlet을 서블릿으로 자동 등록 하면서 모든 경로(urlPatterns="/")에 대해서 매핑
   - 참고 : 더 자세한 경로가 우선순위가 높음. 따라서, 기존에 등록한 서블릿도 함께 동작
3. 요청 흐름
   - 서블릿이 호출되면 HttpServlet이 제공하는 service()가 호출
   - 스프링 MVC는 DispatcherServlet의 부모인 FrameworkServlet에서 service()를 오버라이딩
   - FrameworkServlet.service()를 시작으로 여러 메서드가 호출되면서, DispatcherServlet.doDispatch()가 호출

4. Dispatcher.doDispatch()
```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
    HttpServletRequest processedRequest = request; 
    HandlerExecutionChain mappedHandler = null; 
    ModelAndView mv = null;

    // 1. 핸들러 조회
    mappedHandler = getHandler(processedRequest); 

    if (mappedHandler == null) {
        noHandlerFound(processedRequest, response); 
        return;
    }

    // 2. 핸들러 어댑터 조회 - 핸들러를 처리할 수 있는 어댑터
    HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

    // 3. 핸들러 어댑터 실행 -> 4. 핸들러 어댑터를 통해 핸들러 실행 -> 5. ModelAndView 반환 
    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
    processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
}

private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
    // 뷰 렌더링 호출
    render(mv, request, response); 
}

protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
    View view;
    String viewName = mv.getViewName();

    // 6. 뷰 리졸버를 통해서 뷰 찾기, 7. View 반환
    view = resolveViewName(viewName, mv.getModelInternal(), locale, request);

    // 8. 뷰 렌더링
    view.render(mv.getModelInternal(), request, response); 
}
```

-----
### Spring MVC 구조 및 동작 순서
-----
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/0d22288d-caee-430e-9076-c97d2bd0ee46">
</div>

1. 핸들러 조회 : 핸들러 매핑을 통해 요청 URL에 매핑된 핸들러(컨트롤러) 조회
2. 핸들러 어댑터 조회 : 핸들러를 실행할 수 있는 핸들러 어댑터 조회
3. 핸들러 어댑터 실행 : 핸들러 어댑터를 실행
4. 핸들러 실행 : 핸들러 어댑터가 실제 핸들러 실행
5. ModelAndView : 핸들러 어댑터는 핸들러가 반환하는 정보를 ModelAndView로 변환해 반환
6. viewResolver 호출 : 뷰 리졸버를 찾고 실행
   - JSP의 경우, InternalResoucreViewResolver가 자동 등록되고 사용
7. View 반환 : 뷰 리졸버는 뷰의 논리 이름을 물리 이름으로 바꾸고, 렌더링 역할을 담당하는 뷰 객체 반환
   - JSP의 경우, InternalResourceView(JspView)를 반환하는데, 내부에 forward() 로직 존재
8. 뷰 렌더링 : 뷰를 통해서 렌더링

-----
### 인터페이스 살펴보기
-----
1. 스프링 MVC의 큰 강점은 DispatcherServlet 코드의 변경 없이, 원하는 기능을 변경하거나 확장 가능
2. 즉, 확장 가능할 수 있도록 인터페이스로 제공
3. 이 인터페이스들만 구현해서 DispatcherServlet에 등록하면 컨트롤러 제작 가능
4. 주요 인터페이스 목록
   - 핸들러 매핑 : org.springframework.web.servlet.HandlerMapping
   - 핸들러 어댑터 : org.springframework.web.servlet.HandlerAdapter
   - 뷰 리졸버 : org.springframework.web.servlet.ViewResolver
   - 뷰 : org.springframework.web.servlet.View
