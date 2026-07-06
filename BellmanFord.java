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

        int[] dist = new int[V];
        int[] pred = new int[V];
        java.util.Arrays.fill(dist, Distances.UNREACHABLE);
        java.util.Arrays.fill(pred, Distances.NO_PREDECESSOR);
        dist[source] = 0;

        List<WeightedEdge> edges = graph.edges();

        for (int i = 0; i < V - 1; i++) {
            for (WeightedEdge e : edges) {
                relax(e, dist, pred);
            }
        }

        // V-th round: detect negative cycle
        int cycleEntry = -1;
        for (WeightedEdge e : edges) {
            if (canRelax(e, dist)) {
                cycleEntry = e.to();
                pred[e.to()] = e.from();
                break;
            }
        }

        if (cycleEntry == -1) {
            return new Distances(source, dist, pred);
        }

        return extractNegativeCycle(cycleEntry, pred, V);
    }

    private static boolean canRelax(WeightedEdge e, int[] dist) {
        int u = e.from();
        return dist[u] != Distances.UNREACHABLE
            && (long) dist[u] + e.weight() < dist[e.to()];
    }

    private static void relax(WeightedEdge e, int[] dist, int[] pred) {
        int u = e.from(), v = e.to(), w = e.weight();
        if (dist[u] != Distances.UNREACHABLE && (long) dist[u] + w < dist[v]) {
            dist[v] = dist[u] + w;
            pred[v] = u;
        }
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

        // Reverse to get forward-path order (no closing repeat — caller wraps around)
        Collections.reverse(traceback);
        return new NegativeCycle(traceback);
    }
}
