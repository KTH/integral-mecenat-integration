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

        try {
            ArrayList<MecenatCSVRecord> oldRecords = oldExchange.getIn().getMandatoryBody(ArrayList.class);
            ArrayList<MecenatCSVRecord> newRecords = newExchange.getIn().getMandatoryBody(ArrayList.class);
            newRecords.addAll(oldRecords);
        } catch (InvalidPayloadException e) {
        }
        return newExchange;
    }
}
