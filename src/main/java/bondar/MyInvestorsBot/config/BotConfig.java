package bondar.MyInvestorsBot.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
@RequiredArgsConstructor
@Setter
@Getter
public class BotConfig {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.key}")
    String key;
}
