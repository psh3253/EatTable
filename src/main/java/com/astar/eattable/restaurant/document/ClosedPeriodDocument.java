package com.astar.eattable.restaurant.document;

import com.astar.eattable.restaurant.payload.ClosedPeriodCreateEventPayload;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "close_period")
public class ClosedPeriodDocument {
    @Id
    private Long id;
    private Long restaurantId;
    private String startDate;
    private String endDate;
    private String reason;

    public ClosedPeriodDocument(ClosedPeriodCreateEventPayload payload) {
        this.id = payload.getClosedPeriodId();
        this.restaurantId = payload.getRestaurantId();
        this.startDate = payload.getCommand().getStartDate();
        this.endDate = payload.getCommand().getEndDate();
        this.reason = payload.getCommand().getReason();
    }
}
