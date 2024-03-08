package com.example.staticanalysis.visualizer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.util.DefaultMouseManager;

import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class GraphFromCSV {

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        File dir = new File("./");
        System.out.println("Looking for CSV files in " + dir.getAbsolutePath());
        File[] files = dir.listFiles((d, name) -> name.endsWith(".csv"));
        if (files == null || files.length == 0) {
            System.out.println("No CSV files found");
            return;
        }
        Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
        String latestCsvFile = files[0].getAbsolutePath();
        Graph graph = new SingleGraph("DemoGraph");
        graph.addAttribute("ui.stylesheet", "node { text-size: 18; }");
        try (BufferedReader br = new BufferedReader(new FileReader(latestCsvFile))) {
            String line;
            Node previousNode = null;
            while ((line = br.readLine()) != null) {
                // Assuming the first element is the node, and it connects to others
                String[] parts = line.split(";");
                String methodSignature = parts[0].replaceAll("com.example.staticanalysis.testclasses.constantpropagation.", "").replaceAll("java.lang.String", "String");
                String statement = parts[1].replaceAll("com.example.staticanalysis.testclasses.constantpropagation.", "").replaceAll("java.lang.String", "String");
                String DFF = parts[2].replaceAll("com.example.staticanalysis.testclasses.constantpropagation.", "").replaceAll("java.lang.String", "String");
                if (graph.getNode(methodSignature+" "+statement+" "+DFF) == null) {
                    Node currNode = graph.addNode(statement + "," + DFF);
                    if (previousNode != null) {
                        graph.addEdge(previousNode.getId() + currNode.getId(), previousNode, currNode);
                    }
                    previousNode = currNode;
                }

            }
            for (Node node : graph) {
                node.setAttribute("ui.label", node.getId());
            }

            Viewer viewer = graph.display();
            viewer.getDefaultView().resizeFrame(982, 982);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

