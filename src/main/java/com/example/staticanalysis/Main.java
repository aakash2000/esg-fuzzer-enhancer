package com.example.staticanalysis;

import com.example.staticanalysis.analysis.data.DFF;
import com.example.staticanalysis.analysis.data.DFFManager;
import com.example.staticanalysis.manager.AnalysisManager;
import com.example.staticanalysis.visualizer.GraphFromCSV;
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
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestInterprocedural"; /* WORKS */
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestLoops";
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestMultiplePaths"; /* WORKS */
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.TestSwitch";
        //public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.MazeFuzzerTest"; /* ATTENTION REQUIRED */
        public static final String CLASS_TO_ANALYZE = "com.example.staticanalysis.testclasses.constantpropagation.ConditionalFlowTest"; /* WORKS */

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

            System.out.println("Data Flow Facts:");
            DFFManager.getInstance().getDFFs().forEach(dff -> {
                System.out.println("DFF: "+ dff);
            });
            /*logger.info("Sleeping for 2 seconds to allow the graph to be generated...");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Generating graph from CSV dump...");
            GraphFromCSV.main(args);*/
            logger.info("Done!");
        }

}
