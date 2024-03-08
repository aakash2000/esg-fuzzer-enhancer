package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import com.example.staticanalysis.analysis.data.DFFManager;
import soot.SootMethod;
import soot.Unit;

import java.util.Collections;
import java.util.Set;

public class ReturnFlowFunction extends GenericFlowFunction {

    private Unit callSite;
    private SootMethod calleeMethod;
    private Unit exitStmt;
    private Unit returnSite;
    public ReturnFlowFunction(Unit callSite, SootMethod calleeMethod, Unit exitStmt, Unit returnSite) {
        this.callSite = callSite;
        this.calleeMethod = calleeMethod;
        this.exitStmt = exitStmt;
        this.returnSite = returnSite;
    }

    @Override
    public Set<DFF> computeTargets(DFF dff) {
        return Collections.singleton(dff);
    }
}
