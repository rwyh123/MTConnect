package com.example.MTConnect.config;

public record MapRule(
        String csvKey,
        String container,     // "Samples" | "Events" | "Conditions"
        String mtcType,       // "Position","RotaryVelocity","Temperature","DiscreteEvent","StringEvent","LongEvent",...
        String dataItemId,
        String coordinate,    // "X","Y","Z","B","C","S" | null
        String subType,       // "MCS","WCS","ERROR" | null
        String nativeUnits    // "MILLIMETER","DEGREE","REVOLUTION/MINUTE",... | null
) {}
