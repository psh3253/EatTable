package com.astar.eattable.user.controller;

import com.astar.eattable.security.dto.TokenDTO;
import com.astar.eattable.security.service.TokenService;
import com.astar.eattable.user.dto.LoginDTO;
import com.astar.eattable.user.dto.SignUpDTO;
import com.astar.eattable.user.model.User;
import com.astar.eattable.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<Long> signUp(@Valid @RequestBody SignUpDTO signUpDTO) {
        Long userId = userService.signUp(signUpDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO);
        TokenDTO token = tokenService.generateToken(user.getId(), user.getRole());
        return ResponseEntity.ok(token);
    }
}
