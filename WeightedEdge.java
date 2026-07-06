record WeightedEdge(int from, int to, int weight) implements Edge {

    static WeightedEdge of(int from, int to, int weight) {
        return new WeightedEdge(from, to, weight);
    }
}
