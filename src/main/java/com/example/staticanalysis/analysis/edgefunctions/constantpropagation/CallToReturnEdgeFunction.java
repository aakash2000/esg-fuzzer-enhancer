package com.example.staticanalysis.analysis.edgefunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import soot.Unit;
import soot.Value;

public class CallToReturnEdgeFunction extends GenericEdgeFunction {

    private Unit unit;
    private DFF dff;
    private Unit n1;
    private DFF d1;
    public CallToReturnEdgeFunction(Unit unit, DFF dff, Unit n1, DFF d1) {
        this.unit = unit;
        this.dff = dff;
        this.n1 = n1;
        this.d1 = d1;
    }

    @Override
    public Value computeTarget(Value value) {
        return value;
    }
}
