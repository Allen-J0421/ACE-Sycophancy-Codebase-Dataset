final class Edge {
    private final int to;
    private final int weight;

    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }

    int to() {
        return to;
    }

    int weight() {
        return weight;
    }
}
