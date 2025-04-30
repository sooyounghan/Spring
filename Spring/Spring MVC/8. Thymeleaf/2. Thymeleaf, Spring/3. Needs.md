-----
### 요구사항 추가
-----
1. 기존 상품 서비스에 요구사항 추가
2. 판매 여부
   - 판매 오픈 여부
   - 체크 박스로 선택 가능

3. 등록 지역
   - 서울, 부산, 제주
   - 체크 박스로 다중 선택 가능

4. 상품 종류
   - 도서, 식품, 기타
   - 라디오 버튼으로 하나만 선택 가능

5. 배송 방식
   - 빠른 배송
   - 일반 배송
   - 느린 배송
   - select 박스로 하나만 선택 가능

6. 예시 이미지
<div align="center">
<img src="https://github.com/sooyounghan/Spring/assets/34672301/97bde6f2-2ca6-4412-b302-f907fdc76824">
</div>

-----
### ItemType - 상품 종류
-----
```java
package hello.itemservice.domain.item;

public enum ItemType {
    
    BOOK("도서"), FOOD("식품"), ETC("기타");
    
    private final String description;

    ItemType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

  - 상품 종류는 ENUM 사용
  - 설명을 위해 description 필드 추가

-----
### 배송 방식 - DeliveryCode
-----
```java
package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FAST : 빠른 배송
 * NORMAL : 일반 배송
 * SLOW : 느린 배송
 */
@Data
@AllArgsConstructor
public class DeliveryCode {

    private String code;
    private String displayName;
}
```

  - 배송 방식은 DeliveryCode라는 클래스 사용
  - code는 FAST 같은 시스템에서 전달하는 값
  - displayName은 빠른 배송 같은 고객에게 보여주는 값


-----
### Item - 상품
-----
```java
package hello.itemservice.domain.item;

import lombok.Data;

import java.util.List;

@Data
public class Item {
    private Long id; // 상품 ID
    private String itemName; // 상품명
    private Integer price; // 상품 가격
    private Integer quantity; // 상품 수량

    private Boolean open; // 판매 여부
    private List<String> regions; // 등록 지역
    private ItemType itemType; // 상품 종류
    private String deliveryCode; // 배송 방식
    
    public Item() {

    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
```
