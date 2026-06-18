package depthfirstsearch.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

import depthfirstsearch.graph.Graph;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
        // Utility class.
    }

    public static List<Integer> traverse(Graph graph) {
        Objects.requireNonNull(graph, "graph");
        List<Integer> order = new ArrayList<>(graph.vertexCount());
        traverse(graph, order::add);
        return order;
    }

    public static List<Integer> traverseFrom(Graph graph, int startVertex) {
        Objects.requireNonNull(graph, "graph");
        List<Integer> order = new ArrayList<>(graph.vertexCount());
        traverseFrom(graph, startVertex, order::add);
        return order;
    }

    public static void traverse(Graph graph, IntConsumer visitor) {
        TraversalState.traverseAll(graph, visitor);
    }

    public static void traverseFrom(Graph graph, int startVertex, IntConsumer visitor) {
        TraversalState.traverseFrom(graph, startVertex, visitor);
    }
}
