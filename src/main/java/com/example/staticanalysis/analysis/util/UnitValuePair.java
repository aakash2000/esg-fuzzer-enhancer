package com.example.staticanalysis.analysis.util;

import soot.Unit;
import soot.Value;

public class UnitValuePair {
    private final Unit unit;
    private final Value value;

    public UnitValuePair(Unit unit, Value value) {
        this.unit = unit;
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UnitValuePair{" +
                "unit=" + unit +
                ", value=" + value +
                '}';
    }

}
