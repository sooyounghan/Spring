-----
### 커넥션 풀(Connection Pool) - 데이터베이스 커넥션을 매번 획득
-----
<div align="centeR">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/e00851be-f1a7-4ae2-9721-f3c7c80a124b">
</div>

1. 애플리케이션 로직은 DB 드라이버를 통해 Connection 조회
2. DB 드라이버는 DB와 TCP/IP Connection 연결
   - 이 과정에서 3-Way-Handshake 같은 TCP/IP 연결을 위한 네트워크 동작 발생
3. DB 드라이버는 TCP/IP Connection이 연결되면, ID / PW 등 기타 부가 정보를 DB에 전달
4. DB는 ID, PW를 통해 내부 인증을 완료하고, 내부에 DB 세션을 생성
5. DB는 Connection이 완료되었다는 응답을 보냄
6. DB 드라이버는 Connection 객체를 생성해서 클라이언트에게 반환

-----
### [ 정리 ]
-----
1. Connection을 새로 만드는 것은 과정도 복잡하고, 시간도 많이 소모
2. DB는 물론이고, 애플리케이션 서버에서도 TCP/IP 커넥션을 새로 생성하기 위한 리소스를 매번 사용
3. 고객이 애플리케이션을 사용할 때, SQL을 실행하는 시간 뿐만 아니라 Connection을 새로 만드는 시간이 추가되기 때문에, 결과적으로 응답 속도에도 영향을 줌
4. 즉, 사용자에게 좋지 않은 경험 제공
  - 데이터베이스마다 Connection을 생성하는 시간은 다름
  - 시스템 상황마다 다르지만 MySQL 계열은 수 ms(밀리초) 정도로 매우 빨리 Connection 확보가 가능 (반면, 수십 밀리초 이상 걸리는 데이터베이스들도 존재)

5. 이를 해결하는 아이디어가 바로 Connection을 미리 생성해두고 사용하는 Connection Pool이라는 방법
  - 말 그대로, Connection을 관리하는 풀

-----
### 커넥션 풀 초기화
-----
<div align="centeR">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/b04cd97f-b7da-4871-b434-32a057dc4a87">
</div>

1. 애플리케이션을 시작하는 시점에 Connection Pool은 필요한 만큼 커넥션을 미리 확보해서 풀에 보관
2. 보통 얼마나 보관할지는 서비스의 특징과 서버 스펙에 따라 다르지만, 기본값은 보통 10개

-----
### 커넥션 풀의 연결 상태
-----
<div align="centeR">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/fda2cd40-f37b-4854-a91d-af0aa47fb47a">
</div>

1. 커넥션 풀에 들어있는 커넥션은 TCP/IP로 DB와 커넥션이 연결되어 있는 상태
2. 언제든지, 즉시 SQL을 DB에 전달 가능

-----
### 커넥션 풀 사용
-----
1. 커넥션 풀 조회 및 획득
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/816875f4-cec8-42b7-8851-d4a6b09963a2">
</div>

  - 애플리케이션 로직에서 DB 드라이버를 통해 새로운 커넥션을 획득하는 것이 아님
  - 커넥션 풀을 통해 이미 생성되어 있는 커넥션을 객체 참조로 그냥 가져다 쓰면 됨
  - 커넥션 풀에 커넥션을 요청하면, 커넥션 풀은 가지고 있는 커넥션 중 하나를 반환

2. 커넥션 풀 사용 후 반납 (반환)
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/38cc7639-cae0-4164-893a-37c4bb1ee6fc">
</div>

  - 애플리케이션 로직은 커넥션 풀에서 받은 커넥션을 사용해서 SQL을 데이터베이스에서 전달하고 그 결과를 받아 처리
  - 커넥션을 모두 사용하고 나면, 커넥션을 종료하는 것이 아니라, 다음에 다시 사용할 수 있도록 해당 커넥션을 그대로 커넥션 풀에 반환
  - 💡 커넥션을 종료하는 것이 아니라 커넥션이 살아있는 상태로 커넥션 풀에 반환해야 한다는 것

-----
### 정리
-----
1. 적절한 커넥션 풀 숫자는 서비스의 특징과 애플리케이션 서버 스펙, DB 서버 스펙에 따라 다르기 때문에 성능 테스트를 통해 정해야 함
2. 커넥션 풀은 서버당 최대 커넥션 수 제한 가능
   - 따라서, DB에 무한정 연결이 생성되는 것을 막아주므로 DB를 보호하는 효과
3. 커넥션 풀을 얻는 이점은 매우 크기 때문에, 항상 기본으로 사용
4. 커넥션 풀은 개념적으로 단순해서 직접 구현할 수 있지만, 사용도 편리하고 성능도 뛰어난 오픈소스 커넥션 풀이 많기 때문에, 오픈 소스를 사용하는 것이 좋음
   - 대표적인 커넥션 풀 오픈 소스 : commons-dbcp2, tomcat-jdbc-pool, HikariCP 등
5. 성능과 사용의 편리함 측면으로 최근에는 'hikariCP'를 주로 사용
   - Spring Boot 2.0부터는 기본 커넥션 풀로 hikariCP를 제공
   - 성능, 사용의 편리함, 안정성 측면에서 이미 검증이 되었으므로 커넥션 풀을 사용할 때 사용
  

