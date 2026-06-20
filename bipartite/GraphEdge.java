package bipartite;

/**
 * The common contract for an edge supplied when building a graph: it connects
 * two vertices {@code u} and {@code v}. Concrete edge types may carry extra data
 * (for example {@link WeightedEdge} adds a weight); validation and construction
 * rely only on the endpoints exposed here.
 *
 * <p>For a directed graph the convention is that the edge runs from {@code u} to
 * {@code v}; for an undirected graph the order is irrelevant.
 */
public interface GraphEdge {

    /** @return the first endpoint (the source, for a directed edge) */
    int u();

    /** @return the second endpoint (the target, for a directed edge) */
    int v();
}
