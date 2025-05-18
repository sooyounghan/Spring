-----
### ArgumentResolver 활용
-----
1. HomeController 추가
```java
@GetMapping("/")
public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {
    // 세션에 회원 데이터가 없으면 home
    if(loginMember == null) {
        return "home";
    }

    // 세션이 유지되면 로그인으로 이동
    model.addAttribute("member", loginMember);
    return "loginHome";
}
```
  - @Login이라는 애너테이션을 만들어야 컴파일 오류가 사라짐
  - 💡 @Login이라는 애너테이션만 있으면 ArgumentResolver가 동작해서 자동으로 세션에 있는 로그인 회원을 찾아주고, 만약 없다면 null 반환하도록 개발

2. @Login 애너테이션 생성
```java
package hello.login.web.argumentresolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
}
```
  - @Target(ElementType.PARAMETER) : 파라미터에만 사용
  - @Retention(RetentionPolicy.RUNTIME) : 리플렉션 등 활용할 수 있도록 런타임까지 애너테이션 정보가 남아있음

3. HandlerMethodArgumentResolver 구현 - LoginMemberArgumentResolver 생성
```java
package hello.login.web.argumentresolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);

        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("resolverArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);

        if(session == null) {
            return null;
        }
        
        return session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
```
  - supportsParameter() : @Login 애너테이션이 있으면서 Member 타입이면 ArgumentResolver 사용
  - resolverArgument() : 컨트롤러 호출 직전에 호출되어서 필요한 파라미터 정보 생성
    + 💡 여기서는 세션에 있는 로그인 회원 정보인 member 객체를 찾아 반환 (없다면 null 반환)
    + 💡 이후 스프링 MVC는 컨트롤러의 메서드를 호출하면서 여기에서 반환된 member 객체를 파라미터에 전달 (없다면 null 반환)

4. WebMvcConfigurer에 설정 추가
```java
package hello.login.web.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LoginCheckInterceptor;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    // ...
}
```
  - LoginMemberArgumentResolver를 등록
  - 실행하면, 결과는 동일하지만 더 편리하게 로그인 회원 정보 조회 가능
  - ArgumentResolver를 활용하면 공통 작업이 필요할 때 컨트롤러를 더욱 편리하게 사용 가능
