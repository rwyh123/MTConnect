package com.example.MTConnect.model.Elements.Events;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class StringEvent{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue private String text; // путь к программе, Tool_ID, Line_Content...
    public StringEvent() {}
    public StringEvent(String id, String ts, long seq, String text) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.text = text;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}