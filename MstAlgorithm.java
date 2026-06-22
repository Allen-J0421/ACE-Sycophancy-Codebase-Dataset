/**
 * Strategy for computing a minimum spanning tree of a {@link WeightedGraph}.
 */
public interface MstAlgorithm {

    /**
     * Computes a minimum spanning tree.
     *
     * @param graph the connected, weighted graph to span
     * @return the edges and total weight of the spanning tree
     */
    MstResult computeMst(WeightedGraph graph);
}
