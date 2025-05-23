-----
### YAML
-----
1. 스프링은 설정 데이터를 사용할 때, application.properties 뿐만 아니라 application.yml이라는 형식도 지원
2. YAML(YAML Ain't Markup Language)은 사람이 읽기 좋은 데이터 구조를 목표
   - 확장자는 yaml, yml이며, 주로 yml 사용
3. application.properties 예시
```properties
environments.dev.url=https://dev.example.com
environments.dev.name=Developer Setup
environments.prod.url=https://another.example.com
environments.prod.name=My Cool App
```

4. application.yml 예시
```yml
environments:
    dev:
        url: "https://dev.example.com"
        name: "Developer Setup"
    prod:
        url: "https://another.example.com"
        name: "My Cool App"
```

  - YAML은 사람이 읽기 좋게 계층 구조를 이룬다는 점이 가장 큰 특징
  - YAML은 space(공백)으로 계층 구조를 만듬
    + 일반적으로 보통 2칸을 사용하며 1칸을 사용해도 허용
    + 일관성있게 사용하지 않으면, 읽기 어렵거나 구조가 깨질 수 있음
    + 구분 기호로 :를 사용
    + 💡 만약 값이 있다면 key: value의 형식으로 : 이후에 공백을 하나 넣고 값을 넣어주면 됨

5. 스프링은 YAML 계층 구조를 properties처럼 평평하게 만들어서 읽어들임
   - 즉, 쉽게 이야기해서 위의 application.yml은 application.properties 예시처럼 만들어짐

-----
### 적용
-----
1. application.properties를 사용하지 않도록 파일 이름 변경
   - appplication.backup.properties
```properties
my.datasource.url=local.db.com
my.datasource.username=local_user
my.datasource.password=local_pw
my.datasource.etc.max-connection=1
my.datasource.etc.timeout=3500ms
my.datasource.etc.options=CACHE,ADMIN
```

2. src/main/resources/application.yml을 생성
```yml
my:
    datasource:
        url: local.db.com
        username: local_user
        password: local_pw
        etc:
            max-connection: 1
            timeout: 60s
            options: LOCAL, CACHE
```
  - 실행해보면 application.yml에 입력한 설정 데이터가 조회되는 것 확인 가능
```
2024-09-16T19:00:51.489+09:00  INFO 17740 --- [           main] hello.datasource.MyDataSource            : url = local.db.com
2024-09-16T19:00:51.493+09:00  INFO 17740 --- [           main] hello.datasource.MyDataSource            : username = local_user
2024-09-16T19:00:51.493+09:00  INFO 17740 --- [           main] hello.datasource.MyDataSource            : password = local_pw
2024-09-16T19:00:51.493+09:00  INFO 17740 --- [           main] hello.datasource.MyDataSource            : maxConnection = 1
2024-09-16T19:00:51.493+09:00  INFO 17740 --- [           main] hello.datasource.MyDataSource            : timeout = PT1M
2024-09-16T19:00:51.494+09:00  INFO 17740 --- [           main] hello.datasource.MyDataSource            : options = [LOCAL, CACHE]
```

3. 💡 application.properties와 application.yml을 같이 사용하면 application.properties가 우선권을 가짐
   - 둘을 함께 사용하는 것은 일관성이 없으므로 권장하지 않음
   - 실무에서는 설정 정보가 많아 보기 편한 yml를 선호하기도 함

-----
### YML와 프로필
-----
1. YML에도 프로필 적용 가능
2. application.yml
```yml
my:
    datasource:
        url: local.db.com
        username: local_user
        password: local_pw
        etc:
            max-connection: 1
            timeout: 60s
            options: LOCAL, CACHE
---
spring:
    config:
      activate:
        on-profile: dev
my:
    datasource:
        url: dev.db.com
        username: dev_user
        password: dev_pw
        etc:
            maxConnection: 10
            timeout: 60s
            options: DEV, CACHE
---
spring:
    config:
        activate:
            on-profile: prod
my:
    datasource:
        url: prod.db.com
        username: prod_user
        password: prod_pw
        etc:
            maxConnection: 50
            timeout: 10s
            options: PROD, CACHE
```
3. YML은 --- dash(-) 3개로 논리 파일 구분
4. spring.config.active.on-profile을 사용해 프로필 적용 가능
5. 나머지는 application.properties와 동일
