package com.astar.eattable.user.validator;

import com.astar.eattable.user.exception.PasswordMismatchException;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserValidator {
    private final PasswordEncoder passwordEncoder;

    public void validatePassword(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException(user.getEmail());
        }
    }
}
