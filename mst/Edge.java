package mst;

public record Edge(int from, int to, int weight) implements Comparable<Edge> {
    public Edge {
        // No per-edge validation here; Graph validates vertices against its size.
    }

    public static Edge fromMatrixRow(int[] row) {
        if (row == null || row.length != 3) {
            throw new IllegalArgumentException("Each edge must contain exactly three values: from, to, weight.");
        }
        return new Edge(row[0], row[1], row[2]);
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(weight, other.weight);
    }
}
