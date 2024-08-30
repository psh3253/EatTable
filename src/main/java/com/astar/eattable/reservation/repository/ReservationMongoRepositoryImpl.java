package com.astar.eattable.reservation.repository;

import com.astar.eattable.reservation.document.ReservationDocument;
import com.astar.eattable.reservation.dto.ReservationSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.astar.eattable.reservation.document.QReservationDocument.reservationDocument;

@RequiredArgsConstructor
@Repository
public class ReservationMongoRepositoryImpl implements ReservationMongoRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ReservationDocument> searchByWhere(ReservationSearchCondition condition) {
        SpringDataMongodbQuery<ReservationDocument> query = new SpringDataMongodbQuery<>(mongoTemplate, ReservationDocument.class);
        query.where(restaurantIdEq(condition.getRestaurantId()))
                .where(userIdEq(condition.getUserId()))
                .where(dateEq(condition.getStartDate(), condition.getEndDate()));
        return query.fetch();
    }

    private BooleanExpression restaurantIdEq(Long restaurantId) {
        return restaurantId != null ? reservationDocument.restaurantId.eq(restaurantId) : null;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? reservationDocument.userId.eq(userId) : null;
    }

    private BooleanExpression dateEq(String startDate, String endDate) {
        return startDate != null && endDate != null ? reservationDocument.date.between(startDate, endDate) : null;
    }
}
