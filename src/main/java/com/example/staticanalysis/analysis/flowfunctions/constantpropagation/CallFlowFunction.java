package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import com.example.staticanalysis.analysis.data.DFFManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.ImmediateBox;

import java.util.Collections;
import java.util.Set;

public class CallFlowFunction extends GenericFlowFunction {

    private static final Logger logger = LoggerFactory.getLogger(NormalFlowFunction.class);

    private Unit callStmt;
    private SootMethod destinationMethod;

    public DFFManager dffManager = DFFManager.getInstance();

    public CallFlowFunction(Unit callStmt, SootMethod destinationMethod) {
        this.callStmt = callStmt;
        this.destinationMethod = destinationMethod;
    }

    @Override
    public Set<DFF> computeTargets(DFF dff) {
        if (this.callStmt != null && this.callStmt.getUseBoxes() != null) {
            this.callStmt.getUseBoxes().forEach(useBox -> {
                if (useBox.getClass() == ImmediateBox.class && !destinationMethod.isJavaLibraryMethod()) {
                    DFF newDFF = new DFF(callStmt, useBox.getValue(), destinationMethod);
                    dffManager.addDFF(newDFF);
                }
            });
        }
        return dffManager.getDFFs();
    }
}
