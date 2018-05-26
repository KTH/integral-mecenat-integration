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
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel route to run sql queries against ladok3 database on a schedule.
 */
@Component
public class MecenatRouter extends RouteBuilder {
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

            .setHeader("today").simple("${date:now:yyyy-MM-dd}")

            // TODO: hur räknar vi ut de här?
            .setHeader("termin").constant("20172")
            .setHeader("startDatum").constant("2017-08-28")
            .setHeader("slutDatum").constant("2018-01-14")

            // TODO: reda ut exakta frågor, eventuellt aggregera flera frågor.
            .to("sql:classpath:sql/mecenat.sql")
            .to("log:se.kth.integral.mecenat?level=DEBUG")

            // TODO: hur ska formatet exakt vara?
            .marshal().csv()

            // TODO: var ska vi stoppa filen?
            .to("file://{{ladok3.output.dir}}?fileName=mecenat-${date:now:yyyy-MM-dd-HH-mm-ss}.txt&bufferSize=128000000");
            ;
    }
}
