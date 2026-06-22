import java.util.Objects;

final class Edge implements Comparable<Edge> {
    private final int from;
    private final int to;
    private final int weight;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    static Edge fromMatrixRow(int[] row) {
        if (row == null || row.length != 3) {
            throw new IllegalArgumentException("Each edge must contain exactly three values: from, to, weight.");
        }
        return new Edge(row[0], row[1], row[2]);
    }

    int from() {
        return from;
    }

    int to() {
        return to;
    }

    int weight() {
        return weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Edge)) {
            return false;
        }
        Edge edge = (Edge) other;
        return from == edge.from && to == edge.to && weight == edge.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    @Override
    public String toString() {
        return "Edge{from=" + from + ", to=" + to + ", weight=" + weight + '}';
    }
}
