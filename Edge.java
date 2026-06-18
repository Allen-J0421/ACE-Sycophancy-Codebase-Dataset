final class Edge {
    private final int source;
    private final int destination;

    Edge(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    int source() {
        return source;
    }

    int destination() {
        return destination;
    }
}
