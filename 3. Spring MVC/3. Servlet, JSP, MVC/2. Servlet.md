-----
### 서블릿으로 회원 관리 웹 애플리케이션 만들기
-----
1. 서블릿으로 회원 등록 HTML Form 생성
```java
package hello.servlet.web.servlet;

import hello.servlet.domain.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "memberFormServlet", urlPatterns = "/servlet/members/new-form")
public class MemberFormServlet extends HttpServlet {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer.write("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form action=\"/servlet/members/save\" method=\"post\">\n" +
                "    username: <input type=\"text\" name=\"username\" />\n" +
                "    age:      <input type=\"text\" name=\"age\" />\n" +
                "    <button type=\"submit\">전송</button>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>\n");
    }
}
```
  - MemberFormServlet은 단순하게 회원 정보를 입력할 수 있는 HTML Form을 만들어 응답
  - 자바 코드로 HTML를 작성해야 함
  - 실행 : http://localhost:9090/servlet/members/new-form
  - HTML Form 데이터를 POST로 작성해도, 전달 받는 서블릿이 아직 만들어지지 않았으므로, 오류가 발생하는 것이 정상

2. 회원 저장
   - HTML Form에서 데이터를 입력하고, 전송을 누르면 실제 회원 데이터가 저장되도록 하기 위한 서블릿
   - 전송 방식은 POST HTML Form
```java
package hello.servlet.web.servlet;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "memberSaveServlet", urlPatterns = "/servlet/members/save")
public class MemberSaveServlet extends HttpServlet {
    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("MemberSaveServlet.service");
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        Member member = new Member(username, age);
        memberRepository.save(member);

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "성공\n" +
                "<ul>\n" +
                "    <li>id="+member.getId()+"</li>\n" +
                "    <li>username="+member.getUsername()+"</li>\n" +
                "    <li>age="+member.getAge()+"</li>\n" +
                "</ul>\n" +
                "<a href=\"/index.html\">메인</a>\n" +
                "</body>\n" +
                "</html>");
    }
}
```

  - MemberServletSave의 동작 순서
    + A. 파라미터를 조회해 Member 객체를 만듬
    + B. Member 객체를 MemberRepository를 통해 저장
    + C. Member 객체를 사용해서 결과 화면용 HTML을 동적으로 만들어 응답
  - 실행 : http://localhost:9090/servlet/members/new-form
  - 데이터가 전송되고, 저장 결과 확인 가능
<div align="center">
<img src="https://github.com/sooyounghan/Data-Base/assets/34672301/7b7e56c7-cc98-4246-9c5f-f9fa8787b86a">
<img src="https://github.com/sooyounghan/Data-Base/assets/34672301/9293d1c6-d78a-40cc-8f06-e35bf7a3d6d1">
</div>

3. 회원 목록 조회
```java
package hello.servlet.web.servlet;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "memberListServlet", urlPatterns = "/servlet/members")
public class MemberListServlet extends HttpServlet {
    MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Member> members = memberRepository.findAll();

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter w = response.getWriter();
        w.write("<html>");
        w.write("<head>");
        w.write("    <meta charset=\"UTF-8\">");
        w.write("    <title>Title</title>");
        w.write("</head>");
        w.write("<body>");
        w.write("<a href=\"/index.html\">메인</a>");
        w.write("<table>");
        w.write("    <thead>");
        w.write("    <th>id</th>");
        w.write("    <th>username</th>");
        w.write("    <th>age</th>");
        w.write("    </thead>");
        w.write("    <tbody>");

        /*
        w.write("    <tr>");
        w.write("        <td>1</td>");
        w.write("        <td>userA</td>");
        w.write("        <td>10</td>");
        w.write("    </tr>");
        */

        for (Member member : members) {
            w.write("    <tr>");
            w.write("        <td>" + member.getId() + "</td>");
            w.write("        <td>" + member.getUsername() + "</td>");
            w.write("        <td>" + member.getAge() + "</td>");
            w.write("    </tr>");
        }
        
        w.write("    </tbody>");
        w.write("</table>");
        w.write("</body>");
        w.write("</html>");
    }
}
```
<div align="center">
<img src="https://github.com/sooyounghan/Data-Base/assets/34672301/8d0628db-7332-4482-8f7a-f1dd40f0316f">
</div>

  - MemberListServlet 동작 순서
    + A. memberRepository.findAll()를 통해 모든 회원 조회
    + B. 회원 목록 HTML을 for문을 통해 회원 수 만큼 동적 생성하고 응답
  - 실행 : http://localhost:9090/servlet/members
  - 저장된 회원 목록 확인 가능

4. 서블릿과 자바 코드만으로 HTML를 생성
   - 서블릿 덕분에 동적으로 원하는 HTML을 마음껏 생성 가능
   - 정적인 HTML 문서라면, 화면이 계속 달라지는 회원 저장 결과라던가, 회원 목록 같은 동적 HTML 만드는 일은 불가능
   - 💡 하지만, 코드가 매우 복잡하고 비효율적이며, 자바 코드로 HTML를 만들어내는 것 보다 차라리 HTML 문서에 동적으로 변경해야 하는 부분만 자바 코드를 넣는 것이 편리할 것
   - 💡 바로, 이것이 템플릿 엔진이 나온 이유로, 템플릿 엔진을 사용하면 HTML 문서에 필요한 곳만 코드를 적용해 동적으로 변경 가능
   - 템플릿 엔진 : JSP, Thymeleaf, Freemarker, Velocity 등이 존재
     + JSP는 성능과 기능 면에서 다른 템플릿 엔진과 경쟁에서 밀리면서, 스프링과 잘 통합되는 Thymeleaf를 사용
