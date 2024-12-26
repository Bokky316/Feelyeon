package com.javalab.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 * 파일 서비스
 * - 파일 업로드, 파일 삭제 등의 기능을 제공하는 서비스 클래스
 */
@Service
@Slf4j
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception{

        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        String saveFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + saveFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return saveFileName;
    }

    /**
     * 파일 삭제
     * - 파일을 삭제하는 메서드
     * - 여기서 파일은 파일 시스템인가? DB인가? 파일 시스템
     * @param filePath
     * @throws Exception
     */
    public void deleteFile(String filePath) throws  Exception{
        File deleteFile = new File(filePath);

        if(deleteFile.exists()){
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        }else{
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
