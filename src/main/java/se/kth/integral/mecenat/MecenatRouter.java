package se.kth.integral.mecenat;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Camel route to run sql queries against ladok3 database on a schedule.
 */
@Component
public class MecenatRouter extends RouteBuilder {
    @Override
    public void configure() {
        from("quartz://mecenat?cron={{ladok3.cron}}&trigger.timeZone={{quartz.timezone}}")
            .routeId("se.kth.integral.mecenat")
            .to("sql:select * from UPPFOLJNING.IO_STUDENTUPPGIFTER where PERSONNUMMER = '197103210170'")
            .to("log:out");
    }
}
