package com.javalab.shop.controller;

import com.javalab.shop.constant.ItemSellStatus;
import com.javalab.shop.dto.ItemDto;
import com.javalab.shop.dto.ItemFormDto;
import com.javalab.shop.dto.ItemSearchDto;
import com.javalab.shop.entity.Item;
import com.javalab.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Optional;

@Controller
@RequiredArgsConstructor

public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 상세 페이지
     * 옛날꺼!!
     */
    @GetMapping("/item")
    public String getItem(Model model) {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .itemNm("테스트 상품")
                .price(10000)
                .stockNumber(10)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SOLD_OUT)
                //.regTime(LocalDateTime.now())
                .build();
        model.addAttribute("item", itemDto);
        return "itemView"; // 타임리프 페이지
    }

    /**
     * 상품 상세 페이지
     * - 파라미터를 쿼리 스트링 방식으로 전달
     * 리스트에서 상품명 누르면 이동되는 페이지!!! !! !
     */
    @GetMapping("/view")
    public String getItem(@RequestParam("id") Long id, Model model) {
        ItemDto itemDto = ItemDto.builder()
                .id(id)
                .itemNm("테스트 상품" + id)
                .price(10000)
                .stockNumber(10)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SOLD_OUT)
                .regTime(LocalDateTime.now())
                .build();
        model.addAttribute("item", itemDto);
        return "itemView"; // 타임리프 페이지
    }


    /**
     * 상품 상세 페이지 #2
     * - PathVariavle을 사용한 상품 상세 페이지
     * 리스트에서 상품명 누르면 이동되는 페이지!!! !! !
     */
    @GetMapping("/view/{id}")
    public String getItem2(@PathVariable("id") Long id, Model model) {
        ItemDto itemDto = ItemDto.builder()
                .id(id)
                .itemNm("테스트 상품" + id)
                .price(10000)
                .stockNumber(10)
                .itemDetail("테스트 상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SOLD_OUT)
                .regTime(LocalDateTime.now())
                .build();
        model.addAttribute("item", itemDto);
        model.addAttribute("param1", id);
        return "itemView"; // 타임리프 페이지
    }

    /**
     * 상품 목록 페이지
     */
    @GetMapping("/itemList")
    public String getItemList(Model model) {
        List<ItemDto> itemList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ItemDto item = ItemDto.builder()
                    .id((long) i)
                    .itemNm("테스트 상품 " + i)
                    .price(10000 + (i * 1000))
                    .stockNumber(10 + i)
                    .itemDetail("테스트 상품 " + i + " 상세 설명")
                    .itemSellStatus(i % 2 == 0 ? ItemSellStatus.SOLD_OUT : ItemSellStatus.SELL)
                    .regTime(LocalDateTime.now())
                    .build();
            itemList.add(item);
        }
        model.addAttribute("items", itemList);
        return "itemList";
    }

    /**
     * 상품 등록 페이지
     */
    @GetMapping("/admin/item/new")
    public String getForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }


    /**
     * 상품 등록 처리
     * - 상품 등록 페이지에서 입력한 상품 정보를 전달받아 상품을 등록하는 메서드
     */
    @PostMapping("/admin/item/new")
    public String itemForm(@Valid ItemFormDto itemFormDTO, BindingResult bindingResult, Model model,
                           @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {
        if (bindingResult.hasErrors()) {
            return "item/itemForm"; // 입력 오류 시 폼으로 돌아감
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDTO.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDTO, itemImgFileList); // 상품 저장
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
            return "item/itemForm"; // 에러 발생 시 폼으로 돌아감
        }

        return "redirect:/"; // 성공적으로 등록 후 리다이렉트
    }


    /**
     * 상품 상세 페이지
     * - 상품 번호를 전달 받아 상품 상세 페이지로 이동
     * @param itemId
     */
    @GetMapping("/admin/item/{itemId}")
    public String itemDetail(@PathVariable("itemId") Long itemId, Model model){
        try{
            ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
            return "item/itemForm";
        } catch (EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            return "item/itemForm";
        }
    }

    /**
     * 상품 수정 처리
     * - 상품 수정 페이지에서 입력한 상품 정보를 전달받아 상품을 수정하는 메서드
     * - 상품 이미지가 변경된 경우 기존 이미지를 삭제하고 새로운 이미지를 등록
     * - 상품 이미지를 삭제하는 경우 기존 이미지를 삭제한다.
     */
    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model){
        // 1. 입력값 검증
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        // 2. 첫번째 상품 이미지가 비어있는 경우 검증
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        // 3. 총 첨부할 수 있는 파일의 개수는 5개에서 비어있는 파일을 제외
        itemImgFileList = itemImgFileList.stream()
                .filter(file -> !file.isEmpty())
                .toList();

        // 4. 상품 수정
        try{
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
        }

//        return "redirect:/";
        // 5. 상품 수정 후 상세보기 페이지로 리다이렉트
        return "redirect:/admin/item/" + itemFormDto.getId();
    }


    /**
     * 상품 관리 페이지 이동 및 조회한 상품 데이터를 화면에 전달
     * - /admin/items : 상품 관리 페이지로 이동
     * - /admin/items/{page} : 상품 관리 페이지로 이동(페이지 번호가 있는 경우)
     * - @PathVariable("page") Optional<Integer> page : 페이지 번호를 Optional로 받음
     * - Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3) : 페이지 번호가 있는 경우 해당 페이지로 이동
     *
     * @param itemSearchDto
     * @param page
     * @param model
     * @return
     */
    @GetMapping({"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto,
                             @PathVariable("page") Optional<Integer> page, Model model){

        // page.get() : Optional 객체에서 값을 가져옴, 이 값은 페이지 번호, 없는 경우 0
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        // itemSearchDto : 검색 조건을 화면에 다시 전달

        model.addAttribute("itemSearchDto", itemSearchDto);
        // maxPage : 최대 페이지 수, 화면에 5개의 페이지 번호를 표시
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }


    /**
     * 모든 활성화된 아이템 목록을 조회하는 API.
     * @return HTTP 응답 상태 200 OK와 함께 활성화된 아이템 리스트 반환
     */
    @GetMapping("/admin/items/active")
    public String getActiveItems(ItemSearchDto itemSearchDto, Model model) {
        Pageable pageable = PageRequest.of(0, 3); // 첫 페이지로 설정
        Page<Item> activeItems = itemService.getActiveItems(itemSearchDto, pageable); // 활성화된 아이템 조회

        model.addAttribute("items", activeItems);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5); // 최대 페이지 수 설정

        return "item/itemMng"; // 아이템 관리 페이지로 이동
    }

    /**
     * 상품 상세 페이지
     * - 상품 상세 페이지로 이동
     * @param itemId
     */
    @GetMapping("/item/{itemId}")
    public String itemDetail(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDetail";
    }



    /**
     * 특정 ID의 아이템을 활성화하는 API.
     * @param id 활성화할 아이템의 ID
     * @return HTTP 응답 상태 200 OK
     */
    @PostMapping("/items/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateItem(@PathVariable Long id) {
        itemService.activateItem(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 ID의 아이템을 비활성화하는 API.
     * @param id 비활성화할 아이템의 ID
     * @return HTTP 응답 상태 200 OK
     */
    @PostMapping("/items/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateItem(@PathVariable Long id) {
        itemService.deactivateItem(id);
        return ResponseEntity.ok().build();
    }
}
