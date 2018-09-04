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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import se.kth.integral.mecenat.model.MecenatCSVRecord;
import se.kth.integral.mecenat.route.PeriodDatesProcessor;

public class FakeResarchstudentSqlProcessor implements Processor {

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
        row.put("OMFATTNING_PROCENT", new BigDecimal(75.33));
        row.put("STARTDATUM", PeriodDatesProcessor.dateFromLadokDatum("2018-01-01"));
        row.put("SLUTDATUM", PeriodDatesProcessor.dateFromLadokDatum("2018-06-30"));

        row.put("student_uid", MecenatCSVRecord.guidStringToByteArray("801cc908-5c18-11e7-82f8-4a99985b4246"));

        results.add(row);
        exchange.getIn().setBody(results);
        exchange.getIn().setHeader("CamelSqlRowCount", results.size());
    }
}
