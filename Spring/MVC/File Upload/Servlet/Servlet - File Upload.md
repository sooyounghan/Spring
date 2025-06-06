-----
### 서블릿 - 파일 업로드
-----
1. ServletUploadControllerV1
```java
package hello.upload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v1")
public class ServletUploadControllerV1 {
    
    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }
    
    @PostMapping("/upload")
    public String saveFile1V1(HttpServletRequest request) throws ServletException, IOException {
        log.info("request = {}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName = {}", itemName);

        Collection<Part> parts = request.getParts();
        log.info("parts = {}", parts);

        return "upload-form";
    }
}
```
  - request.getParts() : multipart/form-data 전송 방식에서 각각 나누어진 부분을 받아서 확인 가능

2. resources/templates/upload-form.html
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8">
</head>

<body>
<div class="container">
  <div class="py-5 text-center">
    <h2>상품 등록 폼</h2>
  </div>
  <h4 class="mb-3">상품 입력</h4>

  <form th:action method="post" enctype="multipart/form-data">
    <ul>
      <li>상품명 <input type="text" name="itemName"></li>
      <li>파일<input type="file" name="file" ></li>
    </ul>
    <input type="submit"/>
  </form>

</div> <!-- /container -->
</body>
</html>
```

3. 테스트를 진행하기 전 다음 옵션 추가 (application.properties)
```properties
logging.level.org.apache.coyote.http11=trace
```
  - Spring Boot 3.2 부터는 debug 대신 trace 사용해야 로그 출력
  - 이 옵션을 사용하면 HTTP 요청 메세지 확인 가능

4. 실행 (http://localhost:8080/servlet/v1/upload)
  - 실행해보면 logging.level.org.apache.coyote.http11 옵션을 통한 로그에서 multipart/form-data 방식으로 전송된 것 확인 가능
  - 결과 로그
```
...

Content-Type: multipart/form-data; boundary=----WebKitFormBoundarylz6XDifLkZXBNsA3

...

------WebKitFormBoundarylz6XDifLkZXBNsA3
Content-Disposition: form-data; name="itemName"

123
------WebKitFormBoundarylz6XDifLkZXBNsA3
Content-Disposition: form-data; name="file"; filename="20240720_142321.png"
Content-Type: image/png

...
------WebKitFormBoundarylz6XDifLkZXBNsA3--

