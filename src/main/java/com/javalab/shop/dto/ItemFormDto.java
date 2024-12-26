package com.javalab.shop.dto;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.entity.Item;
import jakarta.validation.constraints.*;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    @Size(max = 50, message = "상품명은 50자 이내로 입력해주세요.")  // 수정
    private String itemNm;

    @NotNull(message = "가격은 필수 입력값입니다.")
    @Min(value = 1, message = "가격은 1원 이상이어야 합니다.")
    private Integer price;

    @NotBlank(message = "상품 상세 설명은 필수 입력값입니다.")  // tinytext에 길이 제한이 없으므로 Size 제거
    private String itemDetail;

    @NotNull(message = "재고 수량은 필수 입력값입니다.")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    /*
     * ItemFormDto에는 ItemImgDto의 id가 아닌 itemImgId를 저장합니다.
     *  - 상품 이미지에 대한 상세 데이터를 담고 있는 DTO 객체들의 리스트.
     *  - 각 ItemImgDto는 상품 이미지의 여러 속성(예: 파일 이름, 경로, 대표 이미지 여부 등)을 포함합니다.
     * 용도
     *  - 상품 이미지의 상세 정보를 프론트엔드에서 보여주기 위해 사용.
     *  - 예를 들어, 상품 수정 화면에서 이미 저장된 이미지의 정보를 표시.
     */
    @Builder.Default    //@Builder.Default가 없으면, 객체를 빌더로 생성할 때 해당 필드가 null로 초기화됩니다.
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    /*
        * 상품 이미지의 고유 ID(Primary Key)만을 담고 있는 리스트.
        * - 각 ID는 데이터베이스에서 이미 저장된 이미지 엔티티(ItemImg)를 식별하는 데 사용됩니다.
        * - 상품 수정 페이지에서는 각 이미지 입력 필드와 함께 해당 이미지의 ID를 숨겨진 필드로 전송합니다.
        * 용도
        * - 이미지 수정/삭제 시 특정 이미지를 식별하기 위해 사용.
        * - 예를 들어, 이미지를 삭제할 때 해당 이미지의 ID를 기반으로 삭제 작업 수행.
     */
     @Builder.Default
    private List<Long> itemImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    // ItemFormDto를 Item 엔티티로 변환
    public Item crateItem(){
        return modelMapper.map(this, Item.class);
    }

    // Item 엔티티를 받아서 ItemFormDto로 변환
    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
    }

}
