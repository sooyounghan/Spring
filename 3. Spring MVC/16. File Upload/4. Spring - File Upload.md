-----
### MultipartFile 
-----
1. 스프링은 MultipartFile이라는 인터페이스로 멀티파트 파일을 매우 편리하게 지원
2. SpringUploadController
```java
package hello.upload.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/spring")
public class SpringUploadController {

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile() {
        return "upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam String itemName,
                           @RequestParam MultipartFile file,
                           HttpServletRequest request) throws IOException {
        log.info("request = {}", request);
        log.info("itemName= {}", itemName);
        log.info("multipartFile = {}", file);

        if(!file.isEmpty()) {
            String fullPath = fileDir + file.getOriginalFilename();

            log.info("파일 저장 fullPath = {}", fullPath);

            file.transferTo(new File(fullPath));
        }

        return "upload-form";
    }
}
```

3. @RequestParam MultipartFile file
   - 업로드 하는 HTML Form의 name에 맞추어 @RequestParam을 적용
   - 추가로, @ModelAttribute에서도 MultipartFile을 동일하게 적용 가능

4. MultipartFile 주요 메서드
   - file.getOriginalFileName() : 업로드 파일 명
   - file.transferTo(...) : 파일 저장

5. 실행 로그
```
2024-07-22T15:47:13.394+09:00  INFO 11036 --- [upload] [nio-9090-exec-3] h.u.controller.SpringUploadController    : request = org.springframework.web.multipart.support.StandardMultipartHttpServletRequest@7f1553e0
2024-07-22T15:47:13.401+09:00  INFO 11036 --- [upload] [nio-9090-exec-3] h.u.controller.SpringUploadController    : itemName= Spring
2024-07-22T15:47:13.402+09:00  INFO 11036 --- [upload] [nio-9090-exec-3] h.u.controller.SpringUploadController    : multipartFile = org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile@1fc5d276
2024-07-22T15:47:13.404+09:00  INFO 11036 --- [upload] [nio-9090-exec-3] h.u.controller.SpringUploadController    : 파일 저장 fullPath = C:/Users/lxx._.han/Desktop/workSpace/file/20240717_153529.png
```
