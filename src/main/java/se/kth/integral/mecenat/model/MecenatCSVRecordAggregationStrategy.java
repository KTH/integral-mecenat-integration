package se.kth.integral.mecenat.model;

import java.util.ArrayList;

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
            // TODO: Aggregera på smartare sätt för att undvika flera rader för samma person.
            newRecords.addAll(oldRecords);
        } catch (InvalidPayloadException e) {
            return oldExchange;
        }
        return newExchange;
    }
}
