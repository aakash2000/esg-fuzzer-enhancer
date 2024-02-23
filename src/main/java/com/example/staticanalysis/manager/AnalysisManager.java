package com.example.staticanalysis.manager;

import com.example.staticanalysis.analysis.ConstantPropagationAnalysis;
import com.example.staticanalysis.analysis.ICFGGeneration;
import com.example.staticanalysis.analysis.data.DFF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.options.Options;

import java.util.Map;
import java.util.Set;

public class AnalysisManager {
    public static final ConstantPropagationAnalysis cp = new ConstantPropagationAnalysis();
    private static final Logger logger = LoggerFactory.getLogger(AnalysisManager.class);
    
    public static void setSootClassPath() {
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(System.getProperty("user.dir") + "/target/classes/:"+System.getProperty("user.dir") + "/src/main/java/");
    }

    public static void setSootInitOptions() {
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jb", "optimize:false");
        Options.v().setPhaseOption("jb.cp-ule", "enabled:false");
        Options.v().setPhaseOption("jb.uce1", "enabled:false");
        Options.v().setPhaseOption("jb.uce2", "enabled:false");
    }

    public static void setSootInitOptionsForICFG() {
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jb", "optimize:false");
        Options.v().setPhaseOption("jb.cp-ule", "enabled:false");
        Options.v().setPhaseOption("jb.uce1", "enabled:false");
        Options.v().setPhaseOption("jb.uce2", "enabled:false");
        Options.v().setPhaseOption("cg.spark", "enabled:true");
        Options.v().setPhaseOption("cg.spark", "verbose:true");
        Options.v().setPhaseOption("cg.spark", "on-fly-cg:true");
    }

    public static void setDebugOptions() {
        Options.v().setPhaseOption("jb", "verbose:true"); // Optional, for debugging
        Options.v().set_debug(true);
    }

    public static void setConstantPropagationAnalysis() {
        PackManager.v().getPack("jtp").add(new Transform("jtp.myConstProp", cp));
    }

    public static void runAnalysis() {
        PackManager.v().runBodyPacks();
    }

    public static void resetSoot() {
        G.reset();
    }

    public static void loadClass(String className) {
        logger.info("Resolving class...");
        SootClass c = Scene.v().forceResolve(className, SootClass.BODIES);

        logger.info("Loading basic classes...");
        Scene.v().loadBasicClasses();

        logger.info("Loading necessary classes...");
        Scene.v().loadNecessaryClasses();

        if (!Scene.v().containsClass(className) || c == null) {
            logger.info("Could not find class " + className);
            System.exit(1);
        }
        c.setApplicationClass();
    }

    public static void runICFGAnalysis(String className, Set<DFF> data_facts) {
        ICFGGeneration.generateCallGraph(className, data_facts);
    }

    public static Map<String, Map<Value, Value[]>> getResults() {
        return cp.getMethodConstantValues();
    }

    public static Set<DFF> getResultsICFG() {
        return cp.getDataFacts();
    }
}
