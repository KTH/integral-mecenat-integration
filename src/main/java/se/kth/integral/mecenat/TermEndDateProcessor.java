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

public class TermEndDateProcessor implements Processor {
    private SimpleDateFormat LADOK_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar calendar = Calendar.getInstance();

    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();

        List<Map<String, Object>> result = (List<Map<String, Object>>) in.getBody();

        if (result.size() != 1) {
            throw new CamelExchangeException("Illegal number of rows in result, should be one.", exchange);
        }

        Date endDate = (Date) result.get(0).get("STARTDATUM");
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        in.setHeader("terminSlutDatum", LADOK_DATE_FORMAT.format(calendar.getTime()));
    }
}
