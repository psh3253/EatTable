package com.astar.eattable.user.service;

import com.astar.eattable.user.dto.LoginDTO;
import com.astar.eattable.user.dto.SignUpDTO;
import com.astar.eattable.user.exception.PasswordMismatchException;
import com.astar.eattable.user.exception.UserAlreadyExistsException;
import com.astar.eattable.user.exception.UserNotFoundException;
import com.astar.eattable.user.model.User;
import com.astar.eattable.user.repository.UserRepository;
import com.astar.eattable.user.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    @Transactional
    public Long signUp(SignUpDTO signUpDTO) {
        validateExistingUser(signUpDTO.getEmail());
        String encodedPassword = passwordEncoder.encode(signUpDTO.getPassword());
        User user = userRepository.save(signUpDTO.toEntity(encodedPassword));
        return user.getId();
    }

    @Transactional(readOnly = true)
    public User login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new UserNotFoundException(loginDTO.getEmail()));
        userValidator.validatePassword(loginDTO.getPassword(), user);
        return user;
    }

    private void validateExistingUser(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
    }
}
