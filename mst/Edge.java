package mst;

public record Edge(int from, int to, int weight) implements Comparable<Edge> {
    public Edge {
        // No per-edge validation here; Graph validates vertices against its size.
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(weight, other.weight);
    }
}
