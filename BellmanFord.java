import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        // V-th round: if any edge can still be relaxed, a negative cycle is reachable
        int cycleEntry = -1;
        for (WeightedEdge e : edges) {
            if (relaxer.canRelax(e)) {
                relaxer.linkPredecessor(e);
                cycleEntry = e.to();
                break;
            }
        }

        if (cycleEntry == -1) {
            return new Distances(source, relaxer.distances(), relaxer.predecessors());
        }

        return extractNegativeCycle(cycleEntry, relaxer.predecessors(), V);
    }

    private static NegativeCycle extractNegativeCycle(int cycleEntry, int[] pred, int V) {
        // Walk back V steps to guarantee landing inside the cycle
        int x = cycleEntry;
        for (int i = 0; i < V; i++) {
            x = pred[x];
        }

        // Trace the cycle in pred order until we revisit x
        List<Integer> traceback = new ArrayList<>();
        int cur = x;
        do {
            traceback.add(cur);
            cur = pred[cur];
        } while (cur != x);

        Collections.reverse(traceback);
        return new NegativeCycle(traceback);
    }
}
