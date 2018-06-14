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

import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.util.ExchangeHelper;

public class PeriodDatesProcessor implements Processor {
    private final static DateTimeFormatter LADOK_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void process(Exchange exchange) throws Exception {
        LocalDate today = LocalDate.parse(ExchangeHelper.getMandatoryHeader(exchange, "today", String.class), LADOK_DATE_FORMAT);
        LocalDate periodStartDate, periodEndDate;

        DateTimeFormatter terminFormatter;

        if (today.getMonthValue() >= Month.JANUARY.getValue() && today.getMonthValue() <= Month.JUNE.getValue()) {
            periodStartDate = today.minus(Period.ofYears(1)).withMonth(Month.DECEMBER.getValue()).withDayOfMonth(1);
            periodEndDate = today.withMonth(Month.MAY.getValue()).withDayOfMonth(31);
            terminFormatter = DateTimeFormatter.ofPattern("yyyy1");
        } else {
            periodStartDate = today.withMonth(Month.JUNE.getValue()).withDayOfMonth(1);
            periodEndDate = today.withMonth(Month.NOVEMBER.getValue()).withDayOfMonth(30);
            terminFormatter = DateTimeFormatter.ofPattern("yyyy2");
        }

        Message in = exchange.getIn();
        in.setHeader("periodStartDatum", periodStartDate.format(LADOK_DATE_FORMAT));
        in.setHeader("periodSlutDatum", periodEndDate.format(LADOK_DATE_FORMAT));
        in.setHeader("termin", today.format(terminFormatter));
    }
}
