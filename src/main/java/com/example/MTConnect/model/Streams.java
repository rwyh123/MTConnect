package com.example.MTConnect.model;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Streams {

    @XmlElement(name = "DeviceStream", namespace = MTConnectNamespaces.STREAMS)
    private List<DeviceStream> deviceStreams = new ArrayList<>();

    public Streams() {}

    public Streams(List<DeviceStream> deviceStreams) {
        this.deviceStreams = deviceStreams;
    }

    public List<DeviceStream> getDeviceStreams() { return deviceStreams; }
    public void setDeviceStreams(List<DeviceStream> deviceStreams) { this.deviceStreams = deviceStreams; }
}
