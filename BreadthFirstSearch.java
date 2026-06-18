import graph.Graph;
import graph.samples.SampleGraphs;
import graph.traversal.BreadthFirstTraversal;
import graph.traversal.TraversalResult;

public final class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = SampleGraphs.disconnectedUndirectedGraph();
        TraversalResult traversalResult = BreadthFirstTraversal.traverseAllComponents(graph);
        System.out.print(traversalResult);
    }
}
