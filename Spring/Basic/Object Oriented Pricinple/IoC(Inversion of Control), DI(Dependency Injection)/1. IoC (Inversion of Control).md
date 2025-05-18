-----
### 제어의 역전 (Inversion of Control)
-----
1. 기존 프로그램 : 클라이언트 구현 객체가 스스로 필요한 서버 구현 객체를 생성하고 연결, 실행
   - 즉, 구현 객체가 프로그램의 제어 흐름을 스스로 조절
2. 반면, AppConfig가 등장하면서, 구현 객체는 자신의 로직을 실행하는 역할만 담당
   - 즉, 프로그램의 제어 흐름을 AppConfig로 이동
   - 예를 들어, OrderServiceImpl은 필요한 인터페이스를 호출하지만, 어떤 구현 객체들이 실행될지 모름
3. 프로그램에 대한 제어 흐름에 대한 권한은 모두 AppConfig가 가짐
   - 심지어 OrderServiceImpl도 AppConfig가 생성
   - AppConfig는 OrderServiceImpl이 아닌 OrderService 인터페이스의 다른 구현 객체를 생성하고 실행할 수도 있음
   - OrderServiceImpl은 자신의 역할만 수행
4. 💡 이처럼, 프로그램 제어 흐름을 직접 제어하는 것이 아니라 외부에서 관리하는 것을 제어의 역전 (IoC, Inversion of Control)
