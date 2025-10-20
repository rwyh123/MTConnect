package com.example.MTConnect.model.Elements.Samples;

import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class RotaryPosition{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;


    @XmlTransient
    protected String coordinate; // X, Y, Z, S, B, C
    @XmlTransient
    protected String subType;    // MCS, WCS, ERROR и т.д.

    @XmlValue private BigDecimal value;
    public RotaryPosition() {}
    public RotaryPosition(String id, String ts, long seq, BigDecimal v, String coordinate, String subType) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = v;
        this.coordinate = coordinate; // B/C/S (spindle)
        this.subType = subType;       // MCS/WCS/ERROR
        this.nativeUnits = "DEGREE";
    }
    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }
}
