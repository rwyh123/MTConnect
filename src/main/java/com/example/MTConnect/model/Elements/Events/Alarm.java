package com.example.MTConnect.model.Elements.Events;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Alarm{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue
    private String value; // e.g., TRUE/FALSE or alarm code text

    public Alarm() {}

    public Alarm(String dataItemId, String timestamp, long sequence, String value) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = value;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}