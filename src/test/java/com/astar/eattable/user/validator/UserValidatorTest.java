package com.astar.eattable.user.validator;

import com.astar.eattable.user.exception.PasswordMismatchException;
import com.astar.eattable.user.model.Role;
import com.astar.eattable.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidator userValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("테스트")
                .phoneNumber("010-1234-5678")
                .role(Role.ROLE_USER)
                .build();
        user.setIdForTest(1L);
    }

    @Test
    @DisplayName("비밀번호가 일치하면 예외가 발생하지 않는다.")
    void validatePassword_withValidPassword_notThrowException() {
        // given
        String password = "test1234";
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(true);

        // when & then
        assertDoesNotThrow(() -> userValidator.validatePassword(password, user));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 PasswordMismatchException이 발생한다.")
    void validatePassword_withInvalidPassword_throwPasswordMismatchException() {
        // given
        String password = "test1234";
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(false);

        // when & then
        assertThrows(PasswordMismatchException.class, () -> userValidator.validatePassword(password, user));
    }
}