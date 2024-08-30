package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.ReservationDocument;
import com.astar.eattable.reservation.dto.ReservationSearchCondition;

import java.util.List;

public interface ReservationMongoRepositoryCustom {
    List<ReservationDocument> searchByWhere(ReservationSearchCondition condition);
}
