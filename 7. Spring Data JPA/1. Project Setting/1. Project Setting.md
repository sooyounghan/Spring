-----
### 프로젝트 환경
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/a6b34d2c-3289-4acc-9455-1357a9eacbde">
</div>

1. 스프링 부트 3.0 관련 내용 : https://bit.ly/springboot3
2. build.gradle
```gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.3.3'
	id 'io.spring.dependency-management' version '1.1.6'
}

group = 'study'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0'

	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'

	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'

	// JUnit4 추가
	testImplementation("org.junit.vintage:junit-vintage-engine") {
		exclude group: "org.hamcrest", module: "hamcrest-core"
	}
}

tasks.named('test') {
	useJUnitPlatform()
}
```
  - Junit4 기준은 다음과 같음
```gradle
// JUnit4 추가
testImplementation("org.junit.vintage:junit-vintage-engine") {
   exclude group: "org.hamcrest", module: "hamcrest-core"
}
```
  - 테스트 컨트롤러
```java
package study.data_jpa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
```

3. 롬복 적용
  - Preferences → plugin → lombok 검색 실행 (재시작)
  - Preferences → Annotation Processors 검색 → Enable annotation processing 체크 (재시작)
  - 임의의 테스트 클래스를 만들고 @Getter, @Setter 확인

4. 스프링 부트 3.2부터는 JDK 17과 Gradle 선택
<div align="center">
<img src="https://github.com/user-attachments/assets/852f7615-6d77-41f5-810b-5edff6ab2758">
</div>

  - Windows : File → Project Structure (Ctrl + Alt + Shift + S)
  - Mac: File → Project Structure

<div align="center">
<img src="https://github.com/user-attachments/assets/0f299edc-821d-4f19-948c-0c11cd7c9937">
</div>

  - Windows : File → Settings(Ctrl+Alt+S)
  - Mac : IntelliJ IDEA → Preferences(⌘,)
  - Preferences → Build, Execution, Deployment → Build Tools → Gradle 빨간색 박스의 Build and run using를 Gradle로 선택
  - 빨간색 박스의 Build tests using를 Gradle로 선택
  - 빨간색 박스 Gradle JVM을 새로 설치한 자바 17또는 그 이상으로 지정
