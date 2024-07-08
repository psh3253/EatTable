package com.astar.eattable.user.service;

import com.astar.eattable.user.dto.LoginDTO;
import com.astar.eattable.user.dto.SignUpDTO;
import com.astar.eattable.user.exception.PasswordMismatchException;
import com.astar.eattable.user.exception.UserNotFoundException;
import com.astar.eattable.user.model.User;
import com.astar.eattable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(SignUpDTO signUpDTO) {
        String encodedPassword = passwordEncoder.encode(signUpDTO.getPassword());
        User user = userRepository.save(signUpDTO.toEntity(encodedPassword));
        return user.getId();
    }

    @Transactional(readOnly = true)
    public User login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new UserNotFoundException(loginDTO.getEmail()));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException(loginDTO.getEmail());
        }
        return user;
    }
}
