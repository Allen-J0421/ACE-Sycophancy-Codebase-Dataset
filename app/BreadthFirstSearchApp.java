package app;

import graph.Graph;
import graph.samples.SampleGraphs;
import graph.traversal.BreadthFirstTraverser;
import graph.traversal.TraversalResult;

public final class BreadthFirstSearchApp {
    private BreadthFirstSearchApp() {
    }

    public static void main(String[] args) {
        Graph graph = SampleGraphs.disconnectedUndirectedGraph();
        BreadthFirstTraverser traverser = BreadthFirstTraverser.forGraph(graph);
        TraversalResult traversalResult = traverser.traverseAllComponents();
        System.out.print(traversalResult);
    }
}
