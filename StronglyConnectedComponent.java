import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public record StronglyConnectedComponent(List<Integer> vertices) implements Iterable<Integer> {

    public StronglyConnectedComponent {
        vertices = List.copyOf(vertices);
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
    public String toString() {
        return vertices.toString();
    }
}
