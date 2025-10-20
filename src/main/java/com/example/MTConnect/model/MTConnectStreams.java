package com.example.MTConnect.model;

import jakarta.xml.bind.annotation.*;
import java.util.List;



@XmlRootElement(name="MTConnectStreams", namespace=MTConnectNamespaces.STREAMS)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"header", "streams"})
public class MTConnectStreams {

    @XmlElement(name = "Header", namespace = MTConnectNamespaces.STREAMS, required = true)
    private StreamsHeader header;

    @XmlElement(name = "Streams", namespace = MTConnectNamespaces.STREAMS, required = true)
    private Streams streams;

    public MTConnectStreams() {}

    public MTConnectStreams(StreamsHeader header, Streams streams) {
        this.header = header;
        this.streams = streams;
    }

    public StreamsHeader getHeader() { return header; }
    public Streams getStreams() { return streams; }

    public void setHeader(StreamsHeader header) { this.header = header; }
    public void setStreams(Streams streams) { this.streams = streams; }
}
