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
public class Currency {
    private LocalDateTime date;
    private String cur_Abbreviation;
    private Double cur_OfficialRate;
}
