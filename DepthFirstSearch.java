import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
    }

    public static void main(String[] args) {
        Graph graph = Graph.withVertices(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(5, 4);

        System.out.println(formatTraversal(dfs(graph)));
    }

    public static List<Integer> dfs(IntGraph graph) {
        return new TraversalSession(graph).traverseAllComponents();
    }

    public static List<Integer> dfsFrom(IntGraph graph, int startVertex) {
        return new TraversalSession(graph).traverseFrom(startVertex);
    }

    private static String formatTraversal(List<Integer> traversal) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : traversal) {
            joiner.add(Integer.toString(vertex));
        }
        return joiner.toString();
    }

    private static final class TraversalSession {
        private final IntGraph graph;
        private final boolean[] visited;
        private final List<Integer> traversal = new ArrayList<>();

        private TraversalSession(IntGraph graph) {
            this.graph = graph;
            this.visited = new boolean[graph.vertexCount()];
        }

        private List<Integer> traverseAllComponents() {
            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!visited[vertex]) {
                    traverseComponent(vertex);
                }
            }

            return traversal;
        }

        private List<Integer> traverseFrom(int startVertex) {
            validateVertex(startVertex);
            traverseComponent(startVertex);
            return traversal;
        }

        private void traverseComponent(int startVertex) {
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(startVertex);

            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                if (visited[vertex]) {
                    continue;
                }

                visited[vertex] = true;
                traversal.add(vertex);

                List<Integer> neighbors = graph.neighborsOf(vertex);
                for (int index = neighbors.size() - 1; index >= 0; index--) {
                    int neighbor = neighbors.get(index);
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= visited.length) {
                throw new IllegalArgumentException("vertex out of bounds: " + vertex);
            }
        }
    }
}
