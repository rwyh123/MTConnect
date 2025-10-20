package com.example.MTConnect.mapping;

import java.util.Map;

/**
 ValueCodec — это маленькая статическая «библиотека парсеров», которая берёт сырые строковые значения из строки CSV (Map<String,String> row)
 и безопасно пытается привести их к нужному типу: Double, Boolean, Long, String.
 Его задача — централизовать и унифицировать разбор значений, чтобы вся логика «как именно понимать текст из датасета» была в одном месте, а не размазана по коду.
*/

public final class ValueCodec {

    // fields...

    // constructor...
    private ValueCodec() {}

    // methods...
    public static Double asDouble(Map<String,String> row, String key) {
        var s = row.get(key);
        if (s == null || s.isBlank()) return null;
        s = s.trim().replace(',', '.');
        try { return Double.valueOf(s); } catch (NumberFormatException e) { return null; }
    }

    public static Boolean asBool(Map<String,String> row, String key) {
        var s = row.get(key);
        if (s == null) return null;
        s = s.trim().toUpperCase();
        return switch (s) {
            case "1","TRUE","ON","OPEN","LOCKED","ACTIVE","RUN","RUNNING" -> true;
            case "0","FALSE","OFF","CLOSED","UNLOCKED","INACTIVE","STOP","IDLE" -> false;
            default -> null;
        };
    }

    public static Long asLong(Map<String,String> row, String key) {
        try { return Long.valueOf(row.get(key).trim()); } catch (Exception e) { return null; }
    }

    public static String asText(Map<String,String> row, String key) {
        var s = row.get(key);
        return (s == null || s.isBlank()) ? null : s.trim();
    }
    // getters/setters...
}
