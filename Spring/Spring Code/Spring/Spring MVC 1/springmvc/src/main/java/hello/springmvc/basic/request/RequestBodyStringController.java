package hello.springmvc.basic.request;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class RequestBodyStringController {

    @PostMapping("/request-body-string-v1")
    public void requestBodyString(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);
        response.getWriter().write("OK");
    }

    /**
     * InputStream(Reader) : HTTP 요청 메세지 바디의 내용 직접 조회
     * OutputStream(Writer) : HTTP 응답 메세지의 바디에 직접 결과 출력
     */
    @PostMapping("/request-body-string-v2")
    public void requestBodyStringV2(InputStream inputStream, Writer responseWriter) throws IOException {
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        responseWriter.write("OK");
    }

    /**
     * HttpEntity : Http Header, Body 정보를 편리하게 조회
     *  - 메세지 바디 정보 직접 조회 (@RequestParam X, @ModelAttribute X)
     *  - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     *
     *  응답에서 HttpEntity 사용 가능
     *   - 메세지 바디 정보 직접 반환 (View 조회 X)
     *  - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     */
    @PostMapping("/request-body-string-v3")
    public HttpEntity<String> requestBodyStringV3(RequestEntity<String> requestEntityEntity){
        String messageBody = requestEntityEntity.getBody();

        log.info("messageBody={}", messageBody);
        return new ResponseEntity<>("OK", HttpStatus.CREATED);
    }

    /**
     * HttpEntity : Http Header, Body 정보를 편리하게 조회
     *  - 메세지 바디 정보 직접 조회 (@RequestParam X, @ModelAttribute X)
     *  - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     *
     *  응답에서 HttpEntity 사용 가능
     *   - 메세지 바디 정보 직접 반환 (View 조회 X)
     *  - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     */
    @ResponseBody
    @PostMapping("/request-body-string-v4")
    public String requestBodyStringV4(@RequestBody String messageBody) {
        log.info("messageBody={}", messageBody);

        return "OK";
    }
}
