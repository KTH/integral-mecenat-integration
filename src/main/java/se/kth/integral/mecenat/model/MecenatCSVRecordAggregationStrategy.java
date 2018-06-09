package se.kth.integral.mecenat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MecenatCSVRecordAggregationStrategy implements AggregationStrategy {
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

        oldRecords.stream().map(r -> mergeRecords(uniqueRecords, r)).collect(Collectors.toList());
        newRecords.stream().map(r -> mergeRecords(uniqueRecords, r)).collect(Collectors.toList());

        newExchange.getIn().setBody(uniqueRecords.values());;
        return newExchange;
    }

    private static MecenatCSVRecord mergeRecords(final Map<String, MecenatCSVRecord> records, final MecenatCSVRecord record) {
        MecenatCSVRecord oldRecord = records.get(record.getPersonnummer());

        if (oldRecord == null) {
            records.put(record.getPersonnummer(), record);
            return record;
        }

        oldRecord.setStudieomfattning(oldRecord.getStudieomfattning().add(record.getStudieomfattning()));
        return oldRecord;
    }

}
