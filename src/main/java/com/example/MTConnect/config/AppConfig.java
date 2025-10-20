package com.example.MTConnect.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MappingProperties.class, DeviceProperties.class})
public class AppConfig {}