package com.example.MTConnect.config;

import com.example.MTConnect.mapping.MappingRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableConfigurationProperties({ MappingProperties.class })
public class MappingConfig {

    @Bean
    public MappingRegistry mappingRegistry(MappingProperties props, ResourceLoader loader) {
        // если задан rulesFile — подгрузим его в props
        props.loadFromRulesFileIfPresent(loader);
        return new MappingRegistry(props);
    }
}
