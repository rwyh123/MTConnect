package com.example.MTConnect.model.Elements;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

public abstract class AbstractEntry {

    @XmlAttribute(name="dataItemId")
    protected String dataItemId;

    @XmlAttribute(name="timestamp")
    protected String timestamp;

    @XmlAttribute(name="sequence")
    protected long sequence;

    @XmlAttribute(name = "nativeUnits")
    protected String nativeUnits;  // MILLIMETER/DEGREE/RPM/AMPERE...

    // --- Добавим неXML-поля ---
    @XmlTransient
    protected String coordinate; // X, Y, Z, S, B, C
    @XmlTransient
    protected String subType;    // MCS, WCS, ERROR и т.д.




    public AbstractEntry() {}

    public AbstractEntry(String dataItemId, String timestamp, long sequence) {
        this.dataItemId = dataItemId;
        this.timestamp = timestamp;
        this.sequence = sequence;
    }

    // + стандартные геттеры/сеттеры
    public String getCoordinate() { return coordinate; }
    public void setCoordinate(String coordinate) { this.coordinate = coordinate; }
    public String getSubType() { return subType; }
    public void setSubType(String subType) { this.subType = subType; }
}
