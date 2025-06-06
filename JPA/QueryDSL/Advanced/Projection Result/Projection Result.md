-----
### 프로젝션 결과 반환 - 기본
-----
1. 프로젝션 : SELECT 대상 지정
2. 프로젝션 대상 하나
```java
@Test
public void simpleProjection() {
    List<String> result = queryFactory.select(member.username)
            .from(member)
            .fetch();

    for (String s : result) {
        System.out.println("s = " + s);
    }
}
```
```
s = member1
s = member2
s = member3
s = member4
```

  - 프로젝션 대상이 하나면, 타입을 명확하게 지정 가능
  - 프로젝션 대상이 둘이면 튜플이나 DTO로 조회

3. 튜플 조회
   - 프로젝션 대상이 둘 이상일 때 사용
   - 💡 가급적 레포지토리 계층에서만 사용할 것
     + 레포지토리 계층에서 사용하는 하부 기술을 앞단 컨트롤러나 서비스 계층까지 노출되는 것이 권장하지 않음
     + 즉, 튜플은 Querydsl에 의존적이므로 이 기술이 노출되는 것이 좋지 않음
   - com.querydsl.core.Tuple
```java
@Test
public void tupleProejction() {
    List<Tuple> result = queryFactory.select(member.username, member.age)
            .from(member)
            .fetch();

    for (Tuple tuple : result) {
        String username = tuple.get(member.username);
        Integer age = tuple.get(member.age);

        System.out.println("username = " + username);
        System.out.println("age = " + age);
    }
}
```
```
username = member1
age = 10
username = member2
age = 20
username = member3
age = 30
username = member4
age = 40
```

-----
### 프로젝션과 결과 반환 - DTO 조회
-----
1. 순수 JPA에서 DTO 조회
2. MemberDto
```java
package study.querydsl.dto;

import lombok.Data;

@Data
public class MemberDto {
    private String username;
    private int age;

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

3. 순수 JPA에서 DTO 조회 코드
```java
@Test
public void findDtoByJPQL() {
    List<MemberDto> result = em.createQuery("SELECT new study.querydsl.dto.MemberDto(m.username, m.age) FROM Member m", MemberDto.class)
            .getResultList();

    for (MemberDto memberDto : result) {
        System.out.println("memberDto = " + memberDto);
    }
}
```
```
memberDto = MemberDto(username=member1, age=10)
memberDto = MemberDto(username=member2, age=20)
memberDto = MemberDto(username=member3, age=30)
memberDto = MemberDto(username=member4, age=40)
```

  - 순수 JPA에서 DTO를 조회할 때 new 명령어 사용해야 함
  - DTO의 package 이름을 다 적어줘야 하므로 지저분
  - 💡 생성자 방식만 지원
    
4. Querydsl 빈 생성 (Bean Population)
  - 결과를 DTO로 반환할 때 사용
  - 다음 3가지 방법 지원
    + 프로퍼티 접근
    + 필드 직접 접근
    + 생성자 사용

  - 프로퍼티 접근 - Setter
    + 기본 생성자 필요
```java
package study.querydsl.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    private String username;
    private int age;

    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```

```java
@Test
public void findDtoBySetter() {
    List<MemberDto> result = queryFactory.select(Projections.bean(MemberDto.class,
                    member.username,
                    member.age))
            .from(member)
            .fetch();

    for (MemberDto memberDto : result) {
        System.out.println("memberDto = " + memberDto);
    }
}
```

```
memberDto = MemberDto(username=member1, age=10)
memberDto = MemberDto(username=member2, age=20)
memberDto = MemberDto(username=member3, age=30)
memberDto = MemberDto(username=member4, age=40)
```

  - 필드 직접 접근
```java
@Test
public void findDtoByField() {
    List<MemberDto> result = queryFactory.select(Projections.fields(MemberDto.class,
                    member.username,
                    member.age))
            .from(member)
            .fetch();

    for (MemberDto memberDto : result) {
        System.out.println("memberDto = " + memberDto);
    }
}
```
```
memberDto = MemberDto(username=member1, age=10)
memberDto = MemberDto(username=member2, age=20)
memberDto = MemberDto(username=member3, age=30)
memberDto = MemberDto(username=member4, age=40)
```

  - 별칭이 다를 때
```java
package study.querydsl.dto;

import lombok.Data;

@Data
public class UserDto {
    private String name;
    private int age;
}
```

  - 프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
```java
@Test
public void findUserDtoByField() {
    QMember memberSub = new QMember("memberSub");

    List<UserDto> result = queryFactory.select(Projections.fields(UserDto.class,
                    member.username.as("name"), // member.username = null -> as name : 값 입력

                    ExpressionUtils.as(JPAExpressions.select(memberSub.age.max())
                                    .from(memberSub), "age")))
            .from(member)
            .fetch();

    for (UserDto userDto : result) {
        System.out.println("userDto = " + userDto);
    }
}
```
  - ExpressionUtils.as(source, alias) : 필드나 서브 쿼리에 별칭 적용
  - username.as("memberName") : 필드에 별칭 적용
```
userDto = UserDto(name=member1, age=40)
userDto = UserDto(name=member2, age=40)
userDto = UserDto(name=member3, age=40)
userDto = UserDto(name=member4, age=40)
```

  - 생성자 사용
```java
@Test
public void findDtoByConstructor() {
    List<MemberDto> result = queryFactory.select(Projections.constructor(MemberDto.class,
                    member.username,
                    member.age))
            .from(member)
            .fetch();

    for (MemberDto memberDto : result) {
        System.out.println("memberDto = " + memberDto);
    }
}
```
```
memberDto = MemberDto(username=member1, age=10)
memberDto = MemberDto(username=member2, age=20)
memberDto = MemberDto(username=member3, age=30)
memberDto = MemberDto(username=member4, age=40)
```
  - 생성자가 없으면 런타임 시점에 오류 발생

-----
### 프로젝션 결과 반환 - @QueryProjection
-----
1. 생성자 + @QueryProjection
```java
package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    private String username;
    private int age;

    @QueryProjection
    public MemberDto(String username, int age) {
        this.username = username;
        this.age = age;
    }
}
```
  - ./gradlew compileQuerydsl
  - QMemberDto 생성 확인
```java
package study.querydsl.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMemberDto extends ConstructorExpression<MemberDto> {

    private static final long serialVersionUID = 1356709634L;

    public QMemberDto(com.querydsl.core.types.Expression<String> username, com.querydsl.core.types.Expression<Integer> age) {
        super(MemberDto.class, new Class<?>[]{String.class, int.class}, username, age);
    }

}

```

2. @QueryProjection 활용
```java
@Test
public void findDtoByQueryProjection() {
    List<MemberDto> result = queryFactory.select(new QMemberDto(member.username, member.age))
            .from(member)
            .fetch();

    for (MemberDto memberDto : result) {
        System.out.println("memberDto = " + memberDto);
    }
}
```
```
memberDto = MemberDto(username=member1, age=10)
memberDto = MemberDto(username=member2, age=20)
memberDto = MemberDto(username=member3, age=30)
memberDto = MemberDto(username=member4, age=40)
```

  - 이 방법은 컴파일러로 타입 체크를 할 수 있으므로 가장 안전한 방법 (컴파일 오류 확인 가능)
  - 다만 DTO에 QueryDSL 애너테이션을 유지해야하는 점(=의존적)과 DTO까지 Q파일을 생성해야 하는 단점 존재

3. DISTINCT
```java
List<String> result = queryFactory
        .select(member.username).distinct()
        .from(member)
        .fetch();
```
  - JPQL의 DISTINCT와 동일
