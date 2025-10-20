package com.example.MTConnect.service;


import com.example.MTConnect.config.DeviceProperties;
import com.example.MTConnect.data.CSVDataReader;
import com.example.MTConnect.mapping.RowMapper;
import com.example.MTConnect.model.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MTConnectService {

    private final CSVDataReader csv;
    private final RowMapper mapper;
    private final DeviceProperties device;
    private final JAXBContext jaxb;

    private final AtomicLong sequence = new AtomicLong(0);
    private final long instanceId = 1L;
    private final long bufferSize = 8192L;

    private final AtomicReference<MTConnectStreams> lastDoc = new AtomicReference<>();

    public MTConnectService(CSVDataReader csv, RowMapper mapper,
                            DeviceProperties device, JAXBContext jaxb) {
        this.csv = csv;
        this.mapper = mapper;
        this.device = device;
        this.jaxb = jaxb;
    }

    /** Тик: читаем CSV и собираем новый MTConnectStreams. */
    @Scheduled(fixedRateString = "${sim.tickMs:1000}")
    public void tick() {
        Map<String,String> row = csv.getNextRow();
        if (row == null || row.isEmpty()) return;

        long seq = sequence.incrementAndGet();
        Instant now = Instant.now();

        // Собираем общий набор
        Samples allSamples = new Samples();
        Events  allEvents  = new Events();
        Conditions allCond = new Conditions();
        mapper.mapRow(row, now, seq, allSamples, allEvents, allCond);

        // Компоненты устройства
        ComponentStream xAxis   = new ComponentStream("Linear",  "X");
        ComponentStream yAxis   = new ComponentStream("Linear",  "Y");
        ComponentStream zAxis   = new ComponentStream("Linear",  "Z");
        ComponentStream spindle = new ComponentStream("Rotary",  "Spindle");
        ComponentStream systems = new ComponentStream("Systems", "ToolMagazine");

        // Локальные контейнеры Samples по компонентам
        Samples xS = new Samples(); Samples yS = new Samples(); Samples zS = new Samples();
        Samples spS = new Samples(); Samples sysS = new Samples();

        // ---------- Распределение SAMPLES ----------
        // 1) Позиции
        allSamples.getPositions().forEach(p -> {
            switch (coord(p.getCoordinate())) {
                case "X" -> xS.getPositions().add(p);
                case "Y" -> yS.getPositions().add(p);
                case "Z" -> zS.getPositions().add(p);
                default  -> spS.getPositions().add(p);
            }
        });

        // 2) Вращательные позиции
        allSamples.getRotaryPositions().forEach(p -> {
            switch (coord(p.getCoordinate())) {
                case "B","C","S","" -> spS.getRotaryPositions().add(p);
                default             -> sysS.getRotaryPositions().add(p);
            }
        });

        // 3) Линейные скорости
        allSamples.getLinearVelocities().forEach(v -> {
            switch (coord(v.getCoordinate())) {
                case "X" -> xS.getLinearVelocities().add(v);
                case "Y" -> yS.getLinearVelocities().add(v);
                case "Z" -> zS.getLinearVelocities().add(v);
            }
        });

        // 4) Вращательные скорости
        allSamples.getRotaryVelocities().forEach(v -> {
            switch (coord(v.getCoordinate())) {
                case "S","B","C","" -> spS.getRotaryVelocities().add(v);
                default             -> sysS.getRotaryVelocities().add(v);
            }
        });

        // 5) Подачи
        allSamples.getFeedrates().forEach(f -> {
            switch (guessByIdOnly(f.getDataItemId())) {
                case X -> xS.getFeedrates().add(f);
                case Y -> yS.getFeedrates().add(f);
                case Z -> zS.getFeedrates().add(f);
                case SPINDLE -> spS.getFeedrates().add(f); // типичный случай для шпинделя
                case SYSTEMS -> sysS.getFeedrates().add(f);
            }
        });

        // 6) Температуры
        allSamples.getTemperatures().forEach(t -> {
            switch (guessByCoordOrId(t.getDataItemId(), t.getDataItemId())) {
                case X -> xS.getTemperatures().add(t);
                case Y -> yS.getTemperatures().add(t);
                case Z -> zS.getTemperatures().add(t);
                case SPINDLE -> spS.getTemperatures().add(t);
                case SYSTEMS -> sysS.getTemperatures().add(t);
            }
        });

        // 7) Токи
        allSamples.getCurrents().forEach(c -> {
            switch (guessByIdOnly(c.getDataItemId())) {
                case X -> xS.getCurrents().add(c);
                case Y -> yS.getCurrents().add(c);
                case Z -> zS.getCurrents().add(c);
                case SPINDLE -> spS.getCurrents().add(c);
                case SYSTEMS -> sysS.getCurrents().add(c);
            }
        });

        // 8) Torque
        allSamples.getTorques().forEach(tq -> {
            switch (guessByIdOnly(tq.getDataItemId())) {
                case X       -> xS.getTorques().add(tq);
                case Y       -> yS.getTorques().add(tq);
                case Z       -> zS.getTorques().add(tq);
                case SPINDLE -> spS.getTorques().add(tq);
                case SYSTEMS -> sysS.getTorques().add(tq);
            }
        });

        // 9) Напряжения
        allSamples.getVoltages().forEach(vv -> {
            switch (guessByIdOnly(vv.getDataItemId())) {
                case X       -> xS.getVoltages().add(vv);
                case Y       -> yS.getVoltages().add(vv);
                case Z       -> zS.getVoltages().add(vv);
                case SPINDLE -> spS.getVoltages().add(vv);
                case SYSTEMS -> sysS.getVoltages().add(vv);
            }
        });

        // 10) Мощности
        allSamples.getPowers().forEach(pw -> {
            switch (guessByIdOnly(pw.getDataItemId())) {
                case X -> xS.getPowers().add(pw);
                case Y -> yS.getPowers().add(pw);
                case Z -> zS.getPowers().add(pw);
                case SPINDLE -> spS.getPowers().add(pw);
                case SYSTEMS -> sysS.getPowers().add(pw);
            }
        });

        // 11) Проценты (универсальный телеметрический канал)
        allSamples.getPercentages().forEach(pc -> {
            switch (guessByIdOnly(pc.getDataItemId())) {
                case X -> xS.getPercentages().add(pc);
                case Y -> yS.getPercentages().add(pc);
                case Z -> zS.getPercentages().add(pc);
                case SPINDLE -> spS.getPercentages().add(pc);
                case SYSTEMS -> sysS.getPercentages().add(pc);
            }
        });

        // Вкладываем собранные Samples в компоненты (если не пусты)
        if (!isEmpty(xS)) xAxis.setSamples(xS);
        if (!isEmpty(yS)) yAxis.setSamples(yS);
        if (!isEmpty(zS)) zAxis.setSamples(zS);
        if (!isEmpty(spS)) spindle.setSamples(spS);
        if (!isEmpty(sysS)) systems.setSamples(sysS);

        // ---------- Распределение EVENTS ----------
        // Подготовим контейнеры Events по компонентам (лениво создаём)
        Events xE = new Events(); Events yE = new Events(); Events zE = new Events();
        Events spE = new Events(); Events sysE = new Events();

        allEvents.getDiscrete().forEach(ev -> {
            switch (guessByIdOnly(ev.getDataItemId())) {
                case X -> xE.getDiscrete().add(ev);
                case Y -> yE.getDiscrete().add(ev);
                case Z -> zE.getDiscrete().add(ev);
                case SPINDLE -> spE.getDiscrete().add(ev);
                case SYSTEMS -> sysE.getDiscrete().add(ev);
            }
        });
        allEvents.getNumbers().forEach(ev -> {
            switch (guessByIdOnly(ev.getDataItemId())) {
                case X -> xE.getNumbers().add(ev);
                case Y -> yE.getNumbers().add(ev);
                case Z -> zE.getNumbers().add(ev);
                case SPINDLE -> spE.getNumbers().add(ev);
                case SYSTEMS -> sysE.getNumbers().add(ev);
            }
        });
        allEvents.getTexts().forEach(ev -> {
            switch (guessByIdOnly(ev.getDataItemId())) {
                case X -> xE.getTexts().add(ev);
                case Y -> yE.getTexts().add(ev);
                case Z -> zE.getTexts().add(ev);
                case SPINDLE -> spE.getTexts().add(ev);
                case SYSTEMS -> sysE.getTexts().add(ev);
            }
        });
        allEvents.getExecutions().forEach(ev -> {
            switch (guessByIdOnly(ev.getDataItemId())) {
                case X -> xE.getExecutions().add(ev);
                case Y -> yE.getExecutions().add(ev);
                case Z -> zE.getExecutions().add(ev);
                case SPINDLE -> spE.getExecutions().add(ev);
                case SYSTEMS -> sysE.getExecutions().add(ev);
            }
        });
        allEvents.getAlarms().forEach(ev -> {
            switch (guessByIdOnly(ev.getDataItemId())) {
                case X -> xE.getAlarms().add(ev);
                case Y -> yE.getAlarms().add(ev);
                case Z -> zE.getAlarms().add(ev);
                case SPINDLE -> spE.getAlarms().add(ev);
                case SYSTEMS -> sysE.getAlarms().add(ev);
            }
        });

        if (!isEmpty(xE)) xAxis.setEvents(xE);
        if (!isEmpty(yE)) yAxis.setEvents(yE);
        if (!isEmpty(zE)) zAxis.setEvents(zE);
        if (!isEmpty(spE)) spindle.setEvents(spE);
        if (!isEmpty(sysE)) systems.setEvents(sysE);

        // ---------- Распределение CONDITIONS ----------
        // В Conditions у нас плоский список entries — раскидаем по эвристике ID/coordinate.
        Conditions xC = new Conditions(); Conditions yC = new Conditions();
        Conditions zC = new Conditions(); Conditions spC = new Conditions(); Conditions sysC = new Conditions();

        allCond.getEntries().forEach(c -> {
            switch (guessByCoordOrId(null, c.getDataItemId())) {
                case X -> xC.getEntries().add(c);
                case Y -> yC.getEntries().add(c);
                case Z -> zC.getEntries().add(c);
                case SPINDLE -> spC.getEntries().add(c);
                case SYSTEMS -> sysC.getEntries().add(c);
            }
        });

        if (!xC.getEntries().isEmpty()) xAxis.setCondition(xC);
        if (!yC.getEntries().isEmpty()) yAxis.setCondition(yC);
        if (!zC.getEntries().isEmpty()) zAxis.setCondition(zC);
        if (!spC.getEntries().isEmpty()) spindle.setCondition(spC);
        if (!sysC.getEntries().isEmpty()) systems.setCondition(sysC);

        // Формируем DeviceStream
        DeviceStream ds = new DeviceStream(device.getName(), device.getUuid());
        ds.getComponentStreams().addAll(
                List.of(xAxis, yAxis, zAxis, spindle, systems)
                        .stream().filter(this::hasAnyData).toList()
        );

        Streams streams = new Streams(List.of(ds));
        StreamsHeader header = new StreamsHeader(
                now.toString(), device.getName(), instanceId, "1.8",
                bufferSize, Math.max(1, seq - bufferSize + 1), seq, seq + 1);
        lastDoc.set(new MTConnectStreams(header, streams));
    }

    // ---------- Эвристики маршрутизации ----------
    private enum Target { X, Y, Z, SPINDLE, SYSTEMS }

    /** Нормализуем coordinate (null -> "") */
    @Contract(value = "!null -> param1", pure = true)
    private static @NotNull String coord(String c) { return c == null ? "" : c; }

    /** По coordinate и/или dataItemId определяем компонент. */
    private Target guessByCoordOrId(String coordinate, String dataItemId) {
        String c = coord(coordinate);
        if (c.equals("X")) return Target.X;
        if (c.equals("Y")) return Target.Y;
        if (c.equals("Z")) return Target.Z;
        if (c.equals("S") || c.equals("B") || c.equals("C") || c.isEmpty()) return Target.SPINDLE;
        // иначе — эвристика по имени dataItemId
        return guessByIdOnly(dataItemId);
    }

    /** Эвристика только по dataItemId: ищем подсказки в имени. */
    private Target guessByIdOnly(String id) {
        String s = id == null ? "" : id.toUpperCase();
        if (s.contains("SPINDLE") || s.startsWith("S_") || s.endsWith("_S")) return Target.SPINDLE;
        if (s.startsWith("X_") || s.endsWith("_X") || s.contains("AXIS_X")) return Target.X;
        if (s.startsWith("Y_") || s.endsWith("_Y") || s.contains("AXIS_Y")) return Target.Y;
        if (s.startsWith("Z_") || s.endsWith("_Z") || s.contains("AXIS_Z")) return Target.Z;
        if (s.contains("MAG") || s.contains("TOOL") || s.contains("SYSTEM")) return Target.SYSTEMS;
        // по умолчанию — системные
        return Target.SYSTEMS;
    }

    // ---------- Хелперы-проверки ----------
    private static boolean isEmpty(@NotNull Samples s) {
        return s.getPositions().isEmpty()
                && s.getRotaryPositions().isEmpty()
                && s.getLinearVelocities().isEmpty()
                && s.getRotaryVelocities().isEmpty()
                && s.getFeedrates().isEmpty()
                && s.getTemperatures().isEmpty()
                && s.getCurrents().isEmpty()
                && s.getTorques().isEmpty()
                && s.getVoltages().isEmpty()
                && s.getPowers().isEmpty()
                && s.getPercentages().isEmpty();
    }
    private static boolean isEmpty(@NotNull Events e) {
        return e.getExecutions().isEmpty()
                && e.getAlarms().isEmpty()
                && e.getDiscrete().isEmpty()
                && e.getTexts().isEmpty()
                && e.getNumbers().isEmpty();
    }
    private boolean hasAnyData(@NotNull ComponentStream cs) {
        return (cs.getSamples() != null && !isEmpty(cs.getSamples()))
                || (cs.getEvents() != null && !isEmpty(cs.getEvents()))
                || (cs.getCondition() != null && !cs.getCondition().getEntries().isEmpty());
    }

    /** Отдать текущее состояние в виде XML. */
    public String currentXml() {
        MTConnectStreams doc = lastDoc.get();
        if (doc == null) doc = emptyDoc();

        try (var sw = new StringWriter(2048)) {
            Marshaller m = jaxb.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            m.marshal(doc, sw);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException("MTConnect marshal error", e);
        }
    }

    /** Пустой документ (на самый первый вызов до первого тика). */
    private MTConnectStreams emptyDoc() {
        Instant now = Instant.now();
        StreamsHeader header = new StreamsHeader(
                now.toString(), device.getName(), instanceId, "1.8",
                bufferSize, 1, 0, 1
        );
        DeviceStream ds = new DeviceStream(device.getName(), device.getUuid());
        Streams streams = new Streams(List.of(ds));
        return new MTConnectStreams(header, streams);
    }
}
