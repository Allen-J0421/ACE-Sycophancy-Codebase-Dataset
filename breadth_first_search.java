import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

class BreadthFirstSearch {
    private static final int SAMPLE_VERTEX_COUNT = 6;
    private static final Edge[] SAMPLE_EDGES = {
            new Edge(1, 2),
            new Edge(2, 0),
            new Edge(0, 3),
            new Edge(4, 5)
    };

    private static final class Edge {
        private final int source;
        private final int destination;

        Edge(int source, int destination) {
            this.source = source;
            this.destination = destination;
        }
    }

    private static final class UndirectedGraph {
        private final int vertexCount;
        private final List<List<Integer>> adjacencyList;

        UndirectedGraph(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must not be negative");
            }

            this.vertexCount = vertexCount;
            adjacencyList = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        void addEdge(int source, int destination) {
            validateVertex(source);
            validateVertex(destination);

            adjacencyList.get(source).add(destination);
            adjacencyList.get(destination).add(source);
        }

        int vertexCount() {
            return vertexCount;
        }

        List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return Collections.unmodifiableList(adjacencyList.get(vertex));
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException("vertex out of range: " + vertex);
            }
        }
    }

    private static final class BreadthFirstTraversal {
        private BreadthFirstTraversal() {
        }

        static List<Integer> traverse(UndirectedGraph graph) {
            boolean[] visited = new boolean[graph.vertexCount()];
            List<Integer> traversal = new ArrayList<>(graph.vertexCount());

            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!visited[vertex]) {
                    traverseComponent(graph, vertex, visited, traversal);
                }
            }

            return Collections.unmodifiableList(traversal);
        }

        private static void traverseComponent(
                UndirectedGraph graph,
                int source,
                boolean[] visited,
                List<Integer> traversal
        ) {
            Deque<Integer> queue = new ArrayDeque<>();
            visited[source] = true;
            queue.addLast(source);

            while (!queue.isEmpty()) {
                int current = queue.removeFirst();
                traversal.add(current);

                enqueueUnvisitedNeighbors(graph, current, visited, queue);
            }
        }

        private static void enqueueUnvisitedNeighbors(
                UndirectedGraph graph,
                int current,
                boolean[] visited,
                Deque<Integer> queue
        ) {
            for (int neighbor : graph.neighborsOf(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.addLast(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {
        UndirectedGraph graph = createSampleGraph();
        printTraversal(BreadthFirstTraversal.traverse(graph));
    }

    private static UndirectedGraph createSampleGraph() {
        UndirectedGraph graph = new UndirectedGraph(SAMPLE_VERTEX_COUNT);
        for (Edge edge : SAMPLE_EDGES) {
            graph.addEdge(edge.source, edge.destination);
        }
        return graph;
    }

    private static void printTraversal(List<Integer> traversal) {
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
