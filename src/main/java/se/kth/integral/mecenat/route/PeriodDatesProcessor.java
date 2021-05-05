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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.ExchangeHelper;

/**
 * Sätter headrar termin, periodStartDatum och periodSlutDatum med utgångspunkt från
 * dagens datum.
 *
 * Se "Valda perioder" i doc/Designval.md för detaljer.
 */
public class PeriodDatesProcessor implements Processor {
    public static final DateTimeFormatter LADOK_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void process(Exchange exchange) throws Exception {
        LocalDate today = LocalDate.parse(ExchangeHelper.getMandatoryHeader(exchange, "today", String.class), LADOK_DATE_FORMAT);
        LocalDate periodStartDate;
        LocalDate periodEndDate;

        String term = term(today);

        if (isSpringTerm(today)) {
            periodStartDate = today.minus(Period.ofYears(1)).withMonth(Month.DECEMBER.getValue()).withDayOfMonth(1);
            periodEndDate = today.withMonth(Month.JUNE.getValue()).withDayOfMonth(30);
        } else {
            periodStartDate = today.withMonth(Month.JULY.getValue()).withDayOfMonth(1);
            periodEndDate = today.withMonth(Month.NOVEMBER.getValue()).withDayOfMonth(30);
        }

        Message in = exchange.getIn();
        in.setHeader("periodStartDatum", periodStartDate.format(LADOK_DATE_FORMAT));
        in.setHeader("periodSlutDatum", periodEndDate.format(LADOK_DATE_FORMAT));
        in.setHeader("termin", term);
    }

    private static boolean isSpringTerm(LocalDate today) {
        return today.getMonthValue() >= Month.JANUARY.getValue() && today.getMonthValue() <= Month.JUNE.getValue();
    }

    public static String term(LocalDate today) {
        if (isSpringTerm(today)) {
            return DateTimeFormatter.ofPattern("yyyy1").format(today);
        } else {
            return DateTimeFormatter.ofPattern("yyyy2").format(today);
        }
    }

    public static Date dateFromLadokDatum(String datum) {
        return Date.from(LocalDate.parse(datum, PeriodDatesProcessor.LADOK_DATE_FORMAT)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());
    }

    public static String ladokDatumFromDate(Date date) {
        return localDateFromDate(date).format(LADOK_DATE_FORMAT);
    }

    public static LocalDate localDateFromDate(Date datum) {
        return datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
