package com.example.MTConnect.mapping;

import com.example.MTConnect.config.MapRule;
import com.example.MTConnect.config.MappingMode;
import com.example.MTConnect.config.MappingProperties;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

/**Зачем: MappingRegistry по имени колонки CSV возвращает, как её превратить в MTConnect-элемент — то есть отдаёт MapRule
 с category (Samples/Events/Conditions), type, subType, units, id, coord.

Откуда берёт правило — ровно два режима, выбирается в application.yml (sim.mapping.mode):

EXPLICIT_ONLY — ищет только в явных правилах, загруженных из внешнего YAML-файла (sim.mapping.rulesFile). Совпадение
 точному ключу csvKey.

HEURISTIC_ONLY — строит правило авто-эвристикой по наборам регулярных выражений (для осей X/Y/Z/B/C, шпинделя,
 тока/момента/мощности/напряжения, температур, RPM/скоростей, POS_X/Y/Z/S, FREAL).

Как работает вызов:

Метод find(csvKey) → Optional<MapRule>.

В EXPLICIT_ONLY: возвращает правило из карты byKey (если есть), иначе empty.

В HEURISTIC_ONLY: прогоняет csvKey через список паттернов; при первом совпадении генерирует MapRule с нужными
 type/units/subType/id. Если ни один паттерн не подошёл — empty.

Где используется:
Выше по потоку, например в RowMapper: бежим по колонкам строки CSV → для каждой зовём
mappingRegistry.find(key) → по найденным правилам собираем MTConnect Samples/Events/... для XML ответа. */

public class MappingRegistry {

    // fields...
    private final Map<String, MapRule> byKey = new HashMap<>();
    private final MappingMode mode;

    // Паттерны авто-эвристики 

    // Оси позиция: X/Y/З + (добавлены B/C на будущее)
    private static final Pattern P_AXIS_POS =
            Pattern.compile("^(X|Y|Z|B|C)-axis_position_(MCS|WCS|[Ee][Rr][Rr][Oo][Rr])$");

    // Оси подача (скорость перемещения)
    private static final Pattern P_AXIS_FEED =
            Pattern.compile("^(X|Y|Z|B|C)-axis_feed$");

    // Позиция шпинделя
    private static final Pattern P_SPINDLE_POS =
            Pattern.compile("^Spindle_position_(MCS|WCS|[Ee][Rr][Rr][Oo][Rr])$");

    // Скорость вращения (в т.ч. smoothed_speed/угловая скорость)
    private static final Pattern P_ROT_SPEED_GENERIC =
            Pattern.compile("^(Spindle_speed|Spindle_angular_velocity|.*_smoothed_speed.*)$");

    // Электрика/механика: ток/момент/мощность/напряжение
    private static final Pattern P_CURRENT = Pattern.compile("^.*_smoothed_current.*$");
    private static final Pattern P_TORQUE  = Pattern.compile("^.*_smoothed_torque.*$");
    private static final Pattern P_POWER   = Pattern.compile("^.*_smoothed_active_power.*$");
    private static final Pattern P_VOLTAGE = Pattern.compile("^.*(intermediate_circuit_voltage|smoothed_DC_voltage).*$");

    // Температуры
    private static final Pattern P_TEMPERATURE =
            Pattern.compile("^(.*_Motor_temperature|Spindle_motor_temperature|General_temperature|Temperature)$");

    // Программные/операторские события
    private static final Pattern P_PROGRAM_EVENT =
            Pattern.compile("^(Operation_mode|Program_status|Program_Start|Program_Stop|Program_.*|Label_\\d+)$");

    // Прочие статусы оборудования (двери/магазины/кнопки/индикаторы)
    private static final Pattern P_MISC_EVENT =
            Pattern.compile("^(Magazine_.*|Tool_.*|Door_.*|LED_.*|Chip_flushing_button|Spindle_flushing_button|Rapid_traverse_button.*|Door_status_locked)$");

    //  позиционирование POS_X/Y/Z и POS_S
    private static final Pattern P_POS_XYZ = Pattern.compile("^POS_(X|Y|Z)$");
    private static final Pattern P_POS_S   = Pattern.compile("^POS_S$");

