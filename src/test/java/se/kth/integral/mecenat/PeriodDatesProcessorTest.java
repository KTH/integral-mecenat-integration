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

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;

import se.kth.integral.mecenat.route.PeriodDatesProcessor;

public class PeriodDatesProcessorTest {
    private CamelContext context = new DefaultCamelContext();
    private Exchange exchange = new DefaultExchange(context);
    private Processor dateProcessor = new PeriodDatesProcessor();

    @Test
    public void startOfSpringTermTest() throws Exception {
        exchange.getIn().setHeader("today", "2018-01-01");
        dateProcessor.process(exchange);
        assertEquals("20181", exchange.getIn().getHeader("termin", String.class));
        assertEquals("2017-12-01", exchange.getIn().getHeader("periodStartDatum", String.class));
        assertEquals("2018-05-31", exchange.getIn().getHeader("periodSlutDatum", String.class));
    }

    @Test
    public void endOfSringTermTest() throws Exception {
        exchange.getIn().setHeader("today", "2018-06-30");
        dateProcessor.process(exchange);
        assertEquals("20181", exchange.getIn().getHeader("termin", String.class));
        assertEquals("2017-12-01", exchange.getIn().getHeader("periodStartDatum", String.class));
        assertEquals("2018-05-31", exchange.getIn().getHeader("periodSlutDatum", String.class));
    }

    @Test
    public void startOfFallTermTest() throws Exception {
        exchange.getIn().setHeader("today", "2018-07-01");
        dateProcessor.process(exchange);
        assertEquals("20182", exchange.getIn().getHeader("termin", String.class));
        assertEquals("2018-06-01", exchange.getIn().getHeader("periodStartDatum", String.class));
        assertEquals("2018-11-30", exchange.getIn().getHeader("periodSlutDatum", String.class));
    }

    @Test
    public void endOfFallTermTest() throws Exception {
        exchange.getIn().setHeader("today", "2018-12-31");
        dateProcessor.process(exchange);
        assertEquals("20182", exchange.getIn().getHeader("termin", String.class));
        assertEquals("2018-06-01", exchange.getIn().getHeader("periodStartDatum", String.class));
        assertEquals("2018-11-30", exchange.getIn().getHeader("periodSlutDatum", String.class));
    }
}
