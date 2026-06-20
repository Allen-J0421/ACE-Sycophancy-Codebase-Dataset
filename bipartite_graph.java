import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Determines whether an undirected graph is bipartite using BFS 2-coloring.
 *
 * A graph is bipartite if its vertices can be split into two disjoint sets
 * such that every edge connects vertices from different sets (equivalently,
 * the graph contains no odd-length cycles).
 */
class BipartiteGraph {

    private static final int UNVISITED = -1;
    private static final int COLOR_A = 0;
    private static final int COLOR_B = 1;

    /**
     * Builds an adjacency list for an undirected graph.
     *
     * @param vertexCount number of vertices (0-indexed)
     * @param edges       array of {u, v} pairs representing undirected edges
     * @return adjacency list where index i holds neighbors of vertex i
     * @throws IllegalArgumentException if vertexCount is negative or an edge
     *                                  references a vertex outside [0, vertexCount)
     */
    static List<List<Integer>> buildAdjacencyList(int vertexCount, int[][] edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            if (u < 0 || u >= vertexCount || v < 0 || v >= vertexCount) {
                throw new IllegalArgumentException(
                    "Edge [" + u + ", " + v + "] references a vertex outside [0, " + vertexCount + ")");
            }
            adjacencyList.get(u).add(v);
            adjacencyList.get(v).add(u);
        }

        return adjacencyList;
    }

    /**
     * Determines whether the graph defined by the given vertices and edges is bipartite.
     *
     * Uses BFS 2-coloring: assigns alternating colors to adjacent vertices and
     * returns false if two neighbors ever receive the same color.
     * Handles disconnected graphs by starting BFS from every unvisited vertex.
     *
     * Time complexity:  O(V + E)
     * Space complexity: O(V)
     *
     * @param vertexCount number of vertices (0-indexed)
     * @param edges       array of {u, v} pairs representing undirected edges
     * @return true if the graph is bipartite, false otherwise
     */
    static boolean isBipartite(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacencyList = buildAdjacencyList(vertexCount, edges);

        int[] color = new int[vertexCount];
        Arrays.fill(color, UNVISITED);

        for (int i = 0; i < vertexCount; i++) {
            if (color[i] != UNVISITED) {
                continue;
            }

            Deque<Integer> queue = new ArrayDeque<>();
            color[i] = COLOR_A;
            queue.offer(i);

            while (!queue.isEmpty()) {
                int u = queue.poll();

                for (int neighbor : adjacencyList.get(u)) {
                    if (color[neighbor] == UNVISITED) {
                        color[neighbor] = (color[u] == COLOR_A) ? COLOR_B : COLOR_A;
                        queue.offer(neighbor);
                    } else if (color[neighbor] == color[u]) {
                        return false;
                    }
                }
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
    }
}
