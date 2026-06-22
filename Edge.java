class Edge {
    final int from;
    final int to;
    final int weight;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

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
