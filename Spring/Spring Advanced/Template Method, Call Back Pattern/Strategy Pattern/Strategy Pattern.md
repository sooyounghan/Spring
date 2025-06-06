-----
### 전략 패턴
-----
1. ContextV1Test
```java
package hello.advanced.trace.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV1Test {
    @Test
    void strategyV0() {
        logic1();
        logic2();
    }

    private void logic1() {
        long startTime = System.currentTimeMillis();

        // 비즈니스 로직 실행
        log.info("비즈니스 로직1 실행");
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("resultTime = {}", resultTime);
    }

    private void logic2() {
        long startTime = System.currentTimeMillis();

        // 비즈니스 로직 실행
        log.info("비즈니스 로직2 실행");
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("resultTime = {}", resultTime);
    }
}
```

```
비즈니스 로직1 실행
resultTime = 9

비즈니스 로직2 실행
resultTime = 1
```

2. 템플릿 메서드 패턴은 부모 클래스에 변하지 않는 템플릿을 두고, 변하는 부분을 자식 클래스에 두어서 상속을 사용해 문제를 해결
3. 💡 전략 패턴은 변하지 않는 부분을 Context라는 곳에 두고, 변하는 부분을 Strategy라는 인터페이스를 만들고, 해당 인터페이스를 구현하도록 해서 문제 해결
  - 즉, 상속이 아니라 위임으로 문제를 해결
4. 💡 즉, 전략 패턴에서 Context는 변하지 않는 템플릿 역할, Strategy는 변하는 알고리즘 역할
5. GOF 디자인 패턴에서 정의한 전략 패턴 의도
  - 알고리즘 제품 군을 정의하고(Strategy - StrategyLogic), 각각을 캡슐화하여 상호 교환 가능하게 만들 수 있음
  - 전략을 사용하면 알고리즘을 사용하는 클라이언트(Context)와 독립적으로 알고리즘(Strategy - StrategyLogic)을 변경할 수 있음
<div align="center">
<img src="https://github.com/user-attachments/assets/1a40ae66-2c49-48ce-9757-34002211d668">
</div>

6. Strategy 인터페이스 (테스트코드 (src/test)에 위치)
```java
package hello.advanced.trace.strategy.code.strategy;

public interface Strategy {
    void call();
}
```
  - 변하는 알고리즘 역할을 하는 인터페이스

7. StreategyLogic1 (테스트코드 (src/test)에 위치)
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrategyLogic1 implements Strategy {
    @Override
    public void call() {
        log.info("비즈니스 로직1 실행");
    }
}
```
  - 변하는 알고리즘은 Strategy 인터페이스를 구현하면 됨
  - 여기서는 비즈니스 로직1을 구현

8. StreategyLogic2 (테스트코드 (src/test)에 위치)
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrategyLogic2 implements Strategy {
    @Override
    public void call() {
        log.info("비즈니스 로직2 실행");
    }
}
```
  - 비즈니스 로직2 구현

9. ContextV1 (테스트코드 (src/test)에 위치)
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextV1 {
    /**
     * 필드에 전략을 보관하는 방식
     */

    private Strategy strategy;

    public ContextV1(Strategy strategy) {
        this.strategy = strategy;
    }

    public void execute() {
        long startTime = System.currentTimeMillis();

        // 비즈니스 로직 실행
        strategy.call();
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("resultTime = {}", resultTime);
    }
}
```
  - ContextV1은 변하지 않는 로직을 가지고 있는 템플릿 역할을 하는 코드인데, 전략 패턴에서는 이를 컨텍스트(문맥)이라고 함
  - 즉, 컨텍스트(문맥)은 크게 변하지 않지만, 그 문맥 속 strategy를 통해 일부 전략이 변경된다 생각하면 됨
  - Context는 내부에 Strategy strategy 필드를 가지고 있으며, 이 필드에 변하는 부분인 Strategy의 구현체를 주입하면 됨
  - 💡 전략 패턴의 핵심은 Context는 Strategy 인터페이스에만 의존한다는 점
    + 덕분에 Strategy 구현체를 변경하거나 새로 만들어도, Context 코드에는 영향을 주지 않음
  - 💡 스프링에서 의존 관계 주입에서 사용하는 방식이 전략 패턴

10. Context1V1Test - 추가
```java
/**
 * 전략 패턴 사용
 */
