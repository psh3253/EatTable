package com.astar.eattable.user.service;

import com.astar.eattable.user.dto.LoginDTO;
import com.astar.eattable.user.dto.SignUpDTO;
import com.astar.eattable.user.exception.PasswordMismatchException;
import com.astar.eattable.user.exception.UserAlreadyExistsException;
import com.astar.eattable.user.exception.UserNotFoundException;
import com.astar.eattable.user.model.Role;
import com.astar.eattable.user.model.User;
import com.astar.eattable.user.repository.UserRepository;
import com.astar.eattable.user.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserValidator userValidator;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .password("test1234")
                .nickname("테스트")
                .phoneNumber("010-1234-5678")
                .role(Role.ROLE_USER)
                .build();
        user.setIdForTest(1L);
    }

    @Test
    @DisplayName("유효한 입력으로 회원가입 시, 사용자 ID를 반환한다.")
    void signUp_withValidInput_thenReturnsUserId() {
        // given
        SignUpDTO signUpDTO = new SignUpDTO("test@test.com", "test1234", "테스트", "010-1234-5678");
        given(userRepository.existsByEmail(signUpDTO.getEmail())).willReturn(false);
        given(passwordEncoder.encode(signUpDTO.getPassword())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        Long userId = userService.signUp(signUpDTO);

        // then
        assertNotNull(userId);
        assertThat(userId).isEqualTo(1L);

        verify(userRepository).existsByEmail(signUpDTO.getEmail());
        verify(passwordEncoder).encode(signUpDTO.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 사용자로 회원가입 시, UserAlreadyExistsException을 던진다.")
    void signUp_withExistingUser_thenThrowsUserAlreadyExistsException() {
        // given
        SignUpDTO signUpDTO = new SignUpDTO("test@test.com", "test1234", "테스트", "010-1234-5678");
        given(userRepository.existsByEmail(signUpDTO.getEmail())).willReturn(true);

        // when & then
        assertThrows(UserAlreadyExistsException.class, () -> userService.signUp(signUpDTO));

        verify(userRepository, times(1)).existsByEmail(signUpDTO.getEmail());
        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    @DisplayName("유효한 입력으로 로그인 시, 사용자를 반환한다.")
    void login_withValidInput_thenReturnsUser() {
        // given
        LoginDTO loginDTO = new LoginDTO("test@test.com", "test1234");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        willDoNothing().given(userValidator).validatePassword("test1234", user);

        // when
        User loginUser = userService.login(loginDTO);

        // then
        assertNotNull(loginUser);
        assertThat(loginUser.getId()).isEqualTo(1L);
        assertThat(loginUser.getEmail()).isEqualTo("test@test.com");
        assertThat(loginUser.getPassword()).isEqualTo("test1234");
        assertThat(loginUser.getNickname()).isEqualTo("테스트");
        assertThat(loginUser.getPhoneNumber()).isEqualTo("010-1234-5678");
        assertThat(loginUser.getRole()).isEqualTo(Role.ROLE_USER);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시, UserNotFoundException을 던진다.")
    void login_withNonExistingUser_thenThrowsUserNotFoundException() {
        // given
        LoginDTO loginDTO = new LoginDTO("test2@test.com", "test1234");
        given(userRepository.findByEmail("test2@test.com")).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.login(loginDTO));

        verify(userRepository, times(1)).findByEmail("test2@test.com");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않는 사용자로 로그인 시, PasswordMismatchException을 던진다.")
    void login_withMismatchedPassword_thenThrowsPasswordMismatchException() {
        // given
        LoginDTO loginDTO = new LoginDTO("test@test.com", "test12345");
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));
        willThrow(new PasswordMismatchException(user.getEmail())).given(userValidator).validatePassword("test12345", user);

        // when & then
        assertThrows(PasswordMismatchException.class, () -> userService.login(loginDTO));

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userValidator, times(1)).validatePassword("test12345", user);
    }
}