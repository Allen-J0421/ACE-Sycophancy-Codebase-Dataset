class Edge {
    private final int from;
    private final int to;
    private final int weight;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    int from() { return from; }
    int to() { return to; }
    int weight() { return weight; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) return false;
        Edge other = (Edge) obj;
        return from == other.from && to == other.to && weight == other.weight;
    }

    @Override
    public int hashCode() {
        int result = from;
        result = 31 * result + to;
        result = 31 * result + weight;
        return result;
    }

    @Override
    public String toString() {
        return from + " - " + to + "\t" + weight;
    }
}
