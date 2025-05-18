-----
### 💡 변경 감지와 병합
-----
1. 준영속 엔티티
   - 영속성 컨텍스트가 더는 관리하지 않는 엔티티
   - 여기서는 itemService.saveItem(book)에서 수정을 시도하는 Book 객체
```java
@PostMapping("items/{itemId}/edit")
public String updateItem(@ModelAttribute("form") BookForm form) {
    Book book = new Book();
    book.setId(form.getId()); // 준영속 엔티티
    book.setPrice(form.getPrice());
    book.setStockQuantity(form.getStockQuantity());
    book.setAutor(form.getAuthor());
    book.setIsbn(form.getIsbn());

    itemService.saveItem(book); // == Merge
    return "redirect:/items";
}
```

   - Book 객체는 이미 DB에 한 번 저장되어서 식별자가 존재
   - 이렇게 임의로 만들어낸 엔티티도 기존 식별자를 가지고 있으면, 준영속 엔티티로 볼 수 있음

2. 준영속 엔티티를 수정하는 2가지 방법
   - 변경 감지 기능 사용 (Dirty-Checking)
   - 병합 (merge) 사용

-----
### 변경 감지 기능 사용 (Dirty-Checking)
-----
```java
@Transactional
void update(Item itemParam) { // itemParam : 파라미터로 넘어온 준영속 상태 엔티티
    Item findItem = em.find(Item.class, itemParam.getId()); // 같은 엔티티를 조회
    findItem.setPrice(itemParam.getPrice()); // 데이터를 수정
    findItem.setName(itemParam.getName());
    findItem.setStockQuantity(itemParam.getStockQuantity());
}
```
1. 영속성 컨텍스트에서 엔티티를 다시 조회한 후 데이터를 수정하는 방법
2. 💡 트랜잭션 안에서 엔티티를 다시 조회, 변경할 값 선택 → 트랜잭션 커밋 시점에 변경 감지(Dirty Checking)이 동작해서 데이터베이스에 UPDATE SQL 실행

-----
### 병합 사용 (Merge)
-----
1. 병합은 준영속 상태에의 엔티티를 영속 상태로 변경할 때 사용하는 기능
```java
@Transactional
public void update(Item itemParam) { // itemParam : 파라미터로 넘어온 준영속 상태 엔티티
    Item mergetItem = em.merge(itemParam);
}
```

2. 병합 : 기존에 있는 엔티티
<div align="center">
<img src="https://github.com/user-attachments/assets/4fb374e0-c383-4324-bc80-4520c9805f1c">
</div>

3. 💡 병합 동작 방식
   - merge() 실행
   - 파라미터로 넘어온 준영속 엔티티의 식별자 값으로 1차 캐시에서 엔티티를 조회
   - 만약 1차 캐시에 엔티티가 없으면, 데이터베이스에 엔티티를 조회하고, 1차 캐시에 저장
   - 조회한 영속 엔티티(mergeMember)에 member 엔티티 값을 채워 넣음
     + member 엔티티의 모든 값을 mergeMember에 저장
     + 이 때, mergeMember의 "회원1"이라는 이름이 "회원명변경"으로 변경
   - 영속 상태의 mergeMember를 반환
```java
@Transactional
public Item updateItem(Long itemId, Item itemParam) { 
    Item findItem = itemRepository.findOne(itemId);
    findItem.setPrice(itemParam.getPrice()); // 데이터를 수정
    findItem.setName(itemParam.getName());
    findItem.setStockQuantity(itemParam.getStockQuantity());

    return findItem;
}
```

4. 병합 시 동작 방식을 간단히 정리
   - 준영속 엔티티 식별자 값으로 영속 엔티티를 조회
   - 영속 엔티티의 값을 준영속 엔티티 값으로 모두 교체 (병합)
   - 트랜잭션 커밋 시점에 변경 감지 기능이 동작해서 데이터베이스 UPDATE SQL 실행

5. 💡 주의 : 변경 감지 기능을 사용하면, 원하는 속성만 선택해서 변경 가능
   - 💡 병합을 사용하면, 모든 속성이 변경
   - 💡 병합 시 값이 없으면 NULL로 업데이트 할 위험 존재 (병합은 모든 필드 교체)

