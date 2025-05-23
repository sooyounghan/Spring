-----
### MyBatis - 동적 쿼리
-----
1. MyBatis 공식 메뉴얼 : https://mybatis.org/mybatis-3/ko/index.html
2. MyBatis 스프링 공식 메뉴얼 : https://mybatis.org/spring/ko/index.html
3. 동적 SQL
   - MyBatis가 제공하는 최고의 기능이나 사용하는 이유
   - 동적 쿼리를 위해 제공되는 기능
     + if
     + choose (when, otherwise)
     + trim (where, set)
     + foreach

4. if
```xml
<select id="findActiveBlogWithTitleLike" result="Blog">
  SELECT *
  FROM BLOG
  WHERE state = 'ACTIVE'
  <if test="title != null">
    AND title LIKE #{title}
  </if>
</select>
```
  - 해당 조건에 따라 값을 추가할 지 말지 판단
  - 내부의 문법을 OGNL을 사용
    + OGNL (Object Graph Navigation Language) : 자바 언어가 지원하는 범위보다 더 단순한 식을 사용하면서 속성을 가져오고 설정하는 것을 허용학, 자바 클래스의 메서드를 실행하는 오픈 소스 표현식 언어
      
5. choose, when, otherwise
```xml
<select id="findActiveBlogLike" result="Blog">
  SELECT *
  FROM BLOG
  WHERE state = 'ACTIVE'
  <choose>
    <when test="title != null">
      AND tile LIKE #{title}
    </when>
    <when test="author != null and author.name != null">
      AND author_name LIKE #{author.name}
    </when>
    <otherwise>
      AND featured = 1
    </otherwise>
  </choose>
</select>
```
  - 자바의 switch 구문과 유사한 구문도 사용할 수 있음

6. trim, where, set
```xml
<select id="findActiveBlogLike" resultType="Blog">
    SELECT *
    FROM BLOG
    WHERE
    <if test="state != null">
      state = #{state}
    </if>
    <if test="title != null">
      AND tile LIKE #{title}
    </if>
    <if test="author != null and author.name != null">
      AND author_name LIKE #{author.name}
    </if>
</select>
```
  - 이 예제의 문제점은 문장을 모두 만족하지 않을 때 발생
```sql
SELECT *
FROM BLOG
WHERE
```

  - title만 만족할 때도 문제가 발생
```sql
SELECT *
FROM BLOG
WHERE
AND title LIKE 'someTitle'
```

  - 결국 WHERE문을 언제 넣어야 할 지 상황에 따라 동적으로 달라지는 문제 발생
  - ```<where>```를 사용하면 문제 해결
```xml
<select id="findActiveBlogLike" resultType="Blog">
    SELECT *
    FROM BLOG
    <where>
        <if test="state != null">
          state = #{state}
        </if>
        <if test="title != null">
          AND tile LIKE #{title}
        </if>
        <if test="author != null and author.name != null">
          AND author_name LIKE #{author.name}
        </if>
    </where>
</select>
```

  - ```<where>```는 문장이 없으면 where를 추가하지 않음
  - 문장이 있으면 where를 추가하고, 만약 AND가 먼저 시작하면 AND를 지움
  - 참고로, trim이라는 기능을 사용해도 되며, ```<where>```와 같은 기능 수행
```xml
<trim prefix="WEHRE" prefixOverride="AND |OR ">
  ...
</trim>
```

7. foreach
```xml
<select id="selectPostIn" resultType="domain.blog.post">
    SELECT *
    FROM POST P
    <where>
        <foreach item="item" index="index" collection="list" open="ID in (" separator="," close=")" nullable="true">
           #{item}
        </foreach>
    </where>
</select>
```
  - 컬렉션을 반복 처리할 때 사용 (where in (1, 2, 3, 4, 5, 6)와 같은 문장 쉽게 완성 가능)
  - 파라미터로 List를 전달하면 됨

8. 동적 쿼리 관련 내용 :  https://mybatis.org/mybatis-3/ko/dynamic-sql.html

