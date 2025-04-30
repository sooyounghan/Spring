-----
### 프로메테우스와 그라파나 소개
-----
1. 프로메테우스 (Prometheus)
   - 애플리케이션에서 발생한 메트릭을 그 순간만 확인하는 것이 아니라 과거 이력까지 함께 확인하려면 메트릭을 보관하는 DB 필요
   - 💡 메트릭을 지속해서 수집하고 DB에 저장하는 역할

2. 그라파나 (Grafana)
   - 💡 프로메테우스가 DB라고 하면, 이 DB에 있는 데이터를 불러서 사용자가 보기 편하게 보여주는 대시보드 역할을 하는 것
   - 그라파나는 매우 유연하고, 데이터를 그래프로 보여주는 툴
   - 수 많은 그래프를 제공하고, 프로메테우스를 포함한 다양한 데이터소스 지원
<div align="center">
<img src="https://github.com/user-attachments/assets/e17a458e-d656-4751-9244-ff668e7270d9">
</div>

3. 전체 구조
<div align="center">
<img src="https://github.com/user-attachments/assets/b4860e25-e1af-4ce7-b93c-6c594bbab08e">
</div>

  - 스프링 부트 액츄에이터와 마이크로미터를 사용하면 수 많은 메트릭을 자동으로 생성
    + 마이크로미터 프로메테우스 구현체는 프로메테우스가 읽을 수 있는 포맷으로 메트릭을 생성
  - 프로메테우스는 만들어진 메트릭을 지속해서 수집
  - 프로메테우스는 수집한 메트릭을 내부 DB에 저장
  - 사용자는 그라파나 대시보드를 통해 그래프로 편리하게 메트릭을 조회
    + 이 때, 필요한 데이터는 프로메테우스를 통해 조회

4. 프로메테우스 아키텍쳐
<div align="center">
<img src="https://github.com/user-attachments/assets/f0a52411-0031-4ad7-b604-fc41924268ee">
</div>

  - 출처 : https://prometheus.io/docs/introduction/overview/