-----
### 상품 레포지토리 저장 메서드 분석 - ItemRepository
-----
```java
package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null) {
            em.persist(item); // Id 값이 없다면, 새로 생성한 객체로 이 값을 신규 등록
        } else {
            em.merge(item); // Id 값이 있다면, merge (이미 DB 등록 또는 가져온 값)
        }
    }

    ...
}
```
  - save() 메서드는 식별자 값이 없으면(null), 새로운 엔티티로 판단해서 영속화(persist)하고 식별자가 있으면 병합(merge)
  - 지금처럼 준영속 상태인 상품 엔티티를 수정할 때는 id 값이 있으므로 병합 수행

1. 새로운 엔티티 저장과 준영속 엔티티 병합을 편리하게 한 번에 처리
   - 상품 레포지토리에선 save() 메서드를 보면, 이 메서드 하나로 저장과 수정(병합) 모두 처리
   - 식별자 값이 없으면 새로운 엔티티로 판단해 persist()로 영속화
   - 식별자 값이 있으면 이미 한 번 영속화되었던 엔티티로 판단해 merge()로 수정(병합)
   - 결국, 여기서의 저장(save)이라는 의미는 신규 데이터를 저장하는 것 뿐만 아니라 변경된 데이터의 저장이라는 의미도 포함
   - 이 메서드를 사용하는 클라이언트는 저장과 수정을 구분하지 않아도 되므로 클라이언트 로직이 단순해짐

2. 여기서 사용하는 수정(병합)은 준영속 상태의 엔티티를 수정할 때 사용
   - 영속 상태의 엔티티는 변경 감지(Dirty-Checking) 기능이 동작해서 트랜잭션이 커밋할 때, 자동으로 수정되므로 별도의 수정 메서드를 호출할 필요가 없으며, 그런 메서드도 없음

3. 참고
   - save() 메서드는 식별자를 자동 생성해야 정상 동작
   - 여기서 사용한 Item 엔티티의 식별자는 자동으로 생성되도록 @GeneratedValue를 선언
   - 따라서, 식별자 없이 save() 메서드를 호출하면 persist()가 호출되면서 식별자 값이 자동 할당
   - 반면, 식별자를 직접 할당하도록 @Id만 선언했다고 가정하면, 이 경우 식별자를 직접 할당하지 않고, save() 메서드를 식별자가 없는 상태로 persist()를 호출
   - 그러면 식별자가 없다는 예외 발생

4. 참고
   - 실무에서는 보통 업데이트 기능이 매우 제한적
   - 그런데 병합은 모든 필드를 변경해버리고, 데이터 없으면 null로 업데이트
   - 병합을 사용하면서 이 문제를 해결하려면, 변경 폼 화면에 모든 데이터를 항상 유지
   - 실무에서는 보통 변경가능한 데이터만 노출하므로, 병합을 사용하는 것이 오히려 번거로움

-----
### 가장 좋은 해결 방법
-----
1. 💡 엔티티를 변경할 때는 항상 변경 감지 사용
2. 컨트롤러에서 어설프게 엔티티를 생성하지 말것
3. 트랜잭션이 있는 서비스 계층에 식별자(id)와 변경할 데이터를 명확하게 전달 (파라미터 or DTO)
4. 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고, 엔티티의 데이터를 직접 변경
5. 트랜잭션 커밋 시점 변경 감지가 실행
```java
package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    ...

    /**
     * 영속성 컨테스트가 자동 변경
     */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // 트랜잭션이 있는 서비스 계층에서 영속 상태 엔티티 조회, 엔티티의 데이터를 직접 변경
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        // 트랜잭션 커밋 시점 변경 감지 실행
    }

    ...
}
```
```java
package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    ...

    /**
     * 상품 수정, 권장 코드
     */
    @PostMapping("items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {
        // 컨트롤러에서 엔티티 미생성
        // 트랜잭션에 있는 서비스 계층에 식별자(id)와 변경할 데이터 명확하게 전달 (파라미터 or DTO)
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}