-----
### 기타 기능
-----
1. 애너테이션으로 SQL 작성
   - XML 대신 애너테이션 SQL을 작성 가능
```java
@Select("SELECT id, item_name, price, quantity FROM item WHERE id=#{id}")
Optional<Item> findById(Long id);
```
  - @Insert, @Update, @Delete, @Select 기능 제공
  - 💡 이 경우 XML에는 ```<select id="findById"> ~ </select>```는 제거해야 함
  - 동적 SQL이 해결되지 않으므로 간단한 경우에만 사용
  - 관련 내용 : https://mybatis.org/mybatis-3/ko/java-api.html

2. 문자열 대체 (String Substitution)
   - #{} 문법은 ?를 넣고 파라미터를 바인딩하는 PreparedStatement를 사용
   - 떄로는 파라미터 바인딩이 아닌 문자 그대로를 처리하고 싶은 경우 존재
   - 이럴 때는, ${} 사용
```sql
OREDER BY ${columnName}
```
```java
@Select("SELECT * FROM user WHERE ${column} = #{value}")
User findByColumn(@Param("column") String column, @Param("value") String value);
```
   - 💡 ${}를 사용하면 SQL Injection 공격을 당할 수 있으므로 가급적 사용하지 않는 것이 좋음

3. 재사용 가능한 SQL 조각
  - ```<sql>```을 사용하면 SQL 코드를 재사용 가능
```xml
<sql id="useColumns"> ${alias}.id, ${alias}.username, ${alias}.password </sql>
```
```xml
<select id="selectUsers" resultType="map">
  SELECT
    <include refid="userColumns"><property name="alias" value="t1"></include>
    <include refid="userColumns"><property name="alias" value="t2"></include>
</select>
```
  - ```<include>```를 통해 ```<sql>``` 조각을 찾아서 사용할 수 있음

```xml
<sql id="sometable">
  ${prefix}Table
</sql>

<sql id="someinclude">
  FROM
    <include refid="${include_target}"/>
</sql>

<select id="select" resultType="map">
  SELECT field1, field2, field3
  <include refid="someinclude">
    <property name="prefix" value="Some"/>
    <property name="include_target" value="sometable"/>
  </include>
</select>
```
  - 프로퍼티 값 전달 가능
  - 해당 값은 내부에서 사용 가능

4. Result Maps
   - 결과를 매핑할 때 테이블은 user_id이지만, 객체는 id
   - 이 경우, 컬럼명과 객체의 프로퍼티 명이 다른데, 이럴 땐 AS(별칭) 사용
```sql
<select id="selectUsers" resultType="User">
    SELECT user_id AS "id", user_name AS "userName", hashed_password AS "hashedPassword"
    FROM some_table
    WHERE id = #{id}
</select>
```
   - 별칭을 사용하지 않고도 문제 해결 가능 : resultMap을 선언해서 사용
```xml
<resultMap id="userResultMap" type="User">
  <id property="id" column="user_id"/>
  <result property="username" column="user_name"/>
  <result proeprty="password" column="hashed_password"/>
</resultMap>

<select id="selectUsers" resultType="userResultMap">
    SELECT user_id, user_name, hashed_password
    FROM some_table
    WHERE id = #{id}
</select>
```

5. 복잡한 결과 매핑
   - MyBatis도 매우 복잡한 결과에 객체 연관 관계를 고려해 데이터를 조회하는 것이 가능
   - 이 떄는 ```<association>, <collection>```등을 사용
   - 이 부분은 성능과 실효성 측면에서 많은 고민이 필요
   - JPA는 객체와 관계형 데이터베이스를 ORM 개념으로 매핑하므로 자연스럽지만, MyBatis는 공수도 많고, 최적화하기도 어려움
   - 따라서, 해당 기능을 사용할 때는 신중하게 사용
   - 공식 메뉴얼 : https://mybatis.org/mybatis-3/ko/sqlmap-xml.html#Result_Maps
