import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Determines whether an undirected graph is bipartite using BFS 2-coloring.
 *
 * A graph is bipartite if its vertices can be split into two disjoint sets
 * such that every edge connects vertices from different sets (equivalently,
 * the graph contains no odd-length cycles).
 */
class BipartiteGraph {

    private BipartiteGraph() {}

    /**
     * Validates a single edge against the graph's vertex count.
     *
     * @throws NullPointerException     if edge is null
     * @throws IllegalArgumentException if edge does not have exactly two endpoints,
     *                                  an endpoint is outside [0, vertexCount), or
     *                                  the edge is a self-loop
     */
    private static void validateEdge(int[] edge, int vertexCount) {
        Objects.requireNonNull(edge, "individual edge must not be null");
        if (edge.length != 2) {
            throw new IllegalArgumentException(
                "Each edge must have exactly 2 endpoints, got " + edge.length);
        }
        int u = edge[0];
        int v = edge[1];
        if (u < 0 || u >= vertexCount || v < 0 || v >= vertexCount) {
            throw new IllegalArgumentException(
                "Edge [" + u + ", " + v + "] references a vertex outside [0, " + vertexCount + ")");
        }
        if (u == v) {
            throw new IllegalArgumentException(
                "Self-loop on vertex " + u + " is not allowed in a bipartite graph");
        }
    }

    /**
     * Builds an adjacency list for an undirected graph.
     *
     * @param vertexCount number of vertices (0-indexed)
     * @param edges       array of {u, v} pairs representing undirected edges
     * @return adjacency list where index i holds neighbors of vertex i
     * @throws NullPointerException     if edges is null or any individual edge is null
     * @throws IllegalArgumentException if vertexCount is negative or any edge is invalid
     *                                  (see {@link #validateEdge})
     */
    private static List<List<Integer>> buildAdjacencyList(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }
        Objects.requireNonNull(edges, "edges must not be null");

        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            validateEdge(edge, vertexCount);
            adjacencyList.get(edge[0]).add(edge[1]);
            adjacencyList.get(edge[1]).add(edge[0]);
        }

        return adjacencyList;
    }

    /**
     * BFS-colors a single connected component starting at {@code start}.
     *
     * @param start         the unvisited vertex to begin BFS from
     * @param visited       tracks which vertices have been reached; modified in place
     * @param side          partition assignment for each visited vertex; modified in place
     * @param adjacencyList the graph's adjacency list
     * @return true if the component is 2-colorable (bipartite), false otherwise
     */
    private static boolean isComponentBipartite(
            int start, boolean[] visited, boolean[] side, List<List<Integer>> adjacencyList) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[start] = true;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int neighbor : adjacencyList.get(u)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    side[neighbor] = !side[u];
                    queue.offer(neighbor);
                } else if (side[neighbor] == side[u]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Determines whether the graph defined by the given vertices and edges is bipartite.
     *
     * Iterates over every unvisited vertex, running a BFS 2-coloring check on its
     * connected component. Handles disconnected graphs correctly.
     *
     * Time complexity:  O(V + E)
     * Space complexity: O(V)
     *
     * @param vertexCount number of vertices (0-indexed)
     * @param edges       array of {u, v} pairs representing undirected edges
     * @return true if the graph is bipartite, false otherwise
     * @throws NullPointerException     if edges is null or any individual edge is null
     * @throws IllegalArgumentException if vertexCount is negative, an edge does not have
     *                                  exactly two endpoints, an endpoint is out of range,
     *                                  or an edge is a self-loop
     */
    public static boolean isBipartite(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacencyList = buildAdjacencyList(vertexCount, edges);

        boolean[] visited = new boolean[vertexCount];
        boolean[] side    = new boolean[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            if (!visited[i] && !isComponentBipartite(i, visited, side, adjacencyList)) {
                return false;
            }
        }

        return true;
    }

    /** Runs a bipartite check, prints PASS/FAIL, and returns true if the result matched. */
    private static boolean check(String description, int vertexCount, int[][] edges, boolean expected) {
        boolean actual = isBipartite(vertexCount, edges);
        boolean passed = (actual == expected);
        System.out.printf("[%s] %s — got %b, expected %b%n",
            passed ? "PASS" : "FAIL", description, actual, expected);
        return passed;
    }

    /** Verifies that isBipartite throws IllegalArgumentException, prints PASS/FAIL, and returns true if it did. */
    private static boolean checkThrows(String description, int vertexCount, int[][] edges) {
        try {
            isBipartite(vertexCount, edges);
            System.out.printf("[FAIL] %s — expected IllegalArgumentException%n", description);
            return false;
        } catch (IllegalArgumentException e) {
            System.out.printf("[PASS] %s — %s%n", description, e.getMessage());
            return true;
        }
    }

    public static void main(String[] args) {
        boolean[] results = {
            check("Non-bipartite: triangle (odd cycle)",            4, new int[][]{{0, 1}, {0, 2}, {1, 2}, {2, 3}},  false),
            check("Bipartite: simple path",                        4, new int[][]{{0, 1}, {1, 2}, {2, 3}},           true),
            check("Bipartite: even cycle",                         4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}},   true),
            check("Zero vertices, no edges",                       0, new int[][]{},                                  true),
            check("Single vertex, no edges",                       1, new int[][]{},                                  true),
            check("Disconnected: bipartite path + isolated vertex",5, new int[][]{{0, 1}, {1, 2}, {2, 3}},           true),
            check("Disconnected: odd cycle in one component",      5, new int[][]{{0, 1}, {1, 2}, {2, 0}, {3, 4}},   false),
            checkThrows("Self-loop",           2, new int[][]{{0, 0}}),
            checkThrows("Out-of-range vertex", 2, new int[][]{{0, 5}}),
            checkThrows("Malformed edge",      2, new int[][]{{0, 1, 2}}),
        };

        int passed = 0;
        for (boolean r : results) if (r) passed++;
        System.out.printf("%nResults: %d/%d passed%n", passed, results.length);
    }
}
