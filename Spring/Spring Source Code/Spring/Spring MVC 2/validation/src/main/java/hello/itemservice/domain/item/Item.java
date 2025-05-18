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
