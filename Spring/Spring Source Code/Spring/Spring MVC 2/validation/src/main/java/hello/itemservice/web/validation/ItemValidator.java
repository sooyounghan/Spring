package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ItemValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        // Item == clazz or Item == subItem
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())) {
            // 물건명에 글자가 없으면, 해당 데이터에 대한 검증 실패 이유 명시
            errors.rejectValue("itemName", "required");
        }

        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            // 가격 정보가 없거나 1000원 미만, 백만원 초과하는 금액이면, 해당 데이터에 대한 검증 실패 이유 명시
            errors.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }

        if(item.getQuantity() == null || item.getQuantity() > 9999) {
            // 수량이 없거나, 9999를 초과한다면, 해당 데이터에 대한 검증 실패 이유 명시
            errors.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 Rule 검증
        if(item.getPrice() != null && item.getQuantity() != null) {
            // 가격과 수량이 존재함
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000) {
                // 가격 * 수량 값이 10000원 미만이면, 해당 데이터에 대한 검증 실패 이유 명시
                errors.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }
    }
}