@Test
void strategyV1() {
    StrategyLogic1 strategyLogic1 = new StrategyLogic1();
    ContextV1 contextV1 = new ContextV1(strategyLogic1);
    contextV1.execute();

    StrategyLogic2 strategyLogic2 = new StrategyLogic2();
    ContextV1 contextV2 = new ContextV1(strategyLogic2);
    contextV2.execute();
}
```
```
17:13:11.856 [Test worker] INFO hello.advanced.trace.strategy.code.strategy.StrategyLogic1 -- 비즈니스 로직1 실행
17:13:11.867 [Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 -- resultTime = 15
17:13:11.873 [Test worker] INFO hello.advanced.trace.strategy.code.strategy.StrategyLogic2 -- 비즈니스 로직2 실행
17:13:11.874 [Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 -- resultTime = 1
```

  - 의존관계 주입을 ContextV1에 Strategy의 구현체인 strategyLogic1를 주입받음
  - 즉, Context 안에는 원하는 전략을 주입
  - 이렇게 원하는 모양으로 조립을 완료하고 난 다음 contextV1.execute()를 호출해 context 실행

11. 전략 패턴 실행 그림
<div align="center">
<img src="https://github.com/user-attachments/assets/0bbf33c4-602a-410e-8256-4470cbf0f6d0">
</div>

  - Context에 원하는 Strategy 구현체 주입
  - 클라이언트는 context 실행
  - context는 context 로직 시작
  - context 로직 중간에 strategy.call()를 호출해 주입 받은 strategy 로직 실행
  - context는 나머지 로직 시행

12. 익명 내부 클래스 사용 가능 
  - ContextV1Test 추가
```java
/**
 * 전략 패턴 익명 내부 클래스1
 */
@Test
void strategyV2() {
    Strategy strategyLogic1 = new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직1 실행");
        }
    };

    ContextV1 contextV1 = new ContextV1(strategyLogic1);
    log.info("strategyLogic1 = {}", strategyLogic1.getClass());
    contextV1.execute();

    Strategy strategyLogic2 = new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직2 실행");
        }
    };

    ContextV1 contextV2 = new ContextV1(strategyLogic2);
    log.info("strategyLogic2 = {}", strategyLogic2.getClass());
    contextV2.execute();
}
```
```
[Test worker] INFO hello.advanced.trace.strategy.ContextV1Test -- strategyLogic1 = class hello.advanced.trace.strategy.ContextV1Test$1
[Test worker] INFO hello.advanced.trace.strategy.ContextV1Test -- 비즈니스 로직1 실행
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 -- resultTime = 4
[Test worker] INFO hello.advanced.trace.strategy.ContextV1Test -- strategyLogic2 = class hello.advanced.trace.strategy.ContextV1Test$2
[Test worker] INFO hello.advanced.trace.strategy.ContextV1Test -- 비즈니스 로직2 실행
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 -- resultTime = 0
```
  - 실행 결과를 보면 ContextV1Test$1, ContextV1Test$2와 같이 익명 내부 클래스가 생성된 것 확인 가능

  - ContextV1Test 추가
```java
/**
 * 전략 패턴 익명 내부 클래스2
 */
@Test
void strategyV3() {
    ContextV1 contextV1 = new ContextV1(new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직1 실행");
        }
    });
    contextV1.execute();

    ContextV1 contextV2 = new ContextV1(new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직2 실행");
        }
    });
    contextV2.execute();
}
```
  - 익명 내부 클래스를 변수에 담아두지 말고, 생성하면서 바로 ContextV1에 전달
```
[Test worker] INFO hello.advanced.trace.strategy.ContextV1Test -- 비즈니스 로직1 실행
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 -- resultTime = 13
[Test worker] INFO hello.advanced.trace.strategy.ContextV1Test -- 비즈니스 로직2 실행
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV1 -- resultTime = 0
```

  - ContextV1Test 추가
```java
/**
 * 전략 패턴 익명 내부 클래스3
 */
@Test
void strategyV4() {
    ContextV1 contextV1 = new ContextV1(() -> log.info("비즈니스 로직1 실행"));
    contextV1.execute();

    ContextV1 contextV2 = new ContextV1(() -> log.info("비즈니스 로직2 실행"));
    contextV2.execute();
}
```
  - 익명 내부 클래스를 자바8부터 제공하는 람다로 변경 가능
  - 람다로 변경하려면 인터페이스에 메서드가 1개만 있으면 되는데, 여기에서 제공하는 Strategy 인터페이스는 메서드가 1개 있으므로 람다 사용 가능

-----
### 정리
-----
1. 변하지 않는 부분을 Context에 두고, 변하는 부분을 Strategy를 구현해서 만듬
2. 그리고 Context 내부 필드에 Strategy를 주입해서 사용

-----
### 선 조립, 후 실행
-----
1. Context 내부 필드에 Strategy를 두고 사용하는 부분
   - 이 방식은 Context와 Strategy를 실행 전에 원하는 모양으로 조립해두고 그 다음에 Context를 실행하는, 선 조립, 후 실행 방식에서 매우 유용
   - Context와 Strategy를 한 번 조립하고 나면, 이후로는 Context만 실행하기만 하면 됨
   - 즉, 우리가 스프링으로 애플리케이션을 개발할 때 애플리케이션 로딩 시점에 의존 관계 주입을 통해 필요한 의존관계를 모두 맺어두고 난 다음, 실제 요청을 처리하는 것과 같은 원리

2. 이 방식의 단점은 Context와 Strategy를 조립한 이후에 전략 변경이 번거로움
   - 물론, Context에 setter를 제공해 Strategy를 넘겨 받아 변경하면 되지만, Context를 싱글톤으로 사용할 때 동시성 이슈 등 고려할 점이 많음
   - 그래서 전략을 실시간으로 변경해야 하면, 차라리 이전에 개발한 테스트 코드처럼 Context를 하나 더 생성하고 그 곳에 다른 Strategy를 주입 받는 것이 더 나은 선택일 수 있음

-----
### 직접 파라미터로 전달하여 사용
-----
1. 전략을 실행할 때 직접 파라미터로 전달하여 사용
2. ContextV2 (테스트코드(src/test)에 위치)
```java
package hello.advanced.trace.strategy.code.strategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 전략을 파라미터로 전달 방식
 */