    //  фактическая подача (мм/мин) — FREAL
    private static final Pattern P_FEEDRATE = Pattern.compile("^(FREAL)$");

    //  RPM_avg|SREAL — об/мин
    private static final Pattern P_RPM = Pattern.compile("^(RPM_avg|SREAL)$");

    //  ускорения A_x/A_y/A_z
    private static final Pattern P_ACCEL = Pattern.compile("^A_(x|y|z)$", Pattern.CASE_INSENSITIVE);

    //  длины/износ/режимы (мм)
    private static final Pattern P_LENGTH_MM = Pattern.compile("^(ae|ap|VB|ToolDiameter)$");

    //  паспорт/описательные поля → Events
    private static final Pattern P_D2_MISC_EVENT =
            Pattern.compile("^(Insert|Edge|Lubrication|Machine|ToolManufacturer|ToolMaterial|ToolReference|WorkpieceMaterial|material|_file_name|ID|ToolID)$");

    //  производные метрики (stats/sync) → Events
    private static final Pattern P_STATS_SYNC =
            Pattern.compile("^(.*_start|.*_end|.*_max|.*_kurt|.*_rms|.*_skew|.*_var|.*_ptp|.*_speckurt|.*_specskew|.*_wavenergy|.*_signal|.*_peak_.*|.*_freq_peaks|.*_peaks_value_.*|.*_sec_.*)$");

    private static final List<Pattern> PATTERNS = List.of(
            P_AXIS_POS, P_AXIS_FEED, P_SPINDLE_POS, P_ROT_SPEED_GENERIC,
            P_CURRENT, P_TORQUE, P_POWER, P_VOLTAGE, P_TEMPERATURE,
            P_PROGRAM_EVENT, P_MISC_EVENT,
            P_POS_XYZ, P_POS_S, P_FEEDRATE, P_RPM, P_ACCEL, P_LENGTH_MM,
            P_D2_MISC_EVENT, P_STATS_SYNC
    );

    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    // constructors...
    public MappingRegistry(MappingProperties props) {
        this.mode = props.getMode() == null ? MappingMode.EXPLICIT_ONLY : props.getMode();
        if (props.getRules() != null) {
            for (var r : props.getRules()) {
                byKey.put(r.csvKey(), r);
            }
        }
    }

    // methods...

    /// Два режима: EXPLICIT_ONLY (только файл) или HEURISTIC_ONLY (только авто-эвристика)
    public Optional<MapRule> find(String csvKey) {
        if (csvKey == null || csvKey.isEmpty()) return Optional.empty();

        if (mode == MappingMode.EXPLICIT_ONLY) {
            return Optional.ofNullable(byKey.get(csvKey));
        }

        if (mode == MappingMode.HEURISTIC_ONLY) {
            for (var p : PATTERNS) {
                Matcher m = p.matcher(csvKey);
                if (m.matches()) {
                    return Optional.of(generateRuleFromPattern(p, m, csvKey));
                }
            }
            return Optional.empty();
        }

        return Optional.empty();
    }

