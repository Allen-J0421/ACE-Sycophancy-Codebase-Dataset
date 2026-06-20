import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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

    private static final int UNVISITED = -1;
    private static final int COLOR_A = 0;
    private static final int COLOR_B = 1;

    /**
     * Builds an adjacency list for an undirected graph.
     *
     * @param vertexCount number of vertices (0-indexed)
     * @param edges       array of {u, v} pairs representing undirected edges
     * @return adjacency list where index i holds neighbors of vertex i
     * @throws NullPointerException     if edges is null
     * @throws IllegalArgumentException if vertexCount is negative, an edge does not
     *                                  have exactly two endpoints, an endpoint is outside
     *                                  [0, vertexCount), or an edge is a self-loop
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
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        return adjacencyList;
    }

    /** Returns the opposite color for a given color value. */
    private static int flipColor(int color) {
        return color == COLOR_A ? COLOR_B : COLOR_A;
    }

    /**
     * BFS-colors a single connected component starting at {@code start}.
     *
     * @return true if the component is 2-colorable (bipartite), false otherwise
     */
    private static boolean isComponentBipartite(int start, int[] color, List<List<Integer>> adjacencyList) {
        Deque<Integer> queue = new ArrayDeque<>();
        color[start] = COLOR_A;
        queue.offer(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int neighbor : adjacencyList.get(u)) {
                if (color[neighbor] == UNVISITED) {
                    color[neighbor] = flipColor(color[u]);
                    queue.offer(neighbor);
                } else if (color[neighbor] == color[u]) {
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
     */
    public static boolean isBipartite(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacencyList = buildAdjacencyList(vertexCount, edges);

        int[] color = new int[vertexCount];
        Arrays.fill(color, UNVISITED);

        for (int i = 0; i < vertexCount; i++) {
            if (color[i] == UNVISITED && !isComponentBipartite(i, color, adjacencyList)) {
                return false;
            }
        }

        return true;
    }

    /** Runs a single test case, printing the description and PASS/FAIL. */
    private static void check(String description, int vertexCount, int[][] edges, boolean expected) {
        boolean actual = isBipartite(vertexCount, edges);
        String status = (actual == expected) ? "PASS" : "FAIL";
        System.out.printf("[%s] %s — got %b, expected %b%n", status, description, actual, expected);
    }

    public static void main(String[] args) {
        check("Non-bipartite: triangle (odd cycle)",            4, new int[][]{{0, 1}, {0, 2}, {1, 2}, {2, 3}},     false);
        check("Bipartite: simple path",                        4, new int[][]{{0, 1}, {1, 2}, {2, 3}},              true);
        check("Bipartite: even cycle",                         4, new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}},      true);
        check("Single vertex, no edges",                       1, new int[][]{},                                     true);
        check("Disconnected: bipartite path + isolated vertex",5, new int[][]{{0, 1}, {1, 2}, {2, 3}},              true);
        check("Disconnected: odd cycle in one component",      5, new int[][]{{0, 1}, {1, 2}, {2, 0}, {3, 4}},      false);

        // Verify self-loop is rejected at construction time
        try {
            isBipartite(2, new int[][]{{0, 0}});
            System.out.println("[FAIL] Self-loop: expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] Self-loop: " + e.getMessage());
        }
    }
}
