package com.example.staticanalysis.analysis.edgefunctions.constantpropagation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Value;

public class NormalEdgeFunction extends GenericEdgeFunction {
    private static final Logger logger = LoggerFactory.getLogger(NormalEdgeFunction.class);
    @Override
    public Value computeTarget(Value source) {
        logger.info("NormalEdgeFunction");
        return source;
    }
}

