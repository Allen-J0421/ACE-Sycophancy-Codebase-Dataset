import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class StronglyConnectedComponent implements Iterable<Integer> {
    private final List<Integer> vertices;

    public StronglyConnectedComponent(List<Integer> vertices) {
        this.vertices = List.copyOf(vertices);
    }

    public List<Integer> vertices() {
        return vertices;
    }

    public int size() {
        return vertices.size();
    }

    public Stream<Integer> stream() {
        return vertices.stream();
    }

    @Override
    public Iterator<Integer> iterator() {
        return vertices.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StronglyConnectedComponent other)) return false;
        return vertices.equals(other.vertices);
    }

    @Override
    public int hashCode() {
        return vertices.hashCode();
    }

    @Override
    public String toString() {
        return vertices.toString();
    }
}
