package com.minjor.datam.config;

import com.minjor.datam.core.datasource.DataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataMConfig {

    @Bean
    public DataSourceFactory dataSourceFactory() {
        return new DataSourceFactory();
    }
}
