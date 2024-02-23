package com.example.staticanalysis.analysis;

import com.example.staticanalysis.analysis.data.AnalysisType;
import com.example.staticanalysis.analysis.data.DFF;
import com.example.staticanalysis.analysis.edgefunctions.EdgeFunctionProvider;
import com.example.staticanalysis.analysis.flowfunctions.FlowFunctionProvider;
import heros.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import heros.IDETabulationProblem;

import java.util.*;

    public class ESGGenerationProblem implements IDETabulationProblem<Unit, DFF, SootMethod, Value, BiDiInterproceduralCFG<Unit, SootMethod>> {
    private final JimpleBasedInterproceduralCFG icfg;
    private final FlowFunctionProvider flowFunctionProvider = new FlowFunctionProvider();

    private final EdgeFunctionProvider edgeFunctionProvider = new EdgeFunctionProvider();

    private static final Logger logger = LoggerFactory.getLogger(ESGGenerationProblem.class);

    private final Set<DFF> data_facts;

    public final static Value TOP = IntConstant.v(Integer.MIN_VALUE); // Unknown

    public final static Value BOTTOM = IntConstant.v(Integer.MAX_VALUE); // Not Constant

    public ESGGenerationProblem(SootMethod method, Set<DFF> data_facts) {
        logger.info("ESGGenerationProblem");
        this.icfg = new JimpleBasedInterproceduralCFG();

        // Do not get facts from outside, compute in flow functions
        this.data_facts = data_facts;
        // Initialize other necessary components
    }

    @Override
    public Map<Unit, Set<DFF>> initialSeeds() {
        logger.info("initialSeeds");
        Map<Unit, Set<DFF>> seeds = new HashMap<>();
        this.data_facts.forEach(f -> {
            if (f.getPair() != null) {
                seeds.put(f.getPair().getUnit(), Collections.singleton(f));
            }
        });
        /* logger.info("Seeds: ");
        seeds.forEach((k, v) -> {
            logger.info(k + " : " + v);
        });*/
        return seeds;
    }

    @Override
    public DFF zeroValue() {
        logger.info("zeroValue");
        Value zero;
        zero = IntConstant.v(0);
        return new DFF(zero);
    }

    @Override
    public FlowFunctions<Unit, DFF, SootMethod> flowFunctions() {
        logger.info("flowFunctions");

        // Specify the type of analysis here
        AnalysisType analysisType = AnalysisType.CONSTANT_PROPAGATION;

        // Get the flow functions for the specified analysis type
        return flowFunctionProvider.getFlowFunctions(analysisType);
    }

    @Override
    public BiDiInterproceduralCFG<Unit, SootMethod> interproceduralCFG() {
        // Return the interprocedural control flow graph used by this problem
        logger.info("interproceduralCFG");
        return this.icfg;
    }

    @Override
    public EdgeFunctions<Unit, DFF, SootMethod, Value> edgeFunctions() {
        logger.info("edgeFunctions");

        // Specify the type of analysis here
        AnalysisType analysisType = AnalysisType.CONSTANT_PROPAGATION;

        // Get the edge functions for the specified analysis type
        return edgeFunctionProvider.getEdgeFunctions(analysisType);
    }

    @Override
    public MeetLattice<Value> meetLattice() {
        logger.info("meetLattice");
        // TODO: Implement meet lattice
        return new MeetLattice<Value>() {
            @Override
            public Value topElement() {
                return TOP;
            }

            @Override
            public Value bottomElement() {
                return BOTTOM;
            }

            @Override
            public Value meet(Value left, Value right) {
                if(left==TOP){
                    return right;
                }
                if(right==TOP){
                    return left;
                }
                if(left==BOTTOM){
                    return left;
                }
                if(right==BOTTOM){
                    return right;
                }
                if(left==right){
                    return left;
                }else{
                    return BOTTOM;
                }
            }
        };
    }

    @Override
    public EdgeFunction<Value> allTopFunction() {
        logger.info("allTopFunction");
        // TODO: Implement all top function
        return null;
    }

    @Override
    public boolean followReturnsPastSeeds() {
        return true;
    }

    @Override
    public boolean autoAddZero() {
        return false;
    }

    @Override
    public int numThreads() {
        return 1;
    }

    @Override
    public boolean computeValues() {
        return true;
    }

    @Override
    public boolean recordEdges() {
        return true;
    }
}
