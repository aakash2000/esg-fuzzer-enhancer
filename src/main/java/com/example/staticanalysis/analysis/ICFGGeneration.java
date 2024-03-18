package com.example.staticanalysis.analysis;

import com.example.staticanalysis.analysis.data.DFF;
import com.example.staticanalysis.manager.AnalysisManager;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.slf4j.Logger;
import soot.*;
import soot.jimple.spark.SparkTransformer;

import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.ide.JimpleIDESolver;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

import java.util.Collections;
import java.util.Set;

public class ICFGGeneration {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(ICFGGeneration.class);
    public static void generateCallGraph(String className, Set<DFF> data_facts) {
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
        Scene.v().setEntryPoints(Collections.singletonList(Scene.v().getMainMethod()));

        logger.info("Running Constant Propagation...");
        AnalysisManager.setConstantPropagationAnalysis();

        logger.info("Running Soot...");
        //AnalysisManager.runAnalysis();

        logger.info("Running Spark...");
        SparkTransformer.v().transform();
        ESGGenerationProblem problem = new ESGGenerationProblem(Scene.v().getMainMethod(), data_facts);
        JimpleIDESolver<?, ?, ?> solver = new JimpleIDESolver<>(problem, true);
        try {
            solver.solve();
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }

        // Initialize GraphStream graph
        /*System.setProperty("org.graphstream.ui", "swing"); // Use Swing for the UI
        Graph graph = new SingleGraph("CallGraph");
        graph.setAttribute("ui.stylesheet", "node { fill-color: blue; }");
        // Access and process the call graph
        CallGraph cg = Scene.v().getCallGraph();
        for (Iterator<Edge> it = cg.iterator(); it.hasNext(); ) {
            Edge e = it.next();
            String srcMethod = e.getSrc().method().getName();
            String tgtMethod = e.getTgt().method().getName();
            // Check if node already exists
            Node srcNode;
            Node tgtNode;
            if (graph.getNode(srcMethod) == null) {
                System.out.println("Adding node: " + srcMethod);
                srcNode = graph.addNode(srcMethod);
                srcNode.setAttribute("ui.label", srcNode);
                handleMethodStmts(e.getSrc().method(), graph);
            } else {
                srcNode = graph.getNode(srcMethod);
            }
            if(graph.getNode(tgtMethod) == null) {
                System.out.println("Adding node: " + tgtMethod);
                tgtNode = graph.addNode(tgtMethod);
                tgtNode.setAttribute("ui.label", tgtNode);
                handleMethodStmts(e.getSrc().method(), graph);
            } else {
                tgtNode = graph.getNode(tgtMethod);
            }

            // Check if edge already exists
            if (srcNode.hasEdgeBetween(tgtNode)) {
                continue;
            }
        }
        // Display the graph
        graph.display();*/
    }

    public static void handleMethodStmts(SootMethod method, Graph graph) {
        if (method.isConcrete()) {
            Body body = method.retrieveActiveBody();
            UnitGraph cfg = new ExceptionalUnitGraph(body);

            String methodName = method.getName();
            Node methodNode = graph.getNode(methodName);

            // Process first and last statements
            Unit firstUnit = cfg.getHeads().isEmpty() ? null : cfg.getHeads().get(0);
            Unit lastUnit = cfg.getTails().isEmpty() ? null : cfg.getTails().get(0);

            if (firstUnit != null) {
                String firstUnitString = firstUnit.toString();
                addNodeToGraph(firstUnitString, graph);
                addEdgeToGraph(methodName, firstUnitString, graph);  // Method entry to first statement
            }

            Body methodBody = method.retrieveActiveBody();
            for (Unit unit : methodBody.getUnits()) {
                String unitString = unit.toString();
                // Add nodes for each statement
                addNodeToGraph(unitString, graph);

                // Process edges between this unit and its successors
                for (Unit succ : cfg.getSuccsOf(unit)) {
                    String succString = succ.toString();
                    addNodeToGraph(succString, graph);
                    addEdgeToGraph(unitString, succString, graph);
                }
            }

            if (lastUnit != null) {
                String lastUnitString = lastUnit.toString();
                addNodeToGraph(lastUnitString, graph);

                // Last statement to method exit1
                addEdgeToGraph(lastUnitString, methodName, graph);
            }

            if (method.getName().equals("main") && firstUnit != null) {
                // Special handling for the main method
                String entryNodeName = "program_entry";
                addNodeToGraph(entryNodeName, graph, "green");

                // Add edge from program entry to main
                addEdgeToGraph(entryNodeName, methodName, graph);
            }

            if (method.getName().equals("main") && lastUnit != null) {
                // Special handling for the main method
                String lastUnitString = lastUnit.toString();
                String exitNodeName = "program_exit";
                addNodeToGraph(exitNodeName, graph, "red");
                addEdgeToGraph(lastUnitString, exitNodeName, graph);  // Last statement of main to program exit
            }
        }
    }

    private static void addNodeToGraph(String nodeName, Graph graph, String color) {
        Node node = graph.getNode(nodeName);
        if (node == null) {
            System.out.println("Adding node: " + nodeName);
            node = graph.addNode(nodeName);
            node.setAttribute("ui.label", nodeName);
            node.setAttribute("ui.style", "fill-color: " + color + ";");
        }
    }

    private static void addNodeToGraph(String nodeName, Graph graph) {
        Node node = graph.getNode(nodeName);
        if (node == null) {
            System.out.println("Adding node: " + nodeName);
            node = graph.addNode(nodeName);
            node.setAttribute("ui.label", nodeName);
        }
    }

    private static void addEdgeToGraph(String srcName, String tgtName, Graph graph) {
        Node srcNode = graph.getNode(srcName);
        Node tgtNode = graph.getNode(tgtName);
        if (srcNode != null && tgtNode != null && !srcNode.hasEdgeBetween(tgtNode)) {
            System.out.println("Adding edge: " + srcName + "->" + tgtName);
            graph.addEdge(srcName + "->" + tgtName, srcNode, tgtNode, true);
        }
    }
}
