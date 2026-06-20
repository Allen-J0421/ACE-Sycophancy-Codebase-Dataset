import java.util.Objects;

public class Edge {
    private final int source;
    private final int destination;

    public Edge(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public int source() {
        return source;
    }

    public int destination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge other)) return false;
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
