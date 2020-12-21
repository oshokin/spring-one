package ru.geekbrains.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("ru.geekbrains.server")
@PropertySource("classpath:application.properties")
public class SpringConfig {

    @Value("${DS.driver}")
    private String dataSourceDriver;

    @Value("${DS.url}")
    private String dataSourceURL;

    @Value("${DS.user}")
    private String dataSourceUser;

    @Value("${DS.password}")
    private String dataSourcePassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource(dataSourceURL, dataSourceUser, dataSourcePassword);
        ds.setDriverClassName(dataSourceDriver);
        return ds;
    }
}