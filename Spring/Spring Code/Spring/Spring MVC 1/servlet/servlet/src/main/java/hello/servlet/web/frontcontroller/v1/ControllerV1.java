package hello.servlet.web.frontcontroller.v1;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ControllerV1 {
    // 회원 폼, 회원 저장, 회원 목록 컨트롤러를 해당 인터페이스를 통해 구현
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
