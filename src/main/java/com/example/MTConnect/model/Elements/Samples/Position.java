package com.example.MTConnect.model.Elements.Samples;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class Position{

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

    public Position() {}

    public Position(String dataItemId, String timestamp, long sequence, BigDecimal value) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = value;
    }

    // getters/setters...
}
