package se.kth.integral.mecenat;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import se.kth.integral.mecenat.model.MecenatCSVRecord;
import se.kth.integral.mecenat.route.SqlToMecenatCSVRecordProcessor;

public class SqlToMecenatCSVRecordProcessorTest {
    private CamelContext context = new DefaultCamelContext();
    private Exchange exchange = new DefaultExchange(context);
    private Processor sqlToMecenatCSVRecordProcessor = new SqlToMecenatCSVRecordProcessor();
    private BindyCsvDataFormat mecenatCsvFormat = new BindyCsvDataFormat(se.kth.integral.mecenat.model.MecenatCSVRecord.class);

    @Before
    public void initialize() {
        mecenatCsvFormat.setLocale("sv_SE");
    }

    @Test
    public void testCase() throws Exception {
        Map<String, Object> sqlResult = new HashMap<String, Object>();
        sqlResult.put("fornamn", "Ture");
        sqlResult.put("efternamn", "Teknolog");
        sqlResult.put("personnummer", "19710321xyzu");
        sqlResult.put("land", "SVERIGE");
        sqlResult.put("utdelningsadress", "Forskarbacken 21");
        sqlResult.put("postort", "Stockholm");
        sqlResult.put("postnummer", "11614");
        sqlResult.put("OMFATTNING_PROCENT", new BigDecimal(75.33));
        sqlResult.put("STARTDATUM", Date.from(LocalDate.parse("2018-01-14").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        sqlResult.put("SLUTDATUM", Date.from(LocalDate.parse("2018-06-26").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        exchange.getIn().setHeader("termin", "20181");
        exchange.getIn().setBody(sqlResult);

        sqlToMecenatCSVRecordProcessor.process(exchange);

        assertTrue(exchange.getIn().getBody() instanceof MecenatCSVRecord);

        OutputStream outputStream = new ByteArrayOutputStream();
        mecenatCsvFormat.marshal(exchange, exchange.getIn().getBody(), outputStream);

        assertEquals("19710321xyzu;Teknolog;Ture;;Forskarbacken 21;11614;Stockholm;;;;;;;;0;75;0;0;2018-01-14;2018-06-26;20181;;\r\n",
                outputStream.toString());
    }

    @Test
    public void testSe() throws Exception {
        Map<String, Object> sqlResult = new HashMap<String, Object>();
        sqlResult.put("fornamn", "Ture");
        sqlResult.put("efternamn", "Teknolog");
        sqlResult.put("personnummer", "19710321xyzu");
        sqlResult.put("land", "SE");
        sqlResult.put("utdelningsadress", "Forskarbacken 21");
        sqlResult.put("postort", "Stockholm");
        sqlResult.put("postnummer", "11614");
        sqlResult.put("OMFATTNING_PROCENT", new BigDecimal(75.33));
        sqlResult.put("STARTDATUM", Date.from(LocalDate.parse("2018-01-14").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        sqlResult.put("SLUTDATUM", Date.from(LocalDate.parse("2018-06-26").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        exchange.getIn().setHeader("termin", "20181");
        exchange.getIn().setBody(sqlResult);

        sqlToMecenatCSVRecordProcessor.process(exchange);

        assertTrue(exchange.getIn().getBody() instanceof MecenatCSVRecord);

        OutputStream outputStream = new ByteArrayOutputStream();
        mecenatCsvFormat.marshal(exchange, exchange.getIn().getBody(), outputStream);

        assertEquals("19710321xyzu;Teknolog;Ture;;Forskarbacken 21;11614;Stockholm;;;;;;;;0;75;0;0;2018-01-14;2018-06-26;20181;;\r\n",
                outputStream.toString());
    }

    @Test
    public void testAnnatLand() throws Exception {
        Map<String, Object> sqlResult = new HashMap<String, Object>();
        sqlResult.put("fornamn", "Ture");
        sqlResult.put("efternamn", "Teknolog");
        sqlResult.put("personnummer", "19710321xyzu");
        sqlResult.put("land", "Frankrike");
        sqlResult.put("utdelningsadress", "Forskarbacken 21");
        sqlResult.put("postort", "Stockholm");
        sqlResult.put("postnummer", "11614");
        sqlResult.put("OMFATTNING_PROCENT", new BigDecimal(75.33));
        sqlResult.put("STARTDATUM", Date.from(LocalDate.parse("2018-01-14").atStartOfDay(ZoneId.systemDefault()).toInstant()));
        sqlResult.put("SLUTDATUM", Date.from(LocalDate.parse("2018-06-26").atStartOfDay(ZoneId.systemDefault()).toInstant()));

        exchange.getIn().setHeader("termin", "20181");
        exchange.getIn().setBody(sqlResult);

        sqlToMecenatCSVRecordProcessor.process(exchange);

        assertTrue(exchange.getIn().getBody() instanceof MecenatCSVRecord);

        OutputStream outputStream = new ByteArrayOutputStream();
        mecenatCsvFormat.marshal(exchange, exchange.getIn().getBody(), outputStream);

        assertEquals("19710321xyzu;Teknolog;Ture;;Forskarbacken 21;11614;Stockholm;Frankrike;;;;;;;0;75;0;0;2018-01-14;2018-06-26;20181;;\r\n",
                outputStream.toString());
    }
}
