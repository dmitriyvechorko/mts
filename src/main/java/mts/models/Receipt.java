package mts.models;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {
    private int id;
    private int conversationId;
    private LocalDate receiptDate;
    private int costId;
    private BigDecimal totalCost;
    private boolean paid;
}