import java.util.List;
import java.util.Optional;

final class BellmanFord {

    private BellmanFord() {}

    static ShortestPathResult shortestPaths(WeightedGraph graph, int source) {
        int V = graph.vertices();
        if (source < 0 || source >= V) {
            throw new IllegalArgumentException(
                "Source vertex " + source + " is out of range [0," + (V - 1) + "]");
        }

        EdgeRelaxer relaxer = new EdgeRelaxer(V, source);
        List<WeightedEdge> edges = graph.edges();

        for (int i = 0; i < V - 1; i++) {
            for (WeightedEdge e : edges) {
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
