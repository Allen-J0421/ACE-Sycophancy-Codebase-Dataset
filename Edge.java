public final class Edge {
    private final int source;
    private final int destination;

    private Edge(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public static Edge between(int source, int destination) {
        return new Edge(source, destination);
    }

    static Edge fromPair(int[] edgePair, int edgeIndex) {
        if (edgePair == null || edgePair.length != 2) {
            throw new IllegalArgumentException(
                    "Each edge must contain exactly two vertices. Invalid edge at index "
                            + edgeIndex
                            + ".");
        }

        return new Edge(edgePair[0], edgePair[1]);
    }

    public int source() {
        return source;
    }

    public int destination() {
        return destination;
    }
}
