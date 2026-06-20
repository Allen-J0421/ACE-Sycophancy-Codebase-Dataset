/**
 * A single directed, weighted edge {@code from -> to} carrying an integer weight.
 *
 * <p>Weights may be negative; Bellman-Ford is specifically chosen over Dijkstra
 * to support that case.
 */
record WeightedEdge(int from, int to, int weight) {

    static WeightedEdge of(int from, int to, int weight) {
        return new WeightedEdge(from, to, weight);
    }
}
