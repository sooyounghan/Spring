package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ControllerV2 {
    // 해당 컨트롤러 조회 후, 해당 컨트롤러 이동 후, 컨트롤러는 해당 뷰로 Forward 되므로, 이 과정을 공통적으로 담고있는 해당하는 View 객체 반환
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
