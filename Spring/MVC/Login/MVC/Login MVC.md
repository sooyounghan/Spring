-----
### 로그인 기능
-----
<div align="center">
<img src="https://github.com/user-attachments/assets/d511595a-f821-4a87-9975-9a92c3d5d9b3">
</div>

1. LoginService
```java
package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;

    /**
     *  @return null이면 로그인 실패
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(member -> member.getPassword().equals(password))
                .orElse(null);
    }
}
```
  - 로그인 핵심 비즈니스 로직은 회원을 조회한 다음에 파라미터로 넘어온 password와 비교해서 같으면 회원 반환
  - 만약, password가 다르면 null 반환

2. LoginForm
```java
package hello.login.web.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {
    
    @NotEmpty
    private String loginId;
    
    @NotEmpty
    private String password;
}
```

3. LoginController
```java
package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 시도
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        // 로그인한 회원의 정보가 없을 때
        if(loginMember == null) {
            bindingResult.reject("loginFail", "ID 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO

        return "redirect:/";
    }
}
```
  - 로그인 컨트롤러는 로그인 서비스를 호출해서 로그인에 성공하면 홈 화면으로 이동
  - 로그인에 실패하면 bindingResult.reject()를 사용해 글로벌 오류(ObjectError) 생성
  - 그리고, 정보를 다시 입력하도록 로그인 폼을 뷰 템플릿으로 사용

4. 로그인 뷰 템플릿 (templates/login/loginForm.html)
```html
<!DOCTYPE HTML>
<html xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="utf-8">
  <link th:href="@{/css/bootstrap.min.css}"
        href="../css/bootstrap.min.css" rel="stylesheet">
  <style>
    .container {
      max-width: 560px;
    }
    .field-error {
      border-color: #dc3545;
      color: #dc3545;
    }
  </style>
</head>
<body>
<div class="container">
  <div class="py-5 text-center">
    <h2>로그인</h2>
  </div>
  <form action="item.html" th:action th:object="${loginForm}" method="post">
    <div th:if="${#fields.hasGlobalErrors()}">
      <p class="field-error" th:each="err : ${#fields.globalErrors()}"
         th:text="${err}">전체 오류 메시지</p>
    </div>
    <div>
      <label for="loginId">로그인 ID</label>
      <input type="text" id="loginId" th:field="*{loginId}" class="form-control" th:errorclass="field-error">
      <div class="field-error" th:errors="*{loginId}" />
    </div>
    <div>
      <label for="password">비밀번호</label>
      <input type="password" id="password" th:field="*{password}" class="form-control" th:errorclass="field-error">
      <div class="field-error" th:errors="*{password}" />
    </div>
    <hr class="my-4">
    <div class="row">
      <div class="col">
        <button class="w-100 btn btn-primary btn-lg" type="submit">로그인
        </button>
      </div>
      <div class="col">
        <button class="w-100 btn btn-secondary btn-lg"
                onclick="location.href='items.html'"
                th:onclick="|location.href='@{/}'|"
                type="button">취소</button>
      </div>
    </div>
  </form>
</div> <!-- /container -->
</body>
</html>
```
  - loginId, password가 틀리면 글로벌 오류 발생

5. 실행
   - 실행하여 로그인이 성공하면 홈으로 이동
   - 로그인에 실패하면, "아이디 또는 비밀번호가 맞지 않습니다."라는 경고와 함께 로그인 폼이 나타남
   
