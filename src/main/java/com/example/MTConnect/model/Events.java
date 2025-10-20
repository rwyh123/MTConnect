package com.example.MTConnect.model;

import com.example.MTConnect.model.Elements.Events.Alarm;
import com.example.MTConnect.model.Elements.Events.Execution;
import com.example.MTConnect.model.Elements.Events.DiscreteEvent;
import com.example.MTConnect.model.Elements.Events.StringEvent;
import com.example.MTConnect.model.Elements.Events.LongEvent;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Events {



    @XmlElement(name = "Execution", namespace = MTConnectNamespaces.STREAMS)
    private List<Execution> executions = new ArrayList<>();

    @XmlElement(name = "Alarm", namespace = MTConnectNamespaces.STREAMS)
    private List<Alarm> alarms = new ArrayList<>();


    @XmlElement(name="Event",     namespace=MTConnectNamespaces.STREAMS)
    private List<DiscreteEvent> discrete = new ArrayList<>();

    @XmlElement(name="Text",      namespace=MTConnectNamespaces.STREAMS)
    private List<StringEvent> texts = new ArrayList<>();

    @XmlElement(name="Line",      namespace=MTConnectNamespaces.STREAMS)
    private List<LongEvent>   numbers = new ArrayList<>();

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    public List<DiscreteEvent> getDiscrete() {
        return discrete;
    }

    public void setDiscrete(List<DiscreteEvent> discrete) {
        this.discrete = discrete;
    }

    public List<StringEvent> getTexts() {
        return texts;
    }

    public void setTexts(List<StringEvent> texts) {
        this.texts = texts;
    }

    public List<LongEvent> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<LongEvent> numbers) {
        this.numbers = numbers;
    }
}
