public class Edge {
    private final int source;
    private final int destination;

    public Edge(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return source + " -> " + destination;
    }
}
