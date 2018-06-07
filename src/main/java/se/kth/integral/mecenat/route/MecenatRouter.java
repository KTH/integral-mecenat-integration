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
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.util.toolbox.AggregationStrategies;
import org.springframework.stereotype.Component;

import se.kth.integral.mecenat.model.MecenatCSVRecord;

/**
 * Camel route to run sql queries against ladok3 database on a schedule.
 */
@Component
public class MecenatRouter extends RouteBuilder {
    private Processor termStartDateProcessor = new TermStartDateProcessor();
    private Processor termEndDateProcessor = new TermEndDateProcessor();
    private Processor halfYearDateProcessor = new HalfYearDateProcessor();

    @Override
    public void configure() {
        System.setProperty("user.timezone", "Europe/Stockholm");

        BindyCsvDataFormat mecenatCsvFormat = new BindyCsvDataFormat(se.kth.integral.mecenat.model.MecenatCSVRecord.class);
        mecenatCsvFormat.setLocale("sv_SE");

        from("quartz://mecenat?cron={{ladok3.cron}}&trigger.timeZone=Europe/Stockholm")
            .routeId("se.kth.integral.mecenat")

            .errorHandler(
                    defaultErrorHandler()
                    .redeliveryDelay(1000)
                    .maximumRedeliveries(6)
                    .retryAttemptedLogLevel(LoggingLevel.WARN))

            .log("Påbörjar Mecenat filexport.")
            .setHeader("today").simple("${date:now:yyyy-MM-dd}")

            .log(LoggingLevel.DEBUG, "Hämtar termin, start- och slutdatum från Ladok3.")
            .to("sql:classpath:sql/nuvarande_termin.sql?dataSource=uppfoljningsDB")
            .process(termStartDateProcessor)

            .to("sql:classpath:sql/nasta_termin.sql?dataSource=uppfoljningsDB")
            .process(termEndDateProcessor)

            .log(LoggingLevel.DEBUG, "Hämtar halvår, start- och slutdatum från Ladok3.")
            .to("sql:classpath:sql/nuvarande_halvar.sql?dataSource=uppfoljningsDB")
            .process(halfYearDateProcessor)

            // TODO: reda ut exakta frågor, eventuellt aggregera flera frågor.
            .log("Hämtar registreringar för ${header.terminText} ${header.terminStartDatum}:${header.terminSlutDatum}.")
            .to("sql:classpath:sql/antagningar-ny.sql?dataSource=uppfoljningsDB")

            .log(LoggingLevel.DEBUG, "Transformerar data till CSV.")
            .split(body())
                .process(new SqlToMecenatRecordProcessor())
            .aggregate(AggregationStrategies.flexible(MecenatCSVRecord.class)
                .accumulateInCollection(ArrayList.class)
                .pick(simple("${body}")))
                .constant(true).completionSize(simple("${header.CamelSqlRowCount}"))
            .marshal(mecenatCsvFormat)

            // TODO: var ska vi stoppa filen? Vad ska den heta?
            .log(LoggingLevel.DEBUG, "Skriver exportfil.")
            .to("file://{{ladok3.output.dir}}?fileName=mecenat-${date:now:yyyy-MM-dd-HH-mm-ss}.txt&charset=Windows-1252")

            .log("Mecenat filexport klar.")
            .end();
    }
}
