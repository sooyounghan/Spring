-----
### Logging
-----
1. 운영 시스템에서는 System.out.println() 같은 시스템 콘솔을 사용해서 필요한 정보를 출력하지 않음
2. 별도의 로깅 라이브러리를 사용해서 로그를 출력

-----
### 로깅 라이브러리
-----
1. 스프링 부트 라이브러리를 사용하면 스프링 부트 로깅 라이브러리(spring-boot-starter-logging)가 함께 포함
2. 스프링 부트 로깅 라이브러리는 기본적으로 다음 로깅 라이브러리를 사용
   - SLF4J - http://www.slf4j.org
   - LogBack - http://logback.qos.ch
3. 로그 라이브러리는 LogBack, Log4J, Log4J2 등 수 많은 라이브러리가 존재
4. 이를 통합해서 인터페이스로 제공하는 것이 SLF4J 라이브러리
5. 즉, SLF4J는 인터페이스이며, 그 구현체로 LogBack 같은 로그 라이브러리를 선택하면 됨
6. 스프링 부트가 기본으로 제공하는 Logback을 대부분 사용

-----
### 로그 선언
-----
1. private Logger log = LoggerFactory.getLogger(getClass());
2. private static final Logger log = LogFactory.getLogger(Xxx.class)
3. @Slf4j : Lombok 사용 가능

-----
### 로그 호출
-----
1. log.info("hello")
2. System.out.println("hello")
3. 시스템 콘솔로 직접 출력하는 것보다 로그를 사용하면 여러 장점 존재

-----
### LogTestController
-----
```java
package hello.springmvc.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogTestController {
    private final Logger log = LoggerFactory.getLogger(LogTestController.class);

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        System.out.println("name = " + name); // 어떠한 요청에도 기록
        log.trace("trace log={}", name);
        log.info("info log={}", name);
        log.debug("debug log={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);

        // 💡 로그를 사용하지 않아도 a + b 계산 로직이 먼저 실행됨(리소스 낭비 문제 발생). 이런 방식으로 사용하면 안 됨
        log.debug("String concat log = " + name);
        return "OK";
    }
}
```
1. 실행 : http://localhost:9090/log-test
2. 매핑 정보 - @RestController
   - 💡 @Controller는 반환 값이 String이면 뷰 이름으로 인식되어, 뷰를 찾고 뷰를 렌더링
   - 💡 @RestController는 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메세지 바디에 바로 입력
   - 따라서, 실행 결과로 OK를 받을 수 있음

3. 테스트
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/2fe85e15-85e2-4ed0-bed3-7b3c40d066f2">
</div>

   - 로그가 출력되는 포맷 확인 : 시간 / 로그 레벨 / 프로세스 ID / 쓰레드 명 / 클래스명 / 로그 메세지
   - 로그 레벨을 변경해서 출력
     + 💡 LEVEL : (낮은 등급) TRACE > DEBUG > INFO > WARN > ERROR (높은 등급)
     + 💡 개발 서버는 debug 출력
     + 💡 운영 서버는 info 출력
     + 즉, 필요한 로그 정보만 볼 수 있도록 로그 레벨 설정 가능

4. 로그 레벨 설정 (apllication.properties)
```properties
# 전체 로그 레벨 설정 (기본 info) (운영 서버)
logging.level.root=info

# hello.springmvc 패키지와 그 하위 로그 레벨 설정 (예) debug로 설정) (개발 서버)
logging.level.hello.springmvc=debug
```

5. @Slf4j
```java
package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class LogTestController {
    // private final Logger log = LoggerFactory.getLogger(LogTestController.class);

    @RequestMapping("/log-test")
    public String logTest() {
        String name = "Spring";

        System.out.println("name = " + name);
        log.trace("trace log={}", name);
        log.info("info log={}", name);
        log.debug("debug log={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);

        // 로그를 사용하지 않아도 a + b 계산 로직이 먼저 실행됨. 이런 방식으로 사용하면 안 됨
        log.debug("String concat log = " + name);
        return "OK";
    }
}
```

6. 올바른 로그 사용법
   - 💡 log.debug("data=" + data) : 로그 출력 레벨을 info로 설정해도 해당 코드가 있는 "data=" + data가 실제 실행되어 버림. 결과적으로 문자 더하기 연산 발생
   - log.debug("data={}", data) : 로그 출력 레벨을 info로 설정하면 아무 일도 발생하지 않음. 즉, 앞과 같은 의미 없는 연산이 발생하지 않음

7. 로그 사용 시 장점
   - 쓰레드 정보, 클래스 이름 같은 부가 정보를 함께 볼 수 있고, 출력 모양 조정 가능
   - 로그 레벨에 따라 개발 서버에서는 모든 로그를 출력, 운영 서버에서는 출력하지않는 등 로그를 상황에 맞게 조절 가능
   - 💡 시스템 아웃 콘솔에만 출력하는 것이 아니라, 파일이나 네트워크, 로그를 별도의 위치에 남길 수 있음
   - 💡 특히, 파일로 남길 때는 일별, 특정 용량에 따라 로그 분할하는 것 가능
   - 성능도 System.out보다 좋음 (내부 버퍼링, 멀티 쓰레드 등)
       
* 스프링 부트가 제공하는 로그 기능 참고 : https://docs.spring.io/spring-boot/redirect.html?page=spring-boot-features#boot-features-logging
