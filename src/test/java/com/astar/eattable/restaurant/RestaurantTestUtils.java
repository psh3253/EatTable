package com.astar.eattable.restaurant;

import com.astar.eattable.restaurant.command.BusinessHoursCommand;
import com.astar.eattable.restaurant.command.BusinessHoursUpdateCommand;
import com.astar.eattable.restaurant.command.RestaurantCreateCommand;

import java.util.List;

public class RestaurantTestUtils {

    public static RestaurantCreateCommand getRestaurantCreateCommand() {
        List<BusinessHoursCommand> businessHoursCommands = List.of(
                new BusinessHoursCommand("MONDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("TUESDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("WEDNESDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("THURSDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("FRIDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("SATURDAY", "09:00", "21:00", "12:00", "13:00", "20:00"),
                new BusinessHoursCommand("SUNDAY", "09:00", "21:00", "12:00", "13:00", "20:00")
        );
        return new RestaurantCreateCommand("테스트 식당", "맛있는 식당", "서울시 강남구", "02-1234-5678", "이미지 URL", "한식", businessHoursCommands, 37.123456, 127.123456);
    }

    public static BusinessHoursUpdateCommand getBusinessHoursUpdateCommand() {
        List<BusinessHoursCommand> businessHoursCommands = List.of(
                new BusinessHoursCommand("MONDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("TUESDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("WEDNESDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("THURSDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("FRIDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("SATURDAY", "10:00", "22:00", "13:00", "14:00", "21:00"),
                new BusinessHoursCommand("SUNDAY", "10:00", "22:00", "13:00", "14:00", "21:00")
        );
        return new BusinessHoursUpdateCommand(businessHoursCommands);
    }
}
