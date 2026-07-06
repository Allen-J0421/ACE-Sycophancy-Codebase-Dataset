record WeightedEdge(int from, int to, int weight) {

    static WeightedEdge of(int from, int to, int weight) {
        return new WeightedEdge(from, to, weight);
    }
}
