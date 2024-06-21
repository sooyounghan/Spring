-----
### JSP 회원 관리 웹 어플리케이션 
-----
-----
### JSP 라이브러리 추가
-----
1. build.gradle
```gradle
// JSP 추가 시작
implementation 'org.apache.tomcat.embed:tomcat-embed-jasper'
implementation 'jakarta.servlet:jakarta.servlet-api' // 스프링부트 3.0 이상
implementation 'jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api' // 스프링부트 3.0 이상
implementation 'org.glassfish.web:jakarta.servlet.jsp.jstl' // 스프링부트 3.0 이상
// JSP 추가 끝
```

2. 회원 등록 폼 (webapp/jsp/members/new-form.jsp)
```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="/jsp/members/save.jsp" method="post">
        username: <input type="text" name="username" />
        age:      <input type="text" name="age" />
        <button type="submit">전송</button>
    </form>
</body>
</html>
```
  - <%@ page contentType="text/html;charset=UTF-8" language="java" %> : 첫 줄이 JSP 문서라는 뜻 (JSP의 시작 부분)
  - 첫 줄을 제외하고 완전히 HTML와 동일
  - JSP는 서버 내부에서 서블릿으로 변환되는데, MemberFormServlet과 비슷한 모습으로 변환
  - 실행 : http://localhost:9090/jsp/members/new-form.jsp (실행 시 .jsp 까지 적어줘야함)

3. 회원 저장 JSP (webapp/jsp/members/save.jsp)
```jsp
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page import="hello.servlet.domain.member.MemberRepository" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // request, response 사용 가능
    MemberRepository memberRepository = MemberRepository.getInstance();

    System.out.println("MemberSaveServlet.service");
    String username = request.getParameter("username");
    int age = Integer.parseInt(request.getParameter("age"));

    Member member = new Member(username, age);
    memberRepository.save(member);
%>

<html>
<head>
    <title>Title</title>
</head>
<body>
    성공
    <ul>
        <li>id = <%=member.getId()%></li>
        <li>username = <%=member.getUsername()%></li>
        <li>age = <%=member.getAge()%></li>
    </ul>

    <a href = "/index.html"> Main </a>

</body>
</html>
```
  - JSP는 자바 코드를 그대로 거의 사용 가능
  - 자바의 import문과 동일
```jsp
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page import="hello.servlet.domain.member.MemberRepository" %>
```

  - 자바 코드 입력 가능
```jsp
<% ... %>
```

  - 자바 코드 출력 가능
```jsp
<%= ... %>
```

  - 회원 저장 JSP를 보면, 회원 저장 서블릿 코드와 동일하나, 다른 점이 있다면, HTML을 중심으로 하고, 자바 코드를 부분적으로 입력
  - <% ... %>를 사용해 HTML 중간에 자바 코드를 출력함

4. 회원 목록 JSP (webapp/jsp/members.jsp)
```jsp
<%@ page import="hello.servlet.domain.member.MemberRepository" %>
<%@ page import="hello.servlet.domain.member.Member" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    MemberRepository memberRepository = MemberRepository.getInstance();

    List<Member> members = memberRepository.findAll();
%>
<html>
<head>
    <title>Title</title>
</head>
<body>

    <table>
        <thead>
        <th>id</th>
        <th>username</th>
        <th>age</th>
        </thead>
        <tbody>
        <%
            for (Member member : members) {
                out.write("    <tr>");
                out.write("        <td>" + member.getId() + "</td>");
                out.write("        <td>" + member.getUsername() + "</td>");
                out.write("        <td>" + member.getAge() + "</td>");
                out.write("    </tr>");
            }
        %>
        </tbody>
    </table>
</body>
</html>
```

<div align="center">
<img src="https://github.com/sooyounghan/Data-Base/assets/34672301/dbc6ea86-8326-4125-8ad4-46abe2b794fa">
</div>

  - 회원 리포지토리를 먼저 조회하고, 결과 List를 사용해 중간에 ```<tr>, <td>``` HTML 태그를 반복해 출력

-----
### Servlet과 JSP의 한계
-----
1. Servlet으로 개발할 때, 뷰(View) 화면을 위한 HTML을 만드는 작업이 자바 코드에 섞여서 지저분하고 복잡
2. JSP를 사용한 덕분에 뷰(View)를 생성하는 HTML 작업을 깔끔하게 가져가고, 중간에 동적으로 변경이 필요한 부분에 자바 코드를 적용
3. 하지만, 회원 저장 JSP를 보면, 코드의 상위 절반은 회원을 저장하기 위한 비즈니스 로직이고, 하위 절반만 결과를 HTML로 보여주기 위한 뷰(View) 영역이며, 회원 목록도 동일
4. 코드를 잘 보면, JAVA 코드 / 데이터를 조회하는 레포지토리 등 다양한 코드가 모두 JSP에 노출
5. 즉, JSP가 너무 많은 역할을 함

-----
### MVC 패턴의 등장
-----
1. 비즈니스 로직은 Servlet 처럼 다른 곳에서 처리하고, JSP는 목적에 맞게 HTML로 화면(View)를 그리는 일에 집중하도록 하자.
2. 즉, MVC 패턴을 적용!



