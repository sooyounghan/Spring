package hello.login.web.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
public class SessionInfoController {
    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if(session == null) {
            return "세션이 없습니다.";
        }

        // 세션 데이터 출력
        session.getAttributeNames()
                .asIterator()
                .forEachRemaining(name -> log.info("Session Name = {}, Value = {}", name, session.getAttribute(name)));

        log.info("SessionId = {}", session.getId());
        log.info("MaxInactiveInterval = {}", session.getMaxInactiveInterval());
        log.info("CreationTime = {}", new Date(session.getCreationTime()));
        log.info("LastAccessedTime = {}", new Date(session.getLastAccessedTime()));
        log.info("IsNew = {}", session.isNew());

        return "세션 출력";
    }
}
