package se.kth.integral.mecenat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

public class TermStartDateProcessor implements Processor {
    private SimpleDateFormat ladokDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @SuppressWarnings("unchecked")
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();

        List<Map<String, Object>> result = (List<Map<String, Object>>) in.getBody();

        if (result.size() != 1) {
            throw new CamelExchangeException("Illegal number of rows in result, should be one.", exchange);
        }

        String periodKod = ((String) result.get(0).get("PERIOD_KOD")).trim();

        in.setHeader("termin", TermStartDateProcessor.ladok2Termin(periodKod));
        Date startDate = (Date) result.get(0).get("STARTDATUM");
        in.setHeader("terminStartDatum", ladokDateFormat.format(startDate));

        in.setHeader("terminText", ((String) result.get(0).get("PERIOD_SV")).trim());
    }

    public static String ladok2Termin(String ladok3Termin) throws IllegalArgumentException {
        if (ladok3Termin.startsWith("VT")) {
            return ladok3Termin.substring(2) + '1';
        } else if (ladok3Termin.startsWith("HT")) {
            return ladok3Termin.substring(2) + '2';
        } else {
            throw new IllegalArgumentException("Unable to interpret string: " + ladok3Termin + " as VT/HT");
        }
    }
}