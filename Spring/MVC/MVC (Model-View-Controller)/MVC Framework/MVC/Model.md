-----
### Model 추가 - v3
-----
1. 서블릿 종속성 제거
  - 컨트롤러 입장에서 HttpServletRequest, HttpServletResponse가 필요한가?
  - 💡 요청 파라미터 정보는 자바의 Map으로 대신 넘기도록 한다면, 컨트롤러가 서블릿 기술을 몰라도 동작 가능
  - 💡 request 객체를 Model로 사용하는 대신 별도의 Model 객체를 반환하면 됨
  - 즉, 구현한 컨트롤러가 서블릿 기술을 전혀 사용하지 않도록 변경하면, 구현 코드도 매우 단순해지고, 테스트 코드 작성이 쉬워짐   

2. 뷰 이름 종속성 제거
   - 컨트롤러에서 지정하는 뷰 이름에 중복이 있는 것 확인 가능
   - 컨트롤러는 '뷰의 논리 이름'을 반환하고, 실제 물리 위치의 이름은 프론트 컨트롤러에서 처리하도록 단순화
   - 향후 뷰의 폴더 위치가 함께 이동해도 프론트 컨트롤러만 고치면 됨
```
/WEB-INF/views/new-form.jsp (뷰의 물리 위치) → new-form (뷰의 논리 이름)
/WEB-INF/views/save-result.jsp (뷰의 물리 위치) → save-result (뷰의 논리 이름)
/WEB-INF/views/members.jsp (뷰의 물리 위치) → members (뷰의 논리 이름)
```

3. v3 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/ee37f8be-09bd-4358-a220-7dfcda774687">
</div>

  - viewResolver : View의 논리 이름을 뷰의 물리 위치로 변환해 반환

4. ModelView 
  - 컨트롤러에서 서블릿에 종속적인 HttpServletRequest 사용, Model도 request.setAttribute()를 통해 데이터를 저장하고 뷰에 전달
  - 서블릿 종속성을 제거하기 위해 Model을 직접 만들고, 추가로 View 이름까지 전달하는 객체 전달
    + 즉, 컨트롤러에서 HttpServletReqeust를 사용할 수 없으며, request.setAttribute()를 호출할 수 없으므로, Model이 별도로 필요

-----
### ModelView
-----
1. ModelView 객체 : frontcontroller에 위치
```java
package hello.servlet.web.frontcontroller;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    private String viewName; // View의 논리적 이름
    private Map<String, Object> model = new HashMap<>(); // Model에 대한 정보

    public ModelView(String viewName) {
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
```

2. 뷰의 이름과 뷰를 렌더링 할 때, 필요한 Model 객체를 가지고 있음
3. model은 단순히 map으로 되어 있어있으므로 컨트롤러에서 뷰에 필요한 데이터를 key, value에 넣어주면 됨

-----
### ControllerV3
-----
```java
package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerV3 {
    ModelView process(Map<String, String> paramMap);
}
```

1. 해당 컨트롤러는 서블릿 기술을 전혀 사용하지 않음
2. 따라서, 구현이 매우 단순해지고, 테스트 코드 작성 시 테스트하기 쉬움
3. HttpServletRequest가 제공하는 파라미터는 Front-Controller가 paramMap에 담아서 호출해주면 됨
4. 응답 결과로 뷰 이름과 뷰에 전달할 Model 데이터를 포함하는 ModelView 객체를 반환하면 됨

-----
### MemberFormControllerV3 - 회원 등록 폼
-----
```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberFormControllerV3 implements ControllerV3 {
    @Override
    public ModelView process(Map<String, String> paramMap) {
        // 뷰의 논리 이름을 매개변수로 ModelView 반환
        return new ModelView("new-form");
    }
}
```

1. ModelView를 생성할 때, new-form이라는 View의 논리적 이름을 지정
2. 실제 물리적 이름은 프론트 컨트롤러에서 처리

