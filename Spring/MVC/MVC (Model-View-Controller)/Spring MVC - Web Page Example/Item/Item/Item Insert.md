-----
### 상품 등록 폼
-----
1. 상품 등록 폼 컨트롤러
```java
// 상품 등록
@GetMapping("/add")
public String addForm() {
    return "/basic/addForm";
}
```
  - 상품 등록 폼은 단순히 뷰 템플릿만 호출

2. 상품 등록 폼 뷰
   - 정적 HTML을 뷰 템플릿 영역으로 복사하고 다음과 같이 수정
```
/resources/static/html/addForm.html → 복사 → /resources/templates/basic/addForm.html
```
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <link th:href="@{/css/bootstrap.min.css}"
          href="../css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        .container {
            max-width: 560px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 등록 폼</h2>
    </div>
    <h4 class="mb-3">상품 입력</h4>
    <form action="item.html" th:action method="post">
        <div>
            <label for="itemName">상품명</label>
            <input type="text" id="itemName" name="itemName" class="form-control" placeholder="이름을 입력하세요">
        </div>
        <div>
            <label for="price">가격</label>
            <input type="text" id="price" name="price" class="form-control" placeholder="가격을 입력하세요">
        </div>
        <div>
            <label for="quantity">수량</label>
            <input type="text" id="quantity" name="quantity" class="form-control" placeholder="수량을 입력하세요">
        </div>
        <hr class="my-4">
        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit">상품 등록</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg" onclick="location.href='items.html'"
                        th:onclick="|location.href='@{/basic/items}'|" type="button">취소</button>
            </div>
        </div>
    </form>
</div> <!-- /container -->
</body>
</html>
```

  - 💡 속성 변경 : th:action - ```th:action```
    + HTML Form에서 action 값이 없으면 현재 URL에 데이터를 전송
    + 즉, 상품 등록 폼의 URL과 실제 상품 등록을 처리하는 URL을 똑같이 맞추고, HTTP 메서드로 두 기능을 구분
      * 상품 등록 폼 : GET /basic/items/add
      * 상품 등록 처리 : POST /basic/items/add
    + 이렇게 하면, 하나의 URL로 등록 폼과 등록 처리를 깔끔하게 처리할 수 있음
```java
// 상품 등록 폼
@GetMapping("/add")
public String addForm() {
    return "basic/addForm";
}

// 상품 등록 처리
@PostMapping("/add")
public String save() {
    return "basic/addForm";
}
```

  - 취소 - ```th:onclick="|location.href='@{/basic/items}'|"```
    + 취소 시 상품 목록으로 이동
