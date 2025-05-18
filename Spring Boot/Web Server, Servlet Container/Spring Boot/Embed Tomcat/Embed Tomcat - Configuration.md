-----
### 내장 톰캣 1 - 설정
-----
1. build.gradle
```gradle
plugins {
    id 'java'
}

group = 'hello'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    // 스프링 MVC 추가
    implementation 'org.springframework:spring-webmvc:6.0.4'

    // 내장 톰켓 추가
    implementation 'org.apache.tomcat.embed:tomcat-embed-core:10.1.5'
}

tasks.named('test') {
    useJUnitPlatform()
}

// 일반 Jar 생성
task buildJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
    }
    with jar
}

// Fat Jar 생성
task buildFatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
    }
    duplicatesStrategy = DuplicatesStrategy.WARN
    from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}
```
  - tomcat-embed-core : 톰캣 라이브러리로, 톰캣을 라이브러리로 포함해 톰캣 서버를 자바 코드로 실행할 수 있음
    + 서블릿 관련 코드도 포함
  - buildJar, buildFatjar

-----
### 내장 톰캣 2 - 서블릿
-----
1. 내장 톰캣은 쉽게 이야기해서 톰캣을 라이브러리로 포함하고 자바 코드로 직접 실행하는 것
2. EmbedTomcatServletMain
```java
package hello.embed;

import hello.servlet.HelloServlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;

public class EmbedTomcatServletMain {
    public static void main(String[] args) throws LifecycleException {
        System.out.println("EmbedTomcatServletMain.main");

        // 톰캣 설정
        // 톰캣 생성
        Tomcat tomcat = new Tomcat();

        // 커넥터 생성
        Connector connector = new Connector();
        connector.setPort(9090); // 포트 설정
        tomcat.setConnector(connector); // 톰캣 - 커넥터 설정

        // 서블릿 등록
        Context context = tomcat.addContext("", "/"); // 톰캣 Context 생성
        tomcat.addServlet("", "helloServlet", new HelloServlet()); // 서블릿 설정
        context.addServletMappingDecoded("/hello-servlet", "helloServlet"); // 서블릿에 대한 URL 매핑

        tomcat.start(); // 톰캣 시작
    }
}
```
  - 톰캣 설정 : 내장 톰캣 생성 후, 톰캣이 제공하는 커넥터를 사용해 9090 포트에 연결
```java
// 톰캣 설정
// 톰캣 생성
Tomcat tomcat = new Tomcat();

// 커넥터 생성
Connector connector = new Connector();
connector.setPort(9090); // 포트 설정
tomcat.setConnector(connector); // 톰캣 - 커넥터 설정
```

 - 서블릿 등록 : 톰캣에 사용할 contextPath와 docBase를 지정
   + tom.addServlet()을 통해 서블릿 등록
   + context.addServletMappingDecoded()를 통해 등록한 서블릿 경로와 매핑
```java
Context context = tomcat.addContext("", "/"); // 톰캣 Context 생성
tomcat.addServlet("", "helloServlet", new HelloServlet()); // 서블릿 설정
context.addServletMappingDecoded("/hello-servlet", "helloServlet"); // 서블릿에 대한 URL 매핑
```

  - 톰캣 시작 : tomcat.start() 코드로 톰캣 시작
```java
tomcat.start(); // 톰캣 시작
```
  - 주의 : 실행 시 특정 환경에서 다음과 같은 오류 발생 하는 경우 존재
```
Caused by: java.lang.IllegalArgumentException: The main resource set specified [...\tomcat\tomcat.8080\webapps] is not valid
```
  - 다음과 같은 코드 추가
```java
...

//서블릿 등록
Context context = tomcat.addContext("", "/");

// == 코드 추가 시작==
File docBaseFile = new File(context.getDocBase());

if (!docBaseFile.isAbsolute()) {
    docBaseFile = new File(((org.apache.catalina.Host) context.getParent()).getAppBaseFile(), docBaseFile.getPath());
}

docBaseFile.mkdirs();

// == 코드 추가 종료==
tomcat.addServlet("", "helloServlet", new HelloServlet());
context.addServletMappingDecoded("/hello-servlet", "helloServlet");
tomcat.start();
```

  - 실행 : http://localhost:9090/hello-servlet
  - 결과 : hello servlet!
  - 내장 톰캣을 사용한 덕분에 IDE에 별도의 복잡한 톰캣 설정 없이 main() 메서드만 실행하면 톰캣까지 매우 편리하게 실행 (톰캣을 설치하지 않아도 됨)

3. 참고
   - 내장 톰캣을 직접 다룰 일은 거의 없음
   - 스프링 부트에서 내장 톰캣 관련 부분을 거의 대부분 자동화해서 제공하므로 내장 톰캣을 깊이있게 학습하는 것은 비권장

