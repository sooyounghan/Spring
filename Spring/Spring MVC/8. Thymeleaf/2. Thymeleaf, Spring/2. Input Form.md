-----
### 입력 폼 처리
-----
1. th:object : 커맨드 객체 지정
2. *{...} : 선택 변수식 (th:object에서 선택한 객체에 접근)
3. th:field : HTML 태그의 id, name, value 속성을 자동 처리
4. 렌더링 전
```html
<input type="text" th:field="*{itemName}"/>
```
5. 렌더링 후
```html
<input type="text" id="itemName" name="itemName" th:value="*{itemName}"/>
```

-----
### 등록 폼
-----
1. th:object를 적용하려면 먼저 해당 오브젝트 정보를 넘겨줘야 함
2. 등록 폼이기 때문에 데이터가 빈 오브젝트를 만들어 뷰에 전달
```java
@GetMapping("/add")
public String addForm(Model model) {
    model.addAttribute("item", new Item());
    return "form/addForm";
}
```

3. 타임리프 등록 폼 변경 (/form/addForm.html)
```html
<form action="item.html" th:action th:object="${item}" method="post">
    <div>
        <label for="itemName">상품명</label>
        <input type="text" id="itemName" th:field="*{itemName}" class="form-control" placeholder="이름을 입력하세요">
    </div>
    <div>
        <label for="price">가격</label>
        <input type="text" id="price" th:field="*{price}" class="form-control" placeholder="가격을 입력하세요">
    </div>
    <div>
        <label for="quantity">수량</label>
        <input type="text" id="quantity" th:field="*{quantity}" class="form-control" placeholder="수량을 입력하세요">
    </div>
    <hr class="my-4">
    <div class="row">
        <div class="col">
            <button class="w-100 btn btn-primary btn-lg" type="submit">상품 등록</button>
        </div>
        <div class="col">
            <button class="w-100 btn btn-secondary btn-lg" onclick="location.href='items.html'"
                    th:onclick="|location.href='@{/form/items}'|" type="button">취소</button>
        </div>
    </div>
</form>
```
  - th:object="${item}" : ```<form>```에서 사용할 객체 지정 - 선택 변수 식(*{...}) 적용 가능
  - th:field="*{itemName}"
    + *{itemName}는 선택 변수 식 사용 = ${item.itemName}과 동일
    + th:object로 item을 선택했기 때문에 선택 변수 식 적용 가능
    + th:field는 id, name, value 속성을 모두 자동으로 만들어줌
      * id : th:field에서 지정한 변수 이름과 같음(id = "itemName")
      * name : th:field에서 지정한 변수 이름과 같음(name = "itemName")
      * 💡 value : th:field에서 지정한 변수의 값을 사용(value = "") (현재 빈 객체이므로 "")

  - 참고로 id 속성을 제거해도 th:field가 자동으로 만들어줌
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/f0fea308-3614-4c0e-91e7-632f86f8c459">
</div>

-----
### 수정 폼
-----
1. FormItemController
```java
@GetMapping("/{itemId}/edit")
public String editForm(@PathVariable Long itemId, Model model) {
    Item item = itemRepository.findById(itemId);
    model.addAttribute("item", item);
    return "form/editForm";
}
```

2. form/edit.html 변경 부분
```html
<form action="item.html" th:action th:object="${item}" method="post">
    <div>
        <label for="id">상품 ID</label>
        <input type="text" id="id" class="form-control" th:field="*{id}" readonly>
    </div>
    <div>
        <label for="itemName">상품명</label>
        <input type="text" id="itemName"class="form-control" th:field="*{itemName}">
    </div>
    <div>
        <label for="price">가격</label>
        <input type="text" id="price" class="form-control" th:field="*{price}">
    </div>
    <div>
        <label for="quantity">수량</label>
        <input type="text" id="quantity" class="form-control" th:field="*{quantity}">
    </div>
    <hr class="my-4">
    <div class="row">
        <div class="col">
            <button class="w-100 btn btn-primary btn-lg" type="submit">저장</button>
        </div>
        <div class="col">
            <button class="w-100 btn btn-secondary btn-lg"
                    onclick="location.href='item.html'"
                    th:onclick="|location.href='@{/form/items/{itemId}(itemId=${item.id})}'|"
                    type="button">취소</button>
        </div>
    </div>
</form>
```

  - 수정 폼의 경우 id, name, value 모두 신경써야 했는데, 많은 부분이 th:field 덕분에 자동 처리

<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/7229f1c0-f7de-412a-b45c-1282845ac747">
</div>

