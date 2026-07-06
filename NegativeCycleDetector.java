import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

final class NegativeCycleDetector {

    private final EdgeRelaxer relaxer;
    private final List<WeightedEdge> edges;
    private final int vertexCount;

    NegativeCycleDetector(EdgeRelaxer relaxer, List<WeightedEdge> edges, int vertexCount) {
        this.relaxer = relaxer;
        this.edges = edges;
        this.vertexCount = vertexCount;
    }

    Optional<NegativeCycle> detect() {
        for (WeightedEdge e : edges) {
            if (relaxer.canRelax(e)) {
                relaxer.linkPredecessor(e);
                return Optional.of(buildCycle(e.to()));
            }
        }
        return Optional.empty();
    }

    private NegativeCycle buildCycle(int cycleEntry) {
        int[] pred = relaxer.predecessors();

        // Walk back vertexCount steps to guarantee landing inside the cycle
        int x = cycleEntry;
        for (int i = 0; i < vertexCount; i++) {
            x = pred[x];
        }

        // Trace the cycle in predecessor order until we revisit x
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
