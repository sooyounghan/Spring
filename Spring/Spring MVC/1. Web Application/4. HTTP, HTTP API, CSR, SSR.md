-----
### 정적 리소스
-----
1. 고정된 HTML 파일, CSS, JS, 이미지, 영상 등 제공
2. 주로 웹 브라우저
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/11e355bd-9835-4dce-9f30-b96f3ed4e8b2">
</div>

-----
### HTML 페이지
-----
1. 동적으로 필요한 HTML 파일을 생성해서 전달
2. 웹 브라우저 : HTML 해석
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/f3b33d54-a3e0-46cd-b043-245553d05ec3">
</div>

-----
### HTTP API
-----
1. HTML이 아닌 데이터를 전달
2. 주로 JSON 형식 사용
3. 💡 다양한 시스템에서 호출
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/8ea13bd8-8538-42f0-933b-e23b26c174c9">
</div>

4. 💡 데이터만 주고 받음
   - UI 화면이 필요하면 클라이언트가 별도 처리
   - 세 가지 상황 : 웹 클라이언트 서버 to 서버, 앱 클라이언트 서버 to 서버, 서버 to 서버
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/4c80efee-e4ba-474c-9eb1-8695e91a938e">
</div>

5. 주로 JSON 형태로 데이터 통신
6. UI 클라이언트 접점
   - 앱 클라이언트(아이폰, 안드로이드, PC 앱)
   - 웹 브라우저에서 자바스크립트를 통한 HTTP API 호출
   - React, Vue.js 같은 웹 클라이언트

7. 서버 to 서버
   - 주문 서버 → 결제 서버
   - 기업 간 데이터 통신

-----
### SSR(Server Side Rendering), CSR(Client Side Rendering)
-----
1. SSR - 서버 사이드 렌더링
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/3af4ba1c-bce0-4e1b-9450-070ed7c417f2">
</div>

   - 💡 HTML 최종 결과를 서버에서 만들어서 웹 브라우저에게 전달
   - 주로 정적 화면에 사용
   - 관련 기술 : JSP / Thymeleaf (Back-End)

2. CSR - 클라이언트 사이드 렌더링
<div align="center">
<img src="https://github.com/sooyounghan/HTTP/assets/34672301/a62a95a1-7e35-4298-ad16-423e310c11fd">
</div>

   - 💡 HTML 결과를 자바스크립트를 사용해 웹 브라우저에서 동적으로 생성해 적용
   - 주로 동적인 화면에 사용
   - 웹 환경을 마치 앱처럼 필요한 부분을 변경할 수 있음
     + 예) 구글 지도, G-Mail, 구글 캘린더
   - 관련 기술 : React.js, Vue.js (Front-End)

3. 참고
   - React.js, Vue.js를 CSR + SSR 동시에 지원하는 웹 프레임워크도 존재
   - SSR를 사용하더라도, 자바스크립트를 사용해 일부 동적으로 변경 가능

-----
### UI 기술
-----
1. 백엔드 - SSR 기술
   - JSP, Thymeleaf
   - 화면이 정적이고, 복잡하지 않을 때 사용

2. 웹 프론트앤드 - CSR 기술
   - React.js, Vue.js
   - 복잡하고 동적인 UI 사용
