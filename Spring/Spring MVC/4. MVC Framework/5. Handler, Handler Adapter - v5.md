-----
### Adapter(어댑터) 패턴 - v5
-----
1. 만약, 어떠한 경우에는 ControllerV3, 어떠한 경우에는 ControllerV4 방식으로 개발하고 싶다면?
```java
public interface ControllerV3 {
    ModelView process(Map<String, String> paramMap);
}
```

```java
public interface ControllerV4 {
    /**
     * @param paramMap
     * @param model
     * @return viewName
     */
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
```

2. 현재까지의 프론트 컨트롤러는 한 가지 방식의 컨트롤러 인터페이스만 사용 가능
   - 즉, ControllerV3, ControllerV4는 완전히 다른 인터페이스이므로, 호환이 불가능
   - 하지만, 어댑터 패턴을 사용해서 프론터 컨트롤러가 다양한 방식의 컨트롤러를 처리할 수 있도록 변경 가능

3. v5 구조
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/515fd8c8-f358-46d7-a973-c74bbfc77dcd">
</div>

  - Handler Adapter (핸들러 어댑터) : 어댑터 역할을 하는 어댑터, 다양한 종류의 컨트롤러 호출 가능
  - Handler (핸들러 ⊃ 컨트롤러) : 컨트롤러의 이름을 더 넓은 범위인 핸들러로 변경
    + Adapter(어댑터)가 있기 때문에, 꼭 컨트롤러의 개념 뿐 아니라 어떠한 것이든 해당하는 종류의 어댑터가 있으면 모두 처리 가능

-----
### MyHandlerAdapter
-----
1. 어댑터 인터페이스
```java
javapackage hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface MyHandlerAdapter {
    boolean supports(Object handler);

    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handle) throws ServletException, IOException;
 }

```

2. boolean support(Object handler)
   - handler는 컨트롤러를 의미
   - 어댑터가 해당 컨트롤러를 처리할 수 있는지 판단하는 메서드
  
3. ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handle)
   - 어댑터는 실제 컨트롤러를 호출하고, 그 결과로 ModelView 반환
   - 실제 컨트롤러가 ModelView를 반환하지 못하면, 어댑터가 ModelView를 직접 생성하더라도 반환해야 함
   - 이전에는 프론트 컨트롤러가 실제 컨트롤러를 호출했지만, 이제는 이 어댑터를 통해서 실제 컨트롤러가 호출

-----
### ControllerV3HandlerAdapter
-----
```java
package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV3HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV3 controller = (ControllerV3) handler;

        Map<String, String> paramMap = createParamMap(request);
        ModelView modelView = controller.process(paramMap);

        return modelView;
    }

    private Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                        .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));

        return paramMap;
    }
}
```

1. ControllverV3을 처리할 수 있는 어댑터
```java
public boolean support(Object handler) {
    return (handler instanceof ControllerV3);
}
```

2. handler를 ControllerV3로 변환한 다음 V3 형식에 맞춰서 호출
   - supports()를 통해 ControllerV3만 지원하기 때문에, 타입 변환에 상관 없이 실행 가능
   - ControllverV3는 ModelView를 반환하므로 그대로 ModelView를 반환하면 됨
```java
ControllerV3 controller = (ControllerV3) handler;

Map<String, String> paramMap = createParamMap(request);
ModelView modelView = controller.process(paramMap);

return modelView;
```

-----
### FrontControllerServletV5
-----
```java
package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {
    private final Map<String, Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();

        initHandlerAdapters();
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3HandlerAdapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object handler = getHandler(request);
        if(handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        ModelView modelView = adapter.handle(request, response, handler);
        String viewName = modelView.getViewName();
        MyView view = viewResolver(viewName);

        view.render(modelView.getModel(), request, response);
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        for (MyHandlerAdapter handlerAdapter : handlerAdapters) {
            if(handlerAdapter.supports(handler)) {
                return handlerAdapter;
            }
        }

        throw new IllegalArgumentException("Handler Adapter를 찾을 수 없습니다. Handler = " + handler);
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }


}
```

1. Controller → Handler
   - 이전에는 컨트롤러를 직접 매핑해서 사용했으나, 어댑터를 사용하므로 컨트롤러 뿐만 아니라 어댑터가 지원하기만 하면, 어떤 것이라도 URL에 매핑해서 사용 가능
   - 이름을 컨트롤러에서 더 넓은 범위의 핸들러로 변경
  
2. 생성자
```java
public FrontControllerServletV5() { // Constructor
    initHandlerMappingMap(); // Handler Mapping 초기화

    initHandlerAdapters(); // Handler Adapter 초기화
}
```
   - 핸들러 매핑과 어댑터를 초기화(등록)

