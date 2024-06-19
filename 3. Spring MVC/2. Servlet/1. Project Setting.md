-----
### 기본 프로젝트 생성 (Spring Boot)
-----
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/d2811dcc-107a-4a0d-811d-700bdec9bc28">
</div>

* 💡 build.gradle Open - Open as Project

1. Packaging : War (JSP를 실행하기 위함)
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/ab0b765a-6736-4aea-87e6-8e67974c5753">
</div>

2. Intellij 무료 버전의 경우 : Building Tools - Gradle 변경
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/320b4669-2213-4ec9-8e2e-7b0cab060b55">
</div>

  + Build and run using : Gradle
  + Run test using : Gradle
  + 해당 이유는 jar 파일의 경우 문제가 없지만, war의 경우 Tomcat이 정상 시작되지 않는 문제 발생
  + 또는 build.gradle의 다음 코드 제거
```java
// providedRuntime 'org.springframework.boot:spring-boot-starter-tomcat'
```

3. Lombok 적용
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/56bb03b7-5c27-482b-98d6-cc83f214b5fe">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/ecface79-f0f1-4673-8053-83dec337cf8b">
</div>

  - File → Setting → plugin → lombok 검색 실행 (재시작)
  - File → Setting → Annotation Processors 검색 → Enable annotation processing 체크 (재시작)
  - 임의의 테스트 클래스를 만들고 @Getter, @Setter 확인

4. Server Port 변경 (application.properties)
```java
server.port=9090
```

5. Postman 설치 : https://www.postman.com/downloads/
