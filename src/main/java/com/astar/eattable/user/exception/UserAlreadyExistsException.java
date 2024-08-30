package com.astar.eattable.user.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("사용자 이메일이 이미 존재합니다. 이메일: " + email);
    }
}
