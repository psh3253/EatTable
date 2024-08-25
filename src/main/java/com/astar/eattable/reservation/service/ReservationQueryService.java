package com.astar.eattable.reservation.service;

import com.astar.eattable.common.dto.Day;
import com.astar.eattable.common.service.CommonService;
import com.astar.eattable.reservation.document.MonthlyAvailabilityDocument;
import com.astar.eattable.reservation.document.ReservationDocument;
import com.astar.eattable.reservation.document.TableAvailabilityDocument;
import com.astar.eattable.reservation.document.TimeAvailabilityDocument;
import com.astar.eattable.reservation.dto.MonthlyAvailabilityDTO;
import com.astar.eattable.reservation.dto.MyReservationDetailsDTO;
import com.astar.eattable.reservation.dto.MyReservationListDTO;
import com.astar.eattable.reservation.dto.TableAvailabilityDTO;
import com.astar.eattable.reservation.exception.MonthlyAvailabilityNotFoundException;
import com.astar.eattable.reservation.exception.ReservationNotFoundException;
import com.astar.eattable.reservation.exception.TableAvailabilityNotFoundException;
import com.astar.eattable.reservation.exception.TimeAvailabilityNotFoundException;
import com.astar.eattable.reservation.payload.ReservationCreatePayload;
import com.astar.eattable.reservation.payload.TableCountUpdatePayload;
import com.astar.eattable.reservation.repository.MonthlyAvailabilityMongoRepository;
import com.astar.eattable.reservation.repository.ReservationMongoRepository;
import com.astar.eattable.reservation.repository.TableAvailabilityMongoRepository;
import com.astar.eattable.reservation.validator.ReservationValidator;
import com.astar.eattable.restaurant.document.BusinessHoursDocument;
import com.astar.eattable.restaurant.document.RestaurantDocument;
import com.astar.eattable.restaurant.exception.RestaurantNotFoundException;
import com.astar.eattable.restaurant.repository.ClosedPeriodMongoRepository;
import com.astar.eattable.restaurant.repository.RestaurantMongoRepository;
import com.astar.eattable.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReservationQueryService {

    private final MonthlyAvailabilityMongoRepository monthlyAvailabilityMongoRepository;
    private final ClosedPeriodMongoRepository closedPeriodMongoRepository;
    private final RestaurantMongoRepository restaurantMongoRepository;
    private final TableAvailabilityMongoRepository tableAvailabilityMongoRepository;
    private final ReservationMongoRepository reservationMongoRepository;
    private final CommonService commonService;
    private final ReservationValidator reservationValidator;

    @Value("${max.reservation.period.days}")
    private int MAX_RESERVATION_PERIOD_DAYS;

    @Value("${reservation.interval.minutes}")
    private int RESERVATION_INTERVAL_MINUTES;

    public MonthlyAvailabilityDTO getMonthlyAvailability(Long restaurantId, Integer year, Integer month) {
        MonthlyAvailabilityDocument monthlyAvailabilityDocument = monthlyAvailabilityMongoRepository.findById(restaurantId + "-" + year + "-" + month)
                .orElseThrow(() -> new MonthlyAvailabilityNotFoundException(restaurantId, year, month));
        return new MonthlyAvailabilityDTO(monthlyAvailabilityDocument);
    }

    public void initMonthlyAvailability(Long restaurantId) {
        LocalDate currendDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(MAX_RESERVATION_PERIOD_DAYS);
        int currentMonth = currendDate.getMonthValue();
        MonthlyAvailabilityDocument monthlyAvailabilityDocument = new MonthlyAvailabilityDocument(restaurantId, currendDate.getYear(), currendDate.getMonthValue());

        while (currendDate.isBefore(endDate)) {
            if (!isDateInClosedPeriod(restaurantId, currendDate.toString())) {
                monthlyAvailabilityDocument.addAvailableDate(currendDate.toString());
            }
            currendDate = currendDate.plusDays(1);

            if (currendDate.getMonthValue() != currentMonth) {
                monthlyAvailabilityMongoRepository.save(monthlyAvailabilityDocument);
                monthlyAvailabilityDocument = new MonthlyAvailabilityDocument(restaurantId, currendDate.getYear(), currendDate.getMonthValue());
                currentMonth = currendDate.getMonthValue();
            }
        }
        monthlyAvailabilityMongoRepository.save(monthlyAvailabilityDocument);
    }

    private boolean isDateInClosedPeriod(Long restaurantId, String date) {
        return closedPeriodMongoRepository.findClosedPeriod(restaurantId, date).isPresent();
    }

    public void initAllMonthlyAvailabilities(List<Long> restaurantIds) {
        restaurantIds.forEach(this::initMonthlyAvailability);
    }

    public void updateMonthlyAvailability(Long restaurantId) {
        List<MonthlyAvailabilityDocument> monthlyAvailabilityDocuments = monthlyAvailabilityMongoRepository.findAllByRestaurantId(restaurantId);
        monthlyAvailabilityMongoRepository.deleteAll(monthlyAvailabilityDocuments);
        initMonthlyAvailability(restaurantId);
    }

    public void initTableAvailability(Long restaurantId) {
        RestaurantDocument restaurantDocument = restaurantMongoRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        List<BusinessHoursDocument> businessHoursDocuments = restaurantDocument.getBusinessHours();
        Map<Day, BusinessHoursDocument> businessHoursMap = businessHoursDocuments.stream().collect(Collectors.toMap(BusinessHoursDocument::getDay, businessHoursDocument -> businessHoursDocument));
        List<TableAvailabilityDocument> tableAvailabilityDocuments = new ArrayList<>();
        LocalDate currentDate = getTableAvailabilityLastDateNextDay(restaurantId);
        LocalDate endDate = LocalDate.now().plusDays(MAX_RESERVATION_PERIOD_DAYS);
        int reservationDuration = 60;
        int[] capacities = {1, 2, 3, 4};

        while (currentDate.isBefore(endDate)) {
            if (isDateInClosedPeriod(restaurantId, currentDate.toString())) {
                currentDate = currentDate.plusDays(1);
                continue;
            }

            Day day = Day.fromDayOfWeek(currentDate.getDayOfWeek());
            BusinessHoursDocument businessHoursDocument = businessHoursMap.get(day);
            LocalTime startTime = LocalTime.parse(businessHoursDocument.getStartTime());
            LocalTime lastTime = businessHoursDocument.getLastOrderTime() != null ? LocalTime.parse(businessHoursDocument.getLastOrderTime()) : LocalTime.parse(businessHoursDocument.getEndTime());
            LocalTime breakStartTime = businessHoursDocument.getBreakStartTime() != null ? LocalTime.parse(businessHoursDocument.getBreakStartTime()) : null;
            LocalTime breakEndTime = businessHoursDocument.getBreakEndTime() != null ? LocalTime.parse(businessHoursDocument.getBreakEndTime()) : null;

            List<TimeAvailabilityDocument> timeAvailabilityDocuments = createTimeAvailabilityDocuments(startTime, lastTime, breakStartTime, breakEndTime, reservationDuration);
            for (int capacity : capacities) {
                TableAvailabilityDocument tableAvailabilityDocument = new TableAvailabilityDocument(restaurantId, currentDate.toString(), capacity, timeAvailabilityDocuments);
                tableAvailabilityDocuments.add(tableAvailabilityDocument);
            }
            currentDate = currentDate.plusDays(1);
        }

        tableAvailabilityMongoRepository.saveAll(tableAvailabilityDocuments);
    }

    private LocalDate getTableAvailabilityLastDateNextDay(Long restaurantId) {
        LocalDate lastDate = tableAvailabilityMongoRepository.findTopByRestaurantIdOrderByDateDesc(restaurantId).map(TableAvailabilityDocument::getDate).map(LocalDate::parse).orElse(LocalDate.now());
        return lastDate.isBefore(LocalDate.now()) ? LocalDate.now() : lastDate.plusDays(1);
    }

    private List<TimeAvailabilityDocument> createTimeAvailabilityDocuments(LocalTime startTime, LocalTime lastTime, LocalTime breakStartTime, LocalTime breakEndTime, int reservationDuration) {
        List<TimeAvailabilityDocument> timeAvailabilityDocuments = new ArrayList<>();
        while (startTime.plusMinutes(reservationDuration).isBefore(lastTime) || startTime.plusMinutes(reservationDuration).equals(lastTime)) {
            if (commonService.isBreakTime(startTime, breakStartTime, breakEndTime)) {
                startTime = startTime.plusMinutes(RESERVATION_INTERVAL_MINUTES);
                continue;
            }
            TimeAvailabilityDocument timeAvailabilityDocument = new TimeAvailabilityDocument(startTime.toString(), 0);
            timeAvailabilityDocuments.add(timeAvailabilityDocument);
            startTime = startTime.plusMinutes(RESERVATION_INTERVAL_MINUTES);
        }
        return timeAvailabilityDocuments;
    }

    public List<TableAvailabilityDTO> getTableAvailability(Long restaurantId, String date, Integer capacity) {
        return tableAvailabilityMongoRepository.findByRestaurantIdAndDateAndCapacity(restaurantId, date, capacity).orElseThrow(() -> new TableAvailabilityNotFoundException(restaurantId, date, capacity))
                .getTimeAvailabilityDocuments().stream().map(TableAvailabilityDTO::new).collect(Collectors.toList());
    }

    public void updateTableAvailability(TableCountUpdatePayload payload) {
        List<TableAvailabilityDocument> tableAvailabilityDocuments = tableAvailabilityMongoRepository.findAllByRestaurantIdAndCapacity(payload.getRestaurantId(), payload.getCommand().getCapacity());
        for(TableAvailabilityDocument tableAvailabilityDocument : tableAvailabilityDocuments) {
            for(TimeAvailabilityDocument timeAvailabilityDocument : tableAvailabilityDocument.getTimeAvailabilityDocuments()) {
                Integer usedCount = reservationMongoRepository.countAllByRestaurantIdAndDateAndTimeAndCapacity(payload.getRestaurantId(), tableAvailabilityDocument.getDate(), timeAvailabilityDocument.getTime(), payload.getCommand().getCapacity());
                timeAvailabilityDocument.updateRemainCount(payload.getCommand().getCount() - usedCount);
            }
        }
        tableAvailabilityMongoRepository.saveAll(tableAvailabilityDocuments);
    }

    public void createReservation(ReservationCreatePayload payload) {
        ReservationDocument reservationDocument = new ReservationDocument(payload);
        reservationMongoRepository.save(reservationDocument);

        TableAvailabilityDocument tableAvailabilityDocument = tableAvailabilityMongoRepository.findByRestaurantIdAndDateAndCapacity(payload.getCommand().getRestaurantId(), payload.getCommand().getDate(), payload.getCommand().getCapacity())
                .orElseThrow(() -> new TableAvailabilityNotFoundException(payload.getCommand().getRestaurantId(), payload.getCommand().getDate(), payload.getCommand().getCapacity()));
        TimeAvailabilityDocument timeAvailabilityDocument = tableAvailabilityDocument.getTimeAvailabilityDocuments().stream()
                .filter(timeAvailability -> timeAvailability.getTime().equals(payload.getCommand().getTime())).findFirst()
                .orElseThrow(() -> new TimeAvailabilityNotFoundException(payload.getCommand().getRestaurantId(), payload.getCommand().getDate(), payload.getCommand().getTime(), payload.getCommand().getCapacity()));
        timeAvailabilityDocument.decreaseRemainCount();
        tableAvailabilityMongoRepository.save(tableAvailabilityDocument);
    }

    public List<MyReservationListDTO> getMyReservations(User currentUser) {
        return reservationMongoRepository.findAllByUserId(currentUser.getId()).stream().map(MyReservationListDTO::new).collect(Collectors.toList());
    }

    public MyReservationDetailsDTO getMyReservation(Long reservationId, User currentUser) {
        ReservationDocument reservationDocument = reservationMongoRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException(reservationId));
        reservationValidator.validateReservationOwner(reservationDocument, currentUser.getId());
        return new MyReservationDetailsDTO(reservationDocument);
    }
}
