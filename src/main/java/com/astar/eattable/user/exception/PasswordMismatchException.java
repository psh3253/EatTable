package com.astar.eattable.user.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String email) {
        super("비밀번호가 일치하지 않습니다. Email: " + email);
    }
}
