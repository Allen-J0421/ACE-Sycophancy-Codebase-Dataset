import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    private Dijkstra() {
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        return dijkstra(LegacyGraph.fromAdjacencyList(adj), src);
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        LegacyGraph.fromAdjacencyList(adj).addUndirectedEdge(u, v, w);
    }

    private static Graph createSampleGraph() {
        return GraphBuilder.withVertexCount(5)
                .addUndirectedEdge(0, 1, 4)
                .addUndirectedEdge(0, 2, 8)
                .addUndirectedEdge(1, 4, 6)
                .addUndirectedEdge(1, 2, 3)
                .addUndirectedEdge(2, 3, 2)
                .addUndirectedEdge(3, 4, 10)
                .build();
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

    public static void main(String[] args) {
        int source = 0;
        Graph graph = createSampleGraph();
        ArrayList<Integer> result = dijkstra(graph, source);
        System.out.println(formatDistances(result));
    }

    private static ArrayList<Integer> dijkstra(LegacyGraph legacyGraph, int src) {
        return dijkstra(LegacyGraphAdapter.toGraph(legacyGraph), src);
    }

    private static ArrayList<Integer> dijkstra(Graph graph, int src) {
        graph.validateSource(src);
        return ShortestPathSolver.solve(graph, src).toList();
    }
}

final class Edge {
    final int to;
    final int weight;

    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}

final class Graph {
    private final List<List<Edge>> adjacency;

    private Graph(List<List<Edge>> adjacency) {
        this.adjacency = adjacency;
    }

    static Graph fromMutableAdjacency(List<List<Edge>> adjacency) {
        return new Graph(freezeAdjacency(adjacency));
    }

    List<Edge> neighborsOf(int vertex) {
        GraphChecks.validateVertex(vertex, vertexCount(), "vertex");
        return adjacency.get(vertex);
    }

    int vertexCount() {
        return adjacency.size();
    }

    void validateSource(int source) {
        GraphChecks.validateVertex(source, vertexCount(), "src");
    }

    private static List<List<Edge>> freezeAdjacency(List<List<Edge>> adjacency) {
        List<List<Edge>> frozenAdjacency = new ArrayList<>(adjacency.size());
        for (List<Edge> edges : adjacency) {
            frozenAdjacency.add(Collections.unmodifiableList(new ArrayList<>(edges)));
        }
        return Collections.unmodifiableList(frozenAdjacency);
    }
}

final class GraphBuilder {
    private final List<List<Edge>> adjacency;

    private GraphBuilder(List<List<Edge>> adjacency) {
        this.adjacency = adjacency;
    }

    static GraphBuilder withVertexCount(int vertexCount) {
        GraphChecks.validateVertexCount(vertexCount);

        List<List<Edge>> adjacency = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacency.add(new ArrayList<>());
        }
        return new GraphBuilder(adjacency);
    }

    GraphBuilder addUndirectedEdge(int from, int to, int weight) {
        validateEndpoints(from, to, weight);
        addDirectedEdgeUnchecked(from, to, weight);
        addDirectedEdgeUnchecked(to, from, weight);
        return this;
    }

    GraphBuilder addDirectedEdge(int from, int to, int weight) {
        validateEndpoints(from, to, weight);
        addDirectedEdgeUnchecked(from, to, weight);
        return this;
    }

    Graph build() {
        return Graph.fromMutableAdjacency(adjacency);
    }

    private void addDirectedEdgeUnchecked(int from, int to, int weight) {
        adjacency.get(from).add(new Edge(to, weight));
    }

    private void validateEndpoints(int from, int to, int weight) {
        GraphChecks.validateVertex(from, adjacency.size(), "from");
        GraphChecks.validateVertex(to, adjacency.size(), "to");
        GraphChecks.validateWeight(weight);
    }
}

final class LegacyGraphAdapter {
    private LegacyGraphAdapter() {
    }

    static Graph toGraph(LegacyGraph legacyGraph) {
        GraphBuilder graph = GraphBuilder.withVertexCount(legacyGraph.vertexCount());
        for (int from = 0; from < legacyGraph.vertexCount(); from++) {
            for (Edge edge : legacyGraph.neighborsOf(from)) {
                graph.addDirectedEdge(from, edge.to, edge.weight);
            }
        }
        return graph.build();
    }
}

final class LegacyGraph {
    private static final int DESTINATION_INDEX = 0;
    private static final int WEIGHT_INDEX = 1;
    private static final int EDGE_FIELD_COUNT = 2;

    private final ArrayList<ArrayList<int[]>> adjacency;

    private LegacyGraph(ArrayList<ArrayList<int[]>> adjacency) {
        this.adjacency = adjacency;
    }

    static LegacyGraph fromAdjacencyList(ArrayList<ArrayList<int[]>> adjacency) {
        validateAdjacencyList(adjacency);
        return new LegacyGraph(adjacency);
    }

    int vertexCount() {
        return adjacency.size();
    }

