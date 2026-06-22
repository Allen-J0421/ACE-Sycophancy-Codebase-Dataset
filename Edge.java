final class Edge implements Comparable<Edge> {
    final int from;
    final int to;
    final int weight;

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

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }
}
