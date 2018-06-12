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

import java.io.UnsupportedEncodingException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.stereotype.Component;

import se.kth.integral.mecenat.model.MecenatCSVRecordAggregationStrategy;

/**
 * Camel route to run sql queries against ladok3 database on a schedule.
 */
@Component
public class MecenatTransferRoute extends RouteBuilder {
    @Override
    public void configure() throws UnsupportedEncodingException {
        BindyCsvDataFormat mecenatCsvFormat = new BindyCsvDataFormat(se.kth.integral.mecenat.model.MecenatCSVRecord.class);
        mecenatCsvFormat.setLocale("sv_SE");
        String keyStore = getClass().getClassLoader().getResource("ftp.mecenat.se.keystore").getPath();

        from("direct:sendToMecenat")
            .routeId("se.kth.integral.mecenat.sender")

            .aggregate(new MecenatCSVRecordAggregationStrategy()).constant(true).completionSize(2)
            .marshal(mecenatCsvFormat)

            .log(LoggingLevel.DEBUG, "Skickar fil till mecenat.")
            .to("ftps://{{mecenat.host}}/mecenat-upload"
                    + "?fileName={{mecenat.customernr}}_${date:now:yyMMdd}_Mecenat_${date:now:HHmmss}_${header.termin}.txt"
                    + "&charset=Windows-1252"
                    + "&ftpClient.trustStore.file=" + keyStore
                    + "&ftpClient.trustStore.password=46D5HQ8dkY"
                    + "&isImplicit=true"
                    + "&passiveMode=true"
                    + "&soTimeout=30000"
                    + "&maximumReconnectAttempts=0"
                    + "&username={{mecenat.username}}"
                    + "&password={{mecenat.password}}"
                    )

            .log("Information skickad till Mecenat.");
    }
}