import java.util.List;

public final class ShortestPath {
    private ShortestPath() {
        // Utility class.
    }

    public static List<Integer> shortestPaths(Graph graph, int source) {
        return ShortestPathSolver.solve(graph, source).toList();
    }
}