    void addUndirectedEdge(int u, int v, int weight) {
        GraphChecks.validateVertex(u, vertexCount(), "u");
        GraphChecks.validateVertex(v, vertexCount(), "v");
        GraphChecks.validateWeight(weight);

        adjacency.get(u).add(edge(v, weight));
        adjacency.get(v).add(edge(u, weight));
    }

    List<Edge> neighborsOf(int from) {
        GraphChecks.validateVertex(from, vertexCount(), "from");

        ArrayList<int[]> rawEdges = requireNeighborList(from);
        ArrayList<Edge> edges = new ArrayList<>(rawEdges.size());
        for (int[] rawEdge : rawEdges) {
            edges.add(toEdge(from, rawEdge));
        }
        return edges;
    }

    private Edge toEdge(int from, int[] rawEdge) {
        validateRawEdge(rawEdge, from);

        int to = rawEdge[DESTINATION_INDEX];
        int weight = rawEdge[WEIGHT_INDEX];
        GraphChecks.validateVertex(to, vertexCount(), "neighbor");
        GraphChecks.validateWeight(weight);
        return new Edge(to, weight);
    }

    private ArrayList<int[]> requireNeighborList(int vertex) {
        ArrayList<int[]> neighbors = adjacency.get(vertex);
        if (neighbors == null) {
            throw new IllegalArgumentException(
                    "Adjacency list contains a null neighbor list at vertex " + vertex);
        }
        return neighbors;
    }

    private static int[] edge(int destination, int weight) {
        return new int[]{destination, weight};
    }

    private static void validateAdjacencyList(ArrayList<ArrayList<int[]>> adjacency) {
        if (adjacency == null) {
            throw new IllegalArgumentException("Adjacency list must not be null");
        }
    }

    private static void validateRawEdge(int[] rawEdge, int fromVertex) {
        if (rawEdge == null || rawEdge.length < EDGE_FIELD_COUNT) {
            throw new IllegalArgumentException(
                    "Edge at vertex " + fromVertex + " must contain destination and weight");
        }
    }
}

final class QueueEntry {
    final int distance;
    final int vertex;

    QueueEntry(int distance, int vertex) {
        this.distance = distance;
        this.vertex = vertex;
    }
}

final class Distances {
    private static final int UNREACHABLE = Integer.MAX_VALUE;

    private final int[] values;

    private Distances(int[] values) {
        this.values = values;
    }

    static Distances forSource(int vertexCount, int source) {
        int[] values = new int[vertexCount];
        Arrays.fill(values, UNREACHABLE);
        values[source] = 0;
        return new Distances(values);
    }

    boolean isStale(QueueEntry entry) {
        return entry.distance != values[entry.vertex];
    }

    boolean tryUpdate(int vertex, int nextDistance) {
        if (nextDistance >= values[vertex]) {
            return false;
        }

        values[vertex] = nextDistance;
        return true;
    }

    ArrayList<Integer> toList() {
        ArrayList<Integer> result = new ArrayList<>(values.length);
        for (int distance : values) {
            result.add(distance);
        }
        return result;
    }
}

final class ShortestPathSolver {
    private final Graph graph;
    private final Distances distances;
    private final PriorityQueue<QueueEntry> minHeap;

    private ShortestPathSolver(Graph graph, int source) {
        this.graph = graph;
        this.distances = Distances.forSource(graph.vertexCount(), source);
        this.minHeap = new PriorityQueue<>(Comparator.comparingInt(entry -> entry.distance));
        minHeap.offer(new QueueEntry(0, source));
    }

    static Distances solve(Graph graph, int source) {
        return new ShortestPathSolver(graph, source).run();
    }

    private Distances run() {
        while (!minHeap.isEmpty()) {
            QueueEntry current = minHeap.poll();
            if (distances.isStale(current)) {
                continue;
            }

            relaxNeighborsOf(current);
        }
        return distances;
    }

    private void relaxNeighborsOf(QueueEntry current) {
        for (Edge edge : graph.neighborsOf(current.vertex)) {
            tryRelax(current, edge);
        }
    }

    private void tryRelax(QueueEntry current, Edge edge) {
        int nextDistance = GraphChecks.addDistances(current.distance, edge.weight);
        if (distances.tryUpdate(edge.to, nextDistance)) {
            minHeap.offer(new QueueEntry(nextDistance, edge.to));
        }
    }
}

final class GraphChecks {
    private GraphChecks() {
    }

    static void validateVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative");
        }
    }

    static void validateVertex(int vertex, int vertexCount, String label) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(label + " vertex out of range: " + vertex);
        }
    }

    static void validateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Dijkstra's algorithm requires non-negative edge weights");
        }
    }

    static int addDistances(int left, int right) {
        long sum = (long) left + right;
        if (sum > Integer.MAX_VALUE) {
            throw new ArithmeticException("Shortest path exceeds supported integer range");
        }
        return (int) sum;
    }
}
