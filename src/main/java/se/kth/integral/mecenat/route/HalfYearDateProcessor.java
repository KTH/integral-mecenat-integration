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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Sub route which fetches start and end dates for half years from the database
 * and add to the headers.
 */
public class HalfYearDateProcessor implements Processor {
    private final static SimpleDateFormat LADOK_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final Calendar calendar = Calendar.getInstance();

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
