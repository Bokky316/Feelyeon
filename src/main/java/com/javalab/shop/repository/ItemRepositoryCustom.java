package com.javalab.shop.repository;

import com.javalab.shop.dto.ItemSearchDto;
import com.javalab.shop.dto.MainItemDto;
import com.javalab.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

}
