package com.example.staticanalysis.analysis.util;

import soot.Unit;
import soot.Value;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnitValuePair that = (UnitValuePair) o;
        return Objects.equals(unit, that.unit) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, value);
    }

}
