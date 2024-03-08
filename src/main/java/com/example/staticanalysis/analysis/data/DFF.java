package com.example.staticanalysis.analysis.data;

import com.example.staticanalysis.analysis.util.UnitValuePair;
import com.example.staticanalysis.analysis.util.ValueValuePair;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

import java.util.Objects;

public class DFF {

    private final UnitValuePair pair;

    private final ValueValuePair valuePair;

    private final Value fact;

    private final SootMethod method;

    public DFF(Unit unit, Value fact, SootMethod method) {
        this.pair = new UnitValuePair(unit, fact);
        this.fact = fact;
        this.method = method;
        this.valuePair = null;
    }

    public DFF(Unit unit, Value fact) {
        this.pair = new UnitValuePair(unit, fact);
        this.fact = fact;
        this.method = null;
        this.valuePair = null;
    }

    public DFF(Value fact, SootMethod method) {
        this.fact = fact;
        this.pair = null;
        this.method = method;
        this.valuePair = null;
    }

    public DFF(Value fact) {
        this.fact = fact;
        this.pair = null;
        this.method = null;
        this.valuePair = null;
    }

    public DFF(Value variableName, Value variableValue, SootMethod method) {
        this.valuePair = new ValueValuePair(variableName, variableValue);
        this.fact = variableValue;
        this.method = method;
        this.pair = null;
    }

    public SootMethod getMethod() {
        return method;
    }

    public Value getFact() {
        return fact;
    }

    public UnitValuePair getPair() {
        return pair;
    }

    public ValueValuePair getValuePair() {
        return valuePair;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DFF{");
        if (pair != null) {
            sb.append("pair=").append(pair);
        }
        if (fact != null) {
            if (sb.length() > 4) sb.append(", ");
            sb.append("fact=").append(fact);
        }
        if (method != null) {
            if (sb.length() > 4) sb.append(", ");
            sb.append("method=").append(method);
        }
        if (valuePair != null) {
            if (sb.length() > 4) sb.append(", ");
            sb.append("valuePair=").append(valuePair);
        }
        sb.append("}");
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DFF dff = (DFF) o;
        return Objects.equals(pair, dff.pair) &&
                Objects.equals(fact, dff.fact) &&
                Objects.equals(method, dff.method) &&
                Objects.equals(valuePair, dff.valuePair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair, fact, method, valuePair);
    }

}