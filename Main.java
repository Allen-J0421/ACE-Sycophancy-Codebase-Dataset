import java.util.List;
import java.util.StringJoiner;

/**
 * Demonstrates the undirected connectivity analysis (articulation points and
 * bridges) and the directed strongly-connected-components analysis on small
 * example graphs.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        demonstrateUndirectedConnectivity();
        demonstrateStronglyConnectedComponents();
    }

    private static void demonstrateUndirectedConnectivity() {
        UndirectedGraph graph =
                UndirectedGraph.fromEdges(5, new int[][] {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}});
        ConnectivityResult result = new GraphConnectivity().analyze(graph);

        System.out.println("Articulation points: " + formatVertices(result.articulationPoints()));
        System.out.println("Bridges: " + formatEdges(result.bridges()));
    }

    private static void demonstrateStronglyConnectedComponents() {
        DirectedGraph graph =
                DirectedGraph.fromEdges(5, new int[][] {{1, 0}, {0, 2}, {2, 1}, {0, 3}, {3, 4}});
        List<List<Integer>> components = new StronglyConnectedComponents().find(graph);

        StringJoiner joiner = new StringJoiner(" ");
        for (List<Integer> component : components) {
            joiner.add(component.toString());
        }
        System.out.println("Strongly connected components: " + joiner);
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
    private static String formatEdges(List<Edge> edges) {
        if (edges.isEmpty()) {
            return "-1";
        }
        StringJoiner joiner = new StringJoiner(" ");
        for (Edge edge : edges) {
            joiner.add(edge.u() + "-" + edge.v());
        }
        return joiner.toString();
    }
}
