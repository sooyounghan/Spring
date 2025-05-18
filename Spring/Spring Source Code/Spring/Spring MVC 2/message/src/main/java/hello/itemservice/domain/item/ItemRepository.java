package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {
    private static final Map<Long, Item> store = new HashMap<>(); // static 사용
    private static long sequence = 0L; // static 사용

    // 상품 저장 기능
    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    // 상품 하나 조회
    public Item findById(Long id) {
        return store.get(id);
    }

    // 상품 전체 조회
    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    // 상품 수정
    public void update(Long itemId, Item updateItem) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateItem.getItemName());
        findItem.setPrice(updateItem.getPrice());
        findItem.setQuantity(updateItem.getQuantity());
    }

    // 상품 전체 삭제
    public void clearStore() {
        store.clear();
    }
}
