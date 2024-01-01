package com.example.staticanalysis.analysis;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Transform;

import java.util.Map;

public class ConstantPropagationAnalysis extends BodyTransformer {

    public static void main(String[] args) {
        PackManager.v().getPack("jtp").add(new Transform("jtp.myConstProp", new ConstantPropagationAnalysis()));
        soot.Main.main(args);
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {

    }
}

