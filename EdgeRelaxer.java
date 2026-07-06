import java.util.Arrays;

final class EdgeRelaxer {

    private final int[] dist;
    private final int[] pred;

    EdgeRelaxer(int vertices, int source) {
        dist = new int[vertices];
        pred = new int[vertices];
        Arrays.fill(dist, Distances.UNREACHABLE);
        Arrays.fill(pred, Distances.NO_PREDECESSOR);
        dist[source] = 0;
    }

    boolean relax(WeightedEdge e) {
        int u = e.from(), v = e.to(), w = e.weight();
        if (dist[u] != Distances.UNREACHABLE && (long) dist[u] + w < dist[v]) {
            dist[v] = dist[u] + w;
            pred[v] = u;
            return true;
        }
        return false;
    }

    boolean canRelax(WeightedEdge e) {
        int u = e.from();
        return dist[u] != Distances.UNREACHABLE
            && (long) dist[u] + e.weight() < dist[e.to()];
    }

    // Records e.from() as the predecessor of e.to() without changing distances.
    // Used during negative-cycle detection when the distance arrays must not be updated further.
    void linkPredecessor(WeightedEdge e) {
        pred[e.to()] = e.from();
    }

    int[] distances() { return dist; }

    int[] predecessors() { return pred; }
}
