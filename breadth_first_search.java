import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;

/**
 * An undirected graph over vertices numbered {@code 0..vertexCount-1}, backed by
 * an adjacency list. Owning the adjacency structure behind this type keeps its
 * invariants (a fixed vertex count and symmetric edges) in one place instead of
 * spread across free functions that pass a raw {@code List<List<Integer>>} around.
 */
final class Graph {

    private final List<List<Integer>> adjacency;

    Graph(int vertexCount) {
        adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    int vertexCount() {
        return adjacency.size();
    }

    /** Adds an undirected edge between vertices {@code u} and {@code v}. */
    void addEdge(int u, int v) {
        adjacency.get(u).add(v);
        adjacency.get(v).add(u);
    }

    /** Returns the neighbors of {@code vertex} in insertion order, as a read-only view. */
    List<Integer> neighbors(int vertex) {
        return Collections.unmodifiableList(adjacency.get(vertex));
    }
}

/**
 * Breadth-first traversal of an undirected {@link Graph}.
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
    static List<Integer> bfs(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> order = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                bfsFromSource(graph, vertex, visited, order);
            }
        }
        return order;
    }

    /**
     * Runs BFS from {@code source}, appending vertices to {@code order} in the
     * sequence they are first dequeued. Vertices reached during this call are
     * marked in {@code visited} so callers can skip them.
     */
    private static void bfsFromSource(Graph graph, int source, boolean[] visited, List<Integer> order) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            order.add(current);

            for (int neighbor : graph.neighbors(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        StringJoiner output = new StringJoiner(" ");
        for (int vertex : bfs(graph)) {
            output.add(Integer.toString(vertex));
        }
        System.out.println(output);
    }
}
