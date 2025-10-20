package com.example.MTConnect.model.Elements.Events;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class LongEvent{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue private long value; // номера строк/инструментов/ячееκ
    public LongEvent() {}
    public LongEvent(String id, String ts, long seq, long value) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = value;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}