package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Integer countByTableAvailabilityId(Long id);

    List<Reservation> findAllByUserId(Long userId);
}