    /// Генерация MapRule по совпавшему паттерну
    private MapRule generateRuleFromPattern(Pattern p, Matcher m, String key) {
        if (p == P_AXIS_POS) {
            String coord = m.group(1); // X|Y|Z|B|C
            String raw = m.group(2);   // MCS|WCS|error
            String sub = raw.equalsIgnoreCase("error") ? "ERROR" : raw.toUpperCase(Locale.ROOT);
            return new MapRule(
                    key, "Samples", "Position",
                    coord.toLowerCase(Locale.ROOT) + "_" + sub.toLowerCase(Locale.ROOT),
                    coord, sub, "MILLIMETER"
            );
        }

        if (p == P_AXIS_FEED) {
            String coord = m.group(1); // X|Y|Z|B|C
            return new MapRule(
                    key, "Samples", "PathFeedrate",
                    coord.toLowerCase(Locale.ROOT) + "_feed",
                    coord, null, "MILLIMETER/SECOND"
            );
        }

        if (p == P_SPINDLE_POS) {
            String raw = m.group(1); // MCS|WCS|error
            String sub = raw.equalsIgnoreCase("error") ? "ERROR" : raw.toUpperCase(Locale.ROOT);
            return new MapRule(
                    key, "Samples", "RotaryPosition",
                    "spindle_pos_" + sub.toLowerCase(Locale.ROOT),
                    "S", sub, "DEGREE"
            );
        }

        if (p == P_ROT_SPEED_GENERIC) {
            return new MapRule(
                    key, "Samples", "RotaryVelocity",
                    normalizeId(key, "rpm"),
                    "S", null, "REVOLUTION/MINUTE"
            );
        }

        if (p == P_CURRENT) {
            return new MapRule(key, "Samples", "ElectricCurrent",
                    normalizeId(key, "current"),
                    null, null, "AMPERE");
        }
        if (p == P_TORQUE) {
            return new MapRule(key, "Samples", "Torque",
                    normalizeId(key, "torque"),
                    null, null, "NEWTON_METER");
        }
        if (p == P_POWER) {
            return new MapRule(key, "Samples", "Power",
                    normalizeId(key, "power"),
                    null, null, "WATT");
        }
        if (p == P_VOLTAGE) {
            return new MapRule(key, "Samples", "Voltage",
                    normalizeId(key, "voltage"),
                    null, null, "VOLT");
        }

        if (p == P_TEMPERATURE) {
            return new MapRule(key, "Samples", "Temperature",
                    normalizeId(key, "temp"),
                    null, null, "CELSIUS");
        }

        if (p == P_PROGRAM_EVENT) {
            return new MapRule(key, "Events", "StringEvent",
                    normalizeId(key, "event"),
                    null, null, null);
        }

        if (p == P_MISC_EVENT) {
            return new MapRule(key, "Events", "StringEvent",
                    normalizeId(key, "event"),
                    null, null, null);
        }

        if (p == P_POS_XYZ) {
            String coord = m.group(1).toUpperCase(Locale.ROOT);
            return new MapRule(
                    key, "Samples", "Position",
                    "pos_" + coord.toLowerCase(Locale.ROOT),
                    coord, null, "MILLIMETER"
            );
        }

        if (p == P_POS_S) {
            return new MapRule(
                    key, "Samples", "RotaryPosition",
                    "pos_s",
                    "S", null, "DEGREE"
            );
        }

        if (p == P_FEEDRATE) {
            return new MapRule(
                    key, "Samples", "PathFeedrate",
                    "feedrate",
                    null, null, "MILLIMETER/MINUTE"
            );
        }

        if (p == P_RPM) {
            return new MapRule(
                    key, "Samples", "RotaryVelocity",
                    normalizeId(key, "rpm"),
                    "S", null, "REVOLUTION/MINUTE"
            );
        }

        if (p == P_ACCEL) {
            String coord = m.group(1).toUpperCase(Locale.ROOT);
            return new MapRule(
                    key, "Samples", "Acceleration",
                    "acc_" + coord.toLowerCase(Locale.ROOT),
                    coord, null, "METER/SECOND^2"
            );
        }

        if (p == P_LENGTH_MM) {
            return new MapRule(
                    key, "Samples", "Length",
                    normalizeId(key, "mm"),
                    null, null, "MILLIMETER"
            );
        }

        if (p == P_D2_MISC_EVENT) {
            return new MapRule(
                    key, "Events", "StringEvent",
                    normalizeId(key, "meta"),
                    null, null, null
            );
        }

        if (p == P_STATS_SYNC) {
            return new MapRule(
                    key, "Events", "StringEvent",
                    normalizeId(key, "stat"),
                    null, null, null
            );
        }

        // дефолт
        return new MapRule(key, "Events", "StringEvent", key, null, null, null);
    }

    private static String normalizeId(String key, String suffix) {
        String base = NON_ALNUM.matcher(key.toLowerCase(Locale.ROOT)).replaceAll("_");
        return base + "_" + suffix;
    }

    // getters/setters...
}
