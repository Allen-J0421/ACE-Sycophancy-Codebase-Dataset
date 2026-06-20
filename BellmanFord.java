import java.util.Arrays;

public final class BellmanFord {
    private static final int EDGE_WIDTH = 3;
    private static final int INF = 100_000_000;
    private static final int[] NEGATIVE_CYCLE_RESULT = {-1};

    private BellmanFord() {
    }

    public static int[] bellmanFord(int vertices, int[][] edgeData, int source) {
        return shortestPaths(vertices, edgeData, source);
    }

    public static int[] shortestPaths(int vertices, int[][] edgeData, int source) {
        Result result = computeShortestPaths(Graph.from(vertices, edgeData), source);
        if (result.hasNegativeCycle()) {
            return NEGATIVE_CYCLE_RESULT.clone();
        }
        return result.distances();
    }

    public static Result computeShortestPaths(Graph graph, int source) {
        validateSource(source, graph.vertices());
        Edge[] edges = graph.edgeArray();
        int[] distances = initializeDistances(graph.vertices(), source);

        for (int pass = 0; pass < graph.vertices() - 1; pass++) {
            boolean updated = false;

            for (Edge edge : edges) {
                updated |= relax(edge, distances);
            }

            if (!updated) {
                return Result.success(distances);
            }
        }

        if (hasNegativeCycle(edges, distances)) {
            return Result.negativeCycle();
        }

        return Result.success(distances);
    }

    private static int[] initializeDistances(int vertices, int source) {
        int[] distances = new int[vertices];
        Arrays.fill(distances, INF);
        distances[source] = 0;
        return distances;
    }

    private static boolean relax(Edge edge, int[] distances) {
        if (distances[edge.from] == INF) {
            return false;
        }

        int candidateDistance = distances[edge.from] + edge.weight;
        if (candidateDistance >= distances[edge.to]) {
            return false;
        }

        distances[edge.to] = candidateDistance;
        return true;
    }

    private static boolean hasNegativeCycle(Edge[] edges, int[] distances) {
        for (Edge edge : edges) {
            if (distances[edge.from] != INF
                    && distances[edge.from] + edge.weight < distances[edge.to]) {
                return true;
            }
        }
        return false;
    }

    private static void validateSource(int source, int vertices) {
        validateVertex(source, vertices, "source");
    }

    private static void validateVerticesCount(int vertices) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be positive");
        }
    }

    private static void validateEdgeData(int[][] edgeData) {
        if (edgeData == null) {
            throw new IllegalArgumentException("edges must not be null");
        }
    }

    private static void validateVertex(int vertex, int vertices, String label) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(label + " must be within the vertex range");
        }
    }

    public static final class Graph {
        private final int vertices;
        private final Edge[] edges;

        private Graph(int vertices, Edge[] edges) {
            this.vertices = vertices;
            this.edges = edges;
        }

        public static Graph from(int vertices, int[][] edgeData) {
            validateVerticesCount(vertices);
            validateEdgeData(edgeData);

            Edge[] edges = new Edge[edgeData.length];
            for (int i = 0; i < edgeData.length; i++) {
                int[] edge = edgeData[i];
                if (edge == null || edge.length != EDGE_WIDTH) {
                    throw new IllegalArgumentException("each edge must contain exactly 3 integers");
                }
                Edge candidate = Edge.of(edge[0], edge[1], edge[2]);
                validateEdge(candidate, vertices);
                edges[i] = candidate;
            }
            return new Graph(vertices, edges);
        }

        public static Graph of(int vertices, Edge... edges) {
            validateVerticesCount(vertices);
            if (edges == null) {
                throw new IllegalArgumentException("edges must not be null");
            }

            Edge[] normalizedEdges = new Edge[edges.length];
            for (int i = 0; i < edges.length; i++) {
                Edge edge = edges[i];
                if (edge == null) {
                    throw new IllegalArgumentException("edges must not contain null entries");
                }
                validateEdge(edge, vertices);
                normalizedEdges[i] = edge;
            }
            return new Graph(vertices, normalizedEdges);
        }

        public int vertices() {
            return vertices;
        }

        public Edge[] edges() {
            return edges.clone();
        }

        private Edge[] edgeArray() {
            return edges;
        }
    }

    public static final class Edge {
        private final int from;
        private final int to;
        private final int weight;

        private Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public static Edge of(int from, int to, int weight) {
            return new Edge(from, to, weight);
        }

        public int from() {
            return from;
        }

        public int to() {
            return to;
        }

        public int weight() {
            return weight;
        }
    }

    public static final class Result {
        private final boolean negativeCycle;
        private final int[] distances;

        private Result(boolean negativeCycle, int[] distances) {
            this.negativeCycle = negativeCycle;
            this.distances = distances;
        }

        public static Result success(int[] distances) {
            return new Result(false, distances.clone());
        }

        public static Result negativeCycle() {
            return new Result(true, new int[0]);
        }

        public boolean hasNegativeCycle() {
            return negativeCycle;
        }

        public int[] distances() {
            return distances.clone();
        }
    }

    private static void validateEdge(Edge edge, int vertices) {
        validateVertex(edge.from, vertices, "edge start");
        validateVertex(edge.to, vertices, "edge end");
    }
}
