package com.astar.eattable.restaurant.validator;

import com.astar.eattable.restaurant.exception.ClosedPeriodOverlapException;
import com.astar.eattable.restaurant.exception.ClosedPeriodPastException;
import com.astar.eattable.restaurant.exception.UnauthorizedRestaurantAccessException;
import com.astar.eattable.restaurant.model.ClosedPeriod;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.user.model.Role;
import com.astar.eattable.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RestaurantValidatorTest {

    @InjectMocks
    private RestaurantValidator restaurantValidator;
    private User user;
    private Restaurant restaurant;
    private ClosedPeriod closedPeriod;

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
        restaurant = Restaurant.builder()
                .name("테스트 식당")
                .description("맛있는 식당")
                .address("서울시 강남구")
                .phone("02-1234-5678")
                .imageUrl("이미지 URL")
                .categoryName("한식")
                .latitude(37.123456)
                .longitude(127.123456)
                .user(user)
                .build();
        restaurant.setIdForTest(1L);
        closedPeriod = ClosedPeriod.builder()
                .startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 9, 7))
                .reason("휴가")
                .restaurant(restaurant)
                .build();
        closedPeriod.setIdForTest(1L);
    }

    @Test
    @DisplayName("식당 소유자가 맞으면 예외가 발생하지 않는다.")
    void validateRestaurantOwner_withValidInput_notThrowException() {
        // given
        Long userId = 1L;

        // when & then
        assertDoesNotThrow(() -> restaurantValidator.validateRestaurantOwner(restaurant, userId));
    }

    @Test
    @DisplayName("식당 소유자가 아니면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void validateRestaurantOwner_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long userId = 2L;

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantValidator.validateRestaurantOwner(restaurant, userId));
    }

    @Test
    @DisplayName("휴뮤 기간이 겹치지 않으면 예외가 발생하지 않는다.")
    void validateNoOverlapClosedPeriod_withValidInput_notThrowException() {
        // given
        Long restaurantId = 1L;
        String startDate = "2024-09-08";
        String endDate = "2024-09-14";

        // when & then
        assertDoesNotThrow(() -> restaurantValidator.validateNoOverlapClosedPeriod(List.of(closedPeriod), startDate, endDate, restaurantId));
    }

    @Test
    @DisplayName("휴뮤 기간이 겹치면 ClosedPeriodOverlapException 예외가 발생한다.")
    void validateNoOverlapClosedPeriod_withOverlapClosedPeriod_throwsClosedPeriodOverlapException() {
        // given
        Long restaurantId = 1L;
        String startDate = "2024-09-05";
        String endDate = "2024-09-10";

        // when & then
        assertThrows(ClosedPeriodOverlapException.class, () -> restaurantValidator.validateNoOverlapClosedPeriod(List.of(closedPeriod), startDate, endDate, restaurantId));
    }


    @Test
    @DisplayName("휴뮤 기간이 오늘 이전이 아니면 예외가 발생하지 않는다.")
    void validateClosePeriodNotBeforeToday_withValidInput_notThrowException() {
        // given
        String startDate = "2024-09-01";

        // when & then
        assertDoesNotThrow(() -> restaurantValidator.validateClosePeriodNotBeforeToday(startDate));
    }

    @Test
    @DisplayName("휴뮤 기간이 오늘 이전이면 ClosedPeriodPastException 예외가 발생한다.")
    void validateClosePeriodNotBeforeToday_withPastDate_throwsClosedPeriodPastException() {
        // given
        String startDate = "2023-09-01";

        // when & then
        assertThrows(ClosedPeriodPastException.class, () -> restaurantValidator.validateClosePeriodNotBeforeToday(startDate));
    }
}