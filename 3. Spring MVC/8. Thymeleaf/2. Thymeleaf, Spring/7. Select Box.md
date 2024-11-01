-----
### 셀렉트 박스
-----
1. 여러 선택지 중 하나를 선택해서 사용 가능
2. 셀렉트 박스를 자바 객체를 활용
```
- 배송 방식
  + 빠른 배송
  + 일반 배송
  + 느린 배송

- 셀렉트 박스 하나만 선택 가능
```

-----
### FormItemController - 추가
-----
```java
@ModelAttribute("deliveryCodes")
public List<DeliveryCode> deliveryCodes() {
    List<DeliveryCode> deliveryCodes = new ArrayList<>();

    deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
    deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
    deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
    return deliveryCodes;
}
```

1. DeliveryCode 라는 자바 객체를 사용하는 방법
2. 등록 폼, 조회, 수정 폼 모두 사용하므로 @ModelAttribute 적용
   - deliverCodes() 메서드는 컨트롤러가 호출될 때 마다 사용되므로 deliverCodes 객체도 계속 생성
   - 따라서, 미리 생성해두고 재사용하는 것이 더 효율적

-----
### addForm.html 추가
-----
```html
<!-- SELECT -->
<div>
    <div>배송 방식</div>
    <select th:field="*{deliveryCode}" class="form-select">
        <option value="">==배송 방식 선택==</option>
        <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                th:text="${deliveryCode.displayName}">FAST</option>
    </select>
</div>
```
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/33858dce-3e93-45b9-b238-5adf54a15d31">
</div>

-----
### 상품 상세와 수정도 추가
-----
1. item.html
```html
<!-- SELECT -->
<div>
    <div>배송 방식</div>
    <select th:field="${item.deliveryCode}" class="form-select" disabled>
        <option value="">==배송 방식 선택==</option>
        <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                th:text="${deliveryCode.displayName}">FAST</option>
    </select>
</div>
```
  - item.html은 th:object를 사용하지 않으므로 th:field 부분에 ${item.deliveryCode}로 작성
  - disabled를 사용해 상품 상세에는 셀렉트 박스가 선택되지 않도록 함

2. editForm.html
```html
<!-- SELECT -->
<div>
    <div>배송 방식</div>
    <select th:field="*{deliveryCode}" class="form-select">
        <option value="">==배송 방식 선택==</option>
        <option th:each="deliveryCode : ${deliveryCodes}" th:value="${deliveryCode.code}"
                th:text="${deliveryCode.displayName}">FAST</option>
    </select>
</div>
```

<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/2f88cc65-5dc5-4c58-9ab6-41c180c1eb86">
</div>

  - 💡 선택된 셀렉트 박스가 유지되는 것 확인 가능
  - 💡 즉, th:value의 값과 th:field 값을 비교해서 선택된 값이 있으면, selected="selected" 추가


