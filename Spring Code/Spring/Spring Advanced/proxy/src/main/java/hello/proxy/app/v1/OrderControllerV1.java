package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.*;

@RestController // 스프링 부트 3.0 이상 부터는 @Controller, @RestController 애너테이션이 있어야 스프링 컨트롤러로 인식
public interface OrderControllerV1 {
    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);

    @GetMapping("/v1/no-log")
    String noLog();
}
