import java.util.Arrays;

final class EdgeRelaxer {

    private final int[] dist;
    private final PredecessorMap predecessorMap;

    EdgeRelaxer(int vertices, int source) {
        dist = new int[vertices];
        Arrays.fill(dist, Distances.UNREACHABLE);
        dist[source] = 0;
        predecessorMap = new ArrayPredecessorMap(vertices);
    }

    boolean relax(Edge e) {
        int u = e.from(), v = e.to(), w = e.weight();
        if (dist[u] != Distances.UNREACHABLE && (long) dist[u] + w < dist[v]) {
            dist[v] = dist[u] + w;
            predecessorMap.set(v, u);
            return true;
        }
        return false;
    }

    boolean canRelax(Edge e) {
        int u = e.from();
        return dist[u] != Distances.UNREACHABLE
            && (long) dist[u] + e.weight() < dist[e.to()];
    }

    // Records e.from() as the predecessor of e.to() without changing distances.
    // Used during negative-cycle detection when the distance arrays must not be updated further.
    void linkPredecessor(Edge e) {
        predecessorMap.set(e.to(), e.from());
    }

    int[] distances() { return dist; }

    PredecessorMap predecessors() { return predecessorMap; }
}
