package com.example.MTConnect.model.Elements.Samples;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class Percentage{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue private BigDecimal value;
    public Percentage() {}
    public Percentage(String id, String ts, long seq, BigDecimal v) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = v;
        this.nativeUnits = "PERCENT";
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}
