package hello.servlet.web.frontcontroller.v1;

import hello.servlet.web.frontcontroller.v1.controller.MemberFormControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberListControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberSaveControllerV1;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

@WebServlet(name = "frontControllerV1", urlPatterns = "/front-controller/v1/*")
// v1 하위의 어떤 URL 들어와도 해당 서블릿 호출
public class FrontControllerV1 extends HttpServlet {
    private Map<String, ControllerV1> controllerMap = new HashMap<>();

    // 생성자에 Map에 각 URL과 해당하는 객체 생성
    public FrontControllerV1() {
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("FrontControllerV1.service");

        // 요청 URL 중 URI 추출 (/front-controller/v1/*)
        String requestURI = request.getRequestURI();

        // 해당 URI에 대해서 Map에 저장된 Key 값과 비교해 일치하는 컨트롤러 객체 추출
        ControllerV1 controller = controllerMap.get(requestURI);

        // 해당 컨트롤러가 없다면, 404 Error (Not Found) 응답 후, Return
        if(controller == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 존재한다면, 해당 컨트롤러 실행
        controller.process(request, response);
    }
}
