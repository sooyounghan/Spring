-----
### 최신 스프링 부트 3.x(2.6 이상)
-----
1. 최신 스프링 부트 3.x(2.6부터)은 Querydsl 5.0을 사용
2. 확인 사항
   - build.gradle 설정 변경
   - PageableExecutionUtils : Deprecated(향후 미지원)으로 인한 패키지 변경
   - Querydsl의 fetchResults(), fetchCount() : Deprecated(향후 미지원)

3. build.gradle 설정
   - https://bit.ly/springboot3
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

	// Querydsl 추가 (스프링 부트 3.0 이상 : jakarta 주의)
	implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
	annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
	annotationProcessor "jakarta.annotation:jakarta.annotation-api"
	annotationProcessor "jakarta.persistence:jakarta.persistence-api"

	// test 롬복 사용
	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'

	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
	useJUnitPlatform()
}

clean {
	delete file('src/main/generated')
}
```

4. PageableExecutionUtils : Deprecated(향후 미지원) 패키지 변경
   - 기능이 Deprecated 된 것이 아닌, 패키지 변경
   - 기존 : org.springframework.data.repository.support.PageableExecutionUtils
   - 변경 : org.springframework.data.support.PageableExecutionUtils

5. Querydsl : fetchResults(), fetchCount() - Deprecated(향후 미지원)
   - 💡 fetchResults(), fetchCount()는 작성한 SELECT 쿼리를 기반으로 COUNT용 쿼리를 내부에서 만들어서 실행
   - 이 기능은 SELECT 구문을 단순히 COUNT 처리하는 용도로 바꾸는 정도로, 따라서 단순한 쿼리에서는 잘 동작하지만, 복잡한 쿼리에서는 제대로 동작하지 않음
   - 따라서, Querydsl에서는 향후 지원하지 않기로 결정했으나, 당장 해당 기능을 제거하지 않음
   - 다음과 같이 COUNT 쿼리 변경
```java
@Test
public void count() {
    Long totalCount = queryFactory
            // .select(Wildcard.count) // SELECT COUNT(*)
            .select(member.count()) // SELECT COUNT(member.id)
            .from(member)
            .fetchOne();
    
    System.out.println("totalCount = " + totalCount);
}
```

  - 💡 COUNT(*)를 사용하고 싶으면 주석처럼 Wildcard.count 사용
  - member.count()는 COUNT(member.id)로 처리
  - 💡 응답 결과는 숫자 하나이므로 fetchOne()

  - MemberRepositoryImpl.searchPageComplex() : SELECT 쿼리와 별개로 COUNT 쿼리 작성하고, fetch()
    + 수정 예제
```java
/**
 * 복잡한 페이징
 * - 데이터 조회 쿼리와, 전체 카운트 쿼리를 분리
 */
@Override
public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
    List<MemberTeamDto> content = queryFactory
            .select(
                    new QMemberTeamDto(
                            member.id,
                            member.username,
                            member.age,
                            team.id,
                            team.name
                    )
            )
            .from(member)
            .join(member.team, team)
            .where(
                    usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe())
            )
            .offset(pageable.getOffset()) // offset
            .limit(pageable.getPageSize()) // limit
            .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(member.count()) // 변경 member.count()
            .from(member)
            .leftJoin(member.team, team)
            .where(
                    usernameEq(condition.getUsername()),
                    teamNameEq(condition.getTeamName()),
                    ageGoe(condition.getAgeGoe()),
                    ageLoe(condition.getAgeLoe())
            );


    // return new PageImpl<>(content, pageable, total);
    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne); // fetchCount -> fetchOne
}
```
