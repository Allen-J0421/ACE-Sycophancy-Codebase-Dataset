import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

class BreadthFirstSearch {
    private static final int SAMPLE_VERTEX_COUNT = 6;
    private static final List<Edge> SAMPLE_EDGES = Collections.unmodifiableList(Arrays.asList(
            new Edge(1, 2),
            new Edge(2, 0),
            new Edge(0, 3),
            new Edge(4, 5)
    ));

    private static final class Edge {
        private final int source;
        private final int destination;

        Edge(int source, int destination) {
            this.source = source;
            this.destination = destination;
        }

        int source() {
            return source;
        }

        int destination() {
            return destination;
        }
    }

    private static final class UndirectedGraph {
        private final int vertexCount;
        private final List<List<Integer>> adjacencyList;

        private UndirectedGraph(int vertexCount, List<List<Integer>> adjacencyList) {
            this.vertexCount = vertexCount;
            this.adjacencyList = adjacencyList;
        }

        static UndirectedGraph fromEdges(int vertexCount, List<Edge> edges) {
            validateVertexCount(vertexCount);
            Objects.requireNonNull(edges, "edges must not be null");

            List<List<Integer>> adjacencyList = createEmptyAdjacencyList(vertexCount);
            for (Edge edge : edges) {
                Edge nonNullEdge = Objects.requireNonNull(edge, "edge must not be null");
                addUndirectedEdge(adjacencyList, vertexCount, nonNullEdge);
            }

            return new UndirectedGraph(vertexCount, immutableCopyOf(adjacencyList));
        }

        private static List<List<Integer>> createEmptyAdjacencyList(int vertexCount) {
            List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacencyList.add(new ArrayList<>());
            }
            return adjacencyList;
        }

        private static void addUndirectedEdge(List<List<Integer>> adjacencyList, int vertexCount, Edge edge) {
            validateVertex(edge.source(), vertexCount);
            validateVertex(edge.destination(), vertexCount);

            adjacencyList.get(edge.source()).add(edge.destination());
            adjacencyList.get(edge.destination()).add(edge.source());
        }

        private static List<List<Integer>> immutableCopyOf(List<List<Integer>> adjacencyList) {
            List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
            for (List<Integer> neighbors : adjacencyList) {
                immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
            }
            return Collections.unmodifiableList(immutableAdjacencyList);
        }

        int vertexCount() {
            return vertexCount;
        }

        List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return adjacencyList.get(vertex);
        }

        private void validateVertex(int vertex) {
            validateVertex(vertex, vertexCount);
        }

        private static void validateVertexCount(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must not be negative");
            }
        }

        private static void validateVertex(int vertex, int vertexCount) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException("vertex out of range: " + vertex);
            }
        }
    }

    private static final class BreadthFirstTraversal {
        private BreadthFirstTraversal() {
        }

        static List<Integer> traverse(UndirectedGraph graph) {
            Objects.requireNonNull(graph, "graph must not be null");

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
        return UndirectedGraph.fromEdges(SAMPLE_VERTEX_COUNT, SAMPLE_EDGES);
    }

    private static void printTraversal(List<Integer> traversal) {
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
