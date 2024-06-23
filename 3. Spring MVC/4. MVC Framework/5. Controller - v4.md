-----
### 단순하고 실용적인 컨트롤러 - v4
-----
1. v3 컨트롤러는 서블릿 종속성 제거, 뷰 경로의 중복 제거 등이 완료된 컨트롤러
2. 하지만, 컨트롤러 인터페이스를 구현하는 입장에서 항상 ModelView 객체를 생성하고 반환하는 부분이 번거로움
3. 좋은 프레임워크는 아키텍쳐도 중요하지만, 그와 더불어 실제 개발함에 있어, 단순하고 편리하게 사용할 수 있어야 함. 즉, 실용성이 있어야 함
4. v4 구조
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/fc5455c1-2f51-46da-863f-2c95a4d55dfe">
</div>

  - 기본적인 구조는 v3와 동일
  - 대신, 컨트롤러가 ModelView를 반환하지 않고, ViewName을 반환

-----
### ControllerV4
-----
```java
package hello.servlet.web.frontcontroller.v4;

import java.util.Map;

public interface ControllerV4 {
    /**
     * @param paramMap
     * @param model
     * @return viewName
     */
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
```
  - 인터페이스에 ModelView가 없음
  - model 객체는 파라미터로 전달되기 때문에 사용하면 되고, 결과로 뷰의 이름만 반환

-----
### MemberFormControllerV4
-----
```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberFormControllerV4 implements ControllerV4 {
    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        return "new-form";
    }
}
```

  - new-form이라는 뷰의 논리 이름을 반환

-----
### MemberSaveControllerV4
-----
```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberSaveControllerV4 implements ControllerV4 {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.put("member", member);
        return "save-result";
    }
}
```
  - model.put("member", member) : 모델이 파라미터로 전달되므로, 모델을 직접 생성하지 않아도 됨

-----
### MemberListControllerV4
-----
```java
package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.List;
import java.util.Map;

public class MemberListControllerV4 implements ControllerV4 {
    private MemberRepository memberRepository = MemberRepository.getInstance();
    
    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        List<Member> members = memberRepository.findAll();
        
        model.put("members", members);
        return "members";
    }
}
```

-----
### FrontControllerV4 구현
-----
```java
package hello.servlet.web.frontcontroller.v4;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerV4 extends HttpServlet {
    private Map<String, ControllerV4> controllerMap = new HashMap<>();

    public FrontControllerV4() {
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        ControllerV4 controller = controllerMap.get(requestURI);

        if(controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Map<String, String> paramMap = createParamMap(request);
        
        // Map 형태의 Model 생성
        Map<String, Object> model = new HashMap<>();

        String viewName = controller.process(paramMap, model);
        MyView view = viewResolver(viewName);
        view.render(model, request, response);
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));

        return paramMap;
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}
```

1. 모델 객체 전달
   - ```Map<String, Object> = model = new HashMap<>()``` 추가
   - 모델 객체를 프론트 컨트롤러에서 생성해서 넘겨줌
   - 컨트롤러에서 모델 객체에 값을 담으면, 그대로 저장됨

2. 뷰의 논리 이름 직접 반환
```java
String viewName = controller.process(paramMap, model);
MyView view = viewResolver(viewName);
```
  - 컨트롤러가 직접 뷰의 논리 이름을 반환하므로 이 값을 사용해 실제 물리 뷰를 찾을 수 있음

3. 실행
   - 등록 : http://localhost:9090/front-controller/v4/members/new-form
   - 목록 : http://localhost:9090/front-controller/v4/members
  
4. 정리
  - 기존 구조에서 파라미터로 넘기고, 뷰의 논리 이름을 반환
