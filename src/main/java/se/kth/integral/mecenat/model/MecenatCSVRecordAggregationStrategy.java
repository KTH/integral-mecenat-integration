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
package se.kth.integral.mecenat.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * AggregationStrategy som aggregerar MecenatCSVRecord.
 *
 * Arbetshypotesen är att summera ihop studieomfattningar för en person
 * som förekommer flera gånger, samt välja det tidigaste startdatumet och det
 * senaste slutdatumet.
 */
public class MecenatCSVRecordAggregationStrategy implements AggregationStrategy {
    final Logger log = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        ArrayList<MecenatCSVRecord> oldRecords = oldExchange.getIn().getBody(ArrayList.class);
        ArrayList<MecenatCSVRecord> newRecords = newExchange.getIn().getBody(ArrayList.class);

        if (oldRecords == null) {
            oldRecords = new ArrayList<>();
        }
        if (newRecords == null) {
            newRecords = new ArrayList<>();
        }

        final Map<String, MecenatCSVRecord> uniqueRecords = new HashMap<>();

        long count = oldRecords.stream().map(r -> mergeRecords(uniqueRecords, r)).count();
        count += newRecords.stream().map(r -> mergeRecords(uniqueRecords, r)).count();

        log.info("Aggregerade {} rader till {} unika rader", count, uniqueRecords.size());

        oldExchange.getIn().setBody(uniqueRecords.values());
        return oldExchange;
    }

    private static MecenatCSVRecord mergeRecords(final Map<String, MecenatCSVRecord> records, final MecenatCSVRecord mecenatCSVRecord) {
        MecenatCSVRecord oldRecord = records.get(mecenatCSVRecord.getPersonnummer());

        if (oldRecord == null) {
            records.put(mecenatCSVRecord.getPersonnummer(), mecenatCSVRecord);
            return mecenatCSVRecord;
        }

        oldRecord.setStudieomfattning(oldRecord.getStudieomfattning().add(mecenatCSVRecord.getStudieomfattning()));

        Date studieperiodStart = mecenatCSVRecord.getStudieperiodStart();
        Date oldStudieperiodStart = oldRecord.getStudieperiodStart();
        if (studieperiodStart.before(oldStudieperiodStart)) {
            oldRecord.setStudieperiodStart(studieperiodStart);
        }

        Date studieperiodSlut = mecenatCSVRecord.getStudieperiodSlut();
        Date oldStudieperiodSlut = oldRecord.getStudieperiodSlut();
        if (studieperiodSlut.after(oldStudieperiodSlut)) {
            oldRecord.setStudieperiodSlut(studieperiodSlut);
        }

        return oldRecord;
    }
}
