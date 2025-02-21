package mts;

import java.time.LocalTime;
import java.time.Duration;

public class CallReceiptCalculator {

    public static double calculateTotalCost(LocalTime startTime, Duration callDuration, double regularCostPerMinute, double discountedCostPerMinute) {
        double totalCost = 0.0;

        // Проверяем, попадает ли время начала разговора в льготный период (с 20:00 до 6:00)
        LocalTime discountStartTime = LocalTime.of(20, 0);
        LocalTime discountEndTime = LocalTime.of(6, 0);

        // Определяем, попадает ли звонок в льготное время
        if (startTime.isAfter(discountStartTime) || startTime.isBefore(discountEndTime)) {
            // Применяем льготную стоимость
            totalCost = callDuration.toMinutes() * discountedCostPerMinute;
        } else {
            // Применяем обычную стоимость
            totalCost = callDuration.toMinutes() * regularCostPerMinute;
        }

        return totalCost;
    }
}