@Slf4j
public class ContextV2 {
    public void execute(Strategy strategy) {
        long startTime = System.currentTimeMillis();

        // 비즈니스 로직 실행
        strategy.call();
        // 비즈니스 로직 종료

        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime;

        log.info("resultTime = {}", resultTime);
    }
}
```
  - 전략을 필드로 가지지 않고, 대신 전략을 execute(...)가 호출될 때 마다 항상 파라미터로 전달 받음

3. ContextV2Test
```java
package hello.advanced.trace.strategy;

import hello.advanced.trace.strategy.code.strategy.ContextV2;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic1;
import hello.advanced.trace.strategy.code.strategy.StrategyLogic2;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContextV2Test {
    /**
     * 전략 패턴 적용
     */
    @Test
    void strategyV1() {
        ContextV2 context = new ContextV2();
        context.execute(new StrategyLogic1());
        context.execute(new StrategyLogic2());
    }
}
```
```
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.StrategyLogic1 -- 비즈니스 로직1 실행
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV2 -- resultTime = 9
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.StrategyLogic2 -- 비즈니스 로직2 실행
[Test worker] INFO hello.advanced.trace.strategy.code.strategy.ContextV2 -- resultTime = 2
```

  - Context와 Strategy를 '선 조립 후 실행'하는 방식이 아니라 Context를 실행할 때마다 전략을 인수로 전달
  - 클라이언트는 Context를 실행하는 시점에 원하는 Streatgy를 전달할 수 있음
  - 따라서, 이전 방식과 비교하면 원하는 전략을 더욱 유연하게 변경 가능
  - 테스트 코드를 보면, 하나의 Context만 생성하며, 하나의 Context에 실행 시점에 여러 전략을 인수로 전달해 유연하게 실행

4. 전략 패턴 파라미터 실행 그림
<div align="center">
<img src="https://github.com/user-attachments/assets/2adf7284-4406-42b3-ade2-cb0578daed6c">
</div>

  - 클라이언트는 Context를 실행하면서 인수로 Strategy 전달
  - Context는 execute() 로직 실행
  - Context는 파라미터로 넘어온 strategy.call() 로직을 실행
  - Context의 execute() 로직 종료

5. ContextV2Test 추가
```java
/**
 * 전략 패턴 익명 내부 클래스 
 */
@Test
void strategyV2() {
    ContextV2 context = new ContextV2();
    context.execute(new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직1 실행");
        }
    });
    context.execute(new Strategy() {
        @Override
        public void call() {
            log.info("비즈니스 로직2 실행");
        }
    });
}
```
  - 물론 익명 내부 클래스 사용 가능
  - 코드 조각을 파라미터로 넘긴다고 생각

```java
/**
 * 전략 패턴 익명 내부 클래스2, 람다
 */
@Test
void strategyV3() {
    ContextV2 context = new ContextV2();
    context.execute(() -> log.info("비즈니스 로직1 실행"));
    context.execute(() -> log.info("비즈니스 로직2 실행"));
}
```
  - 람다를 사용해서 코드를 더 단순하게 만듬

-----
### 정리
-----
1. ContextV1은 필드에 Strategy를 저장하는 방식으로 전략 패턴 구사
   - 선 조립, 후 실행 방법에 적합
   - Context를 실행하는 시점에 이미 조립이 끝났으므로 전략을 신경쓰지 않고 단순히 실행만 하면 됨

2. ContextV2는 파라미터에 Strategy를 전달받는 방식으로 전략 패턴 구사
   - 실행할 때마다 전략을 유연하게 변경 가능
   - 단점 역시 실행할 때마다 전략을 계속 지정해줘야 함

-----
### 템플릿
-----
1. 해결하고 싶은 문제는 변하는 부분과 변하지 않는 부분을 분리
2. 변하지 않는 부분을 템플릿이라 하고, 그 템플릿 안에서 변하는 부분에 약간 다른 코드 조각을 넘겨서 실행하는 것이 목적
3. ContextV1, ContextV2, 두 가지 방식 다 문제 해결이 가능하지만, 우리가 원하는 것은 애플리케이션 의존 관계를 설정하는 것 처럼 선 조립, 후 실행이 아님
4. 단순히 코드를 실행할 때 변하지 않는 템플릿이 있고, 그 템플릿 안에서 원하는 부분만 살짝 다른 코드를 실행하고 싶을 뿐
5. 따라서 실행 시점에 유연하게 실행 코드 조각을 전달하는 ContextV2가 더 적합

