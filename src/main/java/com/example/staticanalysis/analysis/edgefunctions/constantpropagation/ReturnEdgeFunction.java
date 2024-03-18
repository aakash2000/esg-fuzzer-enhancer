package com.example.staticanalysis.analysis.edgefunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

public class ReturnEdgeFunction extends GenericEdgeFunction {

    private Unit unit;
    private SootMethod sootMethod;
    private Unit n1;
    private DFF dff;
    private Unit n2;
    private DFF d1;

    public ReturnEdgeFunction(Unit unit, SootMethod sootMethod, Unit n1, DFF dff, Unit n2, DFF d1) {
        this.unit = unit;
        this.sootMethod = sootMethod;
        this.n1 = n1;
        this.dff = dff;
        this.n2 = n2;
        this.d1 = d1;
    }

    @Override
    public Value computeTarget(Value value) {
        return value;
    }
}
