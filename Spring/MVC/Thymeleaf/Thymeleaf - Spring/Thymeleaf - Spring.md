-----
### 타임리프 스프링 통합
-----
1. 기본 메뉴얼 : https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html
2. 스프링 통합 메뉴얼 : https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html
3. 타임리프는 스프링 없이도 동작하지만, 스프링과 통합을 위한 다양한 기능을 편리하게 제공

-----
###  스프링 통합으로 추가되는 기능
-----
1. 스프링의 SpringEL 문법 통합
2. ${@myBean.doSomething()}와 같은 스프링 빈 호출 지원
3. 편리한 폼 관리를 위한 추가 속성
   - th:object(기능 강화, 폼 커맨드 객체 선택)
   - th:field, th:errors, th:errorclass
4. 폼 컴포넌트 기능
   - checkbox, radio button, List 등 편리하게 사용할 수 있는 기능 지원
5. 스프링의 메세지, 국제화 기능의 편리한 통합
6. 스프링의 검증, 오류 처리 통합
7. 스프링의 변환 서비스 통합 (ConversionService)

-----
### 설정 방법
-----
1. 타임리프 템플릿 엔진을 스프링 빈에 등록하고, 타임리프용 뷰 리졸버를 스프링 빈으로 등록하는 방법
   - https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#the-springstandard-dialect
   - https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html#views-and-view-resolvers

2. 스프링 부트는 이러한 부분을 모두 자동화
   - build.gradle에 다음 한 줄을 넣어주면 Gradle은 타임리프와 관련된 라이브러리를 모두 다운받음
   - 스프링 부트는 앞서 설명한 타임리프와 관련된 설정용 스프링 빈을 자동으로 등록
```gradle
implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
```

3. 타임리프 관련 설정을 변경하고 싶으면, application.properties에 추가하면 됨
4. 스프링 부트가 제공하는 타임리프 설정 (thymeleaf 검색 필요)
  - https://docs.spring.io/spring-boot/index.html
