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

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = MecenatApplication.class)
@EnableAutoConfiguration
public class MainRouteTest extends CamelTestSupport {
    @Autowired
    private CamelContext camelContext;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        return camelContext;
    }

    @EndpointInject(uri = "direct:start")
    protected ProducerTemplate mockStart;

    @EndpointInject(uri = "mock:mecenat")
    protected MockEndpoint mockMecenat;

    @EndpointInject(uri = "mock:studerandesql")
    protected MockEndpoint mockStuderandeSql;

    @EndpointInject(uri = "mock:forskarstuderandesql")
    protected MockEndpoint mockForskarstuderandeSql;

    @Before
    public void initialize() {
        mockStuderandeSql.whenAnyExchangeReceived(new FakeStudentSqlProcessor());
        mockForskarstuderandeSql.whenAnyExchangeReceived(new FakeResarchstudentSqlProcessor());
    }

    @Test
    public void runTest() throws InterruptedException {
        mockStart.sendBody("");

        mockMecenat.expectedMessageCount(1);

        assertEquals("19710321xyzu;Teknolog;Ture;;Forskarbacken 21;11614;Stockholm;;;;;;;;0;100;0;0;2018-01-01;2018-06-30;20181;;\r\n",
                mockMecenat.getExchanges().get(0).getIn().getBody(String.class));

        assertMockEndpointsSatisfied();
    }
}
