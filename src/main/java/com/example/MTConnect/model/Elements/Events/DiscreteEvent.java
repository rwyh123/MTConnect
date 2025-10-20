package com.example.MTConnect.model.Elements.Events;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class DiscreteEvent{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue private String state; // ON/OFF, OPEN/CLOSED, READY/ACTIVE, LOCKED/UNLOCKED...
    public DiscreteEvent() {}
    public DiscreteEvent(String id, String ts, long seq, String state) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.state = state;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}
