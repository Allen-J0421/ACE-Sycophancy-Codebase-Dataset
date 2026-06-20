package bipartite;

import java.util.List;

/**
 * Lightweight, dependency-free test harness for {@link ShortestPath}, matching
 * the style of the other harnesses in this package. Runnable with
 * {@code java bipartite.ShortestPathTests}; the process exits non-zero if any
 * case fails.
 */
public final class ShortestPathTests {

    private static final double EPSILON = 1e-9;
    private static int failures = 0;
    private static final ShortestPath SHORTEST_PATH = new ShortestPath();

    // A small weighted graph reused across cases.
    //   0 -1- 1 -2- 2 -1- 3 -3- 4      (plus the chords 0-2 weight 4, 1-3 weight 5)
    private static WeightedUndirectedGraph sampleGraph() {
        return WeightedUndirectedGraph.of(6, List.of(
                new WeightedEdge(0, 1, 1.0),
                new WeightedEdge(1, 2, 2.0),
                new WeightedEdge(0, 2, 4.0),
                new WeightedEdge(2, 3, 1.0),
                new WeightedEdge(3, 4, 3.0),
                new WeightedEdge(1, 3, 5.0)));
        // Vertex 5 is isolated.
    }

    public static void main(String[] args) {
        findsShortestOfCompetingRoutes();
        distanceMatchesTheReportedPath();
        sourceEqualsTargetIsZeroLengthPath();
        reportsUnreachableTarget();
        rejectsNegativeWeight();
        rejectsNullGraph();
        rejectsOutOfRangeEndpoints();

        if (failures == 0) {
            System.out.println("All tests passed.");
        } else {
            System.out.println(failures + " test(s) failed.");
            System.exit(1);
        }
    }

    private static void findsShortestOfCompetingRoutes() {
        WeightedUndirectedGraph graph = sampleGraph();
        ShortestPathResult result = SHORTEST_PATH.compute(graph, 0, 3);
        // 0-1-2-3 = 4 beats 0-2-3 = 5 and 0-1-3 = 6.
        boolean ok = result instanceof ShortestPathResult.Path path
                && Math.abs(path.distance() - 4.0) < EPSILON
                && path.vertices().equals(List.of(0, 1, 2, 3));
        check("finds the shortest of competing routes", ok);
    }

    private static void distanceMatchesTheReportedPath() {
        WeightedUndirectedGraph graph = sampleGraph();
        ShortestPathResult result = SHORTEST_PATH.compute(graph, 0, 4);
        boolean ok = result instanceof ShortestPathResult.Path path
                && isConsistentPath(graph, path, 0, 4);
        check("reported distance matches the reported path", ok);
    }

    private static void sourceEqualsTargetIsZeroLengthPath() {
        WeightedUndirectedGraph graph = sampleGraph();
        ShortestPathResult result = SHORTEST_PATH.compute(graph, 2, 2);
        boolean ok = result instanceof ShortestPathResult.Path path
                && path.distance() == 0.0
                && path.vertices().equals(List.of(2));
        check("source equal to target is a zero-length path", ok);
    }

    private static void reportsUnreachableTarget() {
        WeightedUndirectedGraph graph = sampleGraph();
        ShortestPathResult result = SHORTEST_PATH.compute(graph, 0, 5);
        boolean ok = result instanceof ShortestPathResult.Unreachable unreachable
                && unreachable.source() == 0
                && unreachable.target() == 5;
        check("reports an unreachable target", ok);
    }

    private static void rejectsNegativeWeight() {
        WeightedUndirectedGraph graph = WeightedUndirectedGraph.of(2,
                List.of(new WeightedEdge(0, 1, -2.0)));
        boolean threw;
        try {
            SHORTEST_PATH.compute(graph, 0, 1);
            threw = false;
        } catch (IllegalArgumentException expected) {
            threw = true;
        }
        check("rejects a negative edge weight", threw);
    }

    private static void rejectsNullGraph() {
        boolean threw;
        try {
            SHORTEST_PATH.compute(null, 0, 1);
            threw = false;
        } catch (NullPointerException expected) {
            threw = true;
        }
        check("rejects null graph", threw);
    }

    private static void rejectsOutOfRangeEndpoints() {
        WeightedUndirectedGraph graph = sampleGraph();
        boolean threw;
        try {
            SHORTEST_PATH.compute(graph, 0, 99);
            threw = false;
        } catch (IndexOutOfBoundsException expected) {
            threw = true;
        }
        check("rejects out-of-range endpoints", threw);
    }

    /**
     * A path is consistent when it starts at the source, ends at the target,
     * every consecutive pair is connected by an edge, and the summed edge weights
     * equal the reported distance.
     */
    private static boolean isConsistentPath(WeightedUndirectedGraph graph,
                                            ShortestPathResult.Path path, int source, int target) {
        List<Integer> vertices = path.vertices();
        if (vertices.isEmpty() || vertices.get(0) != source
                || vertices.get(vertices.size() - 1) != target) {
            return false;
        }
        double total = 0.0;
        for (int i = 0; i + 1 < vertices.size(); i++) {
            Double weight = edgeWeight(graph, vertices.get(i), vertices.get(i + 1));
            if (weight == null) {
                return false;
            }
            total += weight;
        }
        return Math.abs(total - path.distance()) < EPSILON;
    }

    private static Double edgeWeight(WeightedUndirectedGraph graph, int from, int to) {
        for (WeightedEdge edge : graph.weightedNeighbors(from)) {
            if (edge.v() == to) {
                return edge.weight();
            }
        }
        return null;
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            System.out.println("PASS: " + description);
        } else {
            System.out.println("FAIL: " + description);
            failures++;
        }
    }
}
