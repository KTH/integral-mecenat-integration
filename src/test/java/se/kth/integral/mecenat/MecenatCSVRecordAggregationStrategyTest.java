package se.kth.integral.mecenat;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;

import se.kth.integral.mecenat.model.MecenatCSVRecord;
import se.kth.integral.mecenat.model.MecenatCSVRecordAggregationStrategy;
import se.kth.integral.mecenat.route.PeriodDatesProcessor;

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

public class MecenatCSVRecordAggregationStrategyTest {
    private final CamelContext context = new DefaultCamelContext();

    public final MecenatCSVRecord record1 = new MecenatCSVRecord();
    public final MecenatCSVRecord record2 = new MecenatCSVRecord();
    public final MecenatCSVRecord record3 = new MecenatCSVRecord();
    public final List<MecenatCSVRecord> list1 = new ArrayList<>();
    public final List<MecenatCSVRecord> list2 = new ArrayList<>();

    @Before
    public void initialize() {
        record1.setPersonnummer("19710321xyzu");
        record1.setStudieomfattning(new BigDecimal(25));
        record1.setStudieperiodStart(PeriodDatesProcessor.dateFromLadokDatum("2018-01-14"));
        record1.setStudieperiodSlut(PeriodDatesProcessor.dateFromLadokDatum("2018-06-26"));

        record2.setPersonnummer("19710321xyzu");
        record2.setStudieomfattning(new BigDecimal(50));
        record2.setStudieperiodStart(PeriodDatesProcessor.dateFromLadokDatum("2018-01-01"));
        record2.setStudieperiodSlut(PeriodDatesProcessor.dateFromLadokDatum("2018-06-30"));

        record3.setPersonnummer("19930321xyzu");
        record3.setStudieomfattning(new BigDecimal(100));
        record3.setStudieperiodStart(PeriodDatesProcessor.dateFromLadokDatum("2018-01-14"));
        record3.setStudieperiodSlut(PeriodDatesProcessor.dateFromLadokDatum("2018-06-26"));

        list1.add(record1);
        list2.add(record2);
        list2.add(record3);
    }

    @Test
    public void initialAggregationTest() {
        MecenatCSVRecordAggregationStrategy aggregationStrategy = new MecenatCSVRecordAggregationStrategy();
        Exchange exchange1 = new DefaultExchange(context);
        exchange1.getIn().setBody(list1);

        Exchange result = aggregationStrategy.aggregate(null, exchange1);
        assertEquals(exchange1, result);
    }

    @Test
    public void samePnrAggregationTest() throws InvalidPayloadException {
        MecenatCSVRecordAggregationStrategy aggregationStrategy = new MecenatCSVRecordAggregationStrategy();
        Exchange exchange1 = new DefaultExchange(context);
        exchange1.getIn().setBody(list1);

        Exchange exchange2 = new DefaultExchange(context);
        exchange2.getIn().setBody(list2);

        Exchange result1 = aggregationStrategy.aggregate(null, exchange1);
        Exchange result2 = aggregationStrategy.aggregate(result1, exchange2);

        @SuppressWarnings("unchecked")
        Collection<MecenatCSVRecord> records = (Collection<MecenatCSVRecord>) result2.getIn().getMandatoryBody();
        assertEquals(2, records.size());

        Iterator<MecenatCSVRecord> recordIterator = records.iterator();
        while (recordIterator.hasNext()) {
            MecenatCSVRecord record = recordIterator.next();

            if (record.getPersonnummer().equals("19710321xyzu")) {
                // Should be sum of studieomfattning.
                assertEquals(75.0, record.getStudieomfattning().longValue(), 0);
                // Should be first start date.
                assertEquals("2018-01-01", PeriodDatesProcessor.ladokDatumFromDate(record.getStudieperiodStart()));
                // Should be last end date.
                assertEquals("2018-06-30", PeriodDatesProcessor.ladokDatumFromDate(record.getStudieperiodSlut()));
            }

            if (record.getPersonnummer().equals("19930321xyzu")) {
                // Should be sum of studieomfattning.
                assertEquals(100.0, record.getStudieomfattning().longValue(), 0);
                // Should be first start date.
                assertEquals("2018-01-14", PeriodDatesProcessor.ladokDatumFromDate(record.getStudieperiodStart()));
                // Should be last end date.
                assertEquals("2018-06-26", PeriodDatesProcessor.ladokDatumFromDate(record.getStudieperiodSlut()));
            }
        }
    }
}
