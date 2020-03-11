/*
 * MIT License
 *
 * Copyright (c) 2018 Kungliga Tekniska högskolan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package se.kth.integral.mecenat.route;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.ExchangeHelper;
import se.kth.integral.mecenat.model.MecenatCSVRecord;

public class SqlToMecenatCSVRecordProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Object> sqlResult = (Map<String, Object>) exchange.getIn().getMandatoryBody();

        MecenatCSVRecord mecenatRecord = new MecenatCSVRecord();
        mecenatRecord.setPersonnummer((String) sqlResult.get("personnummer"));
        mecenatRecord.setConamn((String) sqlResult.get("careof"));
        mecenatRecord.setEfternamn((String) sqlResult.get("efternamn"));
        mecenatRecord.setEpost((String) sqlResult.get("epostadress"));
        mecenatRecord.setFornamn((String) sqlResult.get("fornamn"));
        mecenatRecord.setGatuadress((String) sqlResult.get("utdelningsadress"));
        mecenatRecord.setLand((String) sqlResult.get("land"));
        mecenatRecord.setOrt((String) sqlResult.get("postort"));
        mecenatRecord.setPostnummer((String) sqlResult.get("postnummer"));
        mecenatRecord.setStudieomfattning((BigDecimal) sqlResult.get("OMFATTNING_PROCENT"));
        mecenatRecord.setTermin(ExchangeHelper.getMandatoryHeader(exchange, "termin", String.class));
        mecenatRecord.setStudieperiodStart((Date) sqlResult.get("STARTDATUM"));
        mecenatRecord.setStudieperiodSlut((Date) sqlResult.get("SLUTDATUM"));

        exchange.getIn().setBody(mecenatRecord);
    }
}
