package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Integer countByTableAvailabilityIdAndCanceledFalse(Long tableAvailabilityId);

    List<Reservation> findAllByUserId(Long userId);
}
