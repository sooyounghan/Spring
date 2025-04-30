package hello.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogController {

    @GetMapping("/log")
    public String log() {
        log.trace("trace log"); // 레벨이 낮은 로그
        log.debug("debug log");
        log.info("info log");
        log.warn("warn log");
        log.error("error log"); // 레벨이 높은 로그
        return "OK";
    }
}
