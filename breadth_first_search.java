import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

interface GraphView<T> {
    Iterable<T> vertices();
    Iterable<T> neighbors(T v);
}

class Graph<T> implements GraphView<T> {
    private final Set<T> vertexSet = new LinkedHashSet<>();
    private final Map<T, List<T>> adj = new LinkedHashMap<>();

    void addVertex(T v) {
        vertexSet.add(v);
    }

    void addDirectedEdge(T from, T to) {
        addVertex(from);
        addVertex(to);
        adj.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
    }

    void addUndirectedEdge(T u, T v) {
        addDirectedEdge(u, v);
        addDirectedEdge(v, u);
    }

    @Override
    public Iterable<T> vertices() {
        return Collections.unmodifiableSet(vertexSet);
    }

    @Override
    public Iterable<T> neighbors(T v) {
        return adj.getOrDefault(v, Collections.emptyList());
    }
}

interface Frontier<T> {
    void add(T node);
    T poll();
    boolean isEmpty();
}

class QueueFrontier<T> implements Frontier<T> {
    private final Deque<T> deque = new ArrayDeque<>();

    @Override public void add(T node) { deque.addLast(node); }
    @Override public T poll()         { return deque.pollFirst(); }
    @Override public boolean isEmpty() { return deque.isEmpty(); }
}

class StackFrontier<T> implements Frontier<T> {
    private final Deque<T> deque = new ArrayDeque<>();

    @Override public void add(T node) { deque.addFirst(node); }
    @Override public T poll()         { return deque.pollFirst(); }
    @Override public boolean isEmpty() { return deque.isEmpty(); }
}

class GraphTraversal<T> {
    private final GraphView<T> graph;
    private final Set<T> visited = new LinkedHashSet<>();
    private final Supplier<Frontier<T>> frontierFactory;

    GraphTraversal(GraphView<T> graph, Supplier<Frontier<T>> frontierFactory) {
        this.graph = graph;
        this.frontierFactory = frontierFactory;
    }

    void traverse(Consumer<T> visitor) {
        for (T vertex : graph.vertices()) {
            if (!visited.contains(vertex))
                expandFrom(vertex, visitor);
        }
    }

    private void expandFrom(T src, Consumer<T> visitor) {
        Frontier<T> frontier = frontierFactory.get();
        visited.add(src);
        frontier.add(src);
        while (!frontier.isEmpty()) {
            T curr = frontier.poll();
            visitor.accept(curr);
            for (T neighbor : graph.neighbors(curr)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    frontier.add(neighbor);
                }
            }
        }
    }

    static <T> List<T> collect(GraphView<T> graph) {
        List<T> result = new ArrayList<>();
        new GraphTraversal<>(graph, () -> new QueueFrontier<>()).traverse(result::add);
        return result;
    }
}

class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph<Integer> graph = new Graph<>();
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(4, 5);

        new GraphTraversal<>(graph, () -> new QueueFrontier<>()).traverse(node -> System.out.print(node + " "));
    }
}
