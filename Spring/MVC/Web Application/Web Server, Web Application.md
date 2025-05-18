-----
### 웹(Web) - HTTP 기반
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/ba97628c-b72d-483d-9dcb-7c7754512191">
</div>

-----
### HTTP (HyperText Transfer Protocol)
-----
1. HTTP 메세지에 모든 것을 전송 (거의 모든 형태의 데이터 전송 가능)
  - HTML, TEXT
  - Image, 음성, 영상, 파일
  - JSON, XML (API)
2. 서버 간에 데이터를 주고 받을 때도 대부분 HTTP 사용

-----
### 웹 서버 (Web Server)
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/a92ac897-b470-4133-96b4-b68ef153fd4b">
</div>

1. HTTP 기반으로 동작 (HTTP Protocol로 요청 / 응답을 주고 받음)
2. 정적 리소스 제공, 기타 부가 기능
3. 정적(파일) HTML, CSS, JS, 이미지, 영상
4. 예) NGINX, Apache

-----
### 웹 어플리케이션 서버 (Web Application Server, WAS)
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/3b84ffc0-a9c4-44d5-9133-f4a7e0e2bafb">
</div>

1. HTTP 기반으로 동작
2. 웹 서버 기능 포함 + (정적 리소스 제공 기능)
3. 프로그램 코드를 실행해서 애플리케이션 로직 수행
   - 동적 HTML, HTTP API (JSON)
   - Servlet, JSP, Spring MVC
4. 예) Tomcat, Jetty, Undertow

-----
### 웹 서버와 웹 애플리케이션 서버의 차이
-----
1. 💡 웹 서버는 정적 리소스 (파일), WAS는 애플리케이션 로직
2. 둘의 용어와 경계가 모호함
   - 웹 서버도 프로그램을 실행하는 기능을 포함하기도 함
   - 웹 애플리케이션 서버도 웹 서버의 기능을 제공함
3. Java는 Servlet Container 기능을 제공하면 WAS
   - 서블릿 없이 자바코드를 실행하는 서버 프레임워크도 존재
4. 💡 WAS는 애플리케이션 코드를 실행하는데 특화

-----
### 웹 시스템 구성 - WAS, DB
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/fb74c271-14f4-43ff-8bf2-da1cd6b0af38">
</div>

1. WAS, DB만으로 시스템 구성 가능
2. WAS는 정적 리소스, 애플리케이션 로직 모두 제공 가능
3. WAS가 너무 많은 역할을 담당 → 서버 과부하 우려
4. 가장 비싼 애플리케이션 로직이 정적 리소스 때문에 수행이 어려울 수 있음
5. WAS 장애 시, 오류 화면도 노출 불가능
<div align="center">
<img src="https://github.com/user-attachments/assets/6836cfae-ca18-4b76-bf0b-f87ff4a6e894">
</div>

-----
### 웹 시스템 구성 - WEB (Web Server), WAS, DB
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/ba484374-e961-4a97-8e39-3e330912d959">
</div>

1. 정적 리소스는 웹 서버가 처리
2. 💡 웹 서버는 애플리케이션 로직같은 동적인 처리가 필요하면 WAS에 요청을 위임
3. WAS는 중요한 애플리케이션 로직 처리 전담
4. 💡 효율적인 리소스 관리
   - 정적 리소스가 많이 사용 : Web Server 증설
   - 애플리케이션 리소스가 많이 사용 : WAS 증설
<div align="center">
<img src="https://github.com/user-attachments/assets/3d823ff4-f98e-444c-be20-8efe506eb575">
</div>

5. 정적 리소스만 제공하는 Web Server는 잘 죽지 않음
6. 애플리케이션 로직이 동작하는 WAS 서버는 잘 죽음
7. WAS, DB 장애시 Web Server가 오류 화면 제공 가능
<div align="center">
<img src="https://github.com/user-attachments/assets/041b265a-6494-43f6-b846-b56120be84e1">
</div>

