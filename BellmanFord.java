import java.util.Arrays;

public final class BellmanFord {
    private static final int INF = 100_000_000;
    private static final int[] NEGATIVE_CYCLE_RESULT = {-1};

    private BellmanFord() {
    }

    public static int[] bellmanFord(int vertices, int[][] edgeData, int source) {
        return shortestPaths(vertices, edgeData, source);
    }

    public static int[] shortestPaths(int vertices, int[][] edgeData, int source) {
        ShortestPathResult result = computeShortestPaths(WeightedGraph.from(vertices, edgeData), source);
        if (result.hasNegativeCycle()) {
            return NEGATIVE_CYCLE_RESULT.clone();
        }
        return result.distances();
    }

    public static ShortestPathResult computeShortestPaths(WeightedGraph graph, int source) {
        validateSource(source, graph.vertices());
        WeightedEdge[] edges = graph.edgeArray();
        int[] distances = initializeDistances(graph.vertices(), source);

        for (int pass = 0; pass < graph.vertices() - 1; pass++) {
            boolean updated = false;

            for (WeightedEdge edge : edges) {
                updated |= relax(edge, distances);
            }

            if (!updated) {
                return ShortestPathResult.success(distances);
            }
        }

        if (hasNegativeCycle(edges, distances)) {
            return ShortestPathResult.negativeCycle();
        }

        return ShortestPathResult.success(distances);
    }

    private static int[] initializeDistances(int vertices, int source) {
        int[] distances = new int[vertices];
        Arrays.fill(distances, INF);
        distances[source] = 0;
        return distances;
    }

    private static boolean relax(WeightedEdge edge, int[] distances) {
        if (distances[edge.from()] == INF) {
            return false;
        }

        int candidateDistance = distances[edge.from()] + edge.weight();
        if (candidateDistance >= distances[edge.to()]) {
            return false;
        }

        distances[edge.to()] = candidateDistance;
        return true;
    }

    private static boolean hasNegativeCycle(WeightedEdge[] edges, int[] distances) {
        for (WeightedEdge edge : edges) {
            if (distances[edge.from()] != INF
                    && distances[edge.from()] + edge.weight() < distances[edge.to()]) {
                return true;
            }
        }
        return false;
    }

    private static void validateSource(int source, int vertices) {
        validateVertex(source, vertices, "source");
    }

    private static void validateVertex(int vertex, int vertices, String label) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(label + " must be within the vertex range");
        }
    }
}
