-----
### 요구사항 분석
-----
1. 상품을 관리할 수 있는 서비스
2. 상품 도메인 모델
   - 상품 ID
   - 상품명
   - 가격
   - 수량

2. 상품 관리 기능 및 서비스 화면
   - 상품 목록
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/48406706-e0b9-4a79-9304-b03ff98bed38">
</div>

   - 상품 상세
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/3d7ac107-60bc-411c-b492-326ce49efbe7">
</div>

   - 상품 등록
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/2941b3ce-fdb9-427f-948b-00b39164a99f">
</div>

   - 상품 수정
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/de7ff834-dfcb-49ab-aa4f-2e4d1d62157e">
</div>

3. 서비스 제공 흐름
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/c27bc87e-8469-4764-8d08-21e39fd26765">
</div>

  - 디자이너 : 요구사항에 맞도록 디자인하고, 디자인 결과물을 웹 퍼블리셔에게 넘겨줌
  - 웹 퍼블리셔 : 디자이너에게서 받은 디자인을 기반으로 HTML, CSS를 만들어 개발자에게 제공
  - 백엔드 개발자 : 디자이너, 웹 퍼블리셔를 통해 HTML 화면이 나오기 전 까지 설계하고, 핵심 비즈니스 모델 개발
    + 이후 HTML이 나오면, 이 HTML을 뷰 템플릿으로 변환해서 동적 화면을 그리고, 또 웹 화면 흐름 제어
  - React.js, Vue.js 같은 웹 클라이언트 기술을 사용하고, 웹 프론트엔드 개발자가 별도로 있으면, 웹 프론트엔드 개발자가 웹 퍼블리셔 역할까지 포함하는 경우 존재
  - 웹 클라이언트 기술을 사용하면, 웹 프론트엔드 개발자가 HTML을 동적으로 만드는 역할과 웹 화면의 흐름을 담당
  - 이 경우, 백엔드 개발자는 HTML 뷰 템플릿을 직접 만지는 대신, HTTP API를 통해 웹 클라이언트가 필요로 하는 데이터와 기능을 제공하면 됨
