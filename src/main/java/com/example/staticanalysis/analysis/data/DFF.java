package com.example.staticanalysis.analysis.data;

import com.example.staticanalysis.analysis.util.UnitValuePair;
import soot.Unit;
import soot.Value;

import java.util.Objects;

public class DFF {

    private final UnitValuePair pair;

    private final Value fact;

    public DFF(Unit unit, Value fact) {
        this.pair = new UnitValuePair(unit, fact);
        this.fact = fact;
    }

    public DFF(Value fact) {
        this.fact = fact;
        this.pair = null;
    }

    public Value getFact() {
        return fact;
    }

    public UnitValuePair getPair() {
        return pair;
    }

    @Override
    public String toString() {
        return "DFF{" +
                "pair=" + pair +
                ", fact=" + fact +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFF dff = (DFF) o;
        return Objects.equals(pair, dff.pair) &&
                Objects.equals(fact, dff.fact);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair, fact);
    }

    public DFF getBottom() {
        return new DFF(null);
    }

    public boolean isConstant() {
        return pair == null;
    }
}