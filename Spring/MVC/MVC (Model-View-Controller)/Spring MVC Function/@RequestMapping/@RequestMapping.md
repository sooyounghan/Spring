-----
### 요청 매핑
-----
```java
package hello.springmvc.basic.requestmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MappingController {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 기본 요청
     * /hello-basic
     * HTTP 메서드 모두 허용(GET, HEAD, POST, PUT, PATCH, DELETE)
     */
    @RequestMapping("/hello-basic")
    public String helloBasic() {
        log.info("hello basic");
        return "OK";
    }
}
```
1. 매핑 정보
   - @Controller : 반환 값이 String이면, 뷰 이름으로 인식되어, 뷰를 찾고 뷰가 렌더링
   - @RestController : 반환 값으로 뷰를 찾는 것이 아니라, HTTP 메세지 바디에 바로 입력
   - 따라서, 실행 결과 OK 메세지를 받을 수 있음

2. @RequestMapping("/hello-basic")
   - /hello-basic URL호출이 오면, 이 메서드가 실행되도록 Mapping
   - Spring-Boot 3.0 이전 (다음, 두 가지 요청은 다른 URL이지만, 스프링은 다음 URL 요청들을 같은 요청들로 매핑)
     + Mapping : /hello-basic
     + URL 요청 : /hello-basic, /hello-basic/
   - 💡 Spring-Boot 3.0 이후 (서로 다른 URL 요청을 사용하므로 다르게 매핑해서 사용)
     + Mapping : /hello-basic → URL 요청 : /hello-basic
     + Mapping : /hello-basic/ → URL 요청 : /hello-basic/
     + 기존에는 마지막에 있는 /(slash)를 제거했지만, 스프링 부트 3.0부터는 마지막의 /(slash) 유지
   - 대부분의 속성을 배열[]로 제공하므로 다중 설정 가능({"/hello-basic", "/hello-go"})

3. HTTP 메서드
    - @RequestMapping에 method 속성으로 HTTP 메서드를 지정하지 않으면, HTTP 메서드와 무관하게 호출
    - 모두 허용 (GET, HEAD, POST, PUT, PATCH, DELETE)
    - HTTP 메서드 매핑
```java
/**
 * method 특정 HTTP 메서드 요청만 허용
 * GET, HEAD, POST, PUT, PATCH, DELETE
 */
@RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)
public String mappingGetV1() {
      log.info("mappingGetV1");
      return "OK";
}
```
   - POST 요청을 하면 Spring MVC는 HTTP 405 상태코드 (Method Not Allowed) 반환 (JSON 형태)

4. HTTP 메서드 매핑 축약
```java
/**
 * 편리한 축약 애너테이션
 * @GetMApping
 * @PostMapping
 * @PutMapping
 * @PatchMapping
 * @DeleteMapping
 */
@GetMapping(value = "/mapping-get-v2")
public String mappingGetV2() {
        log.info("mapping-get-v2");
        return "OK";
}
```
   - HTTP 메서드를 축약한 애너테이션 사용하는 것이 더 직관적
   - 코드를 보면, 내부에서 @RequestMapping과 method를 지정해서 사용하는 것 확인 가능

5. 경로 변수(PathValue) 사용
```java
/**
 * PathVariable 사용
 * 변수명이 같으면 생략 가능
 * @PathVariable("userId") String userId -> @Pathvariable String userId
 */
@GetMapping("/mapping/{userId}")
public String mappingPath(@PathVariable("userId") String data) {
        log.info("mappingPath userId={}", data);
        return "OK";
}
```
   - 실행 : http://localhost:9090/mapping/userA
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/a655b78b-145f-4458-a723-1d228f258166">
</div>

   - 최근 HTTP API는 리소스 경로 식별자를 넣는 스타일 선호
     + /mapping/userA
     + /users/1
   - @RequestMapping은 URL 경로를 템플릿화 할 수 있는데, @PathVariable을 사용하면 매칭 되는 부분을 편리하게 조회 가능
   - @PathVariable의 이름과 파라미터 이름이 같으면 생략 가능

6. PathVariable - 다중
```java
/**
 * PathVariable 다중 사용
 */
@GetMapping("mapping/users/{userId}/orders/{orderId}")
public String mappingPath(@PathVariable String userId, @PathVariable String orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "OK";
}
```
   - 실행 : http://localhost:9090/mapping/users/userA/orders/100
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/fa37efff-19ba-450d-bc73-b994dc5b67a9">
</div>

7. 특정 파라미터 조건 매핑
```java
/**
 * 파라미터로 추가 매핑
 * params = "mode"
 * params = "!mode" (mode라는 이름이 없어야 함)
 * params = "mode=debug" (mode에 대한(Key) Value가 있어야 함)
 * params = "mode!=debug" ( ! = )
 * params = {"mode=debug", "data=good"}
 */
@GetMapping(value = "/mapping-param", params = "mode=debug")
public String mappingParam() {
        log.info("mappingParam");
        return "OK";
}
```
   - 특정 파라미터가 있거나 없는 조건을 추가할 수 있음
   - 실행 : http://localhost:9090/mapping-param?mode=debug

8. 특정 헤더 조건 매핑
```java
/**
 * 특정 헤더로 추가 매핑
 * headers = "mode"
 * headers = "!mode"
 * headers = "mode=debug"
 * headers = "mode!=debug" (! =)
 */
@GetMapping(value = "/mapping-header", headers = "mode=debug")
public String mappingHeader() {
        log.info("mappingHeader");
        return "OK";
}
```
   - http://localhost:9090/mapping-header (Header에 Key가 mode이고, Value가 debug이어야 함)
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/5ec11b27-7a2b-40b0-b953-ee3344317a92">
</div>

   - 파라미터 매핑과 비슷하지만, HTTP 헤더를 사용

9. 미디어 조건 타입 매핑 - HTTP 요청 Content-Type, consumes
```java
/**
 * Content-Type 헤더 기반 추가 매핑 Media Type
 * consumes = "application/json"
 * consumes = "!application/json"
 * consumes = "*\/*"
 * MediaType.APLICATION_JSON_VALUE
 */
@PostMapping("value = /mapping-consume", consumes = "application/json")
public String mappingConsumes() {
        log.info("mappingConsumes");
        return "OK";
}
```
   - PostMan으로 테스트
   - HTTP 요청의 Content-Type 헤더를 기반으로 미디어 타입으로 매핑
   - 만약 맞지 않으면 HTTP 415 상태코드 (Unsupported Media Type)을 반환
   - 예시) consumes
```
consumes = "text/plain"
consumes = {"text/plain", "aplication/*"}
consumes = MediaType.TEXT_PLAIN_VALUE
```

10. 미디어 조건 타입 매핑 - HTTP 요청 Accept, produces
```java
/**
 * Accept 헤더 기반 Media Type
 * produces = "text/html"
 * produces = "!text/html"
 * produces = "text/*"
 * produces = "*\/*"
 */
@PostMapping(value = "/mapping-produce", produces = "text/html")
// @PostMapping(value = "/mapping-produce", produces = MediaType.TEXT_PLAIN_VALUE)
public String mappingProduces() {
        log.info("mappingProduces");
        return "OK";
}
```
   - HTTP 요청의 Accept 헤더를 기반으로 미디어 타입으로 매핑
   - 만약, 맞지 않으면 HTTP 406 상태코드 (Not Aceeptable)을 반환
   - 예시) produces
```
produces = "text/plain"
produces = {"text/plain", "application/*"}
prodcues = MediaType.TEXT_PLAIN_VALUE
produces = "text/plain;charset=UTF-8"
```
