package com.javalab.shop.service;

import com.javalab.shop.dto.ItemFormDto;
import com.javalab.shop.dto.ItemImgDto;
import com.javalab.shop.dto.ItemSearchDto;
import com.javalab.shop.dto.MainItemDto;
import com.javalab.shop.entity.Item;
import com.javalab.shop.entity.ItemImg;
import com.javalab.shop.repository.ItemImgRepository;
import com.javalab.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    /**
     * 상품 등록
     */
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 등록
        Item item = itemFormDto.createItem(); // DTO -> Entity 변환
        itemRepository.save(item); // DB에 상품 저장

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item); // 이미지와 상품 연결

            // 첫 번째 이미지를 대표 이미지로 설정
            if (i == 0) {
                itemImg.setRepimgYn("Y");
            } else {
                itemImg.setRepimgYn("N");
            }

            // 이미지 저장
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId(); // 저장된 상품의 ID 반환
    }

    /**
     * 상품 상세 조회
     */
    @Transactional(readOnly = true)
    public ItemFormDto getItemDetail(Long itemId) {
        // 상품 이미지 리스트 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.entityToDto(itemImg); // 엔티티를 DTO로 변환
            itemImgDtoList.add(itemImgDto);
        }

        // 상품 정보 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new); // 상품이 없으면 예외 발생

        ItemFormDto itemFormDto = ItemFormDto.of(item); // 엔티티 -> DTO 변환
        itemFormDto.setItemImgDtoList(itemImgDtoList); // 이미지 정보 추가

        return itemFormDto;
    }

    /**
     * 상품 수정
     */
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 조회
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new); // 상품이 없으면 예외 발생

        // 상품 정보 업데이트
        item.updateItem(itemFormDto);

        // 수정된 이미지 리스트와 기존 이미지 ID 가져오기
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for (int i = 0; i < itemImgFileList.size(); i++) {
            // 이미지 파일이 있다면 업데이트
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId(); // 수정된 상품 ID 반환
    }

    /**
     * 상품 관리 (관리자 페이지)
     */
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable); // 상품 리스트 조회
    }

    /**
     * 메인 페이지에 표시될 상품 리스트
     */
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable); // 메인 상품 리스트 조회
    }

    /**
     * 상품 삭제
     */
    public boolean deleteItem(Long itemId) {
        try {
            // 상품 삭제
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다. ID: " + itemId));

            // 해당 상품에 대한 이미지 삭제
            List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
            for (ItemImg itemImg : itemImgList) {
                itemImgService.deleteItemImgs(List.of(itemImg.getId()));
            }

            // 상품 삭제
            itemRepository.delete(item);
            return true; // 성공적으로 삭제
        } catch (Exception e) {
            return false; // 삭제 실패
        }
    }
}
