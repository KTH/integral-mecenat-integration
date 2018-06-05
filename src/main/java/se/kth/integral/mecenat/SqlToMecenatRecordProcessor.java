package se.kth.integral.mecenat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.util.ExchangeHelper;

public class SqlToMecenatRecordProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> sqlResult = (Map<String, Object>) exchange.getIn().getMandatoryBody();

        MecenatCSVRecord mecenatRecord = new MecenatCSVRecord();
        mecenatRecord.setPersonnummer((String) sqlResult.get("personnummer"));
        mecenatRecord.setConamn((String) sqlResult.get("COaddress"));
        mecenatRecord.setEfternamn((String) sqlResult.get("efternamn"));
        mecenatRecord.setEpost((String) sqlResult.get("epostadress"));
        mecenatRecord.setFornamn((String) sqlResult.get("fornamn"));
        mecenatRecord.setGatuadress((String) sqlResult.get("gatuadress"));

        String land = (String) sqlResult.get("land");
        if (! (land.equalsIgnoreCase("SVERIGE") || land.equalsIgnoreCase("SE"))) {
            mecenatRecord.setLand(land);
        }

        mecenatRecord.setOrt((String) sqlResult.get("postort"));

        mecenatRecord.setStudieomfattning((BigDecimal) sqlResult.get("REGISTRERING_OMFATTNING_PROCENT"));
        mecenatRecord.setTermin(ExchangeHelper.getMandatoryHeader(exchange, "termin", String.class));
        mecenatRecord.setStudieperiodStart((Date) sqlResult.get("STUDIEPERIOD_STARTDATUM"));
        mecenatRecord.setStudieperiodSlut((Date) sqlResult.get("STUDIEPERIOD_SLUTDATUM"));

        exchange.getIn().setBody(mecenatRecord);
    }
}
