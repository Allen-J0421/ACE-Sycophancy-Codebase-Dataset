import java.util.Objects;

final class Edge {
    private final int from;
    private final int to;

    private Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }

    static Edge between(int from, int to) {
        return new Edge(from, to);
    }

    int from() {
        return from;
    }

    int to() {
        return to;
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
        return from == edge.from && to == edge.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Edge{from=" + from + ", to=" + to + "}";
    }
}