-----
### 내장 톰캣 3 - 스프링
-----
1. 내장 톰캣에 스프링까지 연동
2. EmbedTomcatSpringMain
```java
package hello.embed;

import hello.spring.HelloConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class EmbedTomcatSpringMain {
    public static void main(String[] args) throws LifecycleException {
        System.out.println("EmbedTomcatSpringMain.main");

        // 톰캣 설정
        // 톰캣 생성
        Tomcat tomcat = new Tomcat();
        // 커넥터 생성
        Connector connector = new Connector();
        connector.setPort(9090); // 커넥터 - 포트 설정
        tomcat.setConnector(connector); // 톰캣 - 커넥터 설정

        // 스프링 컨테이너 생성
        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(HelloConfig.class);

        // 스프링 MVC 디스패처 서블릿 생성 - 스프링 컨테이너와 연결
        DispatcherServlet dispatcher = new DispatcherServlet(appContext);

        // 디스패처 서블릿을 서블릿 컨테이너에 등록
        Context context = tomcat.addContext("", "/");

        // == 코드 추가 시작==
        File docBaseFile = new File(context.getDocBase());

        if (!docBaseFile.isAbsolute()) {
            docBaseFile = new File(((org.apache.catalina.Host) context.getParent()).getAppBaseFile(), docBaseFile.getPath());
        }

        docBaseFile.mkdirs();

        // == 코드 추가 종료==

        tomcat.addServlet("", "dispatcher", dispatcher);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
    }
}
```
   - 스프링 컨테이너를 생성하고, 내장 톰캣에 디스패처 서블릿 등록
   - 실행 : http://localhost:9090/hello-spring
   - 결과 : hello spring!

3. 💡 동작 과정 (main() 메서드 실행)
   - 내장 톰캣을 생성해 9090 포트로 연결하도록 설정
   - 스프링 컨테이너를 만들고 필요한 빈을 등록
   - 스프링 MVC 디스패처 서블릿을 만들고 앞서 만든 스프링 컨테이너에 연결
   - 디스패처 서블릿을 내장 톰캣에 등록
   - 내장 톰캣 실행

4. 서블릿 컨테이너 초기화 코드와 거의 같은 코드
   - 단, 시작점이 main()를 직접 실행하는가, 서블릿 컨테이너가 제공하는 초기화 메서드를 통해서 실행하는가의 차이

-----
### 내장 톰캣 4 - 빌드와 배포 1
-----
1. 애플리케이션에 내장 톰캣을 라이브러리로 포함하는데, 이를 어떻게 빌드하고 배포하는가?
2. 자바의 main() 메서드를 실행하기 위해서는 jar 형식으로 빌드
3. 💡 그리고 jar 안에 META-INF/MAINFEST.MF 파일에 실행할 main() 메서드의 클래스를 지정
  - META-INF/MANIFEST.MF
```
Manifest-Version: 1.0
Main-Class: hello.embed.EmbedTomcatSpringMain
```

  - gradle의 도움을 받아 이 과정을 쉽게 진행할 예정
  - build.gradle - build.jar 참고
```gradle
//일반 Jar 생성
task buildJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
    }
    with jar
}
```

4. 다음과 같이 실행
   - jar 빌드 : ./gradle clean buildJar
   - 윈도우 : gradlew clean buildJar
   - 해당 위치에 jar 파일 생성 (build/libs/embed-0.0.1-SNAPSHOT.jar)
   - jar 파일 실행 : jar 파일이 있는 폴더 이동 후 jar 파일 실행
     + java -jar embed-0.0.1-SNAPSHOP.jar

5. 실행 결과
```
Error: Unable to initialize main class hello.embed.EmbedTomcatSpringMain
Caused by: java.lang.NoClassDefFoundError: org/springframework/web/context/WebApplicationContext
```

  - 내장 톰캣 서버가 실행되는 것이 아니라 오류가 발생
  - 오류 메세지를 보면, 스프링 관련 클래스를 찾을 수 없다는 것
  - 문제 확인을 위해 jar 파일 압축 해제
    + jar 압축 풀기
      * build/libs 폴더로 이동
      * jar -xvf embed-0.0.1-SNAPSHOT.jar 명령어로 압축 해제

    + JAR를 푼 결과
      * META-INF
        * MANIFEST.MF
      * hello
        * servlet
          * HelloServlet.class
        * embed
          * EmbedTomcatSpringMain.class
          * EmbedTomcatServletMain.class
        * spring
          * HelloConfig.class
          * HelloController.class

   - JAR를 푼 결과를 보면, 스프링 라이브러리나 내장 톰캣 라이브러리가 전혀 보이지 않음
   - 즉, 해당 오류가 발생한 것

   - WAR로 압축 해제 한 결과
     + WEB-INF
       * classes
          * hello/servlet/TestServlet.class
       * lib
         * jakarta.servlet-api-6.0.0.jar
     + index.html
     + WAR는 내부에 라이브러리 역할을 하는 jar 파일을 포함하고 있음

