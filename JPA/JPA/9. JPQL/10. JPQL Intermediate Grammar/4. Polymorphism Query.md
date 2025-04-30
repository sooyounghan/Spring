-----
### 다형성 쿼리
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/42293fb8-b365-4f38-b9b5-ec7ea631e5cf">
</div>

1. TYPE
  - 💡 조회 대상을 특정 자식으로 한정
  - 예) Item 중 Book, Movie를 조회
  - JPQL
```java
SELECT i FROM Item i WHERE TYPE(i) IN (Book, Movie)
```
  - SQL
```java
SELECT i FROM i WHERE i.DTYPE IN ('B', 'M')
```

2. TREAT (JPA 2.1)
   - 자바의 타입 캐스팅과 유사
   - 💡 상속 구조에서 부모 타입을 특정 자식 타입으로 다룰 때 사용
   - FROM, WHERE, SELECT(하이버네이트 지원) 사용
   - 예) 부모인 Item과 자식 Book이 있음
   - JPQL
```java
SELECT i FROM Item i WHERE TREAT(i AS Book).author = 'kim'
```
   - SQL
```java
SELECT i.* FROM Item i WHERE i.DTYPE = 'B' AND i.author = 'kim'
```
