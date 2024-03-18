package com.example.staticanalysis.analysis.edgefunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Unit;
import soot.Value;

public class NormalEdgeFunction extends GenericEdgeFunction {
    private static final Logger logger = LoggerFactory.getLogger(NormalEdgeFunction.class);

    private Unit unit;
    private DFF dff;
    private Unit n1;
    private DFF d1;

    public NormalEdgeFunction(Unit unit, DFF dff, Unit n1, DFF d1) {
        this.unit = unit;
        this.dff = dff;
        this.n1 = n1;
        this.d1 = d1;
    }

    public NormalEdgeFunction() {
        this.unit = null;
        this.dff = null;
        this.n1 = null;
        this.d1 = null;
    }

    @Override
    public Value computeTarget(Value source) {
        logger.info("NormalEdgeFunction");
        System.out.println("NormalEdgeFunction C" + source.toString() + " " + unit.toString() + " " + dff.toString() + " " + n1.toString() + " " + d1.toString());
        return source;
    }
}

