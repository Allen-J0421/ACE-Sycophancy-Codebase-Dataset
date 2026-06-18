import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    private static final int UNREACHABLE = Integer.MAX_VALUE;

    private Dijkstra() {
    }

    private static final class Edge {
        private final int to;
        private final int weight;

        private Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private static final class Graph {
        private final List<List<Edge>> adjacency;

        private Graph(List<List<Edge>> adjacency) {
            this.adjacency = adjacency;
        }

        private static Graph withVertexCount(int vertexCount) {
            validateVertexCount(vertexCount);

            List<List<Edge>> adjacency = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacency.add(new ArrayList<>());
            }
            return new Graph(adjacency);
        }

        private void addUndirectedEdge(int u, int v, int weight) {
            validateVertex(u, vertexCount(), "u");
            validateVertex(v, vertexCount(), "v");
            validateWeight(weight);

            addDirectedEdge(u, v, weight);
            addDirectedEdge(v, u, weight);
        }

        private void addDirectedEdge(int from, int to, int weight) {
            adjacency.get(from).add(new Edge(to, weight));
        }

        private List<Edge> neighborsOf(int vertex) {
            return adjacency.get(vertex);
        }

        private int vertexCount() {
            return adjacency.size();
        }
    }

    private static final class LegacyGraphAdapter {
        private static final int DESTINATION_INDEX = 0;
        private static final int WEIGHT_INDEX = 1;
        private static final int EDGE_FIELD_COUNT = 2;

        private LegacyGraphAdapter() {
        }

        private static void addUndirectedEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int weight) {
            validateAdjacencyList(adj);
            validateVertex(u, adj.size(), "u");
            validateVertex(v, adj.size(), "v");
            validateWeight(weight);

            adj.get(u).add(edge(v, weight));
            adj.get(v).add(edge(u, weight));
        }

        private static Graph toGraph(ArrayList<ArrayList<int[]>> adj) {
            validateAdjacencyList(adj);

            int vertexCount = adj.size();
            Graph graph = Graph.withVertexCount(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                ArrayList<int[]> rawEdges = adj.get(vertex);
                if (rawEdges == null) {
                    throw new IllegalArgumentException(
                            "Adjacency list contains a null neighbor list at vertex " + vertex);
                }

                for (int[] rawEdge : rawEdges) {
                    validateRawEdge(rawEdge, vertex);
                    int neighbor = rawEdge[DESTINATION_INDEX];
                    int weight = rawEdge[WEIGHT_INDEX];

                    validateVertex(neighbor, vertexCount, "neighbor");
                    validateWeight(weight);
                    graph.addDirectedEdge(vertex, neighbor, weight);
                }
            }
            return graph;
        }

        private static int[] edge(int destination, int weight) {
            return new int[]{destination, weight};
        }
    }

    private static final class QueueEntry {
        private final int distance;
        private final int vertex;

        private QueueEntry(int distance, int vertex) {
            this.distance = distance;
            this.vertex = vertex;
        }
    }

    private static final class ShortestPathSolver {
        private final Graph graph;
        private final int[] distances;
        private final PriorityQueue<QueueEntry> minHeap;

        private ShortestPathSolver(Graph graph, int source) {
            this.graph = graph;
            this.distances = createDistances(graph.vertexCount(), source);
            this.minHeap = new PriorityQueue<>(Comparator.comparingInt(entry -> entry.distance));
            minHeap.offer(new QueueEntry(0, source));
        }

        private static int[] solve(Graph graph, int source) {
            return new ShortestPathSolver(graph, source).run();
        }

        private int[] run() {
            while (!minHeap.isEmpty()) {
                QueueEntry current = minHeap.poll();
                if (isStale(current)) {
                    continue;
                }

                relaxNeighborsOf(current);
            }
            return distances;
        }

        private boolean isStale(QueueEntry entry) {
            return entry.distance != distances[entry.vertex];
        }

        private void relaxNeighborsOf(QueueEntry current) {
            for (Edge edge : graph.neighborsOf(current.vertex)) {
                tryRelax(current, edge);
            }
        }

        private void tryRelax(QueueEntry current, Edge edge) {
            int nextDistance = addDistances(current.distance, edge.weight);
            if (nextDistance < distances[edge.to]) {
                distances[edge.to] = nextDistance;
                minHeap.offer(new QueueEntry(nextDistance, edge.to));
            }
        }

        private static int[] createDistances(int vertexCount, int source) {
            int[] distances = new int[vertexCount];
            Arrays.fill(distances, UNREACHABLE);
            distances[source] = 0;
            return distances;
        }
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        return dijkstra(LegacyGraphAdapter.toGraph(adj), src);
    }

    private static ArrayList<Integer> dijkstra(Graph graph, int src) {
        validateGraph(graph);
        validateSource(src, graph.vertexCount());

        int[] distances = ShortestPathSolver.solve(graph, src);
        return toArrayList(distances);
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        LegacyGraphAdapter.addUndirectedEdge(adj, u, v, w);
    }

    private static ArrayList<Integer> toArrayList(int[] distances) {
        ArrayList<Integer> result = new ArrayList<>(distances.length);
        for (int distance : distances) {
            result.add(distance);
        }
        return result;
    }

    private static Graph createSampleGraph() {
        Graph graph = Graph.withVertexCount(5);
        graph.addUndirectedEdge(0, 1, 4);
        graph.addUndirectedEdge(0, 2, 8);
        graph.addUndirectedEdge(1, 4, 6);
        graph.addUndirectedEdge(1, 2, 3);
        graph.addUndirectedEdge(2, 3, 2);
        graph.addUndirectedEdge(3, 4, 10);
        return graph;
    }

    private static String formatDistances(List<Integer> distances) {
        StringBuilder formatted = new StringBuilder();
        for (int index = 0; index < distances.size(); index++) {
            if (index > 0) {
                formatted.append(' ');
            }
            formatted.append(distances.get(index));
        }
        return formatted.toString();
    }

    private static int addDistances(int left, int right) {
        long sum = (long) left + right;
        if (sum > Integer.MAX_VALUE) {
            throw new ArithmeticException("Shortest path exceeds supported integer range");
        }
        return (int) sum;
    }

    private static void validateGraph(Graph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
    }

    private static void validateAdjacencyList(ArrayList<ArrayList<int[]>> adj) {
        if (adj == null) {
            throw new IllegalArgumentException("Adjacency list must not be null");
        }
    }

    private static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative");
        }
    }

    private static void validateSource(int src, int vertexCount) {
        validateVertex(src, vertexCount, "src");
    }

    private static void validateVertex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(label + " vertex out of range: " + vertex);
        }
    }

    private static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra's algorithm requires non-negative edge weights");
        }
    }

    private static void validateRawEdge(int[] rawEdge, int fromVertex) {
        if (rawEdge == null || rawEdge.length < LegacyGraphAdapter.EDGE_FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "Edge at vertex " + fromVertex + " must contain destination and weight");
        }
    }

    public static void main(String[] args) {
        int source = 0;
        Graph graph = createSampleGraph();
        ArrayList<Integer> result = dijkstra(graph, source);
        System.out.println(formatDistances(result));
    }
}
