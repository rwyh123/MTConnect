package com.example.MTConnect.model.Elements.Events;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Execution{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue
    private String state; // e.g., READY/ACTIVE/STOPPED

    public Execution() {}

    public Execution(String dataItemId, String timestamp, long sequence, String state) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.state = state;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}
