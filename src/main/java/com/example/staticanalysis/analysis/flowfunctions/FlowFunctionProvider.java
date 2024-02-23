package com.example.staticanalysis.analysis.flowfunctions;

import com.example.staticanalysis.analysis.data.AnalysisType;
import com.example.staticanalysis.analysis.flowfunctions.constantpropagation.ConstantPropagationFlowFunctions;
import heros.FlowFunction;
import heros.FlowFunctions;
import soot.SootMethod;
import soot.Unit;
import com.example.staticanalysis.analysis.data.DFF;

public class FlowFunctionProvider {

    public FlowFunctions<Unit, DFF, SootMethod> getFlowFunctions(AnalysisType analysisType) {
        switch (analysisType) {
            case CONSTANT_PROPAGATION:
                return new FlowFunctions<Unit, DFF, SootMethod>() {
                    @Override
                    public FlowFunction<DFF> getNormalFlowFunction(Unit curr, Unit succ) {
                        return ConstantPropagationFlowFunctions.getNormalFlowFunction(curr, succ);
                    }

                    @Override
                    public FlowFunction<DFF> getCallFlowFunction(Unit callStmt, SootMethod destinationMethod) {
                        return ConstantPropagationFlowFunctions.getCallFlowFunction(callStmt, destinationMethod);
                    }

                    @Override
                    public FlowFunction<DFF> getReturnFlowFunction(Unit callSite, SootMethod calleeMethod, Unit exitStmt, Unit returnSite) {
                        return ConstantPropagationFlowFunctions.getReturnFlowFunction(callSite, calleeMethod, exitStmt, returnSite);
                    }

                    @Override
                    public FlowFunction<DFF> getCallToReturnFlowFunction(Unit unit, Unit n1) {
                        return ConstantPropagationFlowFunctions.getCallToReturnFlowFunction(unit, n1);
                    }

                    // Implement other methods as needed...
                };
            // Cases for other analysis types...
            case INTERVAL_ANALYSIS:
                break;
            default:
                throw new IllegalArgumentException("Unsupported analysis type");
        }
        return null;
    }
}
