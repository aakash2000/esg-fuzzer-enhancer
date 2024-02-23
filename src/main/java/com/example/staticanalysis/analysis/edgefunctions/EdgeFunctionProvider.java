package com.example.staticanalysis.analysis.edgefunctions;

import com.example.staticanalysis.analysis.data.AnalysisType;
import com.example.staticanalysis.analysis.edgefunctions.constantpropagation.ConstantPropagationEdgeFunctions;
import heros.EdgeFunction;
import heros.EdgeFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootMethod;
import soot.Unit;
import com.example.staticanalysis.analysis.data.DFF;
import soot.Value;

public class EdgeFunctionProvider {

    private static final Logger logger = LoggerFactory.getLogger(EdgeFunctionProvider.class);
    public EdgeFunctions<Unit, DFF, SootMethod, Value> getEdgeFunctions(AnalysisType analysisType) {
        switch (analysisType) {
            case CONSTANT_PROPAGATION:
                return new EdgeFunctions<Unit, DFF, SootMethod, Value>() {

                    @Override
                    public EdgeFunction<Value> getNormalEdgeFunction(Unit unit, DFF dff, Unit n1, DFF d1) {
                        return ConstantPropagationEdgeFunctions.getNormalEdgeFunction(unit, dff, n1, d1);
                    }

                    @Override
                    public EdgeFunction<Value> getCallEdgeFunction(Unit unit, DFF dff, SootMethod sootMethod, DFF d1) {
                        return ConstantPropagationEdgeFunctions.getCallEdgeFunction(unit, dff, sootMethod, d1);
                    }

                    @Override
                    public EdgeFunction<Value> getReturnEdgeFunction(Unit unit, SootMethod sootMethod, Unit n1, DFF dff, Unit n2, DFF d1) {
                        return ConstantPropagationEdgeFunctions.getReturnEdgeFunction(unit, sootMethod, n1, dff, n2, d1);
                    }

                    @Override
                    public EdgeFunction<Value> getCallToReturnEdgeFunction(Unit unit, DFF dff, Unit n1, DFF d1) {
                        return ConstantPropagationEdgeFunctions.getCallToReturnEdgeFunction(unit, dff, n1, d1);
                    }
                };
            case INTERVAL_ANALYSIS:
                break;
            default:
                throw new IllegalArgumentException("Unsupported analysis type");
        }
        return null;
    }
}
