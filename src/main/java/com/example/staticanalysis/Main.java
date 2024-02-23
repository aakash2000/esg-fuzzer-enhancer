package com.example.staticanalysis;

import com.example.staticanalysis.analysis.data.DFF;
import com.example.staticanalysis.manager.AnalysisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootClass;
import soot.Unit;
import soot.Value;

import java.util.Map;
import java.util.Set;


public class Main {
        private static final Logger logger = LoggerFactory.getLogger(Main.class);
        public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.ConditionalFlowTest"; /* WORKS */
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestInterprocedural"; /* WORKS */
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestLoops";
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestMultiplePaths"; /* WORKS */
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestSwitch";
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.MazeFuzzerTest"; /* ATTENTION REQUIRED */

        public static void main(String[] args) {
            logger.info("Starting analysis...");

            logger.info("=====================================");
            logger.info("Setting Soot init options...");
            AnalysisManager.setSootInitOptions();

            logger.info("Setting debug options...");
            AnalysisManager.setDebugOptions();

            logger.info("Setting Soot class path...");
            AnalysisManager.setSootClassPath();

            logger.info("Loading class...");
            AnalysisManager.loadClass(CLASS_TO_ANALYZE);

            logger.info("Setting analysis...");
            AnalysisManager.setConstantPropagationAnalysis();
            logger.info("Running analysis...");
            AnalysisManager.runAnalysis();
            logger.info("Captured data facts:");
            logger.info("=====================================");
            AnalysisManager.getResultsICFG().forEach(dff -> {
                if (dff.getPair() == null) {
                    logger.info("Fact: " + dff.getFact());
                } else {
                    logger.info("Pair: " + dff.getPair());
                }
            });
            Set<DFF> data_facts = AnalysisManager.getResultsICFG();
            logger.info("=====================================");
            AnalysisManager.resetSoot();
            logger.info("Setting Soot init options...");
            AnalysisManager.setSootInitOptionsForICFG();

            logger.info("Setting debug options...");
            AnalysisManager.setDebugOptions();

            logger.info("Setting Soot class path...");
            AnalysisManager.setSootClassPath();

            logger.info("Loading class...");
            AnalysisManager.loadClass(CLASS_TO_ANALYZE);

            logger.info("Running analysis...");
            AnalysisManager.runICFGAnalysis(CLASS_TO_ANALYZE, data_facts);
            logger.info("Done!");
        }

}
