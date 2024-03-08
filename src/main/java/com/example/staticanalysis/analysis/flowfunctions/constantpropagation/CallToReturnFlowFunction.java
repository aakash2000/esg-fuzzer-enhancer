package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import soot.Unit;

import java.util.Collections;
import java.util.Set;

public class CallToReturnFlowFunction extends GenericFlowFunction {

    private Unit unit;
    private Unit n1;
    public CallToReturnFlowFunction(Unit unit, Unit n1) {
        this.unit = unit;
        this.n1 = n1;
    }

    @Override
    public Set<DFF> computeTargets(DFF dff) {
        return Collections.singleton(dff);
    }
}
