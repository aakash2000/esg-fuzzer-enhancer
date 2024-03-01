package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Unit;

import java.util.*;

public class NormalFlowFunction extends GenericFlowFunction {

    private static final Logger logger = LoggerFactory.getLogger(NormalFlowFunction.class);

    private Unit curr;
    private Unit succ;

    private String type;

    public NormalFlowFunction(Unit curr, Unit succ, String type) {
        this.curr = curr;
        this.succ = succ;
        this.type = type;
    }
    @Override
    public Set<DFF> computeTargets(DFF source) {
        logger.info("computeTargets " + this.curr + " -> " + this.succ + " type: " + this.type);
        Set<DFF> returnSet = new HashSet<>();
        if (Objects.equals(this.type, "normal")) {
            this.curr.getDefBoxes().forEach(defBox -> {
                if (defBox.getValue().getType().toQuotedString().equals("int")) {
                    DFF newDFF = new DFF(curr, defBox.getValue());
                    System.out.println("pushing to set"+ newDFF);
                    returnSet.add(newDFF);
                }
            });
            if (returnSet.isEmpty()) {
                return Collections.singleton(source);
            } else {
                return returnSet;
            }
        }
        return Collections.singleton(source);
    }

}
