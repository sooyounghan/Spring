-----
### Form 전송 객체 분리 
-----
1. 실무에서는 groups를 사용하지 않는데, 등록시 폼에서 전달하는 데이터가 Item 도메인 객체와 딱 맞지 않기 때문임
2. 회원 등록 시 회원과 관련된 데이터만 받는 것이 아니라, 약관 정보도 추가로 받는 등 Item과 관계 없는 수 많은 부가 데이터가 넘어옴
3. 따라서, Item을 전달받는 것이 아니라, 복잡한 폼의 데이터를 컨트롤러까지 전달할 별도의 객체를 만들어 전달
4. 예를 들면, ItemSaveForm이라는 폼을 전달받는 전용 객체를 만들어 @ModelAttribute로 사용
5. 이를 통해 컨트롤러에서 폼 데이터를 전달 받고, 이후 컨트롤러에서 필요한 데이터를 사용해 Item 생성

-----
### 폼 데이터 전달에 Item 도메인 객체 사용
-----
1. Html Form → Item → Controller → Item → Repository
2. 장점 : Item 도메인 객체를 컨트롤러, 레포지토리까지 직접 전달해서 중간에 Item을 만드는 과정이 없어서 간단
3. 단점 : 간단한 경우에만 적용 가능 / 수정 시 검증이 중복될 수 있고, groups 사용해야 함

-----
### 폼 데이터 전달에 별도의 객체 사용
-----
1. Html Form → ItemSaveForm → Controller → Item 생성 → Repository
2. 장점
   - 전송하는 폼 데이터가 복잡해도 거기에 맞춘 별도의 폼 객체를 사용해 데이터 전달 받을 수 있음
   - 또한, 등록과, 수정용으로 별도의 폼 객체를 만들기 때문에 검증이 중복되지 않음
3. 단점 : 폼 데이터를 기반으로 컨트롤러에서 Item 객체를 생성하는 변환 과정 추가

-----
### 수정의 경우
------
1. 등록과 수정은 완전히 다른 데이터가 넘어옴
2. 즉, 회원 가입시 다루는 데이터와 수정 시 다루는 데이터는 범위의 차이가 있음
3. 예를 들어, 로그인 ID, 주민번호 등 받을 수 있지만, 수정 시에는 이런 부분이 빠짐
4. 그리고 검증 로직도 많이 달라지므로, ItemUpdateForm이라는 별도의 객체로 데이터를 전달받는 것이 좋음
5. 따라서, 폼 데이터 전달을 위한 별도의 객체를 사용하고, 등록 / 수정용 폼 객체를 나누면 등록, 수정이 완전히 분리되므로 groups를 적용할 일은 적어짐

-----
### 참고사항
-----
1. 이름은 의미있게 지으면 됨 (ItemSave, ItemSaveForm, ItemSaveRequest, ItemSaveDto 등 사용 가능)
   - 즉, 일관성 있게 지으면 됨
2. 등록, 수정용 뷰 템플릿의 경우 각 장단점은 있지만, 어설프게 합치면 수많은 분기분(등록 / 수정일 때) 때문에 나중에 유지보수에서 문제 발생
   - 따라서, 분기문들이 보이면 분리하는 것이 좋음

-----
### Form 전송 객체 분리 - 개발
-----
1. Item : 이제 Item의 검증은 사용하지 않으므로 검증 코드 제거
```java
package hello.itemservice.domain.item;

import lombok.Data;
@Data
public class Item {
    private Long id; // 상품 ID

    private String itemName; // 상품명

    private Integer price; // 상품 가격

    private Integer quantity; // 상품 수량


    public Item() {

    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```

2. ItemSaveForm - ITEM 저장용 폼
```java
package hello.itemservice.web.validation.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@Data
public class ItemSaveForm {
    @NotBlank
    private String itemName;
    
    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;
    
    @NotNull
    @Max(value = 9999)
    private Integer quantity;
}
```

3. ItemUpdateForm - ITEM 수정용 폼
```java
package hello.itemservice.web.validation.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

@Data
public class ItemUpdateForm {
    @NotNull
    private Long id;

    @NotBlank
    private String itemName;

    @NotNull
    @Range(min = 1000, max = 1000000)
    private Integer price;

    // 수정 시에 수량은 자유롭게 변경 가능
    private Integer quantity;
}
```

4. ValidationItemControllerV4
```java
package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/validation/v4/items")
@RequiredArgsConstructor
public class ValidationItemControllerV4 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v4/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v4/addForm";
    }

    @PostMapping("/add")
    public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 특정 필드가 아닌 전체 예외
        if(form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면, 다시 입력 폼으로 이동
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/addForm";
        }

        // 검증 성공 로직
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v4/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v4/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {
        // 특정 필드가 아닌 전체 예외
        if(form.getPrice() != null && form.getQuantity() != null) {
            int resultPrice = form.getPrice() * form.getQuantity();
            if(resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면, 다시 입력 폼으로 이동
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "validation/v4/editForm";
        }

        Item itemParam = new Item();
        itemParam.setItemName(form.getItemName());
        itemParam.setPrice(form.getPrice());
        itemParam.setQuantity(form.getQuantity());

        itemRepository.update(itemId, itemParam);
        return "redirect:/validation/v4/items/{itemId}";
    }
}
```
  - 기존 코드 제거 : addItem(), addItem2()
  - 기존 코드 제거 : edit(), editV2()
  - 추가 : addItem(), edit()

5. 폼 객체 바인딩
```java
@PostMapping("/add")
public String addItem(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
  // ...
}
```
  - Item 대신 ItemSaveForm을 전달 받음
  - @Validated로 검증도 수행하고, BindingResult로 검증 결과를 받음
  - 💡 @ModelAttribute("item")에 item이름을 넣어준 부분 주의
    + 이를 넣지 않으면, ItemSaveForm의 경우 규칙에 의해 itemSaveForm이라는 이름으로 MVC Model에 담김
    + 이렇게 되면 뷰 템플릿에서 접근하는 th:object 이름도 함께 변경해줘야 함

6. 폼 객체를 Item으로 변환
```java
// 검증 성공 로직
Item item = new Item();
item.setItemName(form.getItemName());
item.setPrice(form.getPrice());
item.setQuantity(form.getQuantity());

Item savedItem = itemRepository.save(item);
````
  - 폼 객체의 데이터를 기반으로 Item 객체 생성
  - 폼 객체처럼 중간에 다른 객체가 추가되면, 변환하는 과정 추가

7. 수정
```java
@PostMapping("/{itemId}/edit")
public String edit(@PathVariable Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form, BindingResult bindingResult) {
  // ...
}
```
  - 수정의 경우도 등록과 동일
  - 폼 객체를 Item으로 변환하는 과정 거침
