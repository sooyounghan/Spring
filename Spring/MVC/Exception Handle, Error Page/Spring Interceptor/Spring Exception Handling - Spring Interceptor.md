-----
### 인터셉터 중복 호출 제거
-----
1. LogInterceptor - DispatcherType 로그 추가
```java
package hello.exception.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        request.setAttribute(LOG_ID, uuid);

        // @RequestMapping : HandlerMethod
        // 정적 리소스 : ResourceHttpRequestHandler
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler; // 호출할 컨트롤러 메서드의 모든 정보 포함
        }
        log.info("REQUEST = [{}][{}][{}][{}]", uuid, request.getDispatcherType(), requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle = [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = (String) request.getAttribute(LOG_ID); // preHandle의 logID 받음

        log.info("RESPONSE = [{}][{}][{}][{}]", uuid, request.getDispatcherType(), requestURI, handler);

        if(ex != null) {
            log.error("afterCompletion error = ", ex);
        }
    }
}
```
  - 앞서 필터의 경우 필터를 등록할 때 어떤 DispatcherType인 경우에 필터를 적용할 지 선택 가능
  - 💡 그런데, 인터셉터는 서블릿이 제공하는 기능이 아닌 스프링이 제공하는 기능으로 DispatcherType과 무관하게 항상 호출

2. 인터셉터는 다음과 같이 요청 경로에 따라 추가하거나 제외하기 쉽게 되어있으므로, 이 설정을 통해 오류 페이지 경로를 excludePathPatterns를 사용해서 빼주면 됨
```java
package hello.exception;

import hello.exception.interceptor.LogInterceptor;
import hello.login.web.filter.LogFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "*.ico", "/error", "/error-page/**"); // 오류 페이지 경로 제거
    }

    // @Bean
    public FilterRegistrationBean<Filter> logFiler() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");

        // DispatcherType이 REQUEST, ERROR인 경우 필터 호출
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return filterRegistrationBean;
    }
}
```

  - /error-page/**를 제거하면 error-page/500과 같은 내부 호출 경우에도 인터셉터 호출 (postHandle도 정상 호출이므로 출력)

3. 전체 흐름 정리
   - /hello 정상 요청
     + WAS(/hello, dispatcherType=REQUEST) → Filter → Servlet → Spring Interceptor → Controller → View
   - /error-ex 오류 요청
     + 필터는 DispatcherType으로 중복 호출 제거 (dispatcherType=REUQEST)
     + 인터셉터는 경로 정보로 중복 호출 제거 (excludePathPatterns("/error-page/**"))
     + WAS(/error-ex, dispatcherType=REQUEST) → Filter → Servlet → Spring Interceptor → Controller
     + WAS (여기까지 전파) ← Filter ← Servlet ← Spring Interceptor ← Controller (Exception 발생)
   - WAS 오류 페이지 확인
     + WAS(/error-apge/500, dispatcherType=ERROR) → Filter(X) → Servlet → Spring Interceptor(X) → Controller(/error-page/500) → View
