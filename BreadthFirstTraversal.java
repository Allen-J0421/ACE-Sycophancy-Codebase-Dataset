import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

final class BreadthFirstTraversal {
    private BreadthFirstTraversal() {
    }

    static List<Integer> traverseFrom(Graph graph, int startVertex) {
        TraversalState traversalState = TraversalState.create(graph);
        traversalState.validateStartVertex(startVertex);
        traversalState.traverseComponent(startVertex);
        return traversalState.traversalOrder();
    }

    static List<Integer> traverseAllComponents(Graph graph) {
        TraversalState traversalState = TraversalState.create(graph);

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!traversalState.hasVisited(vertex)) {
                traversalState.traverseComponent(vertex);
            }
        }

        return traversalState.traversalOrder();
    }

    private static final class TraversalState {
        private final Graph graph;
        private final boolean[] visited;
        private final List<Integer> traversalOrder;

        private TraversalState(Graph graph) {
            this.graph = graph;
            visited = new boolean[graph.vertexCount()];
            traversalOrder = new ArrayList<>();
        }

        static TraversalState create(Graph graph) {
            if (graph == null) {
                throw new IllegalArgumentException("Graph cannot be null.");
            }
            return new TraversalState(graph);
        }

        boolean hasVisited(int vertex) {
            return visited[vertex];
        }

        List<Integer> traversalOrder() {
            return traversalOrder;
        }

        void traverseComponent(int startVertex) {
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

        void validateStartVertex(int startVertex) {
            if (startVertex < 0 || startVertex >= graph.vertexCount()) {
                throw new IllegalArgumentException("Start vertex out of range: " + startVertex);
            }
        }
    }
}
