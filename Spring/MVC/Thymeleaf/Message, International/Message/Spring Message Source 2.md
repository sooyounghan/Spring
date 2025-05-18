-----
### 스프링 메세지 소스 사용
-----
1. MessageSource 인터페이스
```java
public interface MessageSource {
	@Nullable
	String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);

	String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;
}
```
  - 코드를 포함한 일부 파라미터로 메세지를 읽어오는 기능 제공

2. 테스트 코드
```java
package hello.itemservice.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource messageSource;

    @Test
    void helloMessage() {
        String result = messageSource.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }
}
```
  - messageSource.getMessage("hello", null, null)
    + code : hello
    + args : null
    + locale : null

  - 메세지 코드로 hello를 입력하고, 나머지 값은 null 입력
  - locale 정보가 없으면, basenames에서 설정한 기본 이름 메세지 파일 조회
  - basenames으로 messages를 지정했으므로 messages.properties 파일에서 데이터를 조회

-----
### MessageSourceTest 추가 - 메세지가 없는 경우, 기본 메세지
-----
```java
@Test
void notFoundMessageCode() {
    assertThatThrownBy(() -> messageSource.getMessage("no_code", null, null))
            .isInstanceOf(NoSuchMessageException.class);
}

@Test
void notFoundMessageCodeDefaultMessage() {
    String result = messageSource.getMessage("no_code", null, "기본 메세지", null);
    assertThat(result).isEqualTo("기본 메세지");
}
```
  - 메세지가 없는 경우 NoSuchMessageException 발생
  - 메세지가 없어도 기본 메세지(defaultMessage)를 사용하면 기본 메세지 반환

-----
### MessageSourceTest 추가 - 매개변수 사용
-----
```java
@Test
void argumentMessage() {
    String result = messageSource.getMessage("hello.name", new Object[]{"Spring"}, null);
    assertThat(result).isEqualTo("안녕 Spring");
}
```
  - 다음 메세지의 {0} 부분은 매개변수로 전달해서 치환할 수 있음
  - hello.name=안녕 {0} → Spring 단어를 매개변수로 전달 → 안녕 Spring

-----
### 국제화 파일 선택
-----
1. locale 정보를 기반으로 국제화 파일 선택
   - Locale이 en_US인 경우 messages_en_US → messages_en → messages 순서로 찾음
   - Local에 맞추어 구체적인 것이 있으면 구체적인 것을 찾고, 없으면 디폴트를 찾음
  
2. MessageSourceTest 추가 - 국제화 파일 선택1
```java
@Test
void defaultLang() {
    assertThat(messageSource.getMessage("hello", null, null)).isEqualTo("안녕");
    assertThat(messageSource.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
}
```
  - messageSource.getMessage("hello", null, null) : locale 정보가 없으면, Locale.getDefault() 호출해서 시스템 기본 로케일 사용
    + 예) local = null이면, 시스템 기본 locale이 ko_KR 이므로 messages_ko.properties 조회 시도 - 조회 실패 - messages.properties 조회
  - messageSoruce.getMessage("hello", null, Locale.KOREA) : locale 정보가 있지만, messages_ko가 없으므로 messages 사용

2. MessageSourceTest 추가 - 국제화 파일 선택2
```java
@Test
void enLang() {
    assertThat(messageSource.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
}
````
  - messageSource.getMessage("hello", null, Locale.ENGLISH)) : locale 정보가 Locale.ENGLISH 이므로 messages_en을 찾아서 사용
