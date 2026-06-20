package bipartite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Determines whether an {@link UndirectedGraph} is bipartite by attempting a
 * breadth-first 2-coloring of every connected component.
 *
 * <p>A graph is bipartite if and only if its vertices can be split into two
 * sets such that every edge crosses between the sets — equivalently, if it
 * contains no odd-length cycle. When a graph is not bipartite, the checker
 * reconstructs such an odd cycle from the breadth-first search tree as a
 * verifiable witness. The check runs in {@code O(V + E)} time and {@code O(V)}
 * additional space.
 */
public final class BipartiteChecker {

    private static final int UNCOLORED = -1;
    private static final int NO_PARENT = -1;
    private static final int COLOR_A = 0;
    private static final int COLOR_B = 1;

    /**
     * Checks whether the given graph is bipartite.
     *
     * @param graph the graph to inspect
     * @return a {@link BipartiteResult.Bipartite} with the two vertex partitions
     *         if the graph is bipartite, otherwise a
     *         {@link BipartiteResult.NotBipartite} carrying an odd-cycle witness
     * @throws NullPointerException if {@code graph} is null
     */
    public BipartiteResult check(UndirectedGraph graph) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }

        int order = graph.order();
        int[] color = new int[order];
        int[] parent = new int[order];
        Arrays.fill(color, UNCOLORED);
        Arrays.fill(parent, NO_PARENT);

        // Each iteration seeds the next uncolored component, so disconnected
        // graphs are handled correctly.
        for (int start = 0; start < order; start++) {
            if (color[start] != UNCOLORED) {
                continue;
            }
            List<Integer> oddCycle = colorComponent(graph, start, color, parent);
            if (oddCycle != null) {
                return new BipartiteResult.NotBipartite(oddCycle);
            }
        }

        return new BipartiteResult.Bipartite(verticesWithColor(color, COLOR_A),
                verticesWithColor(color, COLOR_B));
    }

    /**
     * Colors the component reachable from {@code start} with alternating colors,
     * recording BFS parents so an odd cycle can be reconstructed on conflict.
     *
     * @return {@code null} if the component is 2-colorable, otherwise the
     *         odd-length cycle formed by the first conflicting edge
     */
    private static List<Integer> colorComponent(UndirectedGraph graph, int start,
                                                int[] color, int[] parent) {
        Deque<Integer> queue = new ArrayDeque<>();
        color[start] = COLOR_A;
        queue.add(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : graph.neighbors(u)) {
                if (color[v] == UNCOLORED) {
                    color[v] = opposite(color[u]);
                    parent[v] = u;
                    queue.add(v);
                } else if (color[v] == color[u]) {
                    return reconstructOddCycle(parent, u, v);
                }
            }
        }
        return null;
    }

    /**
     * Reconstructs an odd cycle from the conflicting edge {@code (u, v)}, whose
     * endpoints share a color. The cycle runs up the BFS tree from {@code u} to
     * the lowest common ancestor of {@code u} and {@code v}, back down to
     * {@code v}, and closes via the conflicting edge {@code v -> u}.
     */
    private static List<Integer> reconstructOddCycle(int[] parent, int u, int v) {
        Map<Integer, Integer> depthOnUPath = new HashMap<>();
        List<Integer> uPath = new ArrayList<>();
        for (int x = u; x != NO_PARENT; x = parent[x]) {
            depthOnUPath.put(x, uPath.size());
            uPath.add(x);
        }

        List<Integer> vPath = new ArrayList<>();
        int ancestor = v;
        while (!depthOnUPath.containsKey(ancestor)) {
            vPath.add(ancestor);
            ancestor = parent[ancestor];
        }

        // u ... lowest common ancestor (inclusive)
        List<Integer> cycle = new ArrayList<>(uPath.subList(0, depthOnUPath.get(ancestor) + 1));
        // ... back down to v (lca already included above, v's own path reversed)
        Collections.reverse(vPath);
        cycle.addAll(vPath);
        return cycle;
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
