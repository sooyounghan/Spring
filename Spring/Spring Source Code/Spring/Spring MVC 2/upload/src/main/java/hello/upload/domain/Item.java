package hello.upload.domain;

import lombok.Data;

import java.util.List;

@Data
public class Item {

    private Long id;
    private String itemName; // 상품 이름
    private UploadFile attachFile; // 첨부 파일
    private List<UploadFile> imageFiles; // 첨부 파일 목록

}
