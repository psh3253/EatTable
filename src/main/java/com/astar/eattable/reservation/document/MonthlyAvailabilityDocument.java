package com.astar.eattable.reservation.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Document(collection = "monthly_availability")
public class MonthlyAvailabilityDocument {
    @Id
    private String id;

    private Long restaurantId;

    private Integer year;

    private Integer month;

    private List<String> availableDates;

    public MonthlyAvailabilityDocument(Long restaurantId, Integer year, Integer month) {
        this.id = restaurantId + "-" + year + "-" + month;
        this.restaurantId = restaurantId;
        this.year = year;
        this.month = month;
        availableDates = new ArrayList<>();
    }

    public void addAvailableDate(String date) {
        availableDates.add(date);
    }
}
