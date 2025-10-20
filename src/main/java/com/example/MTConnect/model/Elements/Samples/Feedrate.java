package com.example.MTConnect.model.Elements.Samples;


import com.example.MTConnect.model.Elements.AbstractEntry;
import jakarta.xml.bind.annotation.*;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class Feedrate{

    @XmlAttribute(name = "dataItemId") private String dataItemId;
    @XmlAttribute(name = "timestamp")  private String timestamp;
    @XmlAttribute(name = "sequence")   private long sequence;
    @XmlAttribute(name = "nativeUnits") private String nativeUnits;



    @XmlValue
    private BigDecimal value;

    public Feedrate() {}

    public Feedrate(String dataItemId, String timestamp, long sequence, BigDecimal value) {
        this.dataItemId = dataItemId;
        this.timestamp  = timestamp;
        this.sequence   = sequence;
        this.value = value;
    }

    public String getDataItemId() {
        return dataItemId;
    }
}
