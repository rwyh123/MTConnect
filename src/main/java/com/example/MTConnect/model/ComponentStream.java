package com.example.MTConnect.model;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentStream {



    @XmlAttribute(name = "component", required = true)
    private String component; // e.g., "Axes", "Rotary", "Path"

    @XmlAttribute(name = "name", required = true)
    private String name;      // logical name, e.g., "LinearAxes", "Spindle"

    @XmlElement(name = "Samples", namespace = MTConnectNamespaces.STREAMS)
    private Samples samples;

    @XmlElement(name = "Events", namespace = MTConnectNamespaces.STREAMS)
    private Events events;

    @XmlElement(name = "Condition", namespace = MTConnectNamespaces.STREAMS)
    private Conditions condition;

    public ComponentStream() {}

    public ComponentStream(String component, String name) {
        this.component = component;
        this.name = name;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Samples getSamples() {
        return samples;
    }

    public void setSamples(Samples samples) {
        this.samples = samples;
    }

    public Events getEvents() {
        return events;
    }

    public void setEvents(Events events) {
        this.events = events;
    }

    public Conditions getCondition() {
        return condition;
    }

    public void setCondition(Conditions condition) {
        this.condition = condition;
    }
}
