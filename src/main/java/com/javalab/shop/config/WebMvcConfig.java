package com.javalab.shop.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    String uploadPath;  // file:///c:/shop/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/images/**")   // /images/** 요청이 오면 uploadPath로 매핑
                .addResourceLocations(uploadPath);  // 로컬 컴퓨터에 저장된 파일을 읽어올 root 경로를 설정합니다.

        registry.addResourceHandler("/static-images/**")
                .addResourceLocations("classpath:/static/images/");  // 정적 리소스
    }
}
