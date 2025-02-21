package mts.models;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cost {
    private int id;
    private LocalDate date;
    private String settlementName;
    private BigDecimal costPerMin;
    private BigDecimal preferentialCost;
}