6. 💡 jar 파일은 jar 파일을 포함할 수 없음
   - WAR와 다르게 JAR 파일은 내부에 라이브러리 역할을 하는 JAR를 포함할 수 없음
   - 포함한다고 해도 인식이 되지 않으며, JAR 파일 스펙의 한계
   - 그렇다고 WAR를 사용할 수 없으며, WAR는 웹 애플리케이션 서버 (WAS) 위에서만 실행
   - 대안으로는 라이브러리 jar 파일을 모두 구해서 MAINFEST 파일에 해당 경로를 적어주면 인식이 되지만, 매우 번거로움
   - 또한, jar 파일 안에 Jar 파일을 포함할 수 없기 때문에 라이브러리 역할을 하는 jar 파일도 항상 함께 가지고 다녀야 하므로 권장하지 않는 방법

-----
### 내장 톰캣 5 - 빌드와 배포 2
-----
1. FatJar
   - 대안으로 fat jar 또는 uber jar라고 불리는 방법
   - Jar 안에 Jar를 포함할 수 없는데, 하지만 클래스는 얼마든지 포함 가능
   - 라이브러리에 사용되는 jar를 풀면 class들이 나오는데, 이 class를 뽑아서 새로 만드는 jar에 포함하는 것
   - 이렇게 하면 수 많은 라이브러리에서 나오는 class 때문에 뚱뚱한(fat) jar 탄생하여, Fat Jar라고 불림

2. build.gradle - buildFatJar 참고
```gradle
// Fat Jar 생성
task buildFatJar(type: Jar) {
    manifest {
        attributes 'Main-Class': 'hello.embed.EmbedTomcatSpringMain'
    }
    duplicatesStrategy = DuplicatesStrategy.WARN
    from { configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}
```
  - jar 빌드 : ./gradlew clean buildFatJar
  - 윈도우 : gradlew clean buildFatJar
  - 빌드 시 Encountered duplicated path 경고가 나올 수 있는데, 이 부분은 무시
  - build/libs/embed-0.0.1-SNAPSHOT.jar 생성 (용량은 10M 이상의 큰 사이즈)

3. jar 파일 실행 : jar 파일이 있는 폴더에서 java -jar embed-0.0.1-SNAPSHOT.jar 파일 실행
  - 실행 결과
```
EmbedTomcatSpringMain.main

...

INFO: Starting Servlet engine: [Apache Tomcat/10.1.5]

...

INFO: Starting ProtocolHandler ["http-nio-9090"]

...

HelloController.hello
```
  - 실행 : http://localhost:9090/hello-spring
  - 결과 : hello spring!
  - 정상 동작

4. jar 압축 풀기
   - build/libs 폴더로 이동해, jar -xvf embed-0.0.1-SNAPSHOT.jar 명령어로 압축 해제
   - Jar를 풀어보면 우리가 만든 클래스를 포함해서, 수 많은 라이브러리에서 제공하는 클래스들이 포함되어 있음

5. 💡 Fat Jar 정리
   - 장점
     + Fat Jar 덕분에 하나의 jar 파일에 필요한 라이브러리 내장 가능
     + 내장 톰캣 라이브러리를 jar 내부에 내장 가능
     + 덕분에 하나의 jar 파일로 배포부터, 웹 서버와 설치, 실행까지 모든 것 단순화 가능

   - 참고 : WAS 단점과 해결
     + 톰캣 같은 WAS를 별도 설치
       * 해결 : WAS를 별도로 설치하지 않아도 됨. 톰캣 같은 WAS가 라이브러리로 jar 내부에 포함되어 있음

     + 개발 환경 설정이 복잡함
       * 단순한 자바라면 별도 설정을 고민하지 않고, main() 메서드만 실행하면 됨
       * 웹 애플리케이션은 WAS를 연동하기 위한 복잡한 설정이 들어감
       * 해결 : IDE에 복잡한 WAS 설정이 필요하지 않고, 단순히 main() 메서드만 실행
      
     + 배포 과정이 복잡. WAR를 만들고 이를 또 WAS에 전달해서 배포
       * 해결 : 배포 과정이 단순. JAR를 만들고 이를 원하는 위치에서 실행하면 됨

     + 톰캣의 버전을 업데이트하면 다시 톰캣 설치
       * 해결 : gradle에서 내장 톰캣 라이브러리의 버전만 변경하고 빌드 후 실행

   - 단점
      + 어떤 라이브러리가 포함되어 있는지 확인하기 어려움
        * 모두 class로 풀려있으니 어떤 라이브러리가 사용되고 있는지 추적하기 어려움

      + 파일명 중복 해결 불가
        * 클래스나 리소스 명이 같은 경우 하나를 포기 (이는 심각한 문제 발생)
        * 예를 들어, 서블릿 컨테이너 초기화 관련 META-INF/services/jakarta.servlet.ServletContainerInitializer 파일이 여러 라이브러리(jar)에 존재 가능
        * A 라이브러리와 B 라이브러리 둘 다 해당 파일을 사용해서 서블릿 컨테이너를 초기화 시도
        * 둘 다 해당 파일을 jar 안에 포함
        * Fat Jar를 만들면 파일명이 같으므로 A, B 라이브러리가 둘 다 가지고 있는 파일 중 하나의 파일만 선택
        * 즉, 결과적으로 나머지 하나는 포함되지 않으므로 정상 동작하지 않음
