package hello.login.web.filter;

import hello.login.web.SessionConst;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {

    private static final String[] whiteList = {"/", "/members/add", "/login", "/logout", "/css/*"};
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 = [{}]", requestURI);

            if(isLoginCheckPath(requestURI)) {
                log.info("인증 체크 로직 실행 = [{}]", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if(session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자 요청 = [{}]", requestURI);

                    // 로그인으로 Redirect
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);

                    return; // 인증을 진행하지 않은 사용자는 더이상 진행하지 않고 끝
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e; // Exception Logging 가능하지만, Tomcat까지 예외를 보내줘야함
        } finally {
            log.info("인증 체크 필터 종료 = [{}]", requestURI);
        }
    }

    /**
     * whiteList의 경우 인증 체크를 하지 않음
     */
    private boolean isLoginCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
