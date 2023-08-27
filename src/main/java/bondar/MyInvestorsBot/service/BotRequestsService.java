package bondar.MyInvestorsBot.service;

import bondar.MyInvestorsBot.model.Currency;
import bondar.MyInvestorsBot.model.Securities;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Scanner;

@Service
public class BotRequestsService {
    /*public static String getCurrencyRate(String message, Currency model) throws IOException, ParseException {
        URL url = new URL("https://api.coingate.com/v2/rates/merchant/" + message + "/RUB");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }

        model.setDate(LocalDateTime.now());
        model.setCur_Abbreviation(message);
        model.setCur_OfficialRate(Double.valueOf(result.toString()));

        return "Official rate of BYN to " + model.getCur_Abbreviation() + "\n" +
                "on the date: " + model.getDate() + "\n" +
                "is: " + model.getCur_OfficialRate() + " " + model.getCur_Abbreviation();
    }*/

    public static String getSecuritiesRate(String message, Securities model) throws IOException, ParseException {
        URL url = new URL("https://api.twelvedata.com/price?symbol=" + message + "&apikey=c8ce5abaed634b90bd5d0dcf7d1c625c");
        Scanner scanner = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (scanner.hasNext()) {
            result.append(scanner.nextLine());
        }

        JSONObject object = new JSONObject(result.toString());

        model.setDate(LocalDateTime.now());
        model.setTicker(message);
        model.setPrice(object.getDouble("price"));

        return "Рыночная стоимость  " + model.getTicker() + "\n" +
                "на дату: " + model.getDate() + "\n" +
                "составляет: " + model.getPrice();
    }
}
