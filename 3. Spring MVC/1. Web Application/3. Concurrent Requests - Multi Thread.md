-----
### 동시 요청 - 멀티 쓰레드
-----
1. 클라이언트가 요청1을 서버에 요청하면 이에 대해 응답1로서 응답
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/2c1b9166-3619-48a6-a912-3da9a52ffcc5">
</div>

2. 세부 과정
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/31a3c930-c133-48f7-831f-64ba78399f28">
</div>

  - 클라이언트가 요청을 하면, WAS와 TCP/IP 연결 (Connection 연결)
  - WAS(서버)는 해당 요청에 일치하는 servlet 호출
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/0c427e52-5fbf-4ed6-8665-70459e49e1ea">
</div>

  - 그렇다면, 이를 호출하는 대상은? Thread (쓰레드)

-----
### 쓰레드 (Thread)
-----
1. 애플리케이션 코드를 하나하나 순차적으로 실행하는 것
2. 자바 메인 메서드를 처음 실행하면 main이라는 이름의 쓰레드 실행
3. 쓰레드가 없다면 자바 애플리케이션 실행 불가능
4. 쓰레드는 한 번에 하나의 코드 라인만 수행
5. 동시 처리가 필요하면 쓰레드를 추가로 생성
  
-----
### 단일 요청 - 쓰레드 하나 사용
-----
1. 기본 상태
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/2c5458d4-3437-47b2-a3ab-23a274709f04">
</div>

2. 클라이언트로부터 요청
  - 이 요청에 대한 쓰레드 할당하여 servlet 호출하여 코드 실행
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/85659e41-fd14-4eef-82e2-2f5484249f97">
</div>

3. 이에 대한 응답 전송
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/91257ad7-3dc8-467b-8e2c-a17b855771e5">
</div>

4. 응답이 완료되면, 다시 쓰레드는 휴식
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/635c9469-f2f9-496e-8e21-58bdbb226a96">
</div>

-----
### 다중 요청 - 쓰레드 하나 사용
-----
1. 클라이언트 요청에 따라 쓰레드가 할당 되어 servlet을 호출 해 요청1이 처리 중이지만, 처리가 지연 중 (요청 중)
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/1aa0b91f-9532-4c46-8f85-8bf16a9def55">
</div>

2. 이 과정에서 클라이언트가 요청2를 요청하면, 쓰레드는 하나이므로 동일 servlet에 대해 쓰레드 할당을 받기 위해 대기 (요청 대기)
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/0fd98009-266f-4a01-90fd-47bf38db6425">
</div>

3. 결과적으로 둘 다 지연되는 상태 발생
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/f724f6ba-4557-4fe7-8afe-2d46f2754692">
</div>


4. 해결 방법 - 요청 마다 쓰레드 생성
   - 즉, 요청에 대해 신규 쓰레드 생성
   - 같은 servlet을 호출하더라도 각 쓰레드마다 servlet 실행
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/74a9cfa0-95aa-4169-9d37-dbc6f35b5c76">
</div>

-----
### 요청 마다 쓰레드를 생성할 시 장/단점
----
1. 장점
   - 동시 요청 처리 가능
   - 리소스(CPU, Memory)가 허용할 때 까지 처리 가능
   - 하나의 쓰레드가 지연되어도, 나머지 쓰레드는 항상 동작

2. 단점
   - 쓰레드는 생성 비용이 매우 비쌈 (즉, 고객이 요청이 올 때마다 쓰레드를 생성하면, 응답 속도가 늦어짐)
   - Context Switching 비용이 발생 (하나의 코어에 쓰레드는 여러 개 존재하고, 수행할 때마다 쓰레드가 변경되어야 함)
   - 쓰레드 생성에 제한이 없음 (즉, 고객 요청이 너무 많이 오면, CPU / Memory 임계점을 넘어 서버가 죽을 수 있음)

-----
### 쓰레드 풀(Thread Pool)
-----
1. 요청에 따라 쓰레드 풀에 존재하는 쓰레드를 요청하고, 쓰레드 풀에 생성된 쓰레드를 이용
2. 쓰레드 사용이 완료되면, 다시 쓰레드 풀에 반납
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/5e313c1b-3e26-4144-9569-943500b20696">
</div>

<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/7c8611db-6fe0-4734-9c89-1a0f8841e72e">
</div>

  - 최대 쓰레드 개수를 저장한 뒤, 쓰레드 풀에 생성
  - 최대 쓰레드 개수를 넘어선 요청이 들어오면, 쓰레드 풀에 쓰레드가 없으므로, 쓰레드를 받을 때 까지 대기하거나 거절 가능

3. 특징
   - 요청 마다 쓰레드 생성하는 것에 대한 단점 보완
   - 필요한 쓰레드를 쓰레드 풀에 보관하고 관리
   - 쓰레드 풀에 생성 가능한 쓰레드의 최대치를 관리 (Tomcat : 최대 200개 기본 설정 (변경 가능)
  
4. 사용
   - 쓰레드가 필요하면, 이미 생성되어 있는 쓰레드를 쓰레드 풀에서 꺼내서 사용
   - 사용을 종료하면, 쓰레드 풀에 해당 쓰레드를 반납
   - 최대 쓰레드가 모두 사용 중이어서, 쓰레드 풀에 쓰레드가 없다면, 기다리는 요청은 거절하거나 특정 숫자만큼만 대기하도록 설정 가능

5. 장점
   - 쓰레드가 미리 생성되어있으므로, 쓰레드를 생성하고 종료하는 비용(CPU)이 절약되고, 응답 시간이 빠름
   - 생성 가능한 쓰레드의 최대치가 있으므로, 너무 많은 요청이 들어와도 안전하게 처리 가능

-----
### 쓰레드 풀 (Thread Pool) 팁
-----
1. WAS의 주요 Tuning Point : 최대 쓰레드(Max Thread) 수
2. 값을 너무 낮게 설정 : 동시 요청이 많으면, 서버 리소스는 여유롭지만, 클라이언트는 금방 응답 지연 발생
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/95e9d2ac-a79a-4e27-bb41-9da443935d8e">
</div>

  - 동시에 10개 요청만 처리 가능하므로, 쓰레드 10개만 사용
  - 나머지 90개는 대기가 되거나 가절
  - CPU의 사용률은 현저히 낮아지므로 효율적이지 못함 (평균적으로 50%는 유지하는 것이 적절함)
    
3. 값을 너무 높게 설정 : 동시 요청이 많으면, CPU / 메모리 리소스 임계점 초과로 서버 다운
4. 장애 발생 시, 클라우드면 일단 서버부터 늘리고 이후 튜닝 / 클라우드가 아니면 적절하게 튜닝
5. 쓰레드 풀의 적정 숫자
   - 애플리케이션 로직의 복잡도, CPU, 메모리, I/O 리소스 상황에 따라 모두 다름
   - 💡 성능 테스트 (최대한 실제 서비스와 유사하게 성능 테스트를 시도)
     + 성능 테스트 Tool : Apache ab, JMeter, nGrinder

-----
### WAS의 멀티 쓰레드 지원
-----
1. 멀티 쓰레드에 대한 부분은 WAS가 처리
2. 멀티 쓰레드 관련 코드를 신경쓰지 않아도 됨
3. 싱글 쓰레드 프로그래밍을 하듯이 편리하게 소스 코드 개발 가능
4. 💡 멀티 쓰레드 환경이므로 싱글톤 객체(Servlet, Spring Bean)는 주의해서 사용
   
