-----
### 자바 웹 기술 역사 - 과거 기술
-----
1. Servlet (1997)
   - HTML 생성이 어려움

2. JSP (1999)
   - HTML 생성은 편리
   - 비즈니스 로직까지 많은 역할 담당

3. Servlet, JSP을 조합한 MVC 패턴 사용
   - Model, View, Controller 역할을 나누어 개발

4. MVC Framework (2000년대 초 ~ 2010년 초)
   - MVC 패턴 자동화, 복잡한 웹 기술을 편리하게 사용할 수 있는 다양한 기능 지원
   - 스트럿츠, 웹워크, 스프링 MVC(과거 버전)
  
-----
### 자바 웹 기술 역사 - 현재 사용 기술
-----
1. Annotation 기반 Spring MVC 등장
   - @Controller

2. Spring Boot
   - 스프링 부트는 서버를 내장
   - 과거에는 서버에 WAS를 직접 설치하고, 소스는 war 파일을 만들어 설치한 WAS에 넣어 배포
   - 스프링 부트는 빌드 결과(jar)를 WAS 서버에 포함 (빌드 배포 단순화)

3. 최신 기술 - 스프링 웹 기술의 분화
   - Web Servlet - Spring MVC
   - Web Reactive - Spring WebFlux

-----
### Spring WebFlux
-----
1. 특징
   - 비동기 None Blocking 처리
   - 최소 쓰레드로 최대 성능 (Thread Context Switching 비용 효율화)
   - 함수형 스타일로 개발 (동시처리 코드 효율화)
   - Servlet 기술을 사용하지 않음

2. 단점
   - 기술적 난이도 매우 높음
   - RDB (관계형 데이터베이스) 지원 부족
   - 일반 MVC의 Thread Model도 충분히 빠름
   - 아직 많이 사용하지 않음 (점유율 : 1% 이하)

-----
### 자바 View Template 역사
-----
1. JSP : 속도 느림, 기능 부족
2. Freemarker, Velocity : 속도 문제 해결, 다양한 기능
3. Thymeleaf
   - Natural Template : HTML 모양을 유지하면서 View Template 적용 가능
   - Spring MVC와 강력한 기능 통합
   - 단, 성능은 Freemarker, Velocity가 더 빠름
