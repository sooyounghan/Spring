-----
### FieldError, ObjectError
-----
1. ValidationItemControllerV2 - addItemV2
```java
@PostMapping("/add")
public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

    // 검증 로직
    if(!StringUtils.hasText(item.getItemName())) {
        // 물건명에 글자가 없으면, 해당 데이터에 대한 검증 실패 이유 명시
        // bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
    }

    if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
        // 가격 정보가 없거나 1000원 미만, 백만원 초과하는 금액이면, 해당 데이터에 대한 검증 실패 이유 명시
        // bindingResult.addError(new FieldError("item", "price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
    }

    if(item.getQuantity() == null || item.getQuantity() > 9999) {
        // 수량이 없거나, 9999를 초과한다면, 해당 데이터에 대한 검증 실패 이유 명시
        // bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        bindingResult.addError( new FieldError("item", "quantity", item.getQuantity(),false, null, null, "수량은 최대 9,999 까지 허용합니다."));
    }

    // 특정 필드가 아닌 복합 Rule 검증
    if(item.getPrice() != null && item.getQuantity() != null) {
        // 가격과 수량이 존재함
        int resultPrice = item.getPrice() * item.getQuantity();
        if(resultPrice < 10000) {
            // 가격 * 수량 값이 10000원 미만이면, 해당 데이터에 대한 검증 실패 이유 명시
            // bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
            bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice));
        }
    }

    // 검증에 실패하면, 다시 입력 폼으로 이동
    if(bindingResult.hasErrors()) {
        log.info("errors={}", bindingResult);
        return "validation/v2/addForm";
    }

    // 검증 성공 로직
    Item savedItem = itemRepository.save(item);
    redirectAttributes.addAttribute("itemId", savedItem.getId());
    redirectAttributes.addAttribute("status", true);
    return "redirect:/validation/v2/items/{itemId}";
}
```
  - FieldError 생성자 (ObjectError도 유사하게 두 가지 생성자 제공)
```java
public FieldError(String objectName, String field, String defaultMessage)
public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure, String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage)
```

  - 파라미터 목록 
    + objectName : 오류가 발생한 객체 이름
    + field : 오류 필드
    + rejectedValue : 사용자가 입력한 값 (거절된 값)
    + bindingFailure : 타입 오류 같은 바인딩 실패인지, 검증 실패인지 구분 값
    + codes : 메세지 코드
    + arguments : 메세지에서 사용하는 인자
    + defaultMessage : 기본 오류 메세지

  - 오류 발생 시 사용자 입력 값 유지
```java
new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다.");
```
  - 사용자의 입력 데이터가 컨트롤러의 @ModelAttribute에 바인딩되는 시점에 오류가 발생하면 모델 객체에 사용자 입력 값을 유지하기 어려움
  - 예를 들어, 가격이 숫자가 아닌 문자가 입력되면 가격은 Integer 타입이므로 문자를 보관할 수 있는 방법이 없음
  - 그래서, 오류가 발생한 경우 사용자 입력 값을 보관하는 별도의 방법 필요
  - 그리고, 이렇게 보관한 사용자 입력 값을 검증 오류 발생 시 화면에 다시 출력
  - 즉, FieldError는 오류 발생 시 사용자 입력 값을 저장하는 기능 제공
  - 💡 여기서, rejectedValue가 오류 발생 시 사용자 입력 값을 저장하는 필드
  - 💡 bindingFailure는 타입 오류 같은 바인딩이 실패했는지 여부를 적어주면 됨 (여기서는 바인딩 실패가 아니므로 false 사용)

  - 타임리프의 사용자 입력 값 유지
```java
th:field="*{price}"
```
  - 타임 리프의 th:field는 정상 상황에는 모델 객체의 값 사용
  - 오류가 발생하면 FieldError에서 보관한 값을 사용해 값을 출력

  - 스프링의 바인딩 오류 처리
    + 타입 오류로 바인딩에 실패하면, FieldError를 생성하면서 사용자가 입력한 값을 넣어둠
    + 그리고 해당 오류를 BindingResult에 담아서 컨트롤러에 호출
    + 따라서, 타입 오류 같은 바인딩 실패 시에도 사용자의 오류 메세지를 정상 출력 가능