-----
### MemberSaveControllerV3 - 회원 저장
-----
```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberSaveControllerV3 implements ControllerV3 {
    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        // paramMap은 프론트 컨트롤러에서 request 정보
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        // 해당 정보를 Member에 저장
        Member member = new Member(username, age);
        memberRepository.save(member);

        // ModelView 객체 생성(뷰의 논리적 이름) 후, 해당 model에 member 저장 후 반환
        ModelView modelView = new ModelView("save-result");
        modelView.getModel().put("member", member);
        return modelView;
    }
}
```

1. paramMap.get("username"), paramMap.get("age") : 파라미터 정보는 Map에 담겨있으므로, Map에 필요한 요청 파라미터 정보 조회
2. modelView.getModel.put("member", member) : Model은 단순한 Map이므로, 모델에 뷰에서 필요한 member 객체를 담고 반환

-----
### MemberListControllerV3 - 회원 목록
-----
```java
package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    @Override
    public ModelView process(Map<String, String> paramMap) {
        // 전체 회원 정보를 List에 저장
        List<Member> members = memberRepository.findAll();

        // ModelView 생성 (뷰 논리적 이름 : members)
        ModelView modelView = new ModelView("members");
        // 해당 정보 저장
        modelView.getModel().put("members", members);
        return modelView;
    }
}
```

-----
### FrontControllerV3 구현
-----
```java
package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerV3 extends HttpServlet {

    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV3 controllerV3 = controllerMap.get(requestURI);
        if(controllerV3 == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);

        // 해당 paramMap에 대한 정보를 Controller에게 전달
        ModelView modelView = controllerV3.process(paramMap);

        String viewName = modelView.getViewName(); // View의 논리 이름
        // View의 논리 이름 → 물리 경로로 변환 /WEB-INF/views/View의 논리이름.jsp
        MyView view = viewResolver(viewName);
        view.render(modelView.getModel(), request, response);
    }

    // Request Parameter 정보 추출해 Map을 생성하고, 저장하는 메서드
    private Map<String, String> createParamMap(HttpServletRequest request) {
        // paramMap 생성
        Map<String, String> paramMap = new HashMap<>();
        // Request Parameter 정보를 paramMap에 저장
        request.getParameterNames().asIterator()
                        .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
```

1. view.render(modelView.getModel(), request, response)
   - MyView 객체에 해당 render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) 메서드 오버로딩 필요

2. 💡 createParamMap()
   - HttpServletRequest에서 파라미터 정보를 꺼내서 Map으로 변환
   - 해당 Map(paramMap)을 컨트롤러에 전달하면서 호출

3. 💡 ViewResolver
   - MyView view = viewResolver(viewName);
   - 컨트롤러가 반환한 논리 뷰 이름을 실제 물리 뷰 경로로 반환
   - 실제 물리 경로가 있는 MyView 객체를 반환
     + 논리 뷰 이름 : members
     + 물리 뷰 경로 : /WEB-INF/views/members.jsp

4. view.render(modelView.getModel(), request, response)
   - 뷰 객체를 통해서 HTML 화면 렌더링
   - 뷰 객체의 render()는 모델 정보도 함께 받음
   - 💡 JSP는 request.getAttribute()로 데이터를 조회하므로, 모델의 데이터를 꺼내서 request.setAttribute()로 저장
   - JSP로 forward 해서 JSP를 Rendering

5. MyView
```java
package hello.servlet.web.frontcontroller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public class MyView {
    private String viewPath; // 뷰 페이지 이동 경로

    // 컨트롤러에 의해 뷰 객체가 생성될 때, 이동할 View 경로 초기화
    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    // 뷰 페이지로 Rendering 메서드
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
        requestDispatcher.forward(request, response);
    }

    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Model → Request Attribute로 변환
        modelToRequestAttribute(model, request);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
        requestDispatcher.forward(request, response);
    }

    private void modelToRequestAttribute(Map<String, Object> model, HttpServletRequest request) {
        model.forEach((key, value) -> request.setAttribute(key, value));
    }
}
```

  - 실행
    + 등록 : http://localhost:9090/front-controller/v3/members/new-form
    + 목록 : http://localhost:9090/front-controller/v3/members
