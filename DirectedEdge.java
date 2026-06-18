public final class DirectedEdge {
    private final int source;
    private final int destination;

    private DirectedEdge(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public static DirectedEdge of(int source, int destination) {
        return new DirectedEdge(source, destination);
    }

    public int source() {
        return source;
    }

    public int destination() {
        return destination;
    }
}
