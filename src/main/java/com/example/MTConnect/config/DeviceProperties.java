package com.example.MTConnect.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sim.device")
public class DeviceProperties {
    private String name;
    private String uuid;

    public String getName() { return name; }
    public String getUuid() { return uuid; }
    public void setName(String name) { this.name = name; }
    public void setUuid(String uuid) { this.uuid = uuid; }
}
