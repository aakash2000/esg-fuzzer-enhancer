package com.example.staticanalysis;

import com.example.staticanalysis.manager.AnalysisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Scene;
import soot.SootClass;
import soot.Value;

import java.util.Map;


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
            logger.info("Captured results:");
            logger.info("=====================================");
            AnalysisManager.getResults().forEach((k, v) -> {
                logger.info("Method: " + k);
                v.forEach((k1, v1) -> {
                    StringBuilder valString = new StringBuilder();
                    for (Value value : v1) {
                        valString.append(value).append(", ");
                    }
                    logger.info("Values: " + k1 + ": " + valString);
                });

            });
            Map<String, Map<Value, Value[]>> data_facts = AnalysisManager.getResults();
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
