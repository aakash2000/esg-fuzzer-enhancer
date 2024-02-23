package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class NormalFlowFunction extends GenericFlowFunction {

    private static final Logger logger = LoggerFactory.getLogger(NormalFlowFunction.class);
    @Override
    public Set<DFF> computeTargets(DFF source) {
        logger.info("NormalFlowFunction");
        return Collections.singleton(source);
    }

}
