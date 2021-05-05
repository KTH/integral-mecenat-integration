package se.kth.integral.mecenat;
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

import static org.apache.camel.component.mock.MockEndpoint.assertIsSatisfied;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.kth.integral.mecenat.route.PeriodDatesProcessor;

@ActiveProfiles("test")
@CamelSpringBootTest
@SpringBootTest(classes = MecenatApplication.class)
@EnableAutoConfiguration
public class MainRouteTest {

    @Autowired
    private CamelContext camelContext;

    @EndpointInject("direct:start")
    protected ProducerTemplate mockStart;

    @EndpointInject("mock:mecenat")
    protected MockEndpoint mockMecenat;

    @EndpointInject("mock:studerandesql")
    protected MockEndpoint mockStuderandeSql;

    @EndpointInject("mock:forskarstuderandesql")
    protected MockEndpoint mockForskarstuderandeSql;

    @Test
    public void runTest() throws InterruptedException {
        mockStuderandeSql.whenAnyExchangeReceived(new FakeStudentSqlProcessor());
        mockForskarstuderandeSql.whenAnyExchangeReceived(new FakeResarchstudentSqlProcessor());

        LocalDate today = LocalDate.now();
        String term = PeriodDatesProcessor.term(today);

        mockStart.sendBody("");
        mockMecenat.expectedMessageCount(1);

        assertEquals("19710321xyzu;Teknolog;Ture;;Forskarbacken 21;11614;Stockholm;;;;;;;;0;100;0;0;2018-01-01;2018-06-30;"
                + term
                +";;\r\n",
                mockMecenat.getExchanges().get(0).getIn().getBody(String.class));

        mockStuderandeSql.whenAnyExchangeReceived(new FakeEmptySqlResultProcessor());

        mockStart.sendBody("");
        mockMecenat.expectedMessageCount(2);

        assertEquals("19710321xyzu;Teknolog;Ture;;Forskarbacken 21;11614;Stockholm;;;;;;;;0;75;0;0;2018-01-01;2018-06-30;"
                + term
                +";;\r\n",
                mockMecenat.getExchanges().get(1).getIn().getBody(String.class));

        assertIsSatisfied(camelContext);
    }
}
