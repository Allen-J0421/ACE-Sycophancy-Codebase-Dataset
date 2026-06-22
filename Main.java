import java.util.List;
import java.util.StringJoiner;

/** Demonstrates finding the articulation points of a small example graph. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        int vertexCount = 5;
        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        Graph graph = Graph.fromEdges(vertexCount, edges);
        List<Integer> articulationPoints = new ArticulationPointFinder().find(graph);

        System.out.println(format(articulationPoints));
    }

    /**
     * Formats the articulation points for display, using {@code -1} to denote a
     * graph with no articulation points.
     */
    private static String format(List<Integer> articulationPoints) {
        if (articulationPoints.isEmpty()) {
            return "-1";
        }
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : articulationPoints) {
            joiner.add(Integer.toString(vertex));
        }
        return joiner.toString();
    }
}
