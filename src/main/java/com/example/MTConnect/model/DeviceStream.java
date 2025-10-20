package com.example.MTConnect.model;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceStream {



    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "uuid", required = true)
    private String uuid;

    @XmlElement(name = "ComponentStream", namespace = MTConnectNamespaces.STREAMS)
    private List<ComponentStream> componentStreams = new ArrayList<>();

    public DeviceStream() {}

    public DeviceStream(String name, String uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<ComponentStream> getComponentStreams() {
        return componentStreams;
    }

    public void setComponentStreams(List<ComponentStream> componentStreams) {
        this.componentStreams = componentStreams;
    }
}
