public final class WeightedEdge {
    private final int from;
    private final int to;
    private final int weight;

    private WeightedEdge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public static WeightedEdge of(int from, int to, int weight) {
        return new WeightedEdge(from, to, weight);
    }

    public int from() {
        return from;
    }

    public int to() {
        return to;
    }

    public int weight() {
        return weight;
    }
}
