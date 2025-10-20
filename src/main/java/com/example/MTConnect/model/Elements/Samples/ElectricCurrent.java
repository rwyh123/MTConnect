package com.example.MTConnect.model.Elements.Samples;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class ElectricCurrent{



    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlValue
    private BigDecimal value;
    public ElectricCurrent() {}
    public ElectricCurrent(String id, String ts, long seq, BigDecimal v) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = v;
        this.nativeUnits = "AMPERE";
    }

    public String getDataItemId() {
        return dataItemId;
    }
}
