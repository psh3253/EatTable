package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.restaurant.RestaurantTestUtils;
import com.astar.eattable.restaurant.command.*;
import com.astar.eattable.restaurant.document.BusinessHoursDocument;
import com.astar.eattable.restaurant.document.ClosedPeriodDocument;
import com.astar.eattable.restaurant.document.MenuSectionMapDocument;
import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.astar.eattable.restaurant.dto.ClosedPeriodListDTO;
import com.astar.eattable.restaurant.dto.MenuSectionDTO;
import com.astar.eattable.restaurant.dto.RestaurantDetailsDTO;
import com.astar.eattable.restaurant.dto.RestaurantListDTO;
import com.astar.eattable.restaurant.payload.*;
import com.astar.eattable.restaurant.repository.ClosedPeriodMongoRepository;
import com.astar.eattable.restaurant.repository.MenuSectionMapMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RestaurantQueryServiceTest {

    @InjectMocks
    private RestaurantQueryService restaurantQueryService;

    @Mock
    private RestaurantMongoRepository restaurantMongoRepository;

    @Mock
    private MenuSectionMapMongoRepository menuSectionMapMongoRepository;

    @Mock
    private ClosedPeriodMongoRepository closedPeriodMongoRepository;

    private List<BusinessHoursDocument> businessHoursDocuments;
    private RestaurantDocument restaurantDocument;
    private MenuSectionMapDocument menuSectionMapDocument;
    private List<ClosedPeriodDocument> closedPeriodDocuments;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(restaurantQueryService, "searchRadiusKm", 2);
        businessHoursDocuments = new ArrayList<>();
        for (Day day : Day.values()) {
            businessHoursDocuments.add(new BusinessHoursDocument(day, "09:00", "21:00", "12:00", "13:00", "20:00"));
        }
        GeoJsonPoint location = new GeoJsonPoint(37.123456, 127.123456);
        restaurantDocument = new RestaurantDocument(1L, "테스트 식당", "맛있는 식당", "이미지 URL", "한식", "02-1234-5678", "서울시 강남구", location, 1L, "테스트", 4.5, 100L, businessHoursDocuments);
        menuSectionMapDocument = new MenuSectionMapDocument(1L);

        MenuSectionCreateCommand createCommand = new MenuSectionCreateCommand("메인 메뉴");
        MenuSectionCreateEventPayload createPayload = new MenuSectionCreateEventPayload(1L, 1L, createCommand);
        menuSectionMapDocument.createMenuSection(createPayload);

        MenuCreateCommand menuCreateCommand = new MenuCreateCommand("치킨", "맛있는 치킨", 15000, "이미지 URL");
        MenuCreateEventPayload menuCreateEventPayload = new MenuCreateEventPayload(1L, 1L, 1L, menuCreateCommand);
        menuSectionMapDocument.createMenu(menuCreateEventPayload);

        closedPeriodDocuments = List.of(new ClosedPeriodDocument(1L, 1L, "2024-09-01", "2024-09-07", "휴가"));
    }

    @Test
    @DisplayName("유효한 페이로드로 식당을 생성하면 식당 Document를 저장한다.")
    void createRestaurant_withValidPayload_thenSaveRestaurantDocument() {
        // given
        RestaurantCreateCommand command = RestaurantTestUtils.getRestaurantCreateCommand();
        RestaurantCreateEventPayload payload = new RestaurantCreateEventPayload(1L, command, 1L, "테스트");

        // when
        restaurantQueryService.createRestaurant(payload);

        // then
        verify(restaurantMongoRepository, times(1)).save(any(RestaurantDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 식당을 삭제하면 식당 Document를 삭제한다.")
    void deleteRestaurant_withValidPayload_thenDeleteRestaurantDocument() {
        // given
        RestaurantDeleteEventPayload payload = new RestaurantDeleteEventPayload(1L);

        // when
        restaurantQueryService.deleteRestaurant(payload);

        // then
        verify(restaurantMongoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("유효한 위치로 주변 식당을 조회하면 식당 목록을 반환한다.")
    void getNearbyRestaurants_withValidLocation_thenReturnRestaurantListDTO() {
        // given
        double longitude = 37.123456;
        double latitude = 127.123456;
        List<RestaurantListDTO> restaurantListDTOs = List.of(new RestaurantListDTO(restaurantDocument));
        given(restaurantMongoRepository.findByLocationNear(longitude, latitude, 2000)).willReturn(List.of(restaurantDocument));

        // when
        List<RestaurantListDTO> resultDTOs = restaurantQueryService.getNearbyRestaurants(longitude, latitude);

        // then
        assertThat(resultDTOs.get(0).getId()).isEqualTo(restaurantListDTOs.get(0).getId());
        assertThat(resultDTOs.get(0).getName()).isEqualTo(restaurantListDTOs.get(0).getName());
        assertThat(resultDTOs.get(0).getImageUrl()).isEqualTo(restaurantListDTOs.get(0).getImageUrl());
        assertThat(resultDTOs.get(0).getCategoryName()).isEqualTo(restaurantListDTOs.get(0).getCategoryName());
        assertThat(resultDTOs.get(0).getPhone()).isEqualTo(restaurantListDTOs.get(0).getPhone());
        assertThat(resultDTOs.get(0).getAddress()).isEqualTo(restaurantListDTOs.get(0).getAddress());
        assertThat(resultDTOs.get(0).getReviewScore()).isEqualTo(restaurantListDTOs.get(0).getReviewScore());
        assertThat(resultDTOs.get(0).getReviewCount()).isEqualTo(restaurantListDTOs.get(0).getReviewCount());
    }

    @Test
    @DisplayName("유효한 페이로드로 식당을 업데이트하면 식당 Document를 업데이트한다.")
    void updateRestaurant_withValidPayload_thenUpdateRestaurantDocument() {
        // given
        RestaurantUpdateCommand command = new RestaurantUpdateCommand("수정된 식당", "더 맛있는 식당", "서울시 서초구", "02-5678-1234", "수정된 이미지 URL", "양식", 37.654321, 127.654321);
        RestaurantUpdateEventPayload payload = new RestaurantUpdateEventPayload(1L, command);
        given(restaurantMongoRepository.findById(1L)).willReturn(Optional.of(restaurantDocument));

        // when
        restaurantQueryService.updateRestaurant(payload);

        // then
        verify(restaurantMongoRepository, times(1)).save(any(RestaurantDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 영업시간을 업데이트하면 식당 Document를 업데이트한다.")
    void updateBusinessHours_withValidPayload_thenUpdateBusinessHoursDocument() {
        // given
        BusinessHoursUpdateCommand command = RestaurantTestUtils.getBusinessHoursUpdateCommand();
        BusinessHoursUpdateEventPayload payload = new BusinessHoursUpdateEventPayload(1L, command);
        given(restaurantMongoRepository.findById(1L)).willReturn(Optional.of(restaurantDocument));

        // when
        restaurantQueryService.updateBusinessHours(payload);

        // then
        verify(restaurantMongoRepository, times(1)).save(any(RestaurantDocument.class));
    }

    @Test
    @DisplayName("유효한 식당 ID로 식당을 조회하면 식당 상세 정보를 반환한다.")
    void getRestaurant_withValidRestaurantId_thenReturnRestaurantDetailsDTO() {
        // given
        Long restaurantId = 1L;
        RestaurantDetailsDTO restaurantDetailsDTO = new RestaurantDetailsDTO(restaurantDocument);
        given(restaurantMongoRepository.findById(restaurantId)).willReturn(Optional.of(restaurantDocument));

        // when
        RestaurantDetailsDTO resultDTO = restaurantQueryService.getRestaurant(restaurantId);

        // then
        assertThat(resultDTO.getId()).isEqualTo(restaurantDetailsDTO.getId());
        assertThat(resultDTO.getName()).isEqualTo(restaurantDetailsDTO.getName());
        assertThat(resultDTO.getDescription()).isEqualTo(restaurantDetailsDTO.getDescription());
        assertThat(resultDTO.getImageUrl()).isEqualTo(restaurantDetailsDTO.getImageUrl());
        assertThat(resultDTO.getCategoryName()).isEqualTo(restaurantDetailsDTO.getCategoryName());
        assertThat(resultDTO.getPhone()).isEqualTo(restaurantDetailsDTO.getPhone());
        assertThat(resultDTO.getAddress()).isEqualTo(restaurantDetailsDTO.getAddress());
        assertThat(resultDTO.getReviewScore()).isEqualTo(restaurantDetailsDTO.getReviewScore());
        assertThat(resultDTO.getReviewCount()).isEqualTo(restaurantDetailsDTO.getReviewCount());
        assertThat(resultDTO.getBusinessHours().size()).isEqualTo(restaurantDetailsDTO.getBusinessHours().size());
        assertThat(resultDTO.getBusinessHours().get(0).getDay()).isEqualTo(restaurantDetailsDTO.getBusinessHours().get(0).getDay());
        assertThat(resultDTO.getBusinessHours().get(0).getStartTime()).isEqualTo(restaurantDetailsDTO.getBusinessHours().get(0).getStartTime());
        assertThat(resultDTO.getBusinessHours().get(0).getEndTime()).isEqualTo(restaurantDetailsDTO.getBusinessHours().get(0).getEndTime());
        assertThat(resultDTO.getBusinessHours().get(0).getBreakStartTime()).isEqualTo(restaurantDetailsDTO.getBusinessHours().get(0).getBreakStartTime());
        assertThat(resultDTO.getBusinessHours().get(0).getBreakEndTime()).isEqualTo(restaurantDetailsDTO.getBusinessHours().get(0).getBreakEndTime());
        assertThat(resultDTO.getBusinessHours().get(0).getLastOrderTime()).isEqualTo(restaurantDetailsDTO.getBusinessHours().get(0).getLastOrderTime());
    }

    @Test
    @DisplayName("유효한 식당 ID로 메뉴 섹션을 초기화하면 메뉴 섹션 맵 Document를 저장한다.")
    void initMenuSections_withValidRestaurantId_thenSaveMenuSectionMapDocument() {
        // given
        Long restaurantId = 1L;

        // when
        restaurantQueryService.initMenuSections(restaurantId);

        // then
        verify(menuSectionMapMongoRepository, times(1)).save(any(MenuSectionMapDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 메뉴 섹션을 생성하면 메뉴 섹션 맵 Document를 저장한다.")
    void createMenuSection_withValidPayload_thenSaveMenuSectionMapDocument() {
        // given
        MenuSectionCreateCommand command = new MenuSectionCreateCommand("사이드 메뉴");
        MenuSectionCreateEventPayload payload = new MenuSectionCreateEventPayload(1L, 2L, command);
        given(menuSectionMapMongoRepository.findByRestaurantId(1L)).willReturn(Optional.of(new MenuSectionMapDocument(1L)));

        // when
        restaurantQueryService.createMenuSection(payload);

        // then
        verify(menuSectionMapMongoRepository, times(1)).save(any(MenuSectionMapDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 메뉴 섹션을 삭제하면 메뉴 섹션 맵 Document를 업데이트한다.")
    void deleteMenuSection_withValidPayload_thenDeleteMenuSectionMapDocument() {
        // given
        MenuSectionDeleteEventPayload payload = new MenuSectionDeleteEventPayload(1L, 1L);
        given(menuSectionMapMongoRepository.findByRestaurantId(1L)).willReturn(Optional.of(menuSectionMapDocument));

        // when
        restaurantQueryService.deleteMenuSection(payload);

        // then
        verify(menuSectionMapMongoRepository, times(1)).save(any(MenuSectionMapDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 메뉴 섹션을 업데이트하면 메뉴 섹션 맵 Document를 업데이트한다.")
    void updateMenuSection_withValidPayload_thenUpdateMenuSectionMapDocument() {
        // given
        MenuSectionUpdateCommand command = new MenuSectionUpdateCommand("사이드 메뉴");
        MenuSectionUpdateEventPayload payload = new MenuSectionUpdateEventPayload(1L, 1L, command);
        given(menuSectionMapMongoRepository.findByRestaurantId(1L)).willReturn(Optional.of(menuSectionMapDocument));

        // when
        restaurantQueryService.updateMenuSection(payload);

        // then
        verify(menuSectionMapMongoRepository, times(1)).save(any(MenuSectionMapDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 메뉴를 생성하면 메뉴 섹션 맵 Document를 업데이트한다.")
    void createMenu_withValidPayload_thenSaveMenuSectionMapDocument() {
        // given
        MenuCreateCommand command = new MenuCreateCommand("햄버거", "맛있는 햄버거", 10000, "수정된 이미지 URL");
        MenuCreateEventPayload payload = new MenuCreateEventPayload(1L, 1L, 1L, command);
        given(menuSectionMapMongoRepository.findByRestaurantId(1L)).willReturn(Optional.of(menuSectionMapDocument));

        // when
        restaurantQueryService.createMenu(payload);

        // then
        verify(menuSectionMapMongoRepository, times(1)).save(any(MenuSectionMapDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 메뉴를 삭제하면 메뉴 섹션 맵 Document를 업데이트한다.")
    void deleteMenu_withValidPayload_thenDeleteMenuSectionMapDocument() {
        // given
        MenuDeleteEventPayload payload = new MenuDeleteEventPayload(1L, 1L, 1L);
        given(menuSectionMapMongoRepository.findByRestaurantId(1L)).willReturn(Optional.of(menuSectionMapDocument));

        // when
        restaurantQueryService.deleteMenu(payload);

        // then
        verify(menuSectionMapMongoRepository, times(1)).save(any(MenuSectionMapDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 메뉴를 업데이트하면 메뉴 섹션 맵 Document를 업데이트한다.")
    void updateMenu_withValidPayload_thenUpdateMenuSectionMapDocument() {
        // given
        MenuUpdateCommand command = new MenuUpdateCommand("햄버거", 10000, "맛있는 햄버거", "수정된 이미지 URL");
        MenuUpdateEventPayload payload = new MenuUpdateEventPayload(1L, 1L, 1L, command);
        given(menuSectionMapMongoRepository.findByRestaurantId(1L)).willReturn(Optional.of(menuSectionMapDocument));

        // when
        restaurantQueryService.updateMenu(payload);

        // then
        verify(menuSectionMapMongoRepository, times(1)).save(any(MenuSectionMapDocument.class));
    }

    @Test
    @DisplayName("유효한 식당 ID로 메뉴 섹션을 조회하면 메뉴 섹션 DTO 목록을 반환한다.")
    void getMenuSections_withValidRestaurantId_thenReturnMenuSectionDTOs() {
        // given
        Long restaurantId = 1L;
        given(menuSectionMapMongoRepository.findByRestaurantId(restaurantId)).willReturn(Optional.of(menuSectionMapDocument));

        // when
        Map<Long, MenuSectionDTO> resultDTOs = restaurantQueryService.getMenuSections(restaurantId);

        // then
        assertThat(resultDTOs.size()).isEqualTo(1);
        assertThat(resultDTOs.get(1L).getName()).isEqualTo("메인 메뉴");
        assertThat(resultDTOs.get(1L).getMenus().size()).isEqualTo(1);
        assertThat(resultDTOs.get(1L).getMenus().get(1L).getName()).isEqualTo("치킨");
        assertThat(resultDTOs.get(1L).getMenus().get(1L).getDescription()).isEqualTo("맛있는 치킨");
        assertThat(resultDTOs.get(1L).getMenus().get(1L).getPrice()).isEqualTo(15000);
        assertThat(resultDTOs.get(1L).getMenus().get(1L).getImageUrl()).isEqualTo("이미지 URL");
    }

    @Test
    @DisplayName("유효한 페이로드로 휴무일을 생성하면 휴무일 Document를 저장한다.")
    void createClosedPeriod_withValidPayload_thenSaveClosedPeriodDocument() {
        // given
        ClosedPeriodCreateCommand command = new ClosedPeriodCreateCommand("2024-09-01", "2024-09-07", "휴가");
        ClosedPeriodCreateEventPayload payload = new ClosedPeriodCreateEventPayload(1L, 1L, command);

        // when
        restaurantQueryService.createClosedPeriod(payload);

        // then
        verify(closedPeriodMongoRepository, times(1)).save(any(ClosedPeriodDocument.class));
    }

    @Test
    @DisplayName("유효한 페이로드로 휴무일을 삭제하면 휴무일 Document를 삭제한다.")
    void deleteClosedPeriod_withValidPayload_thenDeleteClosedPeriodDocument() {
        // given
        ClosedPeriodDeleteEventPayload payload = new ClosedPeriodDeleteEventPayload(1L, 1L);

        // when
        restaurantQueryService.deleteClosedPeriod(payload);

        // then
        verify(closedPeriodMongoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("유효한 식당 ID로 휴무일을 조회하면 휴무일 목록을 반환한다.")
    void getClosedPeriods_withValidRestaurantId_thenReturnClosedPeriodListDTOs() {
        // given
        Long restaurantId = 1L;
        ClosedPeriodDocument closedPeriodDocument = new ClosedPeriodDocument(1L, restaurantId, "2024-09-01", "2024-09-07", "휴가");
        given(closedPeriodMongoRepository.findAllByRestaurantId(restaurantId)).willReturn(closedPeriodDocuments);

        // when
        List<ClosedPeriodListDTO> resultDTOs = restaurantQueryService.getClosedPeriods(restaurantId);

        // then
        assertThat(resultDTOs.size()).isEqualTo(1);
        assertThat(resultDTOs.get(0).getClosedPeriodId()).isEqualTo(1L);
        assertThat(resultDTOs.get(0).getStartDate()).isEqualTo("2024-09-01");
        assertThat(resultDTOs.get(0).getEndDate()).isEqualTo("2024-09-07");
        assertThat(resultDTOs.get(0).getReason()).isEqualTo("휴가");
    }
}