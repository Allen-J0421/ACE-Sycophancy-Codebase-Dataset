import java.util.List;

public final class BellmanFord {
    private static final int LEGACY_UNREACHABLE_DISTANCE = 100_000_000;
    private static final int[] NEGATIVE_CYCLE_RESULT = {-1};

    private BellmanFord() {
    }

    public static int[] bellmanFord(int vertices, int[][] edgeData, int source) {
        return shortestPaths(vertices, edgeData, source);
    }

    public static int[] shortestPaths(int vertices, int[][] edgeData, int source) {
        ShortestPathResult result = computeShortestPaths(vertices, edgeData, source);
        if (result.hasNegativeCycle()) {
            return NEGATIVE_CYCLE_RESULT.clone();
        }
        return result.distances();
    }

    public static ShortestPathResult computeShortestPaths(int vertices, int[][] edgeData, int source) {
        return computeShortestPaths(WeightedGraph.from(vertices, edgeData), source);
    }

    public static ShortestPathResult computeShortestPaths(WeightedGraph graph, int source) {
        validateSource(source, graph.vertices());
        List<WeightedEdge> edges = graph.edges();
        DistanceState state = DistanceState.fromSource(graph.vertices(), source);

        for (int pass = 0; pass < graph.vertices() - 1; pass++) {
            boolean updated = false;

            for (WeightedEdge edge : edges) {
                updated |= relax(edge, state);
            }

            if (!updated) {
                return state.toResult();
            }
        }

        if (hasNegativeCycle(edges, state)) {
            return ShortestPathResult.negativeCycle();
        }

        return state.toResult();
    }

    private static boolean relax(WeightedEdge edge, DistanceState state) {
        if (!state.isReachable(edge.from())) {
            return false;
        }

        long candidateDistance = Math.addExact(state.distanceTo(edge.from()), edge.weight());
        if (state.isReachable(edge.to()) && candidateDistance >= state.distanceTo(edge.to())) {
            return false;
        }

        state.update(edge.to(), candidateDistance);
        return true;
    }

    private static boolean hasNegativeCycle(List<WeightedEdge> edges, DistanceState state) {
        for (WeightedEdge edge : edges) {
            if (!state.isReachable(edge.from())) {
                continue;
            }

            long candidateDistance = Math.addExact(state.distanceTo(edge.from()), edge.weight());
            if (!state.isReachable(edge.to()) || candidateDistance < state.distanceTo(edge.to())) {
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

    private static final class DistanceState {
        private final long[] distances;
        private final boolean[] reachable;

        private DistanceState(long[] distances, boolean[] reachable) {
            this.distances = distances;
            this.reachable = reachable;
        }

        private static DistanceState fromSource(int vertices, int source) {
            long[] distances = new long[vertices];
            boolean[] reachable = new boolean[vertices];
            reachable[source] = true;
            return new DistanceState(distances, reachable);
        }

        private boolean isReachable(int vertex) {
            return reachable[vertex];
        }

        private long distanceTo(int vertex) {
            return distances[vertex];
        }

        private void update(int vertex, long distance) {
            distances[vertex] = distance;
            reachable[vertex] = true;
        }

        private int[] toLegacyDistances() {
            int[] legacyDistances = new int[distances.length];
            for (int vertex = 0; vertex < distances.length; vertex++) {
                legacyDistances[vertex] = reachable[vertex]
                    ? toIntExact(distances[vertex])
                    : LEGACY_UNREACHABLE_DISTANCE;
            }
            return legacyDistances;
        }

        private boolean[] reachableVertices() {
            return reachable.clone();
        }

        private ShortestPathResult toResult() {
            return ShortestPathResult.success(toLegacyDistances(), reachableVertices());
        }

        private int toIntExact(long distance) {
            if (distance < Integer.MIN_VALUE || distance > Integer.MAX_VALUE) {
                throw new ArithmeticException("shortest path distance exceeds int range");
            }
            return (int) distance;
        }
    }
}
