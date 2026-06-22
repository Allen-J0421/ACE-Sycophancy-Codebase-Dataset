import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * An undirected graph backed by an adjacency list.
 *
 * <p>Vertices are identified by the integers {@code 0 .. vertexCount() - 1}.
 * The adjacency list is not exposed directly; callers read neighbours through
 * {@link #neighbours(int)}, which returns an unmodifiable view.
 */
final class Graph {

    private final List<List<Integer>> adjacency;

    /**
     * Creates a graph with the given number of vertices and no edges.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative
     */
    Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    /** Returns the number of vertices in the graph. */
    int vertexCount() {
        return adjacency.size();
    }

    /**
     * Adds an undirected edge between {@code u} and {@code v}.
     *
     * @throws IndexOutOfBoundsException if either endpoint is not a valid vertex
     */
    void addEdge(int u, int v) {
        checkVertex(u);
        checkVertex(v);
        adjacency.get(u).add(v);
        adjacency.get(v).add(u);
    }

    /** Returns an unmodifiable view of the neighbours of {@code vertex}. */
    List<Integer> neighbours(int vertex) {
        checkVertex(vertex);
        return Collections.unmodifiableList(adjacency.get(vertex));
    }

    private void checkVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacency.size()) {
            throw new IndexOutOfBoundsException(
                "vertex out of range [0, " + adjacency.size() + "): " + vertex);
        }
    }
}

/**
 * Finds the connected components of an undirected {@link Graph} using
 * breadth-first search.
 */
final class ConnectedComponentsFinder {

    /**
     * Returns the connected components of {@code graph}. Each component is the
     * list of vertices reachable from one another; the components together
     * partition every vertex of the graph.
     */
    List<List<Integer>> find(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                components.add(exploreFrom(graph, vertex, visited));
            }
        }
        return components;
    }

    /** Collects every vertex reachable from {@code source}, marking it visited. */
    private List<Integer> exploreFrom(Graph graph, int source, boolean[] visited) {
        List<Integer> component = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();

        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            component.add(current);

            for (int neighbour : graph.neighbours(current)) {
                if (!visited[neighbour]) {
                    visited[neighbour] = true;
                    queue.add(neighbour);
                }
            }
        }
        return component;
    }
}

/** Demonstrates {@link ConnectedComponentsFinder} on a small sample graph. */
class ConnectedComponents {

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        List<List<Integer>> components = new ConnectedComponentsFinder().find(graph);

        for (List<Integer> component : components) {
            StringBuilder line = new StringBuilder();
            for (int vertex : component) {
                line.append(vertex).append(' ');
            }
            System.out.println(line.toString().stripTrailing());
        }
    }
}
