package hello.itemservice.domain.item.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {
    private final ItemRepository itemRepository;

    // 상품 목록
    @GetMapping
    public String item(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    // 상품 상세
    @GetMapping("/{itemId}")
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    // 상품 등록 폼
    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    // 상품 등록 처리 (@RequestParam)
    // @PostMapping("/add")
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

    /**
     *
     * 상품 등록 처리 (@ModelAttribute)
     * @ModelAttribute("item") Item item
     * model.Attribute("item", item); 자동 추가
     */
    // @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);
        // model.addAttribute("item", item); // 자동 추가, 생략 가능

        return "basic/item";
    }

    /**
     *
     * 상품 등록 처리 (@ModelAttribute)
     * @ModelAttribute("item") Item item
     * model.Attribute("item", item); 자동 추가. 생략 가능
     * 생략시 model에 저장되는 name은 클래스명 첫글자만 소문자로 등록
     */
    // @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        // model.addAttribute("item", item); // 자동 추가, 생략 가능

        return "basic/item";
    }

    /**
     *
     * 상품 등록 처리 (@ModelAttribute)
     * @ModelAttribute("item") Item item
     * model.Attribute("item", item); 자동 추가
     */
    // @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        // model.addAttribute("item", item); // 자동 추가, 생략 가능

        return "redirect:/basic/items/" + item.getId();
    }

    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 리다이렉트 속성 추가 (redirect에서 사용될 URL 변수 값)
        // 저장이 되었다는 의미의 status=true (쿼리 파라미터 값으로 전송)
        redirectAttributes.addAttribute("status", true); // 리다이렉트 할 때, 추가

        return "redirect:/basic/items/{itemId}";
    }

    // 상품 수정 폼
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    // 상품 수정 처리
    @PostMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("testA", 10000, 10));
        itemRepository.save(new Item("testB", 20000, 20));
    }
}
