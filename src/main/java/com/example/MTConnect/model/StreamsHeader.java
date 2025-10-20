package com.example.MTConnect.model;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class StreamsHeader {

    @XmlAttribute(name = "creationTime", required = true)
    private String creationTime;  // ISO-8601

    @XmlAttribute(name = "sender", required = true)
    private String sender;

    @XmlAttribute(name = "instanceId", required = true)
    private long instanceId;

    @XmlAttribute(name = "version", required = true)
    private String version; // "1.8"

    @XmlAttribute(name = "bufferSize", required = true)
    private long bufferSize;

    @XmlAttribute(name = "firstSequence", required = true)
    private long firstSequence;

    @XmlAttribute(name = "lastSequence", required = true)
    private long lastSequence;

    @XmlAttribute(name = "nextSequence", required = true)
    private long nextSequence;

    public StreamsHeader() {}

    public StreamsHeader(String creationTime, String sender, long instanceId, String version,
                         long bufferSize, long firstSequence, long lastSequence, long nextSequence) {
        this.creationTime = creationTime;
        this.sender = sender;
        this.instanceId = instanceId;
        this.version = version;
        this.bufferSize = bufferSize;
        this.firstSequence = firstSequence;
        this.lastSequence = lastSequence;
        this.nextSequence = nextSequence;
    }

    // getters/setters...
}

