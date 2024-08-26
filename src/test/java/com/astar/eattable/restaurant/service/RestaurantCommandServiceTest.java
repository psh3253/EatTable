package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.restaurant.RestaurantTestUtils;
import com.astar.eattable.restaurant.command.*;
import com.astar.eattable.restaurant.event.*;
import com.astar.eattable.restaurant.exception.*;
import com.astar.eattable.restaurant.model.*;
import com.astar.eattable.restaurant.repository.*;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private MenuSectionRepository menuSectionRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ClosedPeriodRepository closedPeriodRepository;

    @Mock
    private RestaurantValidator restaurantValidator;

    @Mock
    private ApplicationEventPublisher publisher;

    private User user;
    private User notOwnerUser;
    private Restaurant restaurant;
    private List<BusinessHours> businessHoursList;
    private MenuSection menuSection;
    private Menu menu;
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
        notOwnerUser = User.builder()
                .email("test2@test.com")
                .password("test1234")
                .nickname("테스트2")
                .phoneNumber("010-5678-1234")
                .role(Role.ROLE_USER)
                .build();
        notOwnerUser.setIdForTest(2L);
        businessHoursList = new ArrayList<>();
        for (Day day : Day.values()) {
            businessHoursList.add(BusinessHours.builder()
                    .day(day)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(21, 0))
                    .breakStartTime(LocalTime.of(12, 0))
                    .breakEndTime(LocalTime.of(13, 0))
                    .lastOrderTime(LocalTime.of(20, 0))
                    .restaurant(restaurant)
                    .build());
        }
        menuSection = MenuSection.builder()
                .name("메인 메뉴")
                .restaurant(restaurant)
                .build();
        menuSection.setIdForTest(1L);
        menu = Menu.builder()
                .name("치킨")
                .price(15000)
                .description("맛있는 치킨")
                .menuSection(menuSection)
                .restaurant(restaurant)
                .build();
        menu.setIdForTest(1L);
        closedPeriod = ClosedPeriod.builder()
                .startDate(LocalDate.of(2024, 9, 1))
                .endDate(LocalDate.of(2024, 9, 7))
                .reason("휴가")
                .restaurant(restaurant)
                .build();
        closedPeriod.setIdForTest(1L);
    }

    @Test
    @DisplayName("유효한 입력으로 식당을 생성하면 식당 ID를 반환한다.")
    void createRestaurant_withValidInput_returnsRestaurantId() {
        // given
        RestaurantCreateCommand command = RestaurantTestUtils.getRestaurantCreateCommand();
        given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);
        given(businessHoursRepository.save(any(BusinessHours.class))).willReturn(null);

        // when
        Long restaurantId = restaurantCommandService.createRestaurant(command, user);

        // then
        assertNotNull(restaurantId);
        assertThat(restaurantId).isEqualTo(1L);

        verify(restaurantRepository, times(1)).save(any(Restaurant.class));
        verify(businessHoursRepository, times(7)).save(any(BusinessHours.class));
        verify(publisher, times(1)).publishEvent(any(RestaurantCreateEvent.class));
    }

    @Test
    @DisplayName("이미 존재하는 식당 이름과 주소로 식당을 생성하려고 하면 RestaurantAlreadyExistsException 예외가 발생한다.")
    void createRestaurant_withAlreadyExistingRestaurant_throwsRestaurantAlreadyExistsException() {
        // given
        RestaurantCreateCommand command = RestaurantTestUtils.getRestaurantCreateCommand();
        given(restaurantRepository.existsByNameAndAddress(command.getName(), command.getAddress())).willReturn(true);

        // when & then
        assertThrows(RestaurantAlreadyExistsException.class, () -> restaurantCommandService.createRestaurant(command, user));

        verify(restaurantRepository, times(0)).save(any(Restaurant.class));
        verify(businessHoursRepository, times(0)).save(any(BusinessHours.class));
        verify(publisher, times(0)).publishEvent(any(RestaurantCreateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 식당을 삭제하면 식당이 삭제된다.")
    void deleteRestaurant_withValidInput_deletesRestaurant() {
        // given
        Long restaurantId = 1L;
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());

        // when
        restaurantCommandService.deleteRestaurant(restaurantId, user);

        // then
        verify(restaurantRepository, times(1)).delete(restaurant);
        verify(publisher, times(1)).publishEvent(any(RestaurantDeleteEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 식당을 삭제하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void deleteRestaurant_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long restaurantId = 1L;
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.deleteRestaurant(restaurantId, notOwnerUser));

        verify(restaurantRepository, times(0)).delete(restaurant);
        verify(publisher, times(0)).publishEvent(any(RestaurantDeleteEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 식당을 삭제하려고 하면 RestaurantNotFoundException 예외가 발생한다.")
    void deleteRestaurant_withNotExistingRestaurant_throwsRestaurantNotFoundException() {
        // given
        Long restaurantId = 1L;
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        // when & then
        assertThrows(RestaurantNotFoundException.class, () -> restaurantCommandService.deleteRestaurant(restaurantId, user));

        verify(restaurantRepository, times(0)).delete(restaurant);
        verify(publisher, times(0)).publishEvent(any(RestaurantDeleteEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 식당을 수정하면 식당이 수정된다.")
    void updateRestaurant_withValidInput_updatesRestaurant() {
        // given
        Long restaurantId = 1L;
        RestaurantUpdateCommand command = new RestaurantUpdateCommand("수정된 식당", "더 맛있는 식당", "서울시 서초구", "02-5678-1234", "수정된 이미지 URL", "양식", 37.654321, 127.654321);
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());

        // when
        restaurantCommandService.updateRestaurant(restaurantId, command, user);

        // then
        assertThat(restaurant.getId()).isEqualTo(restaurantId);
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
    @DisplayName("식당 소유자가 아닌 사용자가 식당을 수정하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void updateRestaurant_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long restaurantId = 1L;
        RestaurantUpdateCommand command = new RestaurantUpdateCommand("수정된 식당", "더 맛있는 식당", "서울시 서초구", "02-5678-1234", "수정된 이미지 URL", "양식", 37.654321, 127.654321);
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.updateRestaurant(restaurantId, command, notOwnerUser));

        verify(publisher, times(0)).publishEvent(any(RestaurantUpdateEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 식당을 수정하려고 하면 RestaurantNotFoundException 예외가 발생한다.")
    void updateRestaurant_withNotExistingRestaurant_throwsRestaurantNotFoundException() {
        // given
        Long restaurantId = 1L;
        RestaurantUpdateCommand command = new RestaurantUpdateCommand("수정된 식당", "더 맛있는 식당", "서울시 서초구", "02-5678-1234", "수정된 이미지 URL", "양식", 37.654321, 127.654321);
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        // when & then
        assertThrows(RestaurantNotFoundException.class, () -> restaurantCommandService.updateRestaurant(restaurantId, command, user));

        verify(publisher, times(0)).publishEvent(any(RestaurantUpdateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 영업시간을 수정하면 영업시간이 수정된다.")
    void updateBusinessHours_withValidInput_updatesBusinessHours() {
        // given
        Long restaurantId = 1L;
        BusinessHoursUpdateCommand command = RestaurantTestUtils.getBusinessHoursUpdateCommand();
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.MONDAY)).willReturn(Optional.of(businessHoursList.get(0)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.TUESDAY)).willReturn(Optional.of(businessHoursList.get(1)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.WEDNESDAY)).willReturn(Optional.of(businessHoursList.get(2)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.THURSDAY)).willReturn(Optional.of(businessHoursList.get(3)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.FRIDAY)).willReturn(Optional.of(businessHoursList.get(4)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.SATURDAY)).willReturn(Optional.of(businessHoursList.get(5)));
        given(businessHoursRepository.findByRestaurantIdAndDay(1L, Day.SUNDAY)).willReturn(Optional.of(businessHoursList.get(6)));

        // when
        restaurantCommandService.updateBusinessHours(restaurantId, command, user);

        // then
        assertThat(businessHoursList.get(0).getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(businessHoursList.get(0).getEndTime()).isEqualTo(LocalTime.of(22, 0));
        assertThat(businessHoursList.get(0).getBreakStartTime()).isEqualTo(LocalTime.of(13, 0));
        assertThat(businessHoursList.get(0).getBreakEndTime()).isEqualTo(LocalTime.of(14, 0));
        assertThat(businessHoursList.get(0).getLastOrderTime()).isEqualTo(LocalTime.of(21, 0));

        verify(publisher, times(1)).publishEvent(any(BusinessHoursUpdateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 영업시간을 수정하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void updateBusinessHours_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long restaurantId = 1L;
        BusinessHoursUpdateCommand command = RestaurantTestUtils.getBusinessHoursUpdateCommand();
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.updateBusinessHours(restaurantId, command, notOwnerUser));

        verify(publisher, times(0)).publishEvent(any(BusinessHoursUpdateEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 식당의 영업시간을 수정하려고 하면 RestaurantNotFoundException 예외가 발생한다.")
    void updateBusinessHours_withNotExistingRestaurant_throwsRestaurantNotFoundException() {
        // given
        Long restaurantId = 1L;
        BusinessHoursUpdateCommand command = RestaurantTestUtils.getBusinessHoursUpdateCommand();
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        // when & then
        assertThrows(RestaurantNotFoundException.class, () -> restaurantCommandService.updateBusinessHours(restaurantId, command, user));

        verify(publisher, times(0)).publishEvent(any(BusinessHoursUpdateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 메뉴 섹션을 생성하면 메뉴 섹션이 생성된다.")
    void createMenuSection_withValidInput_createsMenuSection() {
        // given
        Long restaurantId = 1L;
        MenuSectionCreateCommand command = new MenuSectionCreateCommand("메인 메뉴");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuSectionRepository.save(any(MenuSection.class))).willReturn(menuSection);
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());

        // when
        restaurantCommandService.createMenuSection(restaurantId, command, user);

        // then
        verify(menuSectionRepository, times(1)).save(any(MenuSection.class));
        verify(publisher, times(1)).publishEvent(any(MenuSectionCreateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 메뉴 섹션을 생성하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void createMenuSection_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long restaurantId = 1L;
        MenuSectionCreateCommand command = new MenuSectionCreateCommand("메인 메뉴");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.createMenuSection(restaurantId, command, notOwnerUser));

        verify(menuSectionRepository, times(0)).save(any(MenuSection.class));
        verify(publisher, times(0)).publishEvent(any(MenuSectionCreateEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 식당에 메뉴 섹션을 생성하려고 하면 RestaurantNotFoundException 예외가 발생한다.")
    void createMenuSection_withNotExistingRestaurant_throwsRestaurantNotFoundException() {
        // given
        Long restaurantId = 1L;
        MenuSectionCreateCommand command = new MenuSectionCreateCommand("메인 메뉴");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        // when & then
        assertThrows(RestaurantNotFoundException.class, () -> restaurantCommandService.createMenuSection(restaurantId, command, user));

        verify(menuSectionRepository, times(0)).save(any(MenuSection.class));
        verify(publisher, times(0)).publishEvent(any(MenuSectionCreateEvent.class));
    }

    @Test
    @DisplayName("이미 존재하는 메뉴 섹션 이름으로 메뉴 섹션을 생성하려고 하면 MenuSectionAlreadyExistsException 예외가 발생한다.")
    void createMenuSection_withAlreadyExistingMenuSection_throwsMenuSectionAlreadyExistsException() {
        // given
        Long  restaurantId = 1L;
        MenuSectionCreateCommand command = new MenuSectionCreateCommand("메인 메뉴");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(menuSectionRepository.existsByRestaurantIdAndName(restaurantId, command.getName())).willReturn(true);

        // when & then
        assertThrows(MenuSectionAlreadyExistsException.class, () -> restaurantCommandService.createMenuSection(restaurantId, command, user));

        verify(menuSectionRepository, times(0)).save(any(MenuSection.class));
        verify(publisher, times(0)).publishEvent(any(MenuSectionCreateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 메뉴 섹션을 수정하면 메뉴 섹션이 수정된다.")
    void updateMenuSection_withValidInput_updatesMenuSection() {
        // given
        Long menuSectionId = 1L;
        MenuSectionUpdateCommand command = new MenuSectionUpdateCommand("사이드 메뉴");
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(menuSection.getRestaurant(), user.getId());

        // when
        restaurantCommandService.updateMenuSection(menuSectionId, command, user);

        // then
        assertThat(menuSection.getName()).isEqualTo("사이드 메뉴");

        verify(publisher, times(1)).publishEvent(any(MenuSectionUpdateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 메뉴 섹션을 수정하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void updateMenuSection_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long menuSectionId = 1L;
        MenuSectionUpdateCommand command = new MenuSectionUpdateCommand("사이드 메뉴");
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        willThrow(new UnauthorizedRestaurantAccessException(menuSection.getRestaurant().getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(menuSection.getRestaurant(), notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.updateMenuSection(menuSectionId, command, notOwnerUser));

        verify(publisher, times(0)).publishEvent(any(MenuSectionUpdateEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 섹션을 수정하려고 하면 MenuSectionNotFoundException 예외가 발생한다.")
    void updateMenuSection_withNotExistingMenuSection_throwsMenuSectionNotFoundException() {
        // given
        Long menuSectionId = 1L;
        MenuSectionUpdateCommand command = new MenuSectionUpdateCommand("사이드 메뉴");
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MenuSectionNotFoundException.class, () -> restaurantCommandService.updateMenuSection(menuSectionId, command, user));

        verify(publisher, times(0)).publishEvent(any(MenuSectionUpdateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 메뉴 섹션을 삭제하면 메뉴 섹션이 삭제된다.")
    void deleteMenuSection_withValidInput_deletesMenuSection() {
        // given
        Long menuSectionId = 1L;
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(menuSection.getRestaurant(), user.getId());

        // when
        restaurantCommandService.deleteMenuSection(menuSectionId, user);

        // then
        verify(menuSectionRepository, times(1)).delete(menuSection);
        verify(publisher, times(1)).publishEvent(any(MenuSectionDeleteEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 메뉴 섹션을 삭제하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void deleteMenuSection_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long menuSectionId = 1L;
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        willThrow(new UnauthorizedRestaurantAccessException(menuSection.getRestaurant().getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(menuSection.getRestaurant(), notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.deleteMenuSection(menuSectionId, notOwnerUser));

        verify(menuSectionRepository, times(0)).delete(menuSection);
        verify(publisher, times(0)).publishEvent(any(MenuSectionDeleteEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 섹션을 삭제하려고 하면 MenuSectionNotFoundException 예외가 발생한다.")
    void deleteMenuSection_withNotExistingMenuSection_throwsMenuSectionNotFoundException() {
        // given
        Long menuSectionId = 1L;
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MenuSectionNotFoundException.class, () -> restaurantCommandService.deleteMenuSection(menuSectionId, user));

        verify(menuSectionRepository, times(0)).delete(menuSection);
        verify(publisher, times(0)).publishEvent(any(MenuSectionDeleteEvent.class));
    }

    @Test
    @DisplayName("메뉴 섹션에 메뉴가 없을 때 메뉴 섹션을 삭제하면 메뉴 섹션이 삭제된다.")
    void deleteMenuSection_withNotEmptyMenuSection_throwsMenuSectionNotEmptyException() {
        // given
        Long menuSectionId = 1L;
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        given(menuRepository.existsByMenuSectionId(menuSectionId)).willReturn(true);

        // when & then
        assertThrows(MenuSectionNotEmptyException.class, () -> restaurantCommandService.deleteMenuSection(menuSectionId, user));

        verify(menuSectionRepository, times(0)).delete(menuSection);
        verify(publisher, times(0)).publishEvent(any(MenuSectionDeleteEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 메뉴를 생성하면 메뉴가 생성된다.")
    void createMenu_withValidInput_createsMenu() {
        // given
        Long menuSectionId = 1L;
        MenuCreateCommand command = new MenuCreateCommand("치킨", "맛있는 치킨", 15000, "이미지 URL");
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        given(menuRepository.save(any(Menu.class))).willReturn(menu);
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(menuSection.getRestaurant(), user.getId());

        // when
        restaurantCommandService.createMenu(menuSectionId, command, user);

        // then
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(publisher, times(1)).publishEvent(any(MenuCreateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 메뉴를 생성하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void createMenu_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long menuSectionId = 1L;
        MenuCreateCommand command = new MenuCreateCommand("치킨", "맛있는 치킨", 15000, "이미지 URL");
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        willThrow(new UnauthorizedRestaurantAccessException(menuSection.getRestaurant().getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(menuSection.getRestaurant(), notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.createMenu(menuSectionId, command, notOwnerUser));

        verify(menuRepository, times(0)).save(any(Menu.class));
        verify(publisher, times(0)).publishEvent(any(MenuCreateEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 메뉴 섹션에 메뉴를 생성하려고 하면 MenuSectionNotFoundException 예외가 발생한다.")
    void createMenu_withNotExistingMenuSection_throwsMenuSectionNotFoundException() {
        // given
        Long menuSectionId = 1L;
        MenuCreateCommand command = new MenuCreateCommand("치킨", "맛있는 치킨", 15000, "이미지 URL");
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MenuSectionNotFoundException.class, () -> restaurantCommandService.createMenu(menuSectionId, command, user));

        verify(menuRepository, times(0)).save(any(Menu.class));
        verify(publisher, times(0)).publishEvent(any(MenuCreateEvent.class));
    }

    @Test
    @DisplayName("이미 존재하는 메뉴 이름으로 메뉴를 생성하려고 하면 MenuAlreadyExistsException 예외가 발생한다.")
    void createMenu_withAlreadyExistingMenu_throwsMenuAlreadyExistsException() {
        // given
        Long menuSectionId = 1L;
        MenuCreateCommand command = new MenuCreateCommand("치킨", "맛있는 치킨", 15000, "이미지 URL");
        given(menuSectionRepository.findById(menuSectionId)).willReturn(Optional.of(menuSection));
        given(menuRepository.existsByMenuSectionIdAndName(menuSectionId, command.getName())).willReturn(true);

        // when & then
        assertThrows(MenuAlreadyExistsException.class, () -> restaurantCommandService.createMenu(menuSectionId, command, user));

        verify(menuRepository, times(0)).save(any(Menu.class));
        verify(publisher, times(0)).publishEvent(any(MenuCreateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 메뉴를 삭제하면 메뉴가 삭제된다.")
    void deleteMenu_withValidInput_deletesMenu() {
        // given
        Long menuId = 1L;
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(menu.getRestaurant(), user.getId());

        // when
        restaurantCommandService.deleteMenu(menuId, user);

        // then
        verify(menuRepository, times(1)).delete(menu);
        verify(publisher, times(1)).publishEvent(any(MenuDeleteEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 메뉴를 삭제하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void deleteMenu_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long menuId = 1L;
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        willThrow(new UnauthorizedRestaurantAccessException(menu.getRestaurant().getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(menu.getRestaurant(), notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.deleteMenu(menuId, notOwnerUser));

        verify(menuRepository, times(0)).delete(menu);
        verify(publisher, times(0)).publishEvent(any(MenuDeleteEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 메뉴를 삭제하려고 하면 MenuNotFoundException 예외가 발생한다.")
    void deleteMenu_withNotExistingMenu_throwsMenuNotFoundException() {
        // given
        Long menuId = 1L;
        given(menuRepository.findById(menuId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MenuNotFoundException.class, () -> restaurantCommandService.deleteMenu(menuId, user));

        verify(menuRepository, times(0)).delete(menu);
        verify(publisher, times(0)).publishEvent(any(MenuDeleteEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 메뉴를 수정하면 메뉴가 수정된다.")
    void updateMenu_withValidInput_updatesMenu() {
        // given
        Long menuId = 1L;
        MenuUpdateCommand command = new MenuUpdateCommand("햄버거", 10000, "맛있는 햄버거", "수정된 이미지 URL");
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(menu.getRestaurant(), user.getId());

        // when
        restaurantCommandService.updateMenu(menuId, command, user);

        // then
        assertThat(menu.getName()).isEqualTo("햄버거");
        assertThat(menu.getDescription()).isEqualTo("맛있는 햄버거");
        assertThat(menu.getPrice()).isEqualTo(10000);
        assertThat(menu.getImageUrl()).isEqualTo("수정된 이미지 URL");

        verify(publisher, times(1)).publishEvent(any(MenuUpdateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 메뉴를 수정하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void updateMenu_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long menuId = 1L;
        MenuUpdateCommand command = new MenuUpdateCommand("햄버거", 10000, "맛있는 햄버거", "수정된 이미지 URL");
        given(menuRepository.findById(menuId)).willReturn(Optional.of(menu));
        willThrow(new UnauthorizedRestaurantAccessException(menu.getRestaurant().getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(menu.getRestaurant(), notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.updateMenu(menuId, command, notOwnerUser));

        verify(publisher, times(0)).publishEvent(any(MenuUpdateEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 메뉴를 수정하려고 하면 MenuNotFoundException 예외가 발생한다.")
    void updateMenu_withNotExistingMenu_throwsMenuNotFoundException() {
        // given
        Long menuId = 1L;
        MenuUpdateCommand command = new MenuUpdateCommand("햄버거", 10000, "맛있는 햄버거", "수정된 이미지 URL");
        given(menuRepository.findById(menuId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MenuNotFoundException.class, () -> restaurantCommandService.updateMenu(menuId, command, user));

        verify(publisher, times(0)).publishEvent(any(MenuUpdateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 휴무 기간을 생성하면 휴무 기간이 생성된다.")
    void createClosedPeriod_withValidInput_createsClosedPeriod() {
        // given
        Long restaurantId = 1L;
        ClosedPeriodCreateCommand command = new ClosedPeriodCreateCommand("2024-09-01", "2024-09-07", "휴가");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(closedPeriodRepository.save(any(ClosedPeriod.class))).willReturn(closedPeriod);
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());

        // when
        restaurantCommandService.createClosedPeriod(restaurantId, command, user);

        // then
        verify(closedPeriodRepository, times(1)).save(any(ClosedPeriod.class));
        verify(publisher, times(1)).publishEvent(any(ClosedPeriodCreateEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 휴무 기간을 생성하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void createClosedPeriod_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long restaurantId = 1L;
        ClosedPeriodCreateCommand command = new ClosedPeriodCreateCommand("2024-09-01", "2024-09-07", "휴가");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willThrow(new UnauthorizedRestaurantAccessException(restaurant.getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(restaurant, notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.createClosedPeriod(restaurantId, command, notOwnerUser));

        verify(closedPeriodRepository, times(0)).save(any(ClosedPeriod.class));
        verify(publisher, times(0)).publishEvent(any(ClosedPeriodCreateEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 식당에 휴무 기간을 생성하려고 하면 RestaurantNotFoundException 예외가 발생한다.")
    void createClosedPeriod_withNotExistingRestaurant_throwsRestaurantNotFoundException() {
        // given
        Long restaurantId = 1L;
        ClosedPeriodCreateCommand command = new ClosedPeriodCreateCommand("2024-09-01", "2024-09-07", "휴가");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.empty());

        // when & then
        assertThrows(RestaurantNotFoundException.class, () -> restaurantCommandService.createClosedPeriod(restaurantId, command, user));

        verify(closedPeriodRepository, times(0)).save(any(ClosedPeriod.class));
        verify(publisher, times(0)).publishEvent(any(ClosedPeriodCreateEvent.class));
    }

    @Test
    @DisplayName("겹치는 휴무 기간이 있는 경우 휴무 기간을 생성하려고 하면 ClosedPeriodOverlapException 예외가 발생한다.")
    void createClosedPeriod_withOverlappedClosedPeriod_throwsClosedPeriodOverlapException() {
        // given
        Long restaurantId = 1L;
        ClosedPeriodCreateCommand command = new ClosedPeriodCreateCommand("2024-09-01", "2024-09-07", "휴가");
        ClosedPeriod overlappedClosedPeriod = ClosedPeriod.builder()
                .startDate(LocalDate.of(2024, 9, 3))
                .endDate(LocalDate.of(2024, 9, 10))
                .reason("휴가")
                .restaurant(restaurant)
                .build();
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        given(closedPeriodRepository.findAllByRestaurantId(restaurantId)).willReturn(List.of(overlappedClosedPeriod));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());
        willThrow(new ClosedPeriodOverlapException(restaurant.getId(), command.getStartDate(), command.getEndDate())).given(restaurantValidator).validateNoOverlapClosedPeriod(List.of(overlappedClosedPeriod), command.getStartDate(), command.getEndDate(), restaurant.getId());

        // when & then
        assertThrows(ClosedPeriodOverlapException.class, () -> restaurantCommandService.createClosedPeriod(restaurantId, command, user));

        verify(closedPeriodRepository, times(0)).save(any(ClosedPeriod.class));
        verify(publisher, times(0)).publishEvent(any(ClosedPeriodCreateEvent.class));
    }

    @Test
    @DisplayName("오늘 날짜보다 이전인 휴무 기간을 생성하려고 하면 ClosedPeriodPastException 예외가 발생한다.")
    void createClosedPeriod_withPastClosedPeriod_throwsClosedPeriodPastException() {
        // given
        Long restaurantId = 1L;
        ClosedPeriodCreateCommand command = new ClosedPeriodCreateCommand("2022-09-01", "2022-09-07", "휴가");
        given(restaurantRepository.findById(restaurantId)).willReturn(Optional.of(restaurant));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(restaurant, user.getId());
        willThrow(new ClosedPeriodPastException(command.getStartDate())).given(restaurantValidator).validateClosePeriodNotBeforeToday(command.getStartDate());

        // when & then
        assertThrows(ClosedPeriodPastException.class, () -> restaurantCommandService.createClosedPeriod(restaurantId, command, user));

        verify(closedPeriodRepository, times(0)).save(any(ClosedPeriod.class));
        verify(publisher, times(0)).publishEvent(any(ClosedPeriodCreateEvent.class));
    }

    @Test
    @DisplayName("유효한 입력으로 휴무 기간을 삭제하면 휴무 기간이 삭제된다.")
    void deleteClosedPeriod_withValidInput_deletesClosedPeriod() {
        // given
        Long closedPeriodId = 1L;
        given(closedPeriodRepository.findById(closedPeriodId)).willReturn(Optional.of(closedPeriod));
        willDoNothing().given(restaurantValidator).validateRestaurantOwner(closedPeriod.getRestaurant(), user.getId());

        // when
        restaurantCommandService.deleteClosedPeriod(closedPeriodId, user);

        // then
        verify(closedPeriodRepository, times(1)).delete(closedPeriod);
        verify(publisher, times(1)).publishEvent(any(ClosedPeriodDeleteEvent.class));
    }

    @Test
    @DisplayName("식당 소유자가 아닌 사용자가 휴무 기간을 삭제하려고 하면 UnauthorizedRestaurantAccessException 예외가 발생한다.")
    void deleteClosedPeriod_withNotOwnerUser_throwsUnauthorizedRestaurantAccessException() {
        // given
        Long closedPeriodId = 1L;
        given(closedPeriodRepository.findById(closedPeriodId)).willReturn(Optional.of(closedPeriod));
        willThrow(new UnauthorizedRestaurantAccessException(closedPeriod.getRestaurant().getId(), notOwnerUser.getId())).given(restaurantValidator).validateRestaurantOwner(closedPeriod.getRestaurant(), notOwnerUser.getId());

        // when & then
        assertThrows(UnauthorizedRestaurantAccessException.class, () -> restaurantCommandService.deleteClosedPeriod(closedPeriodId, notOwnerUser));

        verify(closedPeriodRepository, times(0)).delete(closedPeriod);
        verify(publisher, times(0)).publishEvent(any(ClosedPeriodDeleteEvent.class));
    }

    @Test
    @DisplayName("존재하지 않는 휴무 기간을 삭제하려고 하면 ClosedPeriodNotFoundException 예외가 발생한다.")
    void deleteClosedPeriod_withNotExistingClosedPeriod_throwsClosedPeriodNotFoundException() {
        // given
        Long closedPeriodId = 1L;
        given(closedPeriodRepository.findById(closedPeriodId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ClosedPeriodNotFoundException.class, () -> restaurantCommandService.deleteClosedPeriod(closedPeriodId, user));

        verify(closedPeriodRepository, times(0)).delete(closedPeriod);
        verify(publisher, times(0)).publishEvent(any(ClosedPeriodDeleteEvent.class));
    }

    @Test
    @DisplayName("모든 식당의 ID를 조회하면 식당 ID 목록을 반환한다.")
    void getAllRestaurantIds_withNoInput_returnsRestaurantIds() {
        // given
        given(restaurantRepository.findAll()).willReturn(List.of(restaurant));

        // when
        List<Long> restaurantIds = restaurantCommandService.getAllRestaurantIds();

        // then
        assertThat(restaurantIds).containsExactly(1L);
    }
}