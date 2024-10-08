package hello.servlet.basic.request;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

@WebServlet(name = "requestHeaderServlet", urlPatterns = "/request-header")
public class RequestHeaderServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        printStartLine(request);
        printHeaders(request);
        printHeaderUtils(request);
        printEtc(request);
    }

    private void printStartLine(HttpServletRequest request) {
        System.out.println("--- Request-Line Start ---");

        System.out.println("request.getMetnod() = " + request.getMethod()); // GET
        System.out.println("request.getProtocol() = " + request.getProtocol()); // HTTP/1.1
        System.out.println("request.getScheme() = " + request.getScheme()); // HTTP
        System.out.println("request.getRequestURL() = " + request.getRequestURL()); // http://localhost:9090/request-header
        System.out.println("request.getRequestURI() = " + request.getRequestURI()); // /request-header
        System.out.println("request.getQueryString() = " + request.getQueryString()); // username=hi
        System.out.println("request.isSecure() = " + request.isSecure()); // https 사용 유무

        System.out.println("--- Request-Line End ---");
        System.out.println();
    }

    // Header 모든 정보
    private void printHeaders(HttpServletRequest request) {
        System.out.println("--- Headers Start---");

        /*
         * Eumeration<String> headerNames = request.getHeaderNames();
         * while(headerNames.hasMoreElements()) {
         *      String headerName = headerNames.nextElement();
         *      System.out.println(headerName + " = " + request.getHeader(headerName));
         * }
         */

        request.getHeaderNames().asIterator()
                .forEachRemaining(headerName -> System.out.println(headerName + " = " + request.getHeader(headerName)));

        System.out.println("--- Headers End ---");
        System.out.println();
    }

    // Header 편리한 조회
    private void printHeaderUtils(HttpServletRequest request) {
        System.out.println("--- Header 편의 조회 Start ---");

        System.out.println("[Host 편의 조회]");
        System.out.println("request.getServerName() = " + request.getServerName()); // Host Header
        System.out.println("request.getServerPort() = " + request.getServerPort()); // Host Header
        System.out.println();

        System.out.println("[Accept-Language 편의 조회]");
        request.getLocales().asIterator()
                .forEachRemaining(locale -> System.out.println("locale = " + locale));
        System.out.println("request.getLocale() = " + request.getLocale()); // 가장 높은 우선 순위 locale
        System.out.println();

        System.out.println("[Cookie 편의 조회]");
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                System.out.println("cookie.getName() = " + cookie.getName());
            }
        }
        System.out.println();

        System.out.println("[Content 편의 조회]");
        System.out.println("request.getContentType() = " + request.getContentType());
        System.out.println("request.getContentLength() = " + request.getContentLength());
        System.out.println("request.getCharacterEncoding() = " + request.getCharacterEncoding());
        System.out.println("---- Header 편의 조회 End ---");
        System.out.println();
    }

    private void printEtc(HttpServletRequest request) {
        System.out.println("--- 기타 조회 Start ---");

        System.out.println("[Remote 정보]"); // 요청이 온 것에 대한 정보
        System.out.println("request.getRemoteHost() = " + request.getRemoteHost());
        System.out.println("request.getRemoteAddr() = " + request.getRemoteAddr());
        System.out.println("request.getRemotePort() = " + request.getRemotePort());
        System.out.println();

        System.out.println("[Local 정보]"); // 내 서버에 대한 정보
        System.out.println("request.getLocalName() = " + request.getLocalName());
        System.out.println("request.getLocalAddr() = " + request.getLocalAddr());
        System.out.println("request.getLocalPort() = " + request.getLocalPort());

        System.out.println("--- 기타 조회 End ---");
        System.out.println();
    }
}
