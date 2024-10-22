-----
### HTML Form 데이터 전송
-----
: POST 전송 - 저장
<div align="center">
<img src="https://github.com/user-attachments/assets/d391ad91-2530-40fd-afa2-1a68a1f74d46">
</div>

-----
### 서버에서 처리해야 하는 업무
-----
1. 웹 애플리케이션 서버 직접 구현
<div align="center">
<img src="https://github.com/user-attachments/assets/1f92803b-34c4-4659-91e9-15803c169409">
</div>

2. Servlet을 지원하는 WAS 사용
<div align="center">
<img src="https://github.com/user-attachments/assets/dfc62e7b-86a2-455c-ba5d-76b2105d07b2">
</div>

----- 
### 서블릿 (Servlet)
-----
```java
@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse Response) {
        // Application Logic
    }
}
```
1. 특징
   - urlPatterns(/hello)의 URL이 호출되면 Servlet 코드 실행
   - HttpServletRequest : HTTP 요청 정보를 편리하게 사용할 수 있음
   - HttpServletResponse : HTTP 응답 정보를 편리하게 제공할 수 있음
   - HTTP 스펙을 매우 편리하게 사용

<div align="center">
<img src="https://github.com/user-attachments/assets/234535ef-17cd-447f-a760-8ebba793e6b0">
</div>

2. HTTP 요청, 응답 흐름
   - HTTP 요청 시 WAS는 Request, Response 객체를 새로 만들어서 Servlet 객체 호출
   - Request 객체에서 HTTP 요청 정보를 편리하게 꺼내서 사용
   - Response 객체에 HTTP 응답 정보를 편리하게 입력
   - WAS는 Response 객체에 담겨 있는 내용으로 HTTP 응답 정보 생성

-----
### 서블릿 컨테이너 (Servlet Container)
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/28a94f8b-76d2-43c2-b8c6-eab52c22305c">
</div>

1. Tomcat처럼 Servlet을 지원하는 WAS
2. 서블릿 객체를 생성, 초기화, 호출, 종료하는 생명주기 관리
3. 💡 서블릿 객체는 Singleton으로 관리 (request, response는 Client가 다양하므로 계속 생성, 하지만 Servlet 객체는 하나만 생성)
   - 고객의 요청이 올 때마다 객체를 계속 생성하는 것은 비효율
   - 따라서, 최초 로딩 시점에 서블릿 객체를 미리 만들어두고 재활용
   - 💡 모든 고객 요청은 동일한 서블릿 객체 인스턴스에 접근
   - 💡 공유 변수 사용 주의
   - 서블릿 컨테이너 종료 시 함께 종료
4. JSP도 서블릿으로 변환되어서 사용
5. 💡 동시 요청을 위한 멀티 쓰레드 지원
  - 서버에 다수의 요청이 존재해도, 멀티 쓰레드를 지원하므로 처리 가능
