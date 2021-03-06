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

import java.util.ArrayList;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;
import se.kth.integral.mecenat.model.MecenatCSVRecord;

/**
 * Sub route which fetches information about student participation from the database
 * as a list of MecenatCSVRecord:s.
 */
@Component
public class StuderandeRoute extends RouteBuilder {
    @Value("${redelivery.retries}")
    private int maxRetries;

    @Value("${redelivery.delay}")
    private int delay;

    @Value("${redelivery.maxdelay}")
    private int maxDelay;

    @Override
    public void configure() {
        errorHandler(defaultErrorHandler()
                .maximumRedeliveries(maxRetries)
                .redeliveryDelay(delay)
                .maximumRedeliveryDelay(maxDelay)
                .useExponentialBackOff()
                .retryAttemptedLogLevel(LoggingLevel.WARN));

        onException(CannotGetJdbcConnectionException.class)
            .maximumRedeliveries(-1)
            .redeliveryDelay(delay)
            .maximumRedeliveryDelay(maxDelay)
            .useExponentialBackOff()
            .retryAttemptedLogLevel(LoggingLevel.WARN);

        from("{{endpoint.studeranderoute}}")
            .routeId("se.kth.integral.mecenat.forvantade_deltagare")

            .log("Hämtar förväntat deltagande för studerande med någon registrering.")
            .to("{{endpoint.studerande.sql}}")

            .log(LoggingLevel.DEBUG, "Hittade ${header.CamelSqlRowCount} förväntade deltaganden.")

            .filter().simple("${header.CamelSqlRowCount} == 0")
                .to("{{endpoint.mecenattransferroute}}")
                .end()

            .log(LoggingLevel.DEBUG, "Transformerar förväntade deltaganden till CSV.")

            .split(body())
                .process(new SqlToMecenatCSVRecordProcessor())
            .aggregate(AggregationStrategies.flexible(MecenatCSVRecord.class)
                .accumulateInCollection(ArrayList.class)
                .pick(simple("${body}")))
                .constant(true).completionSize(simple("${header.CamelSqlRowCount}"))
            .to("{{endpoint.mecenattransferroute}}");
    }
}
