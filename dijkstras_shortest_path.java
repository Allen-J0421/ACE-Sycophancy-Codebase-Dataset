import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    private Dijkstra() {
    }

    static ArrayList<Integer> dijkstra(ArrayList<ArrayList<int[]>> adj, int src) {
        return dijkstra(LegacyGraphAdapter.toGraph(adj), src);
    }

    static void addEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int w) {
        LegacyGraphAdapter.addUndirectedEdge(adj, u, v, w);
    }

    private static Graph createSampleGraph() {
        return Graph.fromUndirectedConnections(
                5,
                Connection.of(0, 1, 4),
                Connection.of(0, 2, 8),
                Connection.of(1, 4, 6),
                Connection.of(1, 2, 3),
                Connection.of(2, 3, 2),
                Connection.of(3, 4, 10));
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

    private static ArrayList<Integer> dijkstra(Graph graph, int src) {
        graph.validateSource(src);
        return ShortestPathSolver.solve(graph, src).toList();
    }
}

final class Connection {
    final int from;
    final int to;
    final int weight;

    private Connection(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    static Connection of(int from, int to, int weight) {
        return new Connection(from, to, weight);
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

    static Graph withVertexCount(int vertexCount) {
        GraphChecks.validateVertexCount(vertexCount);

        List<List<Edge>> adjacency = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacency.add(new ArrayList<>());
        }
        return new Graph(adjacency);
    }

    static Graph fromUndirectedConnections(int vertexCount, Connection... connections) {
        Graph graph = withVertexCount(vertexCount);
        for (Connection connection : connections) {
            graph.addUndirectedConnection(connection);
        }
        return graph;
    }

    void addUndirectedEdge(int u, int v, int weight) {
        addUndirectedConnection(Connection.of(u, v, weight));
    }

    void addDirectedConnection(Connection connection) {
        validateConnection(connection);
        addDirectedEdgeUnchecked(connection.from, connection.to, connection.weight);
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

    private void addUndirectedConnection(Connection connection) {
        validateConnection(connection);
        addDirectedEdgeUnchecked(connection.from, connection.to, connection.weight);
        addDirectedEdgeUnchecked(connection.to, connection.from, connection.weight);
    }

    private void addDirectedEdgeUnchecked(int from, int to, int weight) {
        adjacency.get(from).add(new Edge(to, weight));
    }

    private void validateConnection(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection must not be null");
        }

        GraphChecks.validateVertex(connection.from, vertexCount(), "from");
        GraphChecks.validateVertex(connection.to, vertexCount(), "to");
        GraphChecks.validateWeight(connection.weight);
    }
}

final class LegacyGraphAdapter {
    static final int DESTINATION_INDEX = 0;
    static final int WEIGHT_INDEX = 1;
    static final int EDGE_FIELD_COUNT = 2;

    private LegacyGraphAdapter() {
    }

    static void addUndirectedEdge(ArrayList<ArrayList<int[]>> adj, int u, int v, int weight) {
        validateAdjacencyList(adj);
        GraphChecks.validateVertex(u, adj.size(), "u");
        GraphChecks.validateVertex(v, adj.size(), "v");
        GraphChecks.validateWeight(weight);

        adj.get(u).add(edge(v, weight));
        adj.get(v).add(edge(u, weight));
    }

    static Graph toGraph(ArrayList<ArrayList<int[]>> adj) {
        validateAdjacencyList(adj);

        Graph graph = Graph.withVertexCount(adj.size());
        for (int from = 0; from < adj.size(); from++) {
            ArrayList<int[]> rawEdges = requireNeighborList(adj, from);
            for (int[] rawEdge : rawEdges) {
                graph.addDirectedConnection(toConnection(from, rawEdge, adj.size()));
            }
        }
        return graph;
    }

    static int[] edge(int destination, int weight) {
        return new int[]{destination, weight};
    }

    private static Connection toConnection(int from, int[] rawEdge, int vertexCount) {
        validateRawEdge(rawEdge, from);

        int to = rawEdge[DESTINATION_INDEX];
        int weight = rawEdge[WEIGHT_INDEX];
        GraphChecks.validateVertex(to, vertexCount, "neighbor");
        GraphChecks.validateWeight(weight);
        return Connection.of(from, to, weight);
    }

    private static ArrayList<int[]> requireNeighborList(ArrayList<ArrayList<int[]>> adj, int vertex) {
        ArrayList<int[]> neighbors = adj.get(vertex);
        if (neighbors == null) {
            throw new IllegalArgumentException(
                    "Adjacency list contains a null neighbor list at vertex " + vertex);
        }
        return neighbors;
    }

    private static void validateAdjacencyList(ArrayList<ArrayList<int[]>> adj) {
        if (adj == null) {
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
