import java.util.List;
import java.util.Optional;

final class BellmanFord {

    private BellmanFord() {}

    // Delegation shim — preserves the concrete WeightedGraph overload for call sites
    // that were compiled against it, while the algorithm itself works on Graph.
    static ShortestPathResult shortestPaths(WeightedGraph graph, int source) {
        return shortestPaths((Graph) graph, source);
    }

    static ShortestPathResult shortestPaths(Graph graph, int source) {
        int V = graph.vertices();
        if (source < 0 || source >= V) {
            throw new IllegalArgumentException(
                "Source vertex " + source + " is out of range [0," + (V - 1) + "]");
        }

        EdgeRelaxer relaxer = new EdgeRelaxer(V, source);
        List<? extends Edge> edges = graph.edges();

        for (int i = 0; i < V - 1; i++) {
            for (Edge e : edges) {
                relaxer.relax(e);
            }
        }

        Optional<NegativeCycle> cycle =
            new NegativeCycleDetector(relaxer, edges, V).detect();

        return cycle.isPresent()
            ? cycle.get()
            : new Distances(source, relaxer.distances(), relaxer.predecessors());
    }
}
