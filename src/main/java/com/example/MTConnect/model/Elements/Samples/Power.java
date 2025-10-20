package com.example.MTConnect.model.Elements.Samples;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class Power{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue private BigDecimal value;
    public Power() {}
    public Power(String id, String ts, long seq, BigDecimal v) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = v;
        this.nativeUnits = "WATT";
    }

    public String getDataItemId() {
        return dataItemId;
    }
}

