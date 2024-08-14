-----
### 💡 포인트컷, 어드바이스, 어드바이저
-----
1. 포인트컷 (Pointcut)
   - 어디에 부가 기능을 적용할지, 어디에 부가 기능을 적용하지 않을지 판단하는 필터링 로직
   - 클래스와 메서드 이름으로 필터링
   - 이름 그대로 어떤 포인트(Point)에 기능을 적용할지, 하지 않을지 잘라서(cut) 구분

2. 어드바이스 (Advice)
   - 프록시가 호출하는 부가 기능
   - 단순하게 프록시 로직이라 생각하면 됨

3. 어드바이저 (Advisor)
   - 단순하게 하나의 포인트컷과 하나의 어드바이스를 가지고 있는 것
   - 포인트컷 1 + 어드바이스 1
  
4. 즉, 부가 기능 로직을 적용해야 하는데, 포인트 컷으로 어디에 적용할지 선택, 어드바이스로 어떤 로직을 적용할 지 선택, 어디에와 어떤 로직을 모두 알고 있는 것이 어드바이저
5. 조언(Advice)을 어디(Pointcut)에 할 것인가?
6. 조언자(Advisor)는 어디(Pointcut)에 조언(Advice)을 해야할지 알고 있음
7. 역할과 책임
   - 이렇게 구분한 것은 역할과 책임을 명확하게 분리한 것
   - 포인트 컷은 대상 여부를 확인하는 필터 역할만 담당
   - 어드바이스는 깔끔하게 부가 기능 로직만 담당
   - 둘을 합치면 어드바이저가 됨
   - 스프링의 어드바이저는 하나의 포인트컷 + 하나의 어드바이스로 구성

8. 전체 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/a57a7d15-2add-4d3a-9e60-8dead6b955e0">
</div>

-----
### 어드바이저 예제
-----
1. 어드바이저는 하나의 포인트 컷과 하나의 어드바이스를 제공
2. 프록시 팩토리를 통해 프록시를 생성할 떄, 어드바이저를 제공하면 어디에 어떤 기능을 제공할 지 알 수 있음
3. AdvisorTest
```java
package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class AdvisorTest {
    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);

        ServiceInterface proxy = (ServiceInterface) proxyFactory.getProxy();
        
        proxy.save();
        proxy.find();
    }
}
```
  - DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
    + Advisor 인터페이스의 가장 일반적인 구현체
    + 생성자를 통해 하나의 어드바이스를 넣어주면 됨
    + 어드바이저에는 하나의 포인트 컷과 하나의 어드바이스로 구성
    + 💡 Pointcut.TRUE : 항상 true를 반환하는 포인트 컷
    + new TimeAdvice() : TimeAdvice 어드바이스 제공
  - proxyFactory.addAdvisor(advisor);
    + 프록시 팩토리에 적용할 어드바이저를 지정
    + 어드바이저는 내부에 포인트컷과 어드바이스를 모두 가지고 있음
    + 💡 따라서 어디에 어떤 부가 기능을 적용해야 할지, 어드바이저 하나로 알 수 있음
    + 💡 프록시 팩토리를 사용할 때 어드바이저는 필수
  - 과거에는 proxyFactory.addAdvice(new TimeAdvice())로 어드바이스를 바로 적용했는데, 이는 편의 메서드
    + 결과적으로 해당 메서드 내부에서 DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice())의 어드바이저가 생성

<div align="center">
<img src="https://github.com/user-attachments/assets/f396bbc2-1619-4174-944f-89def9eb5282">
</div>

  - 실행 결과
```
# save() 호출
19:42:33.547 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeAdvice 실행
19:42:33.547 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- save 호출
19:42:33.547 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료, resultTime = 0ms

@find() 호출
19:42:33.562 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeAdvice 실행
19:42:33.562 [Test worker] INFO hello.proxy.common.service.ServiceImpl -- find 호출
19:42:33.562 [Test worker] INFO hello.proxy.common.advice.TimeAdvice -- TimeProxy 종료, resultTime = 0ms
```
  - save(), find() 각각 모두 어드바이스가 적용

