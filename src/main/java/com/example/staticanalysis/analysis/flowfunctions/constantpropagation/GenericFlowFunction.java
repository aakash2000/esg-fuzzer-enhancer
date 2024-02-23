package com.example.staticanalysis.analysis.flowfunctions.constantpropagation;

import com.example.staticanalysis.analysis.data.DFF;
import heros.FlowFunction;

import java.util.Collections;
import java.util.Set;

public abstract class GenericFlowFunction implements FlowFunction<DFF> {
    private static class CompositeFlowFunction extends GenericFlowFunction {
        private FlowFunction<DFF> firstFunction;
        private FlowFunction<DFF> secondFunction;

        public CompositeFlowFunction(FlowFunction<DFF> first, FlowFunction<DFF> second) {
            this.firstFunction = first;
            this.secondFunction = second;
        }

        @Override
        public Set<DFF> computeTargets(DFF source) {
            return Collections.singleton(source);
        }
    }
}
