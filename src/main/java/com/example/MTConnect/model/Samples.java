package com.example.MTConnect.model;

import com.example.MTConnect.model.Elements.Samples.Position;
import com.example.MTConnect.model.Elements.Samples.RotaryVelocity;
import com.example.MTConnect.model.Elements.Samples.Feedrate;
import com.example.MTConnect.model.Elements.Samples.Temperature;
import com.example.MTConnect.model.Elements.Samples.RotaryPosition;
import com.example.MTConnect.model.Elements.Samples.LinearVelocity;
import com.example.MTConnect.model.Elements.Samples.ElectricCurrent;
import com.example.MTConnect.model.Elements.Samples.Torque;
import com.example.MTConnect.model.Elements.Samples.Voltage;
import com.example.MTConnect.model.Elements.Samples.Power;
import com.example.MTConnect.model.Elements.Samples.Percentage;
import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Samples {

    @XmlElement(name="Position",        namespace=MTConnectNamespaces.STREAMS) private List<Position> positions = new ArrayList<>();
    @XmlElement(name="RotaryPosition",  namespace=MTConnectNamespaces.STREAMS) private List<RotaryPosition> rotaryPositions = new ArrayList<>();
    @XmlElement(name="Feedrate",        namespace=MTConnectNamespaces.STREAMS) private List<Feedrate> feedrates = new ArrayList<>();
    @XmlElement(name="LinearVelocity",  namespace=MTConnectNamespaces.STREAMS) private List<LinearVelocity> linearVelocities = new ArrayList<>();
    @XmlElement(name="RotaryVelocity",  namespace=MTConnectNamespaces.STREAMS) private List<RotaryVelocity> rotaryVelocities = new ArrayList<>();
    @XmlElement(name="Temperature",     namespace=MTConnectNamespaces.STREAMS) private List<Temperature> temperatures = new ArrayList<>();
    @XmlElement(name="ElectricCurrent", namespace=MTConnectNamespaces.STREAMS) private List<ElectricCurrent> currents = new ArrayList<>();
    @XmlElement(name="Torque",          namespace=MTConnectNamespaces.STREAMS) private List<Torque> torques = new ArrayList<>();
    @XmlElement(name="Voltage",         namespace=MTConnectNamespaces.STREAMS) private List<Voltage> voltages = new ArrayList<>();
    @XmlElement(name="Power",           namespace=MTConnectNamespaces.STREAMS) private List<Power> powers = new ArrayList<>();
    @XmlElement(name="Percentage",      namespace=MTConnectNamespaces.STREAMS) private List<Percentage> percentages = new ArrayList<>();


    public List<RotaryPosition> getRotaryPositions() {
        return rotaryPositions;
    }

    public void setRotaryPositions(List<RotaryPosition> rotaryPositions) {
        this.rotaryPositions = rotaryPositions;
    }

    public List<Feedrate> getFeedrates() {
        return feedrates;
    }

    public void setFeedrates(List<Feedrate> feedrates) {
        this.feedrates = feedrates;
    }

    public List<LinearVelocity> getLinearVelocities() {
        return linearVelocities;
    }

    public void setLinearVelocities(List<LinearVelocity> linearVelocities) {
        this.linearVelocities = linearVelocities;
    }

    public List<RotaryVelocity> getRotaryVelocities() {
        return rotaryVelocities;
    }

    public void setRotaryVelocities(List<RotaryVelocity> rotaryVelocities) {
        this.rotaryVelocities = rotaryVelocities;
    }

    public List<Temperature> getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(List<Temperature> temperatures) {
        this.temperatures = temperatures;
    }

    public List<ElectricCurrent> getCurrents() {
        return currents;
    }

    public void setCurrents(List<ElectricCurrent> currents) {
        this.currents = currents;
    }

    public List<Torque> getTorques() {
        return torques;
    }

    public void setTorques(List<Torque> torques) {
        this.torques = torques;
    }

    public List<Voltage> getVoltages() {
        return voltages;
    }

    public void setVoltages(List<Voltage> voltages) {
        this.voltages = voltages;
    }

    public List<Power> getPowers() {
        return powers;
    }

    public void setPowers(List<Power> powers) {
        this.powers = powers;
    }

    public List<Percentage> getPercentages() {
        return percentages;
    }

    public void setPercentages(List<Percentage> percentages) {
        this.percentages = percentages;
    }


    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    // getters/setters...
}
