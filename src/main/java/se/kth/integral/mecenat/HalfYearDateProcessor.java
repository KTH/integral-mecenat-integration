package se.kth.integral.mecenat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class HalfYearDateProcessor implements Processor {
    private SimpleDateFormat LADOK_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();

    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();

        List<Map<String, Object>> result = (List<Map<String, Object>>) in.getBody();

        if (result.size() != 1) {
            throw new CamelExchangeException("Illegal number of rows in result, should be one.", exchange);
        }

        Date startDate = (Date) result.get(0).get("STARTDATUM");
        calendar.setTime(startDate);
        in.setHeader("halvarStartDatum", LADOK_DATE_FORMAT.format(calendar.getTime()));

        Date endDate = (Date) result.get(0).get("SLUTDATUM");
        calendar.setTime(endDate);
        in.setHeader("halvarSlutDatum", LADOK_DATE_FORMAT.format(calendar.getTime()));

        in.setHeader("halvarText", ((String) result.get(0).get("PERIOD_SV")).trim());
    }
}
