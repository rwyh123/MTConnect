package com.example.MTConnect.config;

import com.example.MTConnect.mapping.MappingRegistry;
import com.example.MTConnect.mapping.RowMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class Beans {

    @Bean
    RowMapper rowMapper(MappingRegistry registry, MappingProperties props) {
        return new RowMapper(registry, props);
    }

    @Bean
    public JAXBContext jaxbContext() {
        try {
            return JAXBContext.newInstance(com.example.MTConnect.model.MTConnectStreams.class);
        } catch (JAXBException ex) {
            throw new RuntimeException("JAXBContext initialization failed", ex);
        }
    }
}

