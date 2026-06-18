import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Function;

public class VertexStream {
    private final List<Integer> vertices;

    public VertexStream(List<Integer> vertices) {
        this.vertices = new ArrayList<>(vertices);
    }

    public VertexStream filter(Predicate<Integer> predicate) {
        List<Integer> filtered = new ArrayList<>();
        for (int vertex : vertices) {
            if (predicate.test(vertex)) {
                filtered.add(vertex);
            }
        }
        return new VertexStream(filtered);
    }

    public <T> List<T> map(Function<Integer, T> mapper) {
        List<T> mapped = new ArrayList<>();
        for (int vertex : vertices) {
            mapped.add(mapper.apply(vertex));
        }
        return mapped;
    }

    public VertexStream limit(int count) {
        List<Integer> limited = new ArrayList<>(vertices.subList(0, Math.min(count, vertices.size())));
        return new VertexStream(limited);
    }

    public VertexStream skip(int count) {
        List<Integer> skipped = new ArrayList<>(vertices.subList(Math.min(count, vertices.size()), vertices.size()));
        return new VertexStream(skipped);
    }

    public List<Integer> collect() {
        return new ArrayList<>(vertices);
    }

    public int count() {
        return vertices.size();
    }

    public void forEach(java.util.function.Consumer<Integer> action) {
        for (int vertex : vertices) {
            action.accept(vertex);
        }
    }

    public boolean anyMatch(Predicate<Integer> predicate) {
        for (int vertex : vertices) {
            if (predicate.test(vertex)) {
                return true;
            }
        }
        return false;
    }

    public boolean allMatch(Predicate<Integer> predicate) {
        for (int vertex : vertices) {
            if (!predicate.test(vertex)) {
                return false;
            }
        }
        return true;
    }

    public java.util.Optional<Integer> first() {
        return vertices.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(vertices.get(0));
    }

    public java.util.Optional<Integer> findAny() {
        return vertices.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(vertices.get(0));
    }

    public int sum() {
        int sum = 0;
        for (int vertex : vertices) {
            sum += vertex;
        }
        return sum;
    }

    public double average() {
        return vertices.isEmpty() ? 0 : (double) sum() / vertices.size();
    }

    public int max() {
        return vertices.stream().mapToInt(Integer::intValue).max().orElse(Integer.MIN_VALUE);
    }

    public int min() {
        return vertices.stream().mapToInt(Integer::intValue).min().orElse(Integer.MAX_VALUE);
    }
}
