-----
### 프로젝트 생성
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/defe4d29-c272-4ee4-988f-9975b1ff8a36">
</div>

1. 💡 build.gradle에 AOP 관련 추가
```gradle
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

2. 테스트 코드에서도 Lombok을 사용할 수 있도록 다음 코드 추가
```gradle
//테스트에서 lombok 사용
testCompileOnly 'org.projectlombok:lombok' 
testAnnotationProcessor 'org.projectlombok:lombok'
```

3. 참고 : @Aspect를 사용하려면 EnableAspectJAutoProxy를 스프링 설정에 추가해야 하지만, 스프링 부트를 사용하면 자동 추가

4. build.gradle
```gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.3.3'
	id 'io.spring.dependency-management' version '1.1.6'
}

group = 'hello'
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
	implementation 'org.springframework.boot:spring-boot-starter'

	// Spring AOP 추가
	implementation 'org.springframework.boot:spring-boot-starter-aop'

	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'

	// 테스트에서 lombok 사용
	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'
}

tasks.named('test') {
	useJUnitPlatform()
}
```
