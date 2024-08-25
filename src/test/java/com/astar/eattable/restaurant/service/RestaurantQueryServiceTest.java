package com.astar.eattable.restaurant.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.restaurant.RestaurantTestUtils;
import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import com.astar.eattable.restaurant.command.RestaurantCreateCommand;
import com.astar.eattable.restaurant.command.RestaurantUpdateCommand;
import com.astar.eattable.restaurant.document.BusinessHoursDocument;
import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.astar.eattable.restaurant.dto.RestaurantDetailsDTO;
import com.astar.eattable.restaurant.dto.RestaurantListDTO;
import com.astar.eattable.restaurant.payload.BusinessHoursUpdateEventPayload;
import com.astar.eattable.restaurant.payload.RestaurantCreateEventPayload;
import com.astar.eattable.restaurant.payload.RestaurantDeleteEventPayload;
import com.astar.eattable.restaurant.payload.RestaurantUpdateEventPayload;
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

    private List<BusinessHoursDocument> businessHoursDocuments;
    private RestaurantDocument restaurantDocument;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(restaurantQueryService, "searchRadiusKm", 2);
        businessHoursDocuments = new ArrayList<>();
        for (Day day : Day.values()) {
            businessHoursDocuments.add(new BusinessHoursDocument(day, "09:00", "21:00", "12:00", "13:00", "20:00"));
        }
        GeoJsonPoint location = new GeoJsonPoint(37.123456, 127.123456);
        restaurantDocument = new RestaurantDocument(1L, "테스트 식당", "맛있는 식당", "이미지 URL", "한식", "02-1234-5678", "서울시 강남구", location, 0.0, 0L, businessHoursDocuments);
    }

    @Test
    @DisplayName("유효한 페이로드로 식당을 생성하면 식당 Document를 저장한다.")
    void createRestaurant_withValidPayload_thenSaveRestaurantDocument() {
        // given
        RestaurantCreateCommand command = RestaurantTestUtils.getRestaurantCreateCommand();
        RestaurantCreateEventPayload payload = new RestaurantCreateEventPayload(1L, command);

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
        List<RestaurantListDTO> restaurantListDTOs = List.of(new RestaurantListDTO(restaurantDocument));
        given(restaurantMongoRepository.findByLocationNear(37.123456, 127.123456, 2000)).willReturn(List.of(restaurantDocument));

        // when
        List<RestaurantListDTO> resultDTOs = restaurantQueryService.getNearbyRestaurants(37.123456, 127.123456);

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
        RestaurantDetailsDTO restaurantDetailsDTO = new RestaurantDetailsDTO(restaurantDocument);
        given(restaurantMongoRepository.findById(1L)).willReturn(Optional.of(restaurantDocument));

        // when
        RestaurantDetailsDTO resultDTO = restaurantQueryService.getRestaurant(1L);

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
    void initMenuSections() {
    }

    @Test
    void createMenuSection() {
    }

    @Test
    void deleteMenuSection() {
    }

    @Test
    void updateMenuSection() {
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
    void getMenuSections() {
    }

    @Test
    void createClosedPeriod() {
    }

    @Test
    void deleteClosedPeriod() {
    }

    @Test
    void getClosedPeriods() {
    }
}