package com.example.staticanalysis.analysis.util;

import soot.Value;
import java.util.Objects;

public class ValueValuePair {
    private final Value variableName;
    private final Value variableValue;

    public ValueValuePair(Value variableName, Value variableValue) {
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    public Value getVariableName() {
        return variableName;
    }

    public Value getVariableValue() {
        return variableValue;
    }

    @Override
    public String toString() {
        return "ValueValuePair{" +
                "variableName=" + variableName +
                ", variableValue=" + variableValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueValuePair that = (ValueValuePair) o;
        return Objects.equals(variableName, that.variableName) &&
                Objects.equals(variableValue, that.variableValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variableName, variableValue);
    }
}
