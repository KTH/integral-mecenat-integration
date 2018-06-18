package se.kth.integral.mecenat.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.processor.aggregate.AggregationStrategy;
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

        ArrayList<MecenatCSVRecord> oldRecords, newRecords;

        try {
            oldRecords = oldExchange.getIn().getMandatoryBody(ArrayList.class);
        } catch (InvalidPayloadException e) {
            return newExchange;
        }

        try {
            newRecords = newExchange.getIn().getMandatoryBody(ArrayList.class);
        } catch (InvalidPayloadException e) {
            return oldExchange;
        }

        final Map<String, MecenatCSVRecord> uniqueRecords = new HashMap<String, MecenatCSVRecord>();

        Long count = oldRecords.stream().map(r -> mergeRecords(uniqueRecords, r)).collect(Collectors.counting());
        count += newRecords.stream().map(r -> mergeRecords(uniqueRecords, r)).collect(Collectors.counting());

        log.debug("Aggregerade {} rader till {} unika rader", count, uniqueRecords.size());

        oldExchange.getIn().setBody(uniqueRecords.values());;
        return oldExchange;
    }

    private static MecenatCSVRecord mergeRecords(final Map<String, MecenatCSVRecord> records, final MecenatCSVRecord record) {
        MecenatCSVRecord oldRecord = records.get(record.getPersonnummer());

        if (oldRecord == null) {
            records.put(record.getPersonnummer(), record);
            return record;
        }

        oldRecord.setStudieomfattning(oldRecord.getStudieomfattning().add(record.getStudieomfattning()));

        Date studieperiodStart = record.getStudieperiodStart();
        Date oldStudieperiodStart = oldRecord.getStudieperiodStart();
        if (studieperiodStart.before(oldStudieperiodStart)) {
            oldRecord.setStudieperiodStart(studieperiodStart);
        }

        Date studieperiodSlut = record.getStudieperiodSlut();
        Date oldStudieperiodSlut = oldRecord.getStudieperiodSlut();
        if (studieperiodSlut.after(oldStudieperiodSlut)) {
            oldRecord.setStudieperiodSlut(studieperiodSlut);
        }

        return oldRecord;
    }
}
