package com.javalab.shop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 파일 서비스
 * - 파일 업로드, 파일 삭제 등의 기능을 제공하는 서비스 클래스
 */
@Service
@Slf4j
public class FileService {

    /**
     * 파일 업로드
     * @param uploadPath - 파일이 업로드될 디렉토리 경로
     * @param originalFileName - 원본 파일 이름
     * @param fileData - 업로드할 파일의 데이터
     * @return 업로드된 파일의 이름
     * @throws IOException - 파일 업로드 중 발생할 수 있는 예외
     */
    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws IOException {

        // 파일 이름에 UUID를 추가하여 중복되지 않는 이름 생성
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String saveFileName = uuid.toString() + extension;

        // 파일이 저장될 전체 경로
        String fileUploadFullUrl = uploadPath + "/" + saveFileName;

        // 디렉토리 존재 여부 확인 후 없으면 디렉토리 생성
        File targetDir = new File(uploadPath);
        if (!targetDir.exists()) {
            targetDir.mkdirs(); // 경로가 없으면 폴더를 생성
        }

        // 파일 업로드
        try (FileOutputStream fos = new FileOutputStream(fileUploadFullUrl)) {
            fos.write(fileData);  // 파일을 지정된 경로에 저장
            log.info("파일 업로드 성공: {}", fileUploadFullUrl);
        } catch (IOException e) {
            log.error("파일 업로드 중 오류가 발생했습니다: {}", e.getMessage());
            throw e;  // 예외를 던져 상위 메서드에서 처리하도록 함
        }

        return saveFileName;  // 저장된 파일 이름 반환
    }

    /**
     * 파일 삭제
     * - 파일을 삭제하는 메서드
     * @param filePath - 삭제할 파일의 경로
     * @throws IOException - 파일 삭제 중 발생할 수 있는 예외
     */
    public void deleteFile(String filePath) throws IOException {

        File deleteFile = new File(filePath);

        // 파일이 존재하면 삭제
        if (deleteFile.exists()) {
            if (deleteFile.delete()) {
                log.info("파일 삭제 성공: {}", filePath);
            } else {
                log.error("파일 삭제 실패: {}", filePath);
            }
        } else {
            log.info("파일이 존재하지 않음: {}", filePath);
        }
    }
}
