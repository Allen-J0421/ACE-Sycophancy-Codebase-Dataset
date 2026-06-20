package bipartite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Determines whether an {@link UndirectedGraph} is bipartite by attempting a
 * breadth-first 2-coloring of every connected component.
 *
 * <p>A graph is bipartite if and only if its vertices can be split into two
 * sets such that every edge crosses between the sets — equivalently, if it
 * contains no odd-length cycle. The check runs in {@code O(V + E)} time and
 * {@code O(V)} additional space.
 */
public final class BipartiteChecker {

    private static final int UNCOLORED = -1;
    private static final int COLOR_A = 0;
    private static final int COLOR_B = 1;

    /**
     * Checks whether the given graph is bipartite.
     *
     * @param graph the graph to inspect
     * @return a {@link BipartiteResult} that reports the answer and, when
     *         bipartite, the two vertex partitions
     * @throws NullPointerException if {@code graph} is null
     */
    public BipartiteResult check(UndirectedGraph graph) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }

        int order = graph.order();
        int[] color = new int[order];
        Arrays.fill(color, UNCOLORED);

        // Each iteration seeds the next uncolored component, so disconnected
        // graphs are handled correctly.
        for (int start = 0; start < order; start++) {
            if (color[start] == UNCOLORED && !colorComponent(graph, start, color)) {
                return BipartiteResult.notBipartite();
            }
        }

        return BipartiteResult.bipartite(verticesWithColor(color, COLOR_A),
                verticesWithColor(color, COLOR_B));
    }

    /**
     * Colors the component reachable from {@code start} with alternating colors.
     *
     * @return {@code true} if the component is 2-colorable, {@code false} if a
     *         conflicting edge (two neighbors sharing a color) is found
     */
    private static boolean colorComponent(UndirectedGraph graph, int start, int[] color) {
        Deque<Integer> queue = new ArrayDeque<>();
        color[start] = COLOR_A;
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : graph.neighbors(u)) {
                if (color[v] == UNCOLORED) {
                    color[v] = opposite(color[u]);
                    queue.add(v);
                } else if (color[v] == color[u]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int opposite(int color) {
        return COLOR_B - color;
    }

    private static List<Integer> verticesWithColor(int[] color, int target) {
        List<Integer> vertices = new ArrayList<>();
        for (int vertex = 0; vertex < color.length; vertex++) {
            if (color[vertex] == target) {
                vertices.add(vertex);
            }
        }
        return vertices;
    }
}
