-----
### 상품 등록 처리
-----
1. 상품 등록 폼에서 전달된 데이터로 실제 상품 등록 처리
2. 상품 등록 폼은 다음 방식으로 서버에 전달
   - POST - HTML Form
     + Content-type : application/x-www-form-urlencoded
     + HTTP 메세지 바디에 쿼리 파라미터 형식으로 전달 : itemName=itemA&price=10000&quantity=10
     + 예) 회원 가입, 상품 주문, HTML Form 사용

3. 요청 파라미터 형식으로 처리 : @RequestParam 사용

-----
### 상품 등록 처리 - @RequestParam - addItemV1
-----
```java
// 상품 등록 처리 (@RequestParam)
@PostMapping("/add")
public String addItemV1(@RequestParam String itemName,
                         @RequestParam Integer price,
                         @RequestParam Integer quantity,
                         Model model) {
     Item item = new Item();
     item.setItemName(itemName);
     item.setPrice(price);
     item.setQuantity(quantity);
     
     itemRepository.save(item);
     
     model.addAttribute("item", item);
     
     return "basic/item";
}
```

1. 먼저 @RequestParam String itemName : itemName 요청 파라미터 데이터를 해당 변수에 받음
2. Item 객체를 생성하고, itemRepository를 통해 저장
3. 저장된 item을 모델에 담아서 뷰에 전달
4. 💡 상품 상세에서 활용한 item.html 뷰 템플릿 재활용

-----
### 상품 등록 처리 - @ModelAttribute
-----
1. @RequestParam으로 변수를 하나씩 받아서 Item 생성 후 처리하는 과정은 불편함
2. 상품 등록 처리 - @ModelAttribute - addItemV2
```java
/**
 *
 * 상품 등록 처리 (@ModelAttribute)
 * @ModelAttribute("item") Item item
 * model.Attribute("item", item); 자동 추가
 */
@PostMapping("/add")
public String addItemV2(@ModelAttribute("item") Item item) {
    itemRepository.save(item);
    // model.addAttribute("item", item); // 자동 추가, 생략 가능

    return "basic/item";
}
```
3. @ModelAttribute - 요청 파라미터 처리
   - @ModelAttribute는 Item 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법 (setXxx)으로 입력

4. @ModelAttribute - Model 추가
   - 모델(Model)에 @ModelAttribute로 지정한 객체를 넣어줌
   - 즉, 위에서 model.addAttribute("item", item); 기능을 수행 (주석처리 되어도, 잘 동작)
   - 모델에 데이터를 담을 때는 이름이 필요한데, 이름은 @ModelAttribute에 지정한 name(value) 속성을 사용
   - 만약, 이름을 다르게 저장하면, 다른 이름으로 모델에 포함
     + @ModelAttribute("hello") Item item : 이름을 hello으로 지정
     + model.Attribute("hello", item) : 모델에 hello 이름으로 저장

5. 실행 전 이전 버전인 addItem1에 @PostMapping("/add")를 꼭 주석 처리해줘야 함 (중복 매핑 오류 방지)

-----
### 상품 등록 처리 - @ModelAttribute 이름 생략
-----
```java
/**
 *
 * 상품 등록 처리 (@ModelAttribute)
 * @ModelAttribute("item") Item item
 * model.Attribute("item", item); 자동 추가. 생략 가능
 * 생략시 model에 저장되는 name은 클래스명 첫글자만 소문자로 등록
 */
@PostMapping("/add")
public String addItemV3(@ModelAttribute Item item) {
    itemRepository.save(item);
    // model.addAttribute("item", item); // 자동 추가, 생략 가능

    return "basic/item";
}
```

1. @ModelAttribute 이름 생략 가능
2. 단, 생략하면 모델에 저장될 때 클래스명을 사용하는데, 클래스의 첫 글자만 소문자로 변경해서 등록
   - 예) @ModelAttribute 클래스명 : 모델에 자동 추가되는 이름
     + Item : item
     + HelloWorld : helloWorld

-----
### 상품 등록 처리 - @ModelAttribute 전체 생략
-----
```java
/**
*
* 상품 등록 처리 (@ModelAttribute)
* @ModelAttribute("item") Item item
* model.Attribute("item", item); 자동 추가
*/
@PostMapping("/add")
public String addItemV4(Item item) {
     itemRepository.save(item);
     // model.addAttribute("item", item); // 자동 추가, 생략 가능

     return "basic/item";
}
```
1. @ModelAttribute 자체도 생략 가능
2. 대상 객체는 모델에 자동 등록
 
