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
    public void saveItemImg(ItemImg iTemImg, MultipartFile itemImgFile) throws Exception{

        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        //상품 이미지 정보 저장
        iTemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(iTemImg);
    }

    /**
     * 상품 이미지 수정
     * - 상품 이미지 정보를 수정하는 메서드
     * - 상품의 이미지 id와 이미지 파일 정보를 전달 받아서 이미지 정보를 수정한다.
     */
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {

        // 1. 상품 이미지 조회, 영속화 - 이미지 정보를 수정하기 위해 조회
        ItemImg itemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);

        // 2. 화면에서 받아온 파일이 존재할 경우 기존 파일 삭제
        if(!itemImgFile.isEmpty()) {
            // 2.1. 기존 파일 삭제, 여기서 기존이란? - 기존 이미지 파일을 삭제
            fileService.deleteFile(itemImgLocation + "/" + itemImg.getImgName());
        }
        // 3. 화면에서 받아온 파일이 존재할 경우 새로운 파일 업로드하기 위해서 필요한 변수 선언
        String oriImgName = itemImgFile.getOriginalFilename(); // 화면에서 받아온 파일명
        String imgName = "";
        String imgUrl = "";

        // 4. 파일 업로드(화면에서 받아온 파일이 존재할 경우)
        if(!StringUtils.isEmpty(oriImgName)){
            // 4.1. 파일 업로드
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 4.2. 이미지 URL, 이미지 URL은 상대경로로 저장, 이렇게 저장한 값이 DB에 저장됨
            imgUrl = "/image/item" + imgName;
        }

        // 5. 상품 이미지 정보 수정, 이렇게 수정하면 JPA가 변경감지하여 수정된 내용을 DB에 반영
        // updateItemImg() 메서드는 ItemImg 엔티티의 메서드로 영속화 되어 있는 ItemImg 엔티티의 정보를 수정하게 되고
        // JPA가 변경감지하여 수정된 내용을 DB에 반영.
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

            // 실제 파일 삭제
            if (!StringUtils.isEmpty(itemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + itemImg.getImgName());
            }

            // 데이터베이스에서 이미지 정보 삭제
            itemImgRepository.delete(itemImg);
        }
    }
}
