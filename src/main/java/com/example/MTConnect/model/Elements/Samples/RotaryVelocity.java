package com.example.MTConnect.model.Elements.Samples;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class RotaryVelocity{

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;

    @XmlTransient
    protected String coordinate; // X, Y, Z, S, B, C
    @XmlTransient
    protected String subType;    // MCS, WCS, ERROR и т.д.

    public String getCoordinate() { return coordinate; }
    public void setCoordinate(String coordinate) { this.coordinate = coordinate; }
    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }

    @XmlValue
    private BigDecimal value;

    public RotaryVelocity() {}

    public RotaryVelocity(String dataItemId, String timestamp, long sequence, BigDecimal value) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = value;
    }
}
