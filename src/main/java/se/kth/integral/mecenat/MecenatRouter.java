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
package se.kth.integral.mecenat;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel route to run sql queries against ladok3 database on a schedule.
 */
@Component
public class MecenatRouter extends RouteBuilder {
    private Processor startDateProcessor = new StartDateProcessor();
    private Processor endDateProcessor = new EndDateProcessor();

    @Override
    public void configure() {
        System.setProperty("user.timezone", "Europe/Stockholm");

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
            .process(startDateProcessor)

            .to("sql:classpath:sql/nasta_termin.sql?dataSource=uppfoljningsDB")
            .process(endDateProcessor )

            // TODO: reda ut exakta frågor, eventuellt aggregera flera frågor.
            .log(LoggingLevel.DEBUG, "Hämtar data från Ladok3 för ${header.startDatum} - ${header.slutDatum}.")
            .to("sql:classpath:sql/mecenat.sql?dataSource=uppfoljningsDB")

            // TODO: hur ska formatet exakt vara?
            .log(LoggingLevel.DEBUG, "Transformerar data till CSV.")
            .marshal().csv()

            // TODO: var ska vi stoppa filen? Vad ska den heta?
            .log(LoggingLevel.DEBUG, "Skriver exportfil.")
            .to("file://{{ladok3.output.dir}}?fileName=mecenat-${date:now:yyyy-MM-dd-HH-mm-ss}.txt")

            .log("Mecenat fil export klar.");
    }
}
