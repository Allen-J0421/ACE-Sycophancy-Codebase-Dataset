import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge other = (Edge) o;
        return source == other.source && destination == other.destination;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }

    @Override
    public String toString() {
        return source + " -> " + destination;
    }
}
