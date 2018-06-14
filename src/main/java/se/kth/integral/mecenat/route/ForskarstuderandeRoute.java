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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

import se.kth.integral.mecenat.model.MecenatCSVRecord;

/**
 * Sub route which fetches information about research students from the database
 * as a list of MecenatCSVRecord:s.
 */
@Component
public class ForskarstuderandeRoute extends RouteBuilder {
    @Override
    public void configure() {
        from("{{endpoint.forskarstuderanderoute}}")
            .routeId("se.kth.integral.mecenat.forskar_studerande")

            .log("Hämtar studieaktivitet för forskarstuderande.")
            .to("{{endpoint.forskarstuderande.sql}}")

            .log(LoggingLevel.DEBUG, "Transformerar data till CSV.")
            .split(body())
                .process(new SqlToMecenatCSVRecordProcessor())
            .aggregate(AggregationStrategies.flexible(MecenatCSVRecord.class)
                .accumulateInCollection(ArrayList.class)
                .pick(simple("${body}")))
                .constant(true).completionSize(simple("${header.CamelSqlRowCount}"))
            .to("{{endpoint.mecenattransferroute}}");
    }
}
