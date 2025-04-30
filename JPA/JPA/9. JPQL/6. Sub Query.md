-----
### 서브 쿼리
-----
1. 나이가 평균보다 많은 회원
```java
SELECT m FROM Member m WHERE m.age > (SELET AVG(m2.age) FROM Member m2)
```
  - 메인 쿼리와 서브 쿼리가 연관 되지 않음

2. 한 건이라도 주문한 고객
```java
SELECT m FROM Member m WHERE (SELECT COUNT(o) FROM Order o WHERE m = o.member) > 0
```
  - 메인 쿼리와 서브 쿼리가 연관

3. 서브 쿼리 지원 함수
   - [NOT] EXISTS (Sub-Query) : 서브쿼리의 결과가 존재하면 참
     + {ALL | ANY | SOME} (Sub-Query) : ALL은 모두 만족하면 참, ANY / SOME은 같은 의미로, 조건을 하나라도 만족하면 참
   - [NOT] IN (Sub-Query) : 서브쿼리 결과 중 하나라도 같은 것이 있으면 참

4. 예제
   - 팀 A 소속인 회원
```java
SELECT m FROM Member m
WHERE EXISTS (SELECT t FROM m.team t WHERE t.name = '팀A')
```

   - 전체 상품 각각의 재고보다 주문량이 많은 주문들
```java
SELECT o FROM Order o
WHERE o.orderAmount > ALL (SELECT p.stockAmount from Product p)
```

   - 어떤 팀이든 팀에 소속된 회원
```java
SELECT m FROM Member m
WHERE m.team = ANY (SELECT t FROM TEAM t)
```

5. JPA 서브 쿼리 한계
   - JPA는 WHERE, HAVING에서만 서브 쿼리 사용 가능
   - Hibernate에서는 SELECT 절도 가능 (지원)
   - Hinbernate 6.0 이전
     + FROM 절의 서브 쿼리(인라인 뷰)는 불가능 → NativeQuery 또는 Join으로 해결
     + 조인으로 풀 수 있으면 풀어서 해결
   - Hiberante 6.0 이후
     + FROM 절 서브 쿼리(인라인 뷰) 지원
     + https://in.relation.to/2022/06/24/hibernate-orm-61-features/
