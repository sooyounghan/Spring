-----
### 💡 JPA의 데이터 타입 분류
-----
1. Entity 타입
   - @Entity로 정의하는 객체
   - 데이터가 변해도 식별자로 지속해서 추적 가능
   - 예) 회원 엔티티의 키나 나이 값을 변경해도 식별자로 인식 가능

2. 값 타입
   - int, Integer, String처럼 단순히 값으로 사용하는 자바 기본 타입이나 객체
   - 식별자가 없고, 값만 있으므로 변경 시 추적 불가
   - 예) 숫자 100을 200으로 변경하면 완전히 다른 값으로 대체

-----
### 값 타입 분류
-----
1. 기본값 타입
   - 자바 기본 타입 : int, double
   - Wrapper 클래스 : Integer, Long
   - String

2. 임베디드 타입 (Embedded Type, 복합 값 타입) [JPA에서 정의]
3. 컬렉션 값 타입 (Collection Value Type) [JPA에서 정의]

-----
### 기본 값 타입
-----
1. 예) String name, int age
2. 생명주기를 엔티티에 의존
   - 예) 회원을 삭제하면, 이름 / 나이 필드도 함께 삭제
3. 값 타입은 공유하면 안 됨
   - 예) 회원 이름 변경 시 다른 회원의 이름도 함께 변경하면 안 됨 (Side Effect(부수효과)가 발생하면 안 됨)
4. 💡 참고 : 자바의 기본 타입은 절대 공유되지 않음
   - int, double 같은 기본 타입(Primitive Type)은 절대 공유되지 않음 (Side Effect(부수효과)가 발생하지 않음)
   - 기본 타입은 항상 값을 복사
```java
package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        int a = 10;
        int b = a; // 기본 타입은 항상 값 복사 (b에 a 값 복사)
        // a, b는 서로 다른 저장 공간 존재
        a = 20;

        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}
```
```
a = 20
b = 10
```

   - Integer 같은 Wrapper Class나 String 같은 특수 클래스는 공유 가능한 객체이지만 변경되면 안 됨
```java
package hellojpa;

public class ValueMain {
    public static void main(String[] args) {
        Integer a = new Integer(10);
        Integer b = a; // 객체이므로 주소 전달 (값을 공유)
        // Reference로 공유되어, 같은 객체 참조
        
        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }
}
```
```
a = 10
b = 10
```
