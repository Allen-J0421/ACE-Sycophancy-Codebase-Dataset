package graph.traversal;

import graph.Graph;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class BreadthFirstTraverser {
    private final Graph graph;

    private BreadthFirstTraverser(Graph graph) {
        this.graph = graph;
    }

    public static BreadthFirstTraverser forGraph(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph cannot be null.");
        }
        return new BreadthFirstTraverser(graph);
    }

    public static TraversalResult traverseFrom(Graph graph, int startVertex) {
        return forGraph(graph).traverseFrom(startVertex);
    }

    public static TraversalResult traverseAllComponents(Graph graph) {
        return forGraph(graph).traverseAllComponents();
    }

    public TraversalResult traverseFrom(int startVertex) {
        graph.requireVertex(startVertex);
        TraversalSession traversalSession = new TraversalSession(graph);
        traversalSession.traverseComponent(startVertex);
        return traversalSession.result();
    }

    public TraversalResult traverseAllComponents() {
        TraversalSession traversalSession = new TraversalSession(graph);
        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!traversalSession.hasVisited(vertex)) {
                traversalSession.traverseComponent(vertex);
            }
        }
        return traversalSession.result();
    }

    private static final class TraversalSession {
        private final Graph graph;
        private final boolean[] visited;
        private final List<Integer> traversalOrder;

        private TraversalSession(Graph graph) {
            this.graph = graph;
            visited = new boolean[graph.vertexCount()];
            traversalOrder = new ArrayList<>();
        }

        private boolean hasVisited(int vertex) {
            return visited[vertex];
        }

        private TraversalResult result() {
            return TraversalResult.fromVisitOrder(traversalOrder);
        }

        private void traverseComponent(int startVertex) {
            Deque<Integer> queue = new ArrayDeque<>();
            visited[startVertex] = true;
            queue.add(startVertex);

            while (!queue.isEmpty()) {
                int currentVertex = queue.remove();
                traversalOrder.add(currentVertex);

                for (int neighbor : graph.neighborsOf(currentVertex)) {
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        queue.add(neighbor);
                    }
                }
            }
        }
    }
}
