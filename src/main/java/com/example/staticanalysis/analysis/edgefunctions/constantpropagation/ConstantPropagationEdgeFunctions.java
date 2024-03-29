package com.example.staticanalysis.analysis.edgefunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import heros.EdgeFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

public class ConstantPropagationEdgeFunctions {

    private static final Logger logger = LoggerFactory.getLogger(ConstantPropagationEdgeFunctions.class);
    public static EdgeFunction<Value> getNormalEdgeFunction(Unit unit, DFF dff, Unit n1, DFF d1) {
        return new NormalEdgeFunction(unit, dff, n1, d1);
    }

    public static EdgeFunction<Value> getCallEdgeFunction(Unit unit, DFF dff, SootMethod sootMethod, DFF d1) {
        return new CallEdgeFunction(unit, dff, sootMethod, d1);
    }

    public static EdgeFunction<Value> getReturnEdgeFunction(Unit unit, SootMethod sootMethod, Unit n1, DFF dff, Unit n2, DFF d1) {
        return new ReturnEdgeFunction(unit, sootMethod, n1, dff, n2, d1);
    }

    public static EdgeFunction<Value> getCallToReturnEdgeFunction(Unit unit, DFF dff, Unit n1, DFF d1) {
        return new CallToReturnEdgeFunction(unit, dff, n1, d1);
    }
}
