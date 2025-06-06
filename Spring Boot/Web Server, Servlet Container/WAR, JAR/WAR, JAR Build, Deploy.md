-----
### WAR 빌드와 배포
-----
1. WAS에 코드를 빌드하고 배포
2. 프로젝트 빌드
   - 프로젝트 폴더로 이동
   - 프로젝트 빌드
     + ./gradlew build
     + 윈도우 OS : gradlew build
   - WAR 파일 생성 확인
     + build/libs/server-0.0.1-SNAPSHOT.war

3. 참고 : build.gradle에 보면 war 플러그인이 사용된 것 확인 가능 - 이 플러그인이 war 파일 만들어줌
```gradle
plugins {
     id 'java'
     id 'war'
}
```

4. WAR 압축 풀기
   - 우리가 빌드한 war 파일의 압축을 풀어 내용 확인
   - build/libs 폴더 이동
   - 다음 명령어를 사용해 압축 풀기
     + jar -xvf server-0.0.1-SNAPSHOT.war

5. WAR를 푼 결과
   - META-INF
     + MAINFEST.MF
   - WEB-INF
     + classes
       * hello/servlet/TestServlet.class
     + lib
       * jakarta.servlet-api-6.0.0.jar
   - index.html

6. WAR를 푼 결과를 보면 WEB-INF, classes, lib 같은 특별한 폴더들이 보임

-----
### 💡 JAR, WAR
-----
1. JAR
   - 자바는 여러 클래스와 리소스를 묶어서 JAR(Java ARchive)라고 하는 압축 파일 생성 가능
   - 이 파일은 JVM 위에서 직접 실행되거나 또는 다른 곳에서 사용하는 라이브러리로 제공
   - 💡 직접 실행하는 경우에는 main() 메서드 필요하고 MAINFEST.MF 파일에 실행할 메인 메서드가 있는 클래스를 지정해둬야 함
   - 실행 예) java -jar abc.jar
   - Jar는 쉽게 이야기해서 클래스와 관련 리소스를 압축한 단순한 파일
   - 필요한 경우, 이 파일을 직접 실행 가능하고, 다른 곳에서 라이브러리로 사용 가능

2. WAR
   - WAR(Web application ARchive)라는 이름에서 알 수 있듯 WAR 파일은 웹 애플리케이션 서버(WAS)에 배포할 떄 사용하는 파일
   - 💡 JAR 파일이 JVM 위에서 실행한다면, WAR는 웹 애플리케이션 서버 위에서 실행
   - 웹 애플리케이션 서버 위에서 실행되고, HTML 같은 정적 리소스와 클래스 파일을 모두 함께 포함하기 때문에 JAR와 비교해서 구조가 더 복잡
   - 그리고 WAR 구조를 지켜야 함

3. WAR 구조
   - WEB-INF
     + classes : 실행 클래스 모음
     + lib : 라이브러리 모음
     + web.xml : 웹 서버 배치 설정 파일 (생략 가능)

   - index.html : 정적 리소스

   - WEB-INF 폴더 하위는 자바 클래스와 라이브러리, 그리고 설정 정보가 들어가는 곳
   - WEB-INF를 제외한 나머지 영역은 HTML, CSS 같은 정적 리소스가 사용되는 영역
  
-----
### WAR 배포
-----
1. WAR 파일을 실제 톰캣 서버에 배포
2. 배포 방법
  - 톰캣 서버 종료 : ./shutdown.sh
  - 톰캣폴더/webapps 하위 모두 삭제
  - 빌드된 server-0.0.1-SNAPSHOT.war를 복사
  - 톰캣폴더/webapps 하위에 붙여넣기
    + 톰캣폴더/webapps/server-0.0.1-SNAPSHOT.war
  - 이름 변경 : 톰캣폴더/webapps/ROOT.war (ROOT는 대문자)
  - 톰캣 서버 실행 : ./startup.sh

3. 실행 결과 확인
  - http://localhost:9090/index.html
  - http://localhost:9090/test
  - 실행해보면 index.html 정적 파일과 /test로 만들어둔 TestServlet 모두 잘 동작
  - 참고 : 진행이 잘 되지 않으면, 톰캣폴더/logs/catalina.out 로그 확인

4. 실제 서버에서는 이렇게 사용하면 되지만, 개발 단계에서 war 파일을 만들고, 이것을 서버에 복사하여 배포하는 과정 너무 번잡
   - IntelliJ나 Ecplise 같은 IDE는 이 부분을 편리하게 자동화
