package com.example.staticanalysis.analysis.edgefunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

public class CallEdgeFunction extends GenericEdgeFunction {

    private Unit unit;
    private DFF dff;
    private SootMethod sootMethod;
    private DFF d1;
    public CallEdgeFunction(Unit unit, DFF dff, SootMethod sootMethod, DFF d1) {
        this.unit = unit;
        this.dff = dff;
        this.sootMethod = sootMethod;
        this.d1 = d1;
    }

    @Override
    public Value computeTarget(Value value) {
        return value;
    }
}
