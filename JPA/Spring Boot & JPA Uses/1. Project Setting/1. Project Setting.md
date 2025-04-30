-----
### 프로젝트 생성
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/93f11a4d-cd5a-415c-b88f-e2ac8d843b2e">
</div>

- build.gradle
```gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.3.2'
	id 'io.spring.dependency-management' version '1.1.6'
}

group = 'jpabook'
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
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'

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
