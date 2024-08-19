package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.restaurant.command.BusinessHoursCommand;
import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import com.astar.eattable.restaurant.command.RestaurantCreateCommand;
import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.restaurant.event.BusinessHoursUpdateEvent;
import com.astar.eattable.restaurant.event.RestaurantCreateEvent;
import com.astar.eattable.restaurant.event.RestaurantDeleteEvent;
import com.astar.eattable.restaurant.event.RestaurantUpdateEvent;
import com.astar.eattable.restaurant.exception.RestaurantAlreadyExistsException;
import com.astar.eattable.restaurant.exception.UnauthorizedRestaurantAccessException;
import com.astar.eattable.restaurant.model.BusinessHours;
import com.astar.eattable.restaurant.model.Restaurant;
import com.astar.eattable.restaurant.repository.BusinessHoursRepository;
import com.astar.eattable.restaurant.repository.RestaurantRepository;
import com.astar.eattable.restaurant.validator.RestaurantValidator;
import com.astar.eattable.user.model.Role;
import com.astar.eattable.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestaurantCommandServiceTest {

    @InjectMocks
    private RestaurantCommandService restaurantCommandService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private BusinessHoursRepository businessHoursRepository;

    @Mock
    private RestaurantValidator restaurantValidator;

    @Mock
    private ApplicationEventPublisher publisher;

    private User user;
    private User notOwnerUser;
    private Restaurant restaurant;
    private List<BusinessHours> businessHoursList;

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
        notOwnerUser = User.builder()
                .email("test2@test.com")
                .password("test1234")
                .nickname("테스트2")
                .phoneNumber("010-5678-1234")
                .role(Role.ROLE_USER)
                .build();
        notOwnerUser.setIdForTest(2L);
        businessHoursList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            businessHoursList.add(BusinessHours.builder()
                    .day(Day.values()[i])
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(21, 0))
                    .breakStartTime(LocalTime.of(12, 0))
                    .breakEndTime(LocalTime.of(13, 0))
                    .lastOrderTime(LocalTime.of(20, 0))
                    .restaurant(restaurant)
                    .build());
        }
    }

    @Test
    @DisplayName("유효한 입력으로 식당을 생성하면 식당 ID를 반환한다.")
    void createRestaurant_withValidInput_returnsRestaurantId() {
        // given
        List<BusinessHoursCommand> businessHoursCommands = List.of(
                new BusinessHoursCommand("MONDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("TUESDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("WEDNESDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("THURSDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("FRIDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("SATURDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("SUNDAY", "09:00", "21:00", "12:00", "13:00", "20:00")
        );
        RestaurantCreateCommand command = new RestaurantCreateCommand("테스트 식당", "맛있는 식당", "서울시 강남구", "02-1234-5678", "이미지 URL", "한식", businessHoursCommands, 37.123456, 127.123456);
        given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);
        given(businessHoursRepository.save(any())).willReturn(null);

        // when
        Long restaurantId = restaurantCommandService.createRestaurant(command, user);

        // then
        assertNotNull(restaurantId);
        assertThat(restaurantId).isEqualTo(1L);

        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        verify(businessHoursRepository, times(7)).save(any());
        verify(publisher, times(1)).publishEvent(any(RestaurantCreateEvent.class));
    }

    @Test
    @DisplayName("이미 존재하는 식당 이름과 주소로 식당을 생성하려고 하면 RestaurantAlreadyExistsException을 던진다.")
    void createRestaurant_WithAlreadyExistingRestaurant_ThrowsRestaurantAlreadyExistsException() {
        // given
        List<BusinessHoursCommand> businessHoursCommands = List.of(
                new BusinessHoursCommand("MONDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("TUESDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("WEDNESDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("THURSDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("FRIDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("SATURDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("SUNDAY", "09:00", "21:00", "12:00", "13:00", "20:00")
        );
        RestaurantCreateCommand command = new RestaurantCreateCommand("테스트 식당", "맛있는 식당", "서울시 강남구", "02-1234-5678", "이미지 URL", "한식", businessHoursCommands, 37.123456, 127.123456);
        given(restaurantRepository.existsByNameAndAddress(command.getName(), command.getAddress())).willReturn(true);

        // when & then
        assertThrows(RestaurantAlreadyExistsException.class, () -> restaurantCommandService.createRestaurant(command, user));

        verify(restaurantRepository, times(0)).save(any(Restaurant.class));
        verify(businessHoursRepository, times(0)).save(any());
        verify(publisher, times(0)).publishEvent(any(RestaurantCreateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 식당을 삭제하면 식당이 삭제된다.")
    void deleteRestaurant_WithValidInput_DeletesRestaurant() {
        // given
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());

        // when
        restaurantCommandService.deleteRestaurant(1L, user);

        // then
        verify(restaurantRepository, times(1)).delete(restaurant);
        verify(publisher, times(1)).publishEvent(any(RestaurantDeleteEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 식당을 삭제하려고 하면 UnauthorizedRestaurantAccessException을 던진다.")
    void deleteRestaurant_WithNotOwnerUser_ThrowsUnauthorizedRestaurantAccessException() {
        // given
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.deleteRestaurant(1L, notOwnerUser));

        verify(restaurantRepository, times(0)).delete(restaurant);
        verify(publisher, times(0)).publishEvent(any(RestaurantDeleteEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 식당을 수정하면 식당이 수정된다.")
    void updateRestaurant_withValidInput_updatesRestaurant() {
        // given
        RestaurantUpdateCommand command = new RestaurantUpdateCommand("수정된 식당", "더 맛있는 식당", "서울시 서초구", "02-5678-1234", "수정된 이미지 URL", "양식", 37.654321, 127.654321);
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());

        // when
        restaurantCommandService.updateRestaurant(1L, command, user);

        // then
        assertThat(restaurant.getName()).isEqualTo("수정된 식당");
        assertThat(restaurant.getDescription()).isEqualTo("더 맛있는 식당");
        assertThat(restaurant.getAddress()).isEqualTo("서울시 서초구");
        assertThat(restaurant.getPhone()).isEqualTo("02-5678-1234");
        assertThat(restaurant.getImageUrl()).isEqualTo("수정된 이미지 URL");
        assertThat(restaurant.getCategoryName()).isEqualTo("양식");
        assertThat(restaurant.getLatitude()).isEqualTo(37.654321);
        assertThat(restaurant.getLongitude()).isEqualTo(127.654321);

        verify(publisher, times(1)).publishEvent(any(RestaurantUpdateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 식당을 수정하려고 하면 UnauthorizedRestaurantAccessException을 던진다.")
    void updateRestaurant_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        RestaurantUpdateCommand command = new RestaurantUpdateCommand("수정된 식당", "더 맛있는 식당", "서울시 서초구", "02-5678-1234", "수정된 이미지 URL", "양식", 37.654321, 127.654321);
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.updateRestaurant(1L, command, notOwnerUser));

        verify(publisher, times(0)).publishEvent(any(RestaurantUpdateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 영업시간을 수정하면 영업시간이 수정된다.")
    void updateBusinessHours_WithValidInput_UpdatesBusinessHours() {
        // given
        List<BusinessHoursCommand> businessHoursCommands = List.of(
                new BusinessHoursCommand("MONDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("TUESDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("WEDNESDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("THURSDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("FRIDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("SATURDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("SUNDAY", "10:00", "22:00", "13:00", "14:00", "21:00")
        );
        BusinessHoursUpdateCommand command = new BusinessHoursUpdateCommand(businessHoursCommands);
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.MONDAY)).willReturn(Optional.of(businessHoursList.get(0)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.TUESDAY)).willReturn(Optional.of(businessHoursList.get(1)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.WEDNESDAY)).willReturn(Optional.of(businessHoursList.get(2)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.THURSDAY)).willReturn(Optional.of(businessHoursList.get(3)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.FRIDAY)).willReturn(Optional.of(businessHoursList.get(4)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.SATURDAY)).willReturn(Optional.of(businessHoursList.get(5)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.SUNDAY)).willReturn(Optional.of(businessHoursList.get(6)));

        // when
        restaurantCommandService.updateBusinessHours(1L, command, user);

        // then
        assertThat(businessHoursList.get(0).getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(businessHoursList.get(0).getEndTime()).isEqualTo(LocalTime.of(22, 0));
        assertThat(businessHoursList.get(0).getBreakStartTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(businessHoursList.get(0).getBreakEndTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(businessHoursList.get(0).getLastOrderTime()).isEqualTo(LocalTime.of(21, 0));

        verify(publisher, times(1)).publishEvent(any(BusinessHoursUpdateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 영업시간을 수정하려고 하면 UnauthorizedRestaurantAccessException을 던진다.")
    void updateBusinessHours_WithNotOwnerUser_ThrowsUnauthorizedRestaurantAccessException() {
        // given
        List<BusinessHoursCommand> businessHoursCommands = List.of(
                new BusinessHoursCommand("MONDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("TUESDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("WEDNESDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("THURSDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("FRIDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("SATURDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("SUNDAY", "10:00", "22:00", "13:00", "14:00", "21:00")
        );
        BusinessHoursUpdateCommand command = new BusinessHoursUpdateCommand(businessHoursCommands);
        given(restaurantRepository.findById(1L)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.updateBusinessHours(1L, command, notOwnerUser));

        verify(publisher, times(0)).publishEvent(any(BusinessHoursUpdateEvent.class));
    }

    @Test
    void createMenuSection() {
    }

    @Test
    void updateMenuSection() {
    }

    @Test
    void deleteMenuSection() {
    }

    @Test
    void createMenu() {
    }

    @Test
    void deleteMenu() {
    }

    @Test
    void updateMenu() {
    }

    @Test
    void createClosedPeriod() {
    }

    @Test
    void deleteClosedPeriod() {
    }

    @Test
    void getAllRestaurantIds() {
    }
}