package se.kth.integral.mecenat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class FakeStudentSqlProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        Map<String, Object> row = new HashMap<String, Object>();
        row.put("fornamn", "Ture");
        row.put("efternamn", "Teknolog");
        row.put("personnummer", "19710321xyzu");
        row.put("land", "SVERIGE");
        row.put("utdelningsadress", "Forskarbacken 21");
        row.put("postort", "Stockholm");
        row.put("postnummer", "11614");
        row.put("OMFATTNING_PROCENT", new BigDecimal(50.33));
        row.put("STARTDATUM", Date.from(LocalDate.parse("2018-01-14").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        row.put("SLUTDATUM", Date.from(LocalDate.parse("2018-06-26").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        results.add(row);
        exchange.getIn().setBody(results);
        exchange.getIn().setHeader("CamelSqlRowCount", results.size());
    }
}
