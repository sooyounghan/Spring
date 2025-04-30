package hellojpa;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
    private String stockAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(String stockAmount) {
        this.stockAmount = stockAmount;
    }
}
