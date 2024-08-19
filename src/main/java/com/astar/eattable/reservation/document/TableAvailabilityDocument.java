package com.astar.eattable.reservation.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "table_availability")
public class TableAvailabilityDocument {
    @Id
    private String id;

    private Long restaurantId;

    private String date;

    private Integer capacity;

    private List<TimeAvailabilityDocument> timeAvailabilityDocuments;

    public TableAvailabilityDocument(Long restaurantId, String date, Integer capacity, List<TimeAvailabilityDocument> timeAvailabilityDocuments) {
        this.id = restaurantId + "-" + date + "-" + capacity;
        this.restaurantId = restaurantId;
        this.date = date;
        this.capacity = capacity;
        this.timeAvailabilityDocuments = timeAvailabilityDocuments;
    }
}
