package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
// @Table(name = "Item") // 객체명과 테이블명이 동일하면 생략 가능
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", length = 10)
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
