package com.example.MTConnect.model;

import com.example.MTConnect.model.Elements.Conditions.ConditionEntry;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Conditions {



    // Упрощённая модель: один список записей с уровнем (NORMAL/WARNING/FAULT)
    @XmlElement(name = "Condition", namespace = MTConnectNamespaces.STREAMS)
    private List<ConditionEntry> entries = new ArrayList<>();

    public List<ConditionEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<ConditionEntry> entries) {
        this.entries = entries;
    }
}
