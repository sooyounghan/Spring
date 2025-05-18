-----
### RedirectAttributes
-----
```java
@PostMapping("/add")
public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId()); // 리다이렉트 속성 추가 (redirect에서 사용될 URL 변수 값)
    // 저장이 되었다는 의미의 status=true (쿼리 파라미터 값으로 전송)
    redirectAttributes.addAttribute("status", true); // 리다이렉트 할 때, 추가

    return "redirect:/basic/items/{itemId}";
}
```
1. 실행 결과 : http://localhost:9090/basic/items/3?status=true
2. RedirectAttributes를 사용하면 URL 인코딩, PathVariable, 쿼리 파라미터까지 처리해줌
   - redirect:/basic/items/{itemId}
   - PathVariable 바인딩 : {itemId}
   - 나머지는 쿼리 파라미터로 처리 : ?status=true

3. 뷰 템플릿 메세지 추가
```html
<div class="container">
    <div class="py-5 text-center">
        <h2>상품 상세</h2>
    </div>
    <!-- 문자 삽입 시 ' '주의 -->
    <h2 th:if="${param.status}" th:text="'저장 완료!'"></h2>
    <div>

    ...
```
  - th:if
    + 해당 조건이 참이면 실행
  - 💡 ${param.status} : 타임리프에서 쿼리 파라미터를 편리하게 조회하는 기능
    + 원래는 컨트롤러에서 모델에 직접 값을 담고 꺼내야 하는데, 쿼리 파라미터는 자주 사용하므로 타입리프에서 직접 지원

    

