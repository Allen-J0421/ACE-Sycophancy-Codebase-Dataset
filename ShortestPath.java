import java.util.ArrayList;
import java.util.List;

public final class ShortestPath {
    private static final int SAMPLE_SOURCE = 0;

    private ShortestPath() {
        // Utility class.
    }

    public static ArrayList<Integer> shortestPaths(Graph graph, int source) {
        return ShortestPathSolver.solve(graph, source).toList();
    }

    private static String formatDistances(List<Integer> distances) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < distances.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(distances.get(i));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        ArrayList<Integer> result = shortestPaths(SampleGraphs.weightedUndirectedExample(), SAMPLE_SOURCE);
        System.out.println(formatDistances(result));
    }
}
