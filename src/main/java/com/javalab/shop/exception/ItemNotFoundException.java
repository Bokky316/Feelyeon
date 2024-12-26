package com.javalab.shop.exception;

/**
 * 사용자 정의 예외
 */
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
