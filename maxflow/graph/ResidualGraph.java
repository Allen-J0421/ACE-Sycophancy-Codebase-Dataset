package maxflow.graph;

/**
 * The mutable residual graph used while computing a maximum flow.
 *
 * <p>It is initialised from a {@link FlowNetwork}: the residual capacity of every
 * edge starts equal to its capacity. As flow is pushed along an edge, residual
 * capacity is moved from the forward edge to the corresponding reverse edge,
 * which is what allows the algorithm to "cancel" flow later.
 */
public final class ResidualGraph {

    private final FlowNetwork network;
    private final int[][] residual;

    public ResidualGraph(FlowNetwork network) {
        this.network = network;
        this.residual = network.toMatrix();
    }

    /** Returns the number of vertices. */
    public int vertexCount() {
        return residual.length;
    }

    /** Returns the remaining residual capacity of the edge {@code from -> to}. */
    public Capacity residualCapacity(int from, int to) {
        return Capacity.of(residual[from][to]);
    }

    /** Returns true if more flow can currently be pushed along {@code from -> to}. */
    public boolean hasResidualCapacity(int from, int to) {
        return residual[from][to] > 0;
    }

    /**
     * Receives each residual edge leaving a vertex during a traversal. The residual
     * capacity is passed as a raw {@code int} rather than a {@link Capacity}: this
     * callback fires in the inner loop of every search, so it stays allocation-free.
     */
    @FunctionalInterface
    public interface EdgeConsumer {
        void accept(int to, int residualCapacity);
    }

    /**
     * Invokes {@code consumer} for every vertex reachable from {@code from} along an
     * edge with positive residual capacity. This lets traversals iterate the graph's
     * adjacencies without depending on its underlying representation.
     */
    public void forEachResidualEdge(int from, EdgeConsumer consumer) {
        int[] row = residual[from];
        for (int to = 0; to < row.length; to++) {
            if (row[to] > 0) {
                consumer.accept(to, row[to]);
            }
        }
    }

    /**
     * Pushes {@code amount} of flow along {@code from -> to}, decreasing the forward
     * residual capacity and increasing the reverse residual capacity.
     *
     * @throws IllegalArgumentException if {@code amount} is zero, or exceeds the
     *         residual capacity
     */
    public void pushFlow(int from, int to, Capacity amount) {
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("Flow amount must be positive, was " + amount);
        }
        int units = amount.units();
        if (units > residual[from][to]) {
            throw new IllegalArgumentException(
                    "Cannot push " + amount + " along " + from + " -> " + to
                            + "; residual capacity is " + residual[from][to]);
        }
        residual[from][to] -= units;
        residual[to][from] += units;
    }

    /**
     * Returns the net flow currently routed along the original edge {@code from -> to},
     * i.e. {@code capacity - residualCapacity}, clamped at zero so reverse residual
     * edges are not reported as negative flow.
     */
    public Capacity flowOn(int from, int to) {
        return Capacity.of(Math.max(0, network.capacity(from, to).units() - residual[from][to]));
    }
}
