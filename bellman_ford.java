import java.util.Arrays;

final class BellmanFord {
    private static final int EDGE_WIDTH = 3;
    private static final int INF = 100_000_000;
    private static final int[] NEGATIVE_CYCLE_RESULT = {-1};

    private BellmanFord() {
    }

    static int[] bellmanFord(int vertices, int[][] edgeData, int source) {
        validateInput(vertices, edgeData, source);
        return bellmanFord(vertices, toEdges(vertices, edgeData), source);
    }

    private static int[] bellmanFord(int vertices, Edge[] edges, int source) {
        int[] distances = initializeDistances(vertices, source);

        for (int pass = 0; pass < vertices - 1; pass++) {
            boolean updated = false;

            for (Edge edge : edges) {
                updated |= relax(edge, distances);
            }

            if (!updated) {
                return distances;
            }
        }

        if (hasNegativeCycle(edges, distances)) {
            return NEGATIVE_CYCLE_RESULT;
        }

        return distances;
    }

    private static void validateInput(int vertices, int[][] edgeData, int source) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("vertices must be positive");
        }
        if (source < 0 || source >= vertices) {
            throw new IllegalArgumentException("source must be within the vertex range");
        }
        if (edgeData == null) {
            throw new IllegalArgumentException("edges must not be null");
        }
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

    private static Edge[] toEdges(int vertices, int[][] edgeData) {
        Edge[] edges = new Edge[edgeData.length];
        for (int i = 0; i < edgeData.length; i++) {
            int[] edge = edgeData[i];
            if (edge == null || edge.length != EDGE_WIDTH) {
                throw new IllegalArgumentException("each edge must contain exactly 3 integers");
            }

            int from = edge[0];
            int to = edge[1];
            validateVertex(from, vertices, "edge start");
            validateVertex(to, vertices, "edge end");

            edges[i] = new Edge(from, to, edge[2]);
        }
        return edges;
    }

    private static void validateVertex(int vertex, int vertices, String label) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(label + " must be within the vertex range");
        }
    }

    private static final class Edge {
        private final int from;
        private final int to;
        private final int weight;

        private Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    public static void main(String[] args) {
        int vertices = 5;
        int[][] edges = {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        };
        int source = 0;

        int[] shortestDistances = bellmanFord(vertices, edges, source);
        for (int distance : shortestDistances) {
            System.out.print(distance + " ");
        }
    }
}
