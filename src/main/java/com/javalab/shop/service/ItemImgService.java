package com.javalab.shop.service;

import com.javalab.shop.entity.ItemImg;
import com.javalab.shop.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    /**
     * 상품 이미지 저장
     * - 상품 이미지 정보를 저장하는 메서드
     * - 상품 이미지 파일을 업로드하고, 상품 이미지 정보를 저장한다.
     */
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();  // 원본 이미지 파일명
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            // 파일을 업로드하고, 업로드된 파일의 이름을 가져옴
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 업로드된 파일의 URL 설정
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 업데이트 (DB에 저장될 이미지 정보)
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);  // DB에 상품 이미지 저장
    }

    /**
     * 상품 이미지 수정
     * - 상품 이미지 정보를 수정하는 메서드
     * - 상품의 이미지 id와 이미지 파일 정보를 전달 받아서 이미지 정보를 수정한다.
     */
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        // 1. 상품 이미지 조회
        ItemImg itemImg = itemImgRepository.findById(itemImgId)
                .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다. ID: " + itemImgId));

        // 2. 새로운 이미지 파일이 전달되면 기존 파일을 삭제
        if (!itemImgFile.isEmpty()) {
            fileService.deleteFile(itemImgLocation + "/" + itemImg.getImgName());
        }

        // 3. 새 이미지 파일을 업로드
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        // 4. 상품 이미지 정보 수정 (JPA가 변경 감지하여 자동으로 DB에 반영)
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
    }

    /**
     * 상품 이미지 삭제
     * - 주어진 ID 목록에 해당하는 상품 이미지들을 삭제하는 메서드
     */
    public void deleteItemImgs(List<Long> deleteImgIds) throws Exception {
        for (Long imgId : deleteImgIds) {
            ItemImg itemImg = itemImgRepository.findById(imgId)
                    .orElseThrow(() -> new EntityNotFoundException("이미지를 찾을 수 없습니다. ID: " + imgId));

            // 실제 이미지 파일 삭제
            if (!StringUtils.isEmpty(itemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + itemImg.getImgName());
            }

            // DB에서 이미지 정보 삭제
            itemImgRepository.delete(itemImg);
        }
    }
}
