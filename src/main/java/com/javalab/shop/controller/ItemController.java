package com.javalab.shop.controller;

import com.javalab.shop.dto.ItemFormDto;
import com.javalab.shop.dto.ItemSearchDto;
import com.javalab.shop.entity.Item;
import com.javalab.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록 페이지
     */
    @GetMapping("/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    /**
     * 상품 등록 처리
     */
    @PostMapping("/admin/item/new")
    public String saveItem(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                           @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                           Model model) {

        // 폼 검증 오류가 있으면 다시 폼을 반환
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        // 첫 번째 이미지 파일이 비어있으면 오류 메시지 처리
        if (itemImgFileList.get(0).isEmpty()) {
            model.addAttribute("errorMessage", "첫 번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            // 상품 저장 처리
            Long savedItemId = itemService.saveItem(itemFormDto, itemImgFileList);
            // 저장 후 상품 상세 페이지로 리다이렉션
            return "redirect:/admin/item/" + savedItemId;
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
            return "item/itemMng";
        }
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/admin/item/{itemId}")
    public String itemDetail(@PathVariable Long itemId, Model model) {
        try {
            // 상품 상세 정보 불러오기
            ItemFormDto itemFormDto = itemService.getItemDetail(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
        }
        return "item/itemForm";
    }

    /**
     * 상품 수정 처리
     */
    @PostMapping("/admin/item/{itemId}")
    public String updateItem(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model) {

        // 폼 검증 오류가 있으면 다시 폼을 반환
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        try {
            // 상품 수정 처리
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
            return "item/itemForm";
        }

        // 수정 후 상세 페이지로 리다이렉션
        return "redirect:/admin/item/" + itemFormDto.getId();
    }

    /**
     * 상품 관리 페이지
     */
    @GetMapping({"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.orElse(0), 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "item/itemMng";
    }
}