3. 매핑 정보
```java
 private final Map<String, Object> handlerMappingMap = new HashMap<>();
```
   - 매핑 정보의 값이 ControllerV3, ControllerV4 같은 인터페이스에서 아무 값이나 받을 수 있는 Object로 변경

4. 핸들러 매핑
```java
Object handler = getHandler(request);

private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
}
```
   - 핸들러 매핑 정보인 handlerMappingMap에서 URL에 매핑된 핸들러(컨트롤러) 객체를 찾아서 반환

5. 핸들러를 처리할 수 있는 어댑터 조회
```java
MyHandlerAdapter adapter = getHandlerAdapter(handler);

private MyHandlerAdapter getHandlerAdapter(Object handler) {
    for (MyHandlerAdapter handlerAdapter : handlerAdapters) {
        if(handlerAdapter.supports(handler)) {
            return handlerAdapter;
        }
    }

    throw new IllegalArgumentException("Handler Adapter를 찾을 수 없습니다. Handler = " + handler);
}
```
   - handler를 처리할 수 있는 어댑터를 adapter.supports(handler)를 통해서 찾음
   - handler가 ControllerV3 인터페이스를 구현했다면, ControllerV3HandlerAdapter 객체 반환

6. 어댑터 호출
```java
ModelView modelView = adapter.handle(request, response, handler);
```
   - 어댑터의 handle(request, response, handler) 메서드를 통해 실제 어댑터 호출
   - 어댑터는 handler(Controller)를 호출하고, 그 결과를 어댑터에 맞추어 반환
   - ControllerV3HandlerAdapter의 경우, 어댑터의 모양과 컨트롤러의 모양이 유사해 변환 로직이 단순

7. 실행
   - 등록 : http://localhost:8080/front-controller/v5/v3/members/new-form
   - 목록 : http://localhost:8080/front-controller/v5/v3/members
  
-----
### FrontControllerServletV5에 ControllerV4 기능 추가
-----
```java
private void initHandlerMappingMap() {
    // ControllerV3
    handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
    handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
    handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

    // ControllerV4
    handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
    handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
    handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
}

private void initHandlerAdapters() {
    // ControllerV3HandlerAdapter 
    handlerAdapters.add(new ControllerV3HandlerAdapter());
    
    // ControllerV4HandlerAdapter
    handlerAdapters.add(new ControllerV4HandlerAdapter());
}
```

1. 핸들러 매핑(handlerMappingMap)에 ControllerV4를 사용하는 컨트롤러 추가, 해당 컨트롤러를 처리할 수 있는 어댑터인 ControllerV4HandlerAdapter도 추가

2. ControllerV4HandlerAdapter
```java
package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v4.ControllerV4;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ControllerV4HandlerAdapter implements MyHandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV4);
    }

    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        ControllerV4 controller = (ControllerV4) handler;

        Map<String, String> paramMap = createParam(request); 
        Map<String, Object> model = new HashMap<>(); // Model Map 추가

        String viewName = controller.process(paramMap, model);

        ModelView modelView = new ModelView(viewName); // ModelView 객체 생성
        modelView.setModel(model); // ModelView에 model 추가

        return modelView; // modelView 반환
    }

    private Map<String, String> createParam(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();

        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));

        return paramMap;
    }
}
```

3. handler가 ControllerV4인 경우에만 처리하는 어댑터
```java
public boolean supports(Object handler) {
    return (handler instanceof ControllerV4);
}
```

4. 실행 로직
    - handler를 ControllerV4로 Casting
    - paramMap, model를 만들어서 해당 컨트롤러 호출
    - viewName을 반환
```java
ControllerV4 controller = (ControllerV4) handler;

Map<String, String> paramMap = createParam(request); 
Map<String, Object> model = new HashMap<>(); // Model Map 추가

String viewName = controller.process(paramMap, model);
```

5. 💡 어댑터 변환
    - 어댑터가 호출하는 ControllerV4는 뷰의 이름을 반환
    - 하지만, 어댑터는 뷰의 이름이 아니라 ModelView를 만들어서 반환 (어댑터가 꼭 필요한 이유)
    - 💡 ControllerV4는 뷰의 이름을 반환했지만, 어댑터는 이것을 ModelView로 만들어서 형식을 맞추어 반환
```java
ModelView modelView = new ModelView(viewName); // ModelView 객체 생성
modelView.setModel(model); // ModelView에 model 추가

return modelView; // modelView 반환
```

6. 어댑터와 ControllerV4
```java
public interface ControllerV4 {
    String process(Map<String, String> paramMap, Map<String, Object> model); 
}
```
```java
public interface MyHandlerAdapter {
    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException;
}
```

7. 실행
   - 등록 : http://localhost:9090/front-controller/v5/v4/members/new-form
   - 목록 : http://localhost:9090/front-controller/v5/v4/members
