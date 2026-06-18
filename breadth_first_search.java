import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * An undirected graph over vertices of arbitrary type {@code V}, backed by an
 * adjacency map. Vertices may be any non-null type with well-behaved
 * {@code equals}/{@code hashCode}; they need not be dense integer indices.
 *
 * <p>{@link #vertices()} iterates in insertion order, which keeps whole-graph
 * traversals deterministic. The graph is itself {@link Iterable} over its
 * vertices, so {@code for (V v : graph)} is equivalent to iterating
 * {@code graph.vertices()}.
 */
final class Graph<V> implements Iterable<V> {

    private final Map<V, List<V>> adjacency = new LinkedHashMap<>();

    /** Adds {@code vertex} with no edges if it is not already present. */
    void addVertex(V vertex) {
        Objects.requireNonNull(vertex, "vertex");
        adjacency.computeIfAbsent(vertex, key -> new ArrayList<>());
    }

    /**
     * Adds an undirected edge between {@code u} and {@code v}, creating either
     * endpoint that is not yet present.
     */
    void addEdge(V u, V v) {
        addVertex(u);
        addVertex(v);
        adjacency.get(u).add(v);
        adjacency.get(v).add(u);
    }

    int vertexCount() {
        return adjacency.size();
    }

    /** Returns the graph's vertices in insertion order, as a read-only view. */
    Set<V> vertices() {
        return Collections.unmodifiableSet(adjacency.keySet());
    }

    /**
     * Returns the neighbors of {@code vertex} in insertion order, as a read-only view.
     *
     * @throws IllegalArgumentException if {@code vertex} is not in the graph
     */
    List<V> neighbors(V vertex) {
        List<V> adjacent = adjacency.get(vertex);
        if (adjacent == null) {
            throw new IllegalArgumentException("vertex not in graph: " + vertex);
        }
        return Collections.unmodifiableList(adjacent);
    }

    /** Iterates the graph's vertices in insertion order; the iterator is read-only. */
    @Override
    public Iterator<V> iterator() {
        return vertices().iterator();
    }
}

/**
 * Breadth-first traversal of an undirected {@link Graph} of any vertex type.
 *
 * <p>The traversal visits every vertex, starting a fresh BFS from each unvisited
 * vertex so that disconnected components are all covered.
 */
final class BreadthFirstSearch {

    private BreadthFirstSearch() {
    }

    /**
     * Returns the BFS visitation order across the whole graph, including every
     * connected component.
     */
    static <V> List<V> bfs(Graph<V> graph) {
        Set<V> visited = new HashSet<>();
        List<V> order = new ArrayList<>();

        for (V vertex : graph) {
            if (!visited.contains(vertex)) {
                bfsFromSource(graph, vertex, visited, order);
            }
        }
        return order;
    }

    /**
     * Runs BFS from {@code source}, appending vertices to {@code order} in the
     * sequence they are first dequeued. Vertices reached during this call are
     * added to {@code visited} so callers can skip them.
     */
    private static <V> void bfsFromSource(Graph<V> graph, V source, Set<V> visited, List<V> order) {
        Deque<V> queue = new ArrayDeque<>();
        visited.add(source);
        queue.add(source);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            order.add(current);

            for (V neighbor : graph.neighbors(current)) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Integer vertices: the familiar sample. Adding 0..5 up front fixes a
        // numeric iteration order so the output matches the original demo.
        Graph<Integer> numbers = new Graph<>();
        for (int v = 0; v < 6; v++) {
            numbers.addVertex(v);
        }
        numbers.addEdge(1, 2);
        numbers.addEdge(2, 0);
        numbers.addEdge(0, 3);
        numbers.addEdge(4, 5);
        System.out.println(join(bfs(numbers)));

        // String vertices: the same algorithm over a different vertex type.
        Graph<String> words = new Graph<>();
        words.addEdge("a", "b");
        words.addEdge("b", "c");
        words.addEdge("d", "e");
        System.out.println(join(bfs(words)));
    }

    private static <V> String join(List<V> values) {
        StringJoiner joiner = new StringJoiner(" ");
        for (V value : values) {
            joiner.add(String.valueOf(value));
        }
        return joiner.toString();
    }
}
