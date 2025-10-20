package com.example.MTConnect.mapping;

import com.example.MTConnect.config.MappingMode;
import com.example.MTConnect.config.MapRule;
import com.example.MTConnect.config.MappingProperties;
import com.example.MTConnect.model.*;
import com.example.MTConnect.model.Elements.Conditions.ConditionEntry;
import com.example.MTConnect.model.Elements.Samples.*;
import com.example.MTConnect.model.Events;
import com.example.MTConnect.model.Samples.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static com.example.MTConnect.mapping.ValueCodec.*;

/**RowMapper — это слой преобразования «сырой строки данных» (обычно CSV-запись: Map<String,String> row) в объектную модель
 MTConnect: Samples, Events, Conditions.
 Он делает то, что мы ранее обсуждали: по заранее заданным правилам сопоставляет столбцы датасета типам MTConnect
 (Samples / Events / Conditions) и конкретным data item’ам, а для неизвестных полей применяет «разумные фолбэки».
 Это ровно та логика, где используется наша сохранённая карта соответствий «имя колонки → модель MTConnect».
*/
public class RowMapper {

    // fields...
    private final MappingRegistry registry;
    private final MappingMode mode;

    // constructor...
    public RowMapper(MappingRegistry registry, MappingProperties props) {
        this.mode = props.getMode() == null ? MappingMode.EXPLICIT_ONLY : props.getMode();
        this.registry = registry;
    }
    // methods...
    public void mapRow(
            Map<String,String> row,
            Instant ts, long seq,
            Samples samples, Events events, Conditions conditions
    ) {
        String tsIso = ts.toString();

        for (var entry : row.entrySet()) {
            String key = entry.getKey();
            String raw = entry.getValue();
            if (raw == null || raw.isBlank()) continue;

            Optional<MapRule> r = registry.find(key);
            if (r.isPresent()) {
                applyRule(r.get(), row, tsIso, seq, samples, events, conditions);
                continue;
            }

            // fallback — ТОЛЬКО в HEURISTIC_ONLY
            if (mode == MappingMode.HEURISTIC_ONLY) {
                Double num = asDouble(row, key);
                if (num != null) {
                    samples.getPercentages().add(
                            new Percentage(key, tsIso, seq, BigDecimal.valueOf(num)));
                    continue;
                }
                Boolean flag = asBool(row, key);
                if (flag != null) {
                    events.getDiscrete().add(new com.example.MTConnect.model.Elements.Events.DiscreteEvent(
                            key, tsIso, seq, flag ? "ON" : "OFF"));
                    continue;
                }
                String txt = asText(row, key);
                if (txt != null) {
                    events.getTexts().add(new com.example.MTConnect.model.Elements.Events.StringEvent(
                            key, tsIso, seq, txt));
                }
            }
            // В EXPLICIT_ONLY просто пропускаем неизвестные поля
        }
    }


    private void applyRule(
            MapRule r, Map<String,String> row,
            String ts, long seq,
            Samples samples, Events events, Conditions conditions
    ) {
        switch (r.container()) {
            case "Samples" -> emitSample(r, row, ts, seq, samples);
            case "Events" -> emitEvent(r, row, ts, seq, events);
            case "Conditions" -> emitCondition(r, row, ts, seq, conditions);
            default -> { /* ignore */ }
        }
    }

    private void emitSample(MapRule r, Map<String,String> row, String ts, long seq, Samples samples) {
        Double v = asDouble(row, r.csvKey());
        if (v == null) return;
        var val = BigDecimal.valueOf(v);

        switch (r.mtcType()) {
            case "Position" -> samples.getPositions().add(
                    new Position(r.dataItemId(), ts, seq, val));
            case "RotaryPosition" -> samples.getRotaryPositions().add(
                    new RotaryPosition(r.dataItemId(), ts, seq, val, r.coordinate(), r.subType()));
            case "LinearVelocity" -> samples.getLinearVelocities().add(
                    new LinearVelocity(r.dataItemId(), ts, seq, val, r.coordinate()));
            case "RotaryVelocity" -> samples.getRotaryVelocities().add(
                    new RotaryVelocity(r.dataItemId(), ts, seq, val));
            case "Feedrate" -> samples.getFeedrates().add(
                    new Feedrate(r.dataItemId(), ts, seq, val));
            case "Temperature" -> samples.getTemperatures().add(
                    new Temperature(r.dataItemId(), ts, seq, val));
            case "ElectricCurrent" -> samples.getCurrents().add(
                    new ElectricCurrent(r.dataItemId(), ts, seq, val));
            case "Torque" -> samples.getTorques().add(
                    new Torque(r.dataItemId(), ts, seq, val));
            case "Voltage" -> samples.getVoltages().add(
                    new Voltage(r.dataItemId(), ts, seq, val));
            case "Power" -> samples.getPowers().add(
                    new Power(r.dataItemId(), ts, seq, val));
            case "Percentage" -> samples.getPercentages().add(
                    new Percentage(r.dataItemId(), ts, seq, val));
        }
    }

    private void emitEvent(MapRule r, Map<String,String> row, String ts, long seq, Events events) {
        Boolean b = asBool(row, r.csvKey());
        if (b != null) {
            events.getDiscrete().add(new com.example.MTConnect.model.Elements.Events.DiscreteEvent(
                    r.dataItemId(), ts, seq, b ? "ON" : "OFF"));
            return;
        }
        Long n = asLong(row, r.csvKey());
        if (n != null) {
            events.getNumbers().add(new com.example.MTConnect.model.Elements.Events.LongEvent(
                    r.dataItemId(), ts, seq, n));
            return;
        }
        String s = asText(row, r.csvKey());
        if (s != null) {
            events.getTexts().add(new com.example.MTConnect.model.Elements.Events.StringEvent(
                    r.dataItemId(), ts, seq, s));
        }
    }

    private void emitCondition(MapRule r, Map<String,String> row, String ts, long seq, Conditions conditions) {
        String txt = asText(row, r.csvKey());
        if (txt == null) return;
        conditions.getEntries().add(new ConditionEntry(
                r.dataItemId(), ts, seq, "NORMAL", txt));
    }

    // getters/setters...
}
