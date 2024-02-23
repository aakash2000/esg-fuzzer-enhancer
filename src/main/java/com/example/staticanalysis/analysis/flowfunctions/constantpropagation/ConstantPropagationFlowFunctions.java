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
        logger.info("getNormalFlowFunction");
        return new NormalFlowFunction();
    }

    public static FlowFunction<DFF> getCallFlowFunction(Unit callStmt, SootMethod destinationMethod) {
        logger.info("getCallFlowFunction");
        return null;
    }

    public static FlowFunction<DFF> getReturnFlowFunction(Unit callSite, SootMethod calleeMethod, Unit exitStmt, Unit returnSite) {
        logger.info("getReturnFlowFunction");
        return null;
    }

    public static FlowFunction<DFF> getCallToReturnFlowFunction(Unit unit, Unit n1) {
        logger.info("getCallToReturnFlowFunction");
        return null;
    }
}