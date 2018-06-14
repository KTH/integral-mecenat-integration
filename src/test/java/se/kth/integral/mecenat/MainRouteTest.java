package se.kth.integral.mecenat;
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

        assertEquals("19710321xyzu;Teknolog;Ture;;Forskarbacken 21;11614;Stockholm;;;2018-01-01;2018-06-30;100;0;;;;;20181\r\n",
                mockMecenat.getExchanges().get(0).getIn().getBody(String.class));

        assertMockEndpointsSatisfied();
    }
}
