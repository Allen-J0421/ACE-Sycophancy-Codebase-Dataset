import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class BreadthFirstSearch {
    private BreadthFirstSearch() {}

    static <V> Stream<V> stream(Graph<V> graph) {
        Objects.requireNonNull(graph, "graph must not be null");
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                new BfsIterator<>(graph),
                Spliterator.ORDERED | Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.IMMUTABLE
            ),
            false
        );
    }

    static <V> List<V> traverse(Graph<V> graph) {
        return stream(graph).collect(Collectors.toList());
    }

    private static final class BfsIterator<V> implements Iterator<V> {
        private final Graph<V> graph;
        private final Set<V> visited = new HashSet<>();
        private final Queue<V> queue = new ArrayDeque<>();
        private final Iterator<V> vertexIterator;

        BfsIterator(Graph<V> graph) {
            this.graph = graph;
            this.vertexIterator = graph.iterator();
            seedNextComponent();
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public V next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            V current = queue.poll();
            for (V neighbor : graph.neighbors(current)) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
            if (queue.isEmpty()) {
                seedNextComponent();
            }
            return current;
        }

        private void seedNextComponent() {
            while (vertexIterator.hasNext()) {
                V candidate = vertexIterator.next();
                if (visited.add(candidate)) {
                    queue.add(candidate);
                    return;
                }
            }
        }
    }
}
