package se.kth.integral.mecenat;

import javax.sql.DataSource;

import org.apache.camel.component.sql.SqlComponent;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for an SQL component connection to an SQL database source.
 * Uses properties prefixed with sql.
 */
@Configuration
@ConfigurationProperties(prefix = "sql")
public class SqlComponentConfiguration {
    private String username;
    private String password;
    private String url;
    private String driver;

    // Configurations with defaults.s
    private int timeBetweenEvitionRunsMillis = 10000;
    private int minEvictableIdleTimeMillis = 30000;
    private int maxWaitMillis = 60000;

    @Primary
    @Bean(name = "sql")
    public SqlComponent sql(
        @Qualifier("datasource") DataSource datasource) {

        SqlComponent sql = new SqlComponent();
        sql.setDataSource(datasource);
        return sql;
    }

    @Primary
    @Bean(name = "datasource")
    public DataSource dataSource() {
        BasicDataSource datasource = new BasicDataSource();
        datasource.setUrl(url);
        datasource.setUsername(username);
        datasource.setPassword(password);

        datasource.setTestWhileIdle(true);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvitionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setMaxWaitMillis(maxWaitMillis);
        datasource.setDriverClassName(driver);

        return datasource;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimeBetweenEvitionRunsMillis() {
        return timeBetweenEvitionRunsMillis;
    }

    public void setTimeBetweenEvitionRunsMillis(int timeBetweenEvitionRunsMillis) {
        this.timeBetweenEvitionRunsMillis = timeBetweenEvitionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(int maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}