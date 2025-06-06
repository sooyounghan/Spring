-----
### 자바 진영의 추운 겨울
-----
1. EJB (Enterprise Java Beans) : 자바 진영의 표준 기술
   - 단점 : 어렵고, 복잡하고, 무엇보다도 느린 속도
   - 이러한 단점과 문제점을 해결하기 위해 등장하기 시작한 Spring, Hiberante

2. Spring
   - EJB 컨테이너 대체
   - 단순하며, 더 좋은 방법하기 위해 Spring을 개발 (현재 사실상 표준 기술)

3. Hibernate
   - EJB Entity Bean 기술을 대체하기 위해 개발
   - JPA(Java Persistence API)의 새로운 표준 정의
<div align="center">
<img src="https://github.com/sooyounghan/Java/assets/34672301/218eb365-d7f5-474f-b629-16492fd5518a">
</div>

   - 자바 표준을 EJB Entity Bean에서 Hibernate를 통해 JPA를 표준 기술을 만듬
   - 현재 JPA 인터페이스와 JPA의 구현체들
<div align="center">
<img src="https://github.com/sooyounghan/Java/assets/34672301/eb991112-8836-46d8-baaa-d74c7b449057">
</div>

-----
### Spring의 역사
-----
1. 2002년 Rod Jonson 책 출간 : EJB의 문제점 지적
   - EJB 없이도 충분히 고품질 확장 애플리케이션을 개발할 수 있음을 보여주고, 30,000라인 이상 기반 기술을 예제 코드로 선보임
   - 지금의 스프링 핵심 개념과 기반 코드가 존재
     + BeanFactory, ApplicationContext, POJO, 제어의 역전, 의존 관계 주입 등
   - 책이 유명해지고, 개발자들이 책의 예제 코드를 프로젝트에 사용

2. 책 출간 직후 Juergen Holler(유겐 휠러), Yann Caroff(얀 카로프)가 Rod Jonson(로드 존슨)에게 오픈 소스 프로젝트 제안
   - 스프링의 핵심 코드 상당수는 유겐 휠러가 지금도 개발
   - 스프링의 이름은 전통적인 J2EE(EJB)라는 겨울을 넘어 새로운 시작이라는 뜻으로 지음
  
3. Spring 역사 - Release
   - 2003년 Spring Framework 1.0 출시 : XML
   - 2006년 Spring Framework 2.0 출시 : XML 편의 기능 지원
   - 2009년 Spring Framework 3.0 출시 : 자바 코드로 설정
   - 2013년 Spring Framework 4.0 출시 : 자바8
   - 2014년 Spring Boot 1.0 출시
   - 2017년 Spring Framework 5.0, Spring Boot 2.0 출시 : Reactive Programming 지원
   - 2020년 9월 Spring Framework 5.2.x, Spring Boot 2.3.x
   - 2024년 4월 기준 Spring Framework 6.0.11, Spring Boot 3.2.2
