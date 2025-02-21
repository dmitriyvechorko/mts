package mts.models;

import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private int id;
    private String fullName;
    private String address;
    private LocalDate registrationDate;
}
