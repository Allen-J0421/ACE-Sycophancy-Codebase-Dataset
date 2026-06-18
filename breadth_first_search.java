import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;

/**
 * Breadth-first traversal of an undirected graph represented as an adjacency list.
 *
 * <p>The traversal visits every vertex, starting a new BFS from each unvisited
 * vertex so that disconnected components are all covered. Vertices are numbered
 * {@code 0..V-1}.
 */
class BreadthFirstSearch {

    /**
     * Runs BFS from {@code source}, appending vertices to {@code order} in the
     * sequence they are first dequeued. Vertices reached during this call are
     * marked in {@code visited} so callers can skip them.
     */
    static void bfsFromSource(List<List<Integer>> adj, int source, boolean[] visited, List<Integer> order) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            order.add(current);

            for (int neighbor : adj.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    /**
     * Returns the BFS visitation order across the whole graph, including every
     * connected component.
     */
    static List<Integer> bfs(List<List<Integer>> adj) {
        int vertexCount = adj.size();
        boolean[] visited = new boolean[vertexCount];
        List<Integer> order = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                bfsFromSource(adj, vertex, visited, order);
            }
        }
        return order;
    }

    /** Adds an undirected edge between vertices {@code u} and {@code v}. */
    static void addEdge(List<List<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    /** Creates an adjacency list for a graph with {@code vertexCount} vertices and no edges. */
    static List<List<Integer>> newGraph(int vertexCount) {
        List<List<Integer>> adj = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }
        return adj;
    }

    public static void main(String[] args) {
        List<List<Integer>> adj = newGraph(6);

        addEdge(adj, 1, 2);
        addEdge(adj, 2, 0);
        addEdge(adj, 0, 3);
        addEdge(adj, 4, 5);

        StringJoiner output = new StringJoiner(" ");
        for (int vertex : bfs(adj)) {
            output.add(Integer.toString(vertex));
        }
        System.out.println(output);
    }
}
