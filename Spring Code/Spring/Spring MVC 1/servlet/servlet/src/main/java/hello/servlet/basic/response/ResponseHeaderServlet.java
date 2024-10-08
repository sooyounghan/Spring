package hello.servlet.basic.response;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "responseHeaderServlet", urlPatterns = "/response-header")
public class ResponseHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [Status Line]
        response.setStatus(HttpServletResponse.SC_OK); // = response.setStatus(200); (HTTP 상태 코드 지정)

        // [Response-Header]
        response.setHeader("Content-Type", "text/plain;charset=UTF-8"); // Content-Type Header 지정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // Cache-Control 헤더 지정 (캐시 미사용)
        response.setHeader("Pragma", "no-cache"); // Cache-Control 헤더 지정 (캐시 미사용)
        response.setHeader("my-header", "hello"); // Custom-Header 지정 가능

        // [Header] 편의 메서드
        content(response);
        cookie(response);
        redirect(response);

        // [Message Body]
        PrintWriter writer = response.getWriter();
        writer.println("OK");
    }

    private void content(HttpServletResponse response) {
        // Content-Type: text/plain;Charset=UTF-8
        // Content-Length: 2
        // response.setHeader("Content-Type", "text/plain;Charset=UTF-8");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        // response.setContentLength(2); // 생략 시 자동 생성
    }

    private void cookie(HttpServletResponse response) {
        // Set-Cookie: myCookie=good, Max-Age:60;
        // response.setHeader("Set-Cookie", "myCookie=good; Max-Age=60");
        Cookie cookie = new Cookie("myCookie", "good");
        cookie.setMaxAge(600); // 600초
        response.addCookie(cookie);
    }

    private void redirect(HttpServletResponse response) throws IOException {
        // Status Code 302
        // Location: /basic/hello-form.html

        // response.setStatus(HTTPServletResponse.SC_FOUND); // 302
        // response.setHeader("Location", "/basic/hello-form.html");
        response.sendRedirect("/basic/hello-form.html");
    }
}
