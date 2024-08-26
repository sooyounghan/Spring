package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JdbcTemplate
 */
@Slf4j
@Repository
public class JdbcTemplateRepositoryV1 implements ItemRepository {

    private final JdbcTemplate template;

    public JdbcTemplateRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "INSERT INTO ITEM(item_name, price, quantity) VALUES(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            // 자동 증가 키
            PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
            pstmt.setString(1, item.getItemName());
            pstmt.setInt(2, item.getPrice());
            pstmt.setInt(3, item.getQuantity());
            return pstmt;
        }, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);

        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "UPDATE ITEM SET item_name = ?, price = ?, quantity = ? WHERE id = ?";
        template.update(sql,
                        updateParam.getItemName(),
                        updateParam.getPrice(),
                        updateParam.getQuantity(),
                        itemId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "SELECT id, item_name, price, quantity FROM ITEM WHERE id = ?";

        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "SELECT id, item_name, price, quantity FROM ITEM";

        // 동적 쿼리
        if(StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;

        List<Object> param = new ArrayList<>();
        if(StringUtils.hasText(itemName)) {
            sql += " item_name LIKE CONCAT('%', ?, '%')";
            param.add(itemName);
            andFlag = true;
        }

        if(maxPrice != null) {
            if(andFlag) {
                sql += " and";
            }

            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql = {}", sql);
        return template.query(sql, itemRowMapper(), param.toArray());
    }

    private RowMapper<Item> itemRowMapper() {
        return ((rs, RowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        });
    }
}
