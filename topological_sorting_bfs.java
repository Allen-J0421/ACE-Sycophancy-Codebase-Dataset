import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Topological sort of a directed graph using Kahn's algorithm (BFS over
 * in-degrees). The graph is represented as an adjacency list where index {@code i}
 * holds the list of vertices that vertex {@code i} points to.
 */
class TopologicalSort {

    /**
     * Returns a topological ordering of the graph's vertices.
     *
     * @param adj adjacency list; {@code adj.get(u)} are the successors of {@code u}
     * @return vertices in topological order
     * @throws IllegalArgumentException if the graph contains a cycle
     */
    static List<Integer> topoSort(List<List<Integer>> adj) {
        int n = adj.size();
        int[] indegree = computeIndegrees(adj);

        Queue<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < n; v++) {
            if (indegree[v] == 0) {
                ready.add(v);
            }
        }

        List<Integer> order = new ArrayList<>(n);
        while (!ready.isEmpty()) {
            int node = ready.poll();
            order.add(node);
            for (int next : adj.get(node)) {
                if (--indegree[next] == 0) {
                    ready.add(next);
                }
            }
        }

        if (order.size() != n) {
            throw new IllegalArgumentException(
                "Graph contains a cycle; no topological ordering exists");
        }
        return order;
    }

    private static int[] computeIndegrees(List<List<Integer>> adj) {
        int[] indegree = new int[adj.size()];
        for (List<Integer> successors : adj) {
            for (int next : successors) {
                indegree[next]++;
            }
        }
        return indegree;
    }

    private static List<List<Integer>> newGraph(int vertexCount) {
        List<List<Integer>> adj = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adj.add(new ArrayList<>());
        }
        return adj;
    }

    private static void addEdge(List<List<Integer>> adj, int from, int to) {
        adj.get(from).add(to);
    }

    public static void main(String[] args) {
        List<List<Integer>> adj = newGraph(6);
        addEdge(adj, 0, 1);
        addEdge(adj, 1, 2);
        addEdge(adj, 2, 3);
        addEdge(adj, 4, 5);
        addEdge(adj, 5, 1);
        addEdge(adj, 5, 2);

        List<Integer> order = topoSort(adj);
        StringBuilder sb = new StringBuilder();
        for (int vertex : order) {
            sb.append(vertex).append(' ');
        }
        System.out.println(sb.toString().trim());
    }
}
