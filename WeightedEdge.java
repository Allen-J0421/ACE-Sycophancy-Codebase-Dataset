final class WeightedEdge {
    final int from;
    final int to;
    final int weight;

    private WeightedEdge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    static WeightedEdge of(int from, int to, int weight) {
        return new WeightedEdge(from, to, weight);
    }
}
