package com.example.MTConnect.model.Elements.Conditions;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionEntry{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlAttribute(name = "level", required = true)
    private String level; // NORMAL / WARNING / FAULT

    @XmlValue
    private String text;  // optional description/code

    public ConditionEntry() {}

    public ConditionEntry(String dataItemId, String timestamp, long sequence, String level, String text) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.level = level;
        this.text = text;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}
