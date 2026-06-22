package maxflow.solve;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import maxflow.graph.Capacity;
import maxflow.graph.Edge;
import maxflow.graph.FlowNetwork;
import maxflow.graph.ResidualGraph;

/**
 * The minimum cut induced by a maximum flow, by the max-flow min-cut theorem.
 *
 * <p>Once a flow is maximal, no augmenting path remains, so flooding the residual graph
 * from the source reaches a set of vertices {@code S} that excludes the sink. {@code S} is
 * the <em>source side</em> and its complement {@code T} the <em>sink side</em>; the
 * original edges that run from {@code S} to {@code T} are the cut, and their capacities sum
 * to the value of the maximum flow.
 *
 * <p>A {@code MinCut} is derived purely from a {@link FlowNetwork} and the
 * {@link MaxFlowResult} of solving it: replaying the result's per-edge flows reconstructs
 * the residual graph at maximum flow, from which the partition is read.
 */
public final class MinCut {

    private final Set<Integer> sourceSide;
    private final Set<Integer> sinkSide;
    private final List<Edge> cutEdges;
    private final Capacity capacity;

    private MinCut(Set<Integer> sourceSide, Set<Integer> sinkSide, List<Edge> cutEdges, Capacity capacity) {
        this.sourceSide = sourceSide;
        this.sinkSide = sinkSide;
        this.cutEdges = cutEdges;
        this.capacity = capacity;
    }

    /**
     * Computes the minimum cut of {@code network} given the {@code result} of computing its
     * maximum flow. The result must be the maximum flow of the given network.
     */
    public static MinCut of(FlowNetwork network, MaxFlowResult result) {
        Objects.requireNonNull(network, "network");
        Objects.requireNonNull(result, "result");

        ResidualGraph residual = new ResidualGraph(network);
        for (Edge flow : result.flowEdges()) {
            residual.pushFlow(flow.from(), flow.to(), flow.value());
        }

        boolean[] reachable = reachableFrom(result.source(), residual);

        Set<Integer> sourceSide = new LinkedHashSet<>();
        Set<Integer> sinkSide = new LinkedHashSet<>();
        for (int v = 0; v < network.vertexCount(); v++) {
            (reachable[v] ? sourceSide : sinkSide).add(v);
        }

        List<Edge> cutEdges = new ArrayList<>();
        Capacity capacity = Capacity.ZERO;
        for (Edge edge : network.edges()) {
            if (reachable[edge.from()] && !reachable[edge.to()]) {
                cutEdges.add(edge);
                capacity = capacity.plus(edge.value());
            }
        }

        return new MinCut(
                Collections.unmodifiableSet(sourceSide),
                Collections.unmodifiableSet(sinkSide),
                List.copyOf(cutEdges),
                capacity);
    }

    /** Floods the residual graph from {@code source} over edges with positive residual capacity. */
    private static boolean[] reachableFrom(int source, ResidualGraph residual) {
        boolean[] reachable = new boolean[residual.vertexCount()];
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(source);
        reachable[source] = true;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            residual.forEachResidualEdge(u, (v, residualCapacity) -> {
                if (!reachable[v]) {
                    reachable[v] = true;
                    queue.add(v);
                }
            });
        }
        return reachable;
    }

    /** Returns the source-side partition {@code S}: vertices still reachable from the source. */
    public Set<Integer> sourceSide() {
        return sourceSide;
    }

    /** Returns the sink-side partition {@code T}: vertices separated from the source. */
    public Set<Integer> sinkSide() {
        return sinkSide;
    }

    /** Returns the original edges crossing from the source side to the sink side. */
    public List<Edge> cutEdges() {
        return cutEdges;
    }

    /** Returns the total capacity of the cut, which equals the maximum flow value. */
    public Capacity capacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return "MinCut " + sourceSide + " | " + sinkSide + " (capacity " + capacity + ")";
    }
}
