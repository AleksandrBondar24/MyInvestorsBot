package bondar.MyInvestorsBot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Securities {
    private Double price;
    private LocalDateTime date;
    private String ticker;
}
