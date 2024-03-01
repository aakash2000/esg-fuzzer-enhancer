package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import heros.FlowFunction;
import soot.SootMethod;
import soot.Unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

public class ConstantPropagationFlowFunctions {

    private static final Logger logger = LoggerFactory.getLogger(ConstantPropagationFlowFunctions.class);

    public static FlowFunction<DFF> getNormalFlowFunction(Unit curr, Unit succ) {
        return new NormalFlowFunction(curr, succ, "normal");
    }

    public static FlowFunction<DFF> getCallFlowFunction(Unit callStmt, SootMethod destinationMethod) {
        return new NormalFlowFunction(callStmt, callStmt, "call");
    }

    public static FlowFunction<DFF> getReturnFlowFunction(Unit callSite, SootMethod calleeMethod, Unit exitStmt, Unit returnSite) {
        return new NormalFlowFunction(callSite, returnSite, "return");
    }

    public static FlowFunction<DFF> getCallToReturnFlowFunction(Unit unit, Unit n1) {
        return new NormalFlowFunction(unit, n1, "call-to-return");
    }
}