2024-07-20T15:06:34.802+09:00  INFO 7556 --- [upload] [nio-9090-exec-7] h.u.c.ServletUploadControllerV1          : request = org.springframework.web.multipart.support.StandardMultipartHttpServletRequest@4bdedea
2024-07-20T15:06:34.802+09:00  INFO 7556 --- [upload] [nio-9090-exec-7] h.u.c.ServletUploadControllerV1          : itemName = 123
2024-07-20T15:06:34.802+09:00  INFO 7556 --- [upload] [nio-9090-exec-7] h.u.c.ServletUploadControllerV1          : parts = [org.apache.catalina.core.ApplicationPart@a7a130d, org.apache.catalina.core.ApplicationPart@e45869a]
```

-----
### Multi-Part 사용 옵션
-----
1. 업로드 사이즈 제한
```properties
spring.servlet.multipart.max-file-size=1MB
srping.servlet.multipart.max-request-size=10MB
```
  - 큰 파일을 무제한 업로드 하게 할 수 없으므로 업로드 사이즈 제한 가능
  - 사이즈를 넘으면 예외(SizeLimitExceededException) 발생
  - 💡 max-file-size : 파일 하나 최대 사이즈 (기본 1MB)
  - 💡 max-request-size : 멀티파트 요청 하나에 여러 파일 업로드 가능, 그 전체의 합 (기본 10MB)

2. spring.servlet.multipart.enabled 끄기
```properties
spring.servlet.multipart.enabled=false
```
  - 결과 로그
```
2024-07-20T15:20:08.902+09:00  INFO 16124 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV1          : request = org.apache.catalina.connector.RequestFacade@3e62d11e
2024-07-20T15:20:08.905+09:00  INFO 16124 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV1          : itemName = null
2024-07-20T15:20:08.905+09:00  INFO 16124 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV1          : parts = []
```

3. 멀티파트는 일반적인 폼 요청인 application/x-www-form-urlencoded보다 훨씬 복잡
   - spring.servlet.multipart.enabled 옵션을 끄면, 서블릿 컨테이너는 멀티파트와 관련된 처리를 하지 않음
   - 따라서, 결과를 보면 request.getParameter("itemName"), request.getParts()의 결과가 비어있음

4. spring.servlet.multipart.enabled 켜키
```properties
spring.servlet.multipart.enabled=true
```
  - 기본값 : true
  - 이 옵션을 켜면, 스프링 부트는 서블릿 컨테이너에게 멀티파트 데이터를 처리하라고 설정
  - 결과 로그
```
2024-07-20T15:06:34.802+09:00  INFO 7556 --- [upload] [nio-9090-exec-7] h.u.c.ServletUploadControllerV1          : request = org.springframework.web.multipart.support.StandardMultipartHttpServletRequest@4bdedea
2024-07-20T15:06:34.802+09:00  INFO 7556 --- [upload] [nio-9090-exec-7] h.u.c.ServletUploadControllerV1          : itemName = 123
2024-07-20T15:06:34.802+09:00  INFO 7556 --- [upload] [nio-9090-exec-7] h.u.c.ServletUploadControllerV1          : parts = [org.apache.catalina.core.ApplicationPart@a7a130d, org.apache.catalina.core.ApplicationPart@e45869a] // parts=[ApplicationPart1, ApplicationPart2]
```
  - request.getParmater("itemName")의 결과도 출력. request.getParts()에도 요청한 두 가지 멀티 파트의 부분 데이터가 포함된 것 확인 가능
  - 이 옵션을 켜면 복잡한 멀티파트 요청을 처리해서 사용할 수 있도록 제공
  - 로그를 보면 HttpServletRequest 객체가 RequestFacade → StandardMutlipartHttpServletRequest로 변한 것 확인 가능

5. 참고로 spring.servlet.multipart.enabled 옵션을 켜면, 스프링의 DispatcherServlet에서 멀티파트 리졸버(MultipartResolver)를 실행
   - 멀티파트 리졸버는 멀티파트 요청인 경우, 서블릿 컨테이너가 전달하는 일반적인 HttpServletReqeuest를 MultipartHttpServletRequest로 변환해서 반환
   - MultipartHttpServletRequest는 HttpServletRequest의 자식 인터페이스이고, 멀티파트와 관련된 추가 기능 제공

6. 스프링이 제공하는 기본 멀티파트 리졸버는 MultipartHttpServletRequest 인터페이스를 구현한 StandardMultipartHttpServletRequest를 반환
   - 이제 컨트롤러에서 HttpServletRequest 대신, MultipartHttpServletRequest를 주입받을 수 있는데, 이를 사용하면 멀티파트와 관련된 여러 가지 처리 편리하게 가능
   - 그런데, MultipartFile을 사용하는 것이 더 편하므로 MultipartHttpServletRequest를 잘 사용하지 않음
  
-----
### Part
-----
1. 파일 업로드를 위한 실제 파일 경로 지정
   - 💡 해당 경로에 실제 폴더 생성 후, 절대 경로 입력
   - application.properties
```properties
예) file.dir=C:/Users/lxx._.han/Desktop/workSpace/file
```
   - 마지막에 /(슬래시) 포함 주의

2. ServletUploadControllerV2
```java
package hello.upload.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
@Controller
@RequestMapping("/servlet/v2")
public class ServletUploadControllerV2 {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }
    
    @PostMapping("/upload")
    public String saveFile1V2(HttpServletRequest request) throws ServletException, IOException {
        log.info("request = {}", request);

        String itemName = request.getParameter("itemName");
        log.info("itemName = {}", itemName);

        Collection<Part> parts = request.getParts();
        log.info("parts = {}", parts);

        for (Part part : parts) {
            log.info("===== PART =====");
            log.info("name = {}", part.getName());
            Collection<String> headerNames = part.getHeaderNames();
            for (String headerName : headerNames) {
                log.info("header {} : {}", headerName, part.getHeader(headerName));
            }

            // 편의 메서드
            // Content-Disposition; fileName
            log.info("submittedFileName = {}", part.getSubmittedFileName());
            log.info("size = {}" , part.getSize());

            // 데이터 읽기
            InputStream inputStream = part.getInputStream();
            String body = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            log.info("body = {}", body);

            // 파일에 저장하기
            if(StringUtils.hasText(part.getSubmittedFileName())) {
                String fullPath = fileDir + part.getSubmittedFileName();
                log.info("파일 저장 fullPath = {}", fullPath);
                part.write(fullPath);
            }
        }
        return "upload-form";
    }
}
```

3. fileDir
```java
@Value("${file.dir}")
private String fileDir;
```
   - application.properties에서 설정한 file.dir 값을 주입
   - 멀티파트 형식은 전송 데이터를 하나하나 각 부분(Part)로 나누어 전송
   - parts에서는 나누어진 데이터가 각각 담김
   - 서블릿이 제공하는 Part는 멀티파트 형식을 편리하게 읽을 수 있는 다양한 메서드 제공

4. Part 주요 메서드
   - part.getSubmittedName() : 클라이언트가 전달한 파일명
   - part.getInputStream() : Part의 전송 데이터 읽기
   - part.write(...) : Part를 통해 전송된 데이터 저장

5. 결과 로그
```
2024-07-22T15:30:52.731+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : ===== PART =====
2024-07-22T15:30:52.737+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : name = itemName
2024-07-22T15:30:52.738+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : header content-disposition : form-data; name="itemName"
2024-07-22T15:30:52.741+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : submittedFileName = null
2024-07-22T15:30:52.742+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : size = 6
2024-07-22T15:30:52.776+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : body = Spring
2024-07-22T15:30:52.776+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : ===== PART =====
2024-07-22T15:30:52.776+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : name = file
2024-07-22T15:30:52.777+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : header content-disposition : form-data; name="file"; filename="20240717_153529.png"
2024-07-22T15:30:52.778+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : header content-type : image/png
2024-07-22T15:30:52.779+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : submittedFileName = 20240717_153529.png
2024-07-22T15:30:52.781+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : size = 25357
2024-07-22T15:30:52.789+09:00  INFO 17240 --- [upload] [nio-9090-exec-1] h.u.c.ServletUploadControllerV2 : body = �PNG
```

6. 큰 용량 파일을 업로드 테스트를 할 때는 로그가 많이 남으므로 다음 옵션 제거
```properties
logging.level.org.apache.coyote.http11=trace
```
   - 또한, 바이너리 데이터 출력 로그도 제거
```java
log.info("body = {}, body);
```

8. 서블릿이 제공하는 Part는 편하기 하지만, HttpServletRequest를 사용해야하고, 추가로 파일 부분만 구분하려면 여러 코드 필요
