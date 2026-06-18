import java.util.ArrayList;
import java.util.List;

/**
 * Depth-first traversal of an undirected graph represented as an adjacency list.
 *
 * <p>The graph is stored as a {@code List<List<Integer>>} where index {@code v}
 * holds the neighbours of vertex {@code v}. {@link #dfs(List)} visits every
 * vertex, restarting from any unvisited vertex so that disconnected components
 * are also covered, and returns vertices in the order they were first reached.
 *
 * <p>Note: this class is intentionally package-private so the file name
 * ({@code depth_first_search.java}) need not match a public class name.
 */
final class DepthFirstSearch {

    private DepthFirstSearch() {
        // Utility class: not meant to be instantiated.
    }

    /**
     * Returns the depth-first traversal order of {@code adjacency}, covering
     * every connected component.
     *
     * @param adjacency adjacency list; entry {@code v} lists the neighbours of v
     * @return vertices in the order they were first visited
     */
    static List<Integer> dfs(List<List<Integer>> adjacency) {
        boolean[] visited = new boolean[adjacency.size()];
        List<Integer> order = new ArrayList<>();

        for (int vertex = 0; vertex < adjacency.size(); vertex++) {
            if (!visited[vertex]) {
                dfsFrom(adjacency, vertex, visited, order);
            }
        }
        return order;
    }

    /**
     * Recursively visits {@code vertex} and all vertices reachable from it that
     * have not yet been visited, appending each to {@code order}.
     */
    private static void dfsFrom(List<List<Integer>> adjacency, int vertex,
                                boolean[] visited, List<Integer> order) {
        visited[vertex] = true;
        order.add(vertex);

        for (int neighbour : adjacency.get(vertex)) {
            if (!visited[neighbour]) {
                dfsFrom(adjacency, neighbour, visited, order);
            }
        }
    }

    /** Adds an undirected edge between {@code u} and {@code v}. */
    static void addEdge(List<List<Integer>> adjacency, int u, int v) {
        adjacency.get(u).add(v);
        adjacency.get(v).add(u);
    }

    /** Builds an empty adjacency list for a graph with {@code vertexCount} vertices. */
    static List<List<Integer>> newGraph(int vertexCount) {
        List<List<Integer>> adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
        return adjacency;
    }

    public static void main(String[] args) {
        List<List<Integer>> adjacency = newGraph(6);

        addEdge(adjacency, 1, 2);
        addEdge(adjacency, 0, 3);
        addEdge(adjacency, 2, 0);
        addEdge(adjacency, 5, 4);

        for (int vertex : dfs(adjacency)) {
            System.out.print(vertex + " ");
        }
    }
}
