import java.util.List;
import java.util.StringJoiner;

/**
 * Demonstrates finding the articulation points and bridges of a small example
 * graph.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        int vertexCount = 5;
        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        Graph graph = Graph.fromEdges(vertexCount, edges);
        ConnectivityResult result = new GraphConnectivity().analyze(graph);

        System.out.println("Articulation points: " + formatVertices(result.articulationPoints()));
        System.out.println("Bridges: " + formatEdges(result.bridges()));
    }

    /**
     * Formats articulation points for display, using {@code -1} to denote a graph
     * with none.
     */
    private static String formatVertices(List<Integer> vertices) {
        if (vertices.isEmpty()) {
            return "-1";
        }
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : vertices) {
            joiner.add(Integer.toString(vertex));
        }
        return joiner.toString();
    }

    /**
     * Formats bridges as {@code u-v} pairs for display, using {@code -1} to denote
     * a graph with none.
     */
    private static String formatEdges(List<Graph.Edge> edges) {
        if (edges.isEmpty()) {
            return "-1";
        }
        StringJoiner joiner = new StringJoiner(" ");
        for (Graph.Edge edge : edges) {
            joiner.add(edge.u() + "-" + edge.v());
        }
        return joiner.toString();
    }